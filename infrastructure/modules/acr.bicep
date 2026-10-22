// Azure Container Registry。staging と production で同じイメージ (digest) を昇格させるため、1 つを共有する。
targetScope = 'resourceGroup'

@description('レジストリ名。グローバルで一意な英小文字と数字の 5 から 50 文字')
@minLength(5)
@maxLength(50)
param name string

param location string = resourceGroup().location
param tags object = {}

@description('Standard で十分。プライベートエンドポイントやゾーン冗長、geo レプリケーションが必要になったら Premium にする')
@allowed(['Basic', 'Standard', 'Premium'])
param sku string = 'Standard'

resource registry 'Microsoft.ContainerRegistry/registries@2025-11-01' = {
  name: name
  location: location
  tags: tags
  sku: {
    name: sku
  }
  properties: {
    // 認証は Entra ID (マネージド ID / OIDC) に統一し、管理者ユーザーと匿名 pull は無効にする
    adminUserEnabled: false
    anonymousPullEnabled: false
    // GitHub Actions のランナーから push するため、公開ネットワークは有効のままにする。閉域化は Premium で検討する
    publicNetworkAccess: 'Enabled'
    dataEndpointEnabled: false
    policies: {
      azureADAuthenticationAsArmPolicy: {
        status: 'enabled'
      }
    }
  }
}

output id string = registry.id
output name string = registry.name
output loginServer string = registry.properties.loginServer
