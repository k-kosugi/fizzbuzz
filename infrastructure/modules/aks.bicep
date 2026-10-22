// AKS クラスター。
// - 認証: Entra ID + Azure RBAC for Kubernetes。ローカルアカウント (静的な管理者 kubeconfig) は無効
// - ID: OIDC issuer と Workload Identity を有効化
// - ネットワーク: Azure CNI Overlay + Cilium (VNet は AKS が管理)
// - 更新: Kubernetes は patch、ノード OS はノードイメージを自動更新
targetScope = 'resourceGroup'

param name string
param location string = resourceGroup().location
param tags object = {}

@allowed(['Free', 'Standard', 'Premium'])
param skuTier string = 'Standard'

@description('空の場合は AKS の既定バージョンを使う')
param kubernetesVersion string = ''

param nodeVmSize string = 'Standard_D2s_v5'

@minValue(1)
param nodeMinCount int = 1

@minValue(1)
param nodeMaxCount int = 3

@description('空の場合はゾーンを指定しない。東日本リージョンでは [1, 2, 3] を使える')
param availabilityZones string[] = []

@description('クラスターの管理者にする Entra ID グループのオブジェクト ID')
param adminGroupObjectIds string[] = []

resource cluster 'Microsoft.ContainerService/managedClusters@2025-10-01' = {
  name: name
  location: location
  tags: tags
  sku: {
    name: 'Base'
    tier: skuTier
  }
  identity: {
    type: 'SystemAssigned'
  }
  properties: {
    dnsPrefix: name
    kubernetesVersion: empty(kubernetesVersion) ? null : kubernetesVersion
    disableLocalAccounts: true
    aadProfile: {
      managed: true
      enableAzureRBAC: true
      adminGroupObjectIDs: adminGroupObjectIds
      tenantID: tenant().tenantId
    }
    oidcIssuerProfile: {
      enabled: true
    }
    securityProfile: {
      workloadIdentity: {
        enabled: true
      }
    }
    networkProfile: {
      networkPlugin: 'azure'
      networkPluginMode: 'overlay'
      networkDataplane: 'cilium'
      networkPolicy: 'cilium'
      loadBalancerSku: 'standard'
      outboundType: 'loadBalancer'
    }
    agentPoolProfiles: [
      {
        name: 'system'
        mode: 'System'
        type: 'VirtualMachineScaleSets'
        osType: 'Linux'
        osSKU: 'AzureLinux'
        vmSize: nodeVmSize
        count: nodeMinCount
        minCount: nodeMinCount
        maxCount: nodeMaxCount
        enableAutoScaling: true
        availabilityZones: empty(availabilityZones) ? null : availabilityZones
      }
    ]
    autoUpgradeProfile: {
      upgradeChannel: 'patch'
      nodeOSUpgradeChannel: 'NodeImage'
    }
  }
}

output id string = cluster.id
output name string = cluster.name
output oidcIssuerUrl string = cluster.properties.oidcIssuerProfile.issuerURL
output kubeletPrincipalId string = cluster.properties.identityProfile.kubeletidentity.objectId
