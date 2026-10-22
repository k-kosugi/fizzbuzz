using 'main.bicep'

param workloadName = 'fizzbuzz'
param location = 'japaneast'
param acrSku = 'Standard'

// クラスター管理者にする Entra ID グループのオブジェクト ID (例: ['00000000-0000-0000-0000-000000000000'])
param adminGroupObjectIds = []

// GitHub Actions の OIDC を信頼するオーナー。リポジトリは限定しないので、リポジトリを増やしても ID の追加は不要。
// 数値 ID は gh api users/<オーナー名> --jq .id で取得できる
param githubOwner = 'k-kosugi'
param githubOwnerId = '17643197'
// 空以外にすると、job_workflow_ref が一致するワークフロー (例: 共通のデプロイ用ワークフロー) だけに限定できる
// 例: 'kentakosugi_microsoft/platform/.github/workflows/*@refs/heads/main'
param githubWorkflowRefPattern = ''
param githubBuildBranch = 'main'

// アプリのデプロイ先 namespace。GitHub Actions にはこの namespace への書き込み権限だけを付与する
param appNamespace = 'fizzbuzz'

// Flux (GitOps) が監視する配備用リポジトリ。空の間は、Flux の導入だけを行う。
// リポジトリを作成して初期のマニフェストを push したら、URL を指定して再デプロイする。
// 各クラスターは <fluxPathPrefix>/<環境名> (例: ./clusters/staging) を監視する。
param fluxRepositoryUrl = ''
// param fluxRepositoryUrl = 'https://github.com/kentakosugi_microsoft/fizzbuzz-deploy'
param fluxBranch = 'main'
param fluxPathPrefix = './clusters'
// private リポジトリの場合は、読み取り専用のトークンを環境変数で渡す (ファイルに保存しない)
param fluxHttpsUser = readEnvironmentVariable('FLUX_HTTPS_USER', '')
param fluxHttpsKey = readEnvironmentVariable('FLUX_HTTPS_KEY', '')

param environments = [
  {
    // 総合テスト用。コストを抑えるため Free tier、ゾーンなし、小さい構成
    // Contoso のサブスクリプションでは japaneast の D2s_v5 が制限されているため、D2s_v3 を使う
    name: 'staging'
    skuTier: 'Free'
    nodeVmSize: 'Standard_D2s_v3'
    nodeMinCount: 1
    nodeMaxCount: 2
    availabilityZones: []
  }
  // 本番。今回は作成しない。SLA のある Standard tier で、3 つのゾーンに分散する
  // {
  //   name: 'production'
  //   skuTier: 'Standard'
  //   nodeVmSize: 'Standard_D2s_v5'
  //   nodeMinCount: 3
  //   nodeMaxCount: 5
  //   availabilityZones: ['1', '2', '3']
  // }
]
