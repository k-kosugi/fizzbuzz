// ACR の AcrPull ロールを、AKS の kubelet ID に付与する。
// AKS からのイメージ取得は、パスワードではなくこのロールで認可する。
targetScope = 'resourceGroup'

param registryName string

@description('AKS の kubelet ID (identityProfile.kubeletidentity) のオブジェクト ID')
param kubeletPrincipalId string

// 組み込みロール AcrPull
var acrPullRoleId = '7f951dda-4ed3-4680-a7ca-43fe172d538d'

resource registry 'Microsoft.ContainerRegistry/registries@2025-11-01' existing = {
  name: registryName
}

resource acrPull 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  scope: registry
  name: guid(registry.id, kubeletPrincipalId, acrPullRoleId)
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', acrPullRoleId)
    principalId: kubeletPrincipalId
    principalType: 'ServicePrincipal'
  }
}
