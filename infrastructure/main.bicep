// FizzBuzz の Azure 基盤。サブスクリプションスコープでデプロイする。
//   rg-<workload>-shared      : ACR (staging と production で共有)
//   rg-<workload>-<環境名>    : AKS (環境ごと)
targetScope = 'subscription'

@description('リソース名に使う短い名前')
param workloadName string = 'fizzbuzz'

param location string = 'japaneast'

@description('ACR の SKU')
@allowed(['Basic', 'Standard', 'Premium'])
param acrSku string = 'Standard'

@description('クラスターの管理者にする Entra ID グループのオブジェクト ID')
param adminGroupObjectIds string[] = []

@description('GitHub のオーナー名 (ユーザー名または Organization 名)。GitHub Actions の OIDC を信頼する対象で、リポジトリは限定しない')
param githubOwner string

@description('GitHub のオーナーの数値 ID。gh api users/<オーナー名> --jq .id などで取得する')
param githubOwnerId string

@description('空以外の場合、job_workflow_ref がこのパターンに一致するワークフローだけに限定する (例: contoso/platform/.github/workflows/*@refs/heads/main)')
param githubWorkflowRefPattern string = ''

@description('イメージの push を許可するブランチ')
param githubBuildBranch string = 'main'

@description('アプリのデプロイ先 Kubernetes namespace。GitHub Actions にはこの namespace への権限だけを付与する')
param appNamespace string = 'fizzbuzz'

@description('Flux が監視する配備用 Git リポジトリの URL (https)。空の場合は Flux の導入だけを行い、リポジトリは後から接続する')
param fluxRepositoryUrl string = ''

param fluxBranch string = 'main'

@description('配備用リポジトリ内のディレクトリの接頭辞。各クラスターは <接頭辞>/<環境名> を監視する')
param fluxPathPrefix string = './clusters'

@description('private リポジトリの場合の HTTPS ユーザー名。public リポジトリなら空')
param fluxHttpsUser string = ''

@secure()
@description('private リポジトリの場合の読み取り用アクセストークン。値は環境変数などで渡し、ファイルに保存しない')
param fluxHttpsKey string = ''

param tags object = {
  workload: workloadName
  managedBy: 'bicep'
}

type EnvironmentConfig = {
  @description('環境名。リソース名に使う (例: staging, production)')
  name: string
  skuTier: 'Free' | 'Standard' | 'Premium'
  nodeVmSize: string
  nodeMinCount: int
  nodeMaxCount: int
  availabilityZones: string[]
}

@description('作成する AKS の環境。環境ごとに専用のリソースグループとクラスターを作る')
param environments EnvironmentConfig[]

var sharedResourceGroupName = 'rg-${workloadName}-shared'
// グローバルで一意な名前にするため、サブスクリプション ID からハッシュを付ける
var registryName = 'cr${workloadName}${uniqueString(subscription().id, workloadName)}'

resource sharedResourceGroup 'Microsoft.Resources/resourceGroups@2025-04-01' = {
  name: sharedResourceGroupName
  location: location
  tags: tags
}

resource environmentResourceGroups 'Microsoft.Resources/resourceGroups@2025-04-01' = [
  for environment in environments: {
    name: 'rg-${workloadName}-${environment.name}'
    location: location
    tags: union(tags, { environment: environment.name })
  }
]

module registry 'modules/acr.bicep' = {
  scope: sharedResourceGroup
  name: 'acr'
  params: {
    name: registryName
    location: location
    sku: acrSku
    tags: tags
  }
}

module aks 'modules/aks.bicep' = [
  for (environment, index) in environments: {
    scope: environmentResourceGroups[index]
    name: 'aks-${environment.name}'
    params: {
      name: 'aks-${workloadName}-${environment.name}'
      location: location
      tags: union(tags, { environment: environment.name })
      skuTier: environment.skuTier
      nodeVmSize: environment.nodeVmSize
      nodeMinCount: environment.nodeMinCount
      nodeMaxCount: environment.nodeMaxCount
      availabilityZones: environment.availabilityZones
      adminGroupObjectIds: adminGroupObjectIds
    }
  }
]

module flux 'modules/flux.bicep' = [
  for (environment, index) in environments: {
    scope: environmentResourceGroups[index]
    name: 'flux-${environment.name}'
    params: {
      clusterName: aks[index].outputs.name
      repositoryUrl: fluxRepositoryUrl
      branch: fluxBranch
      path: '${fluxPathPrefix}/${environment.name}'
      httpsUser: fluxHttpsUser
      httpsKey: fluxHttpsKey
    }
  }
]

module acrPull 'modules/acr-pull-role.bicep' = [
  for (environment, index) in environments: {
    scope: sharedResourceGroup
    name: 'acr-pull-${environment.name}'
    params: {
      registryName: registry.outputs.name
      kubeletPrincipalId: aks[index].outputs.kubeletPrincipalId
    }
  }
]

module githubBuildAccess 'modules/github-build-access.bicep' = {
  scope: sharedResourceGroup
  name: 'github-build-access'
  params: {
    name: 'id-${workloadName}-github-build'
    location: location
    tags: tags
    githubOwner: githubOwner
    githubOwnerId: githubOwnerId
    workflowRefPattern: githubWorkflowRefPattern
    branch: githubBuildBranch
    registryName: registry.outputs.name
  }
}

// GitHub Environment の名前は、環境名 (staging, production) と同じにする
module githubDeployAccess 'modules/github-deploy-access.bicep' = [
  for (environment, index) in environments: {
    scope: environmentResourceGroups[index]
    name: 'github-deploy-access-${environment.name}'
    params: {
      name: 'id-${workloadName}-github-deploy-${environment.name}'
      location: location
      tags: union(tags, { environment: environment.name })
      githubOwner: githubOwner
      githubOwnerId: githubOwnerId
      githubEnvironment: environment.name
      workflowRefPattern: githubWorkflowRefPattern
      clusterName: aks[index].outputs.name
      namespaceName: appNamespace
    }
  }
]

output registryName string = registry.outputs.name
output registryLoginServer string = registry.outputs.loginServer
output tenantId string = tenant().tenantId
output subscriptionId string = subscription().subscriptionId
output buildClientId string = githubBuildAccess.outputs.clientId
output clusters array = [
  for (environment, index) in environments: {
    environment: environment.name
    resourceGroup: environmentResourceGroups[index].name
    name: aks[index].outputs.name
    oidcIssuerUrl: aks[index].outputs.oidcIssuerUrl
    deployClientId: githubDeployAccess[index].outputs.clientId
  }
]
