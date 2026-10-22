// GitHub Actions がイメージを push するための ID。
// ユーザー割り当てマネージド ID に GitHub の OIDC を信頼させ、ACR への AcrPush だけを付与する。
// パスワードやシークレットは持たない。
//
// 信頼する対象はリポジトリ単位ではなく「GitHub のオーナー (ユーザーまたは Organization) 配下のリポジトリ」。
// リポジトリを増やしても、この ID と資格情報の追加は不要。
// 柔軟なフェデレーション資格情報 (プレビュー) を使う。GitHub の場合は、sub に加えて
// 変更不可な repository_owner_id の一致が必須で、オーナー名が再利用されても別のオーナーは通らない。
targetScope = 'resourceGroup'

param name string
param location string = resourceGroup().location
param tags object = {}

@description('GitHub のオーナー名 (ユーザー名または Organization 名)')
param githubOwner string

@description('GitHub のオーナーの数値 ID。gh api users/<オーナー名> --jq .id などで取得する')
param githubOwnerId string

@description('push を許可するブランチ。このブランチで動くジョブだけがこの ID でログインできる')
param branch string = 'main'

@description('空以外の場合、job_workflow_ref がこのパターンに一致するワークフローだけに限定する (例: contoso/platform/.github/workflows/*@refs/heads/main)')
param workflowRefPattern string = ''

param registryName string

// 組み込みロール AcrPush
var acrPushRoleId = '8311e382-0749-4cb8-b61a-304f252e45ec'

// 新しいリポジトリの sub は、オーナーとリポジトリの ID を含む変更不可の形式: repo:<owner>@<ownerId>/<repo>@<repoId>:ref:refs/heads/<branch>
var workflowClause = empty(workflowRefPattern)
  ? ''
  : ' and claims[\'job_workflow_ref\'] matches \'${workflowRefPattern}\''
var expression = 'claims[\'sub\'] matches \'repo:${githubOwner}@${githubOwnerId}/*:ref:refs/heads/${branch}\' and claims[\'repository_owner_id\'] eq \'${githubOwnerId}\'${workflowClause}'

resource identity 'Microsoft.ManagedIdentity/userAssignedIdentities@2024-11-30' = {
  name: name
  location: location
  tags: tags
}

resource federatedCredential 'Microsoft.ManagedIdentity/userAssignedIdentities/federatedIdentityCredentials@2025-05-31-PREVIEW' = {
  parent: identity
  name: 'github-${branch}'
  properties: {
    issuer: 'https://token.actions.githubusercontent.com'
    audiences: ['api://AzureADTokenExchange']
    claimsMatchingExpression: {
      languageVersion: 1
      value: expression
    }
  }
}

resource registry 'Microsoft.ContainerRegistry/registries@2025-11-01' existing = {
  name: registryName
}

resource acrPush 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  scope: registry
  name: guid(registry.id, identity.id, acrPushRoleId)
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', acrPushRoleId)
    principalId: identity.properties.principalId
    principalType: 'ServicePrincipal'
  }
}

output clientId string = identity.properties.clientId
output principalId string = identity.properties.principalId
