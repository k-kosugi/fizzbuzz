// GitHub Actions が、特定の環境 (GitHub Environment) から AKS にデプロイするための ID。
// - ユーザー割り当てマネージド ID に GitHub の OIDC を信頼させる。信頼する対象はリポジトリ単位ではなく
//   「GitHub のオーナー配下のリポジトリで、指定した Environment から動くジョブ」。
//   リポジトリを増やしても、この ID と資格情報の追加は不要。
// - 柔軟なフェデレーション資格情報 (プレビュー) を使う。GitHub の場合は、sub に加えて
//   変更不可な repository_owner_id の一致が必須で、オーナー名が再利用されても別のオーナーは通らない。
// - 対象の namespace を AKS のマネージド namespace として作成する
// - 付与する権限は最小限:
//     クラスター             : Azure Kubernetes Service Cluster User Role (kubeconfig の取得だけ)
//     対象の namespace のみ  : Azure Kubernetes Service RBAC Writer (リソースの作成・更新)
//   クラスター全体の管理者権限は付けない。
//
// 注意: Environment 名は各リポジトリの管理者が作れるため、オーナー配下の任意のリポジトリが
// 同名の Environment を作ればこの ID を使える。範囲を絞りたい場合は workflowRefPattern を指定する。
targetScope = 'resourceGroup'

param name string
param location string = resourceGroup().location
param tags object = {}

@description('GitHub のオーナー名 (ユーザー名または Organization 名)')
param githubOwner string

@description('GitHub のオーナーの数値 ID。gh api users/<オーナー名> --jq .id などで取得する')
param githubOwnerId string

@description('GitHub Environment の名前。この環境で動くジョブだけがこの ID でログインできる')
param githubEnvironment string

@description('空以外の場合、job_workflow_ref がこのパターンに一致するワークフローだけに限定する (例: contoso/platform/.github/workflows/*@refs/heads/main)')
param workflowRefPattern string = ''

param clusterName string

@description('デプロイ先の namespace')
param namespaceName string

@description('namespace のリソースクォータ (必須)。この namespace の全 Pod の合計の上限。クォータがあるため、Pod には requests と limits の指定が必要')
param quotaCpuRequest string = '1500m'
param quotaCpuLimit string = '3000m'
param quotaMemoryRequest string = '3Gi'
param quotaMemoryLimit string = '6Gi'

@description('namespace に入ってくる通信のポリシー (必須)。AllowSameNamespace は同じ namespace 内からのみ許可する。Ingress コントローラーなど他の namespace から受けるときは、AllowAll か別途 NetworkPolicy が必要')
@allowed(['DenyAll', 'AllowAll', 'AllowSameNamespace'])
param networkIngress string = 'AllowSameNamespace'

@description('namespace から出ていく通信のポリシー (必須)')
@allowed(['DenyAll', 'AllowAll', 'AllowSameNamespace'])
param networkEgress string = 'AllowAll'

// 組み込みロール
var clusterUserRoleId = '4abbcc35-e782-43d8-92c5-2d3f1bd2253f' // Azure Kubernetes Service Cluster User Role
var rbacWriterRoleId = 'a7ffa36f-339b-4b5c-8bdf-e2c188b2c0eb' // Azure Kubernetes Service RBAC Writer

// 新しいリポジトリの sub は、オーナーとリポジトリの ID を含む変更不可の形式: repo:<owner>@<ownerId>/<repo>@<repoId>:environment:<環境名>
var workflowClause = empty(workflowRefPattern)
  ? ''
  : ' and claims[\'job_workflow_ref\'] matches \'${workflowRefPattern}\''
var expression = 'claims[\'sub\'] matches \'repo:${githubOwner}@${githubOwnerId}/*:environment:${githubEnvironment}\' and claims[\'repository_owner_id\'] eq \'${githubOwnerId}\'${workflowClause}'

resource identity 'Microsoft.ManagedIdentity/userAssignedIdentities@2024-11-30' = {
  name: name
  location: location
  tags: tags
}

resource federatedCredential 'Microsoft.ManagedIdentity/userAssignedIdentities/federatedIdentityCredentials@2025-05-31-PREVIEW' = {
  parent: identity
  name: 'github-env-${githubEnvironment}'
  properties: {
    issuer: 'https://token.actions.githubusercontent.com'
    audiences: ['api://AzureADTokenExchange']
    claimsMatchingExpression: {
      languageVersion: 1
      value: expression
    }
  }
}

resource cluster 'Microsoft.ContainerService/managedClusters@2025-10-01' existing = {
  name: clusterName
}

resource namespace 'Microsoft.ContainerService/managedClusters/managedNamespaces@2025-10-01' = {
  parent: cluster
  name: namespaceName
  location: location
  tags: tags
  properties: {
    adoptionPolicy: 'Never'
    deletePolicy: 'Keep'
    defaultNetworkPolicy: {
      ingress: networkIngress
      egress: networkEgress
    }
    defaultResourceQuota: {
      cpuRequest: quotaCpuRequest
      cpuLimit: quotaCpuLimit
      memoryRequest: quotaMemoryRequest
      memoryLimit: quotaMemoryLimit
    }
  }
}

resource clusterUser 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  scope: cluster
  name: guid(cluster.id, identity.id, clusterUserRoleId)
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', clusterUserRoleId)
    principalId: identity.properties.principalId
    principalType: 'ServicePrincipal'
  }
}

resource namespaceWriter 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  scope: namespace
  name: guid(namespace.id, identity.id, rbacWriterRoleId)
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', rbacWriterRoleId)
    principalId: identity.properties.principalId
    principalType: 'ServicePrincipal'
  }
}

output clientId string = identity.properties.clientId
output principalId string = identity.properties.principalId
