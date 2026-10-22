// AKS に Flux v2 (GitOps 拡張: microsoft.flux) を導入し、必要なら Git リポジトリの監視を設定する。
// - 拡張は常に導入する
// - repositoryUrl が空の間は、Flux の導入だけを行う (リポジトリを後から接続できる)
// - repositoryUrl を指定すると、そのブランチとパスを監視して、クラスターに自動で反映する
targetScope = 'resourceGroup'

param clusterName string

@description('監視する Git リポジトリの URL (https)。空の場合は Flux の導入だけを行う')
param repositoryUrl string = ''

param branch string = 'main'

@description('リポジトリ内の、このクラスターに適用するディレクトリ。Kustomize または Helm の入口を置く')
param path string = './'

@description('private リポジトリの場合の HTTPS ユーザー名。public リポジトリなら空')
param httpsUser string = ''

@secure()
@description('private リポジトリの場合のアクセストークン (読み取り専用)。public リポジトリなら空。値をリポジトリに保存しない')
param httpsKey string = ''

@description('Git の変更を確認する間隔 (秒)')
param syncIntervalInSeconds int = 60

var useAuth = !empty(httpsUser) && !empty(httpsKey)

resource cluster 'Microsoft.ContainerService/managedClusters@2025-10-01' existing = {
  name: clusterName
}

resource flux 'Microsoft.KubernetesConfiguration/extensions@2024-11-01' = {
  scope: cluster
  name: 'flux'
  properties: {
    extensionType: 'microsoft.flux'
    autoUpgradeMinorVersion: true
    scope: {
      cluster: {
        releaseNamespace: 'flux-system'
      }
    }
  }
}

resource configuration 'Microsoft.KubernetesConfiguration/fluxConfigurations@2025-04-01' = if (!empty(repositoryUrl)) {
  scope: cluster
  name: 'fizzbuzz'
  dependsOn: [flux]
  properties: {
    scope: 'cluster'
    namespace: 'flux-system'
    sourceKind: 'GitRepository'
    suspend: false
    gitRepository: {
      url: repositoryUrl
      timeoutInSeconds: 600
      syncIntervalInSeconds: syncIntervalInSeconds
      repositoryRef: {
        branch: branch
      }
      httpsUser: useAuth ? httpsUser : null
    }
    configurationProtectedSettings: useAuth
      ? {
          // 保護された設定は base64 で渡す
          httpsKey: base64(httpsKey)
        }
      : {}
    kustomizations: {
      apps: {
        path: path
        timeoutInSeconds: 600
        syncIntervalInSeconds: syncIntervalInSeconds
        retryIntervalInSeconds: 120
        // Git から削除したリソースは、クラスターからも削除する
        prune: true
        force: false
      }
    }
  }
}

output extensionName string = flux.name
