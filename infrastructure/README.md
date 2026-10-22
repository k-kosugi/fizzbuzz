# infrastructure

FizzBuzz を動かす Azure の基盤を Bicep で定義します。

## 作成するもの

| リソースグループ | リソース |
|---|---|
| `rg-fizzbuzz-shared` | Azure Container Registry (staging と production で共有)、GitHub Actions の push 用 ID `id-fizzbuzz-github-build` |
| `rg-fizzbuzz-staging` | AKS `aks-fizzbuzz-staging`、namespace `fizzbuzz`、GitHub Actions のデプロイ用 ID `id-fizzbuzz-github-deploy-staging` |
| `rg-fizzbuzz-production` | 同上 (現在はコメントアウトしており、作成しない) |

各 AKS の kubelet ID には、ACR の `AcrPull` ロールを付与します。

## GitHub Actions 用の ID

パスワードやシークレットは使わず、GitHub の OIDC を信頼するユーザー割り当てマネージド ID でログインします。

**信頼する対象は、リポジトリ単位ではなく GitHub のオーナー (ユーザーまたは Organization) です。** リポジトリを増やしても、ID や資格情報の追加は不要です。
そのために、**柔軟なフェデレーション資格情報 (プレビュー)** のワイルドカードを使っています。
オーナーは [main.bicepparam](./main.bicepparam) の `githubOwner` と `githubOwnerId` で指定します。数値 ID は `gh api users/<オーナー名> --jq .id` で取得できます。

| ID | 信頼する条件 (`claimsMatchingExpression`) | 付与する権限 |
|---|---|---|
| `id-fizzbuzz-github-build` | `sub` が `repo:<owner>@<ownerId>/*:ref:refs/heads/main` に一致し、かつ `repository_owner_id` が一致 | ACR の `AcrPush` |
| `id-fizzbuzz-github-deploy-<環境名>` | `sub` が `repo:<owner>@<ownerId>/*:environment:<環境名>` に一致し、かつ `repository_owner_id` が一致 | クラスターの `Azure Kubernetes Service Cluster User Role`、namespace `fizzbuzz` だけの `Azure Kubernetes Service RBAC Writer` |

- GitHub は、2026-07-15 以降に作成・改名・移管されたリポジトリの `sub` を、オーナーとリポジトリの ID を含む変更不可の形式 (`repo:<owner>@<ownerId>/<repo>@<repoId>:...`) にします。このテンプレートはその形式に対応しています。既存のリポジトリで、従来の形式 (`repo:<owner>/<repo>:...`) のままのものは、そのままでは一致しません。`gh api repos/<owner>/<repo>/actions/oidc/customization/sub` の `sub_claim_prefix` で確認できます。
- デプロイ用の ID には、クラスター全体の管理者権限を付けていません。
- GitHub の Environment 名は、環境名 (`staging`、`production`) と同じにしてください。
- 作成した namespace は、AKS のマネージド namespace です。

### 信頼範囲についての注意

Environment 名は各リポジトリの管理者が作れます。**オーナー配下の任意のリポジトリが `staging` という Environment を作れば、この ID でログインできます。** 範囲を絞りたい場合は、`githubWorkflowRefPattern` に、共通のデプロイ用ワークフローを指定してください (例: `<owner>/platform/.github/workflows/*@refs/heads/main`)。`job_workflow_ref` がこのパターンに一致するジョブだけが対象になります。

デプロイ後、出力に表示される値を GitHub の Variables に登録します。これらは秘密情報ではありません。

| 登録先 | 変数 | 出力 |
|---|---|---|
| リポジトリ | `AZURE_TENANT_ID`、`AZURE_SUBSCRIPTION_ID`、`ACR_NAME`、`AZURE_BUILD_CLIENT_ID` | `tenantId`、`subscriptionId`、`registryName`、`buildClientId` |
| Environment `staging` | `AZURE_CLIENT_ID`、`AKS_RESOURCE_GROUP`、`AKS_NAME` | `clusters` の `deployClientId`、`resourceGroup`、`name` |

```bash
az deployment sub show --name fizzbuzz-infra --query properties.outputs
```

## 構成の方針

- **認証**: Entra ID と Azure RBAC for Kubernetes。ローカルアカウント (静的な管理者 kubeconfig) は無効です。
- **ID**: OIDC issuer と Workload Identity を有効にしています。
- **ネットワーク**: Azure CNI Overlay + Cilium。VNet は AKS が管理します。
- **更新**: Kubernetes は patch、ノード OS はノードイメージを自動更新します。
- **ACR**: 管理者ユーザーと匿名 pull は無効です。公開ネットワークは有効のままです (GitHub Actions から push するため)。
- **ノード**: システムノードプールのみで、アプリもここで動かします。ワークロードが増えたら、ユーザーノードプールを分けてください。

環境ごとの設定 (SKU、ノード数、ゾーン) は [main.bicepparam](./main.bicepparam) で変更できます。

## デプロイ

サブスクリプションスコープのデプロイです。サブスクリプションに対する権限 (Contributor と、ロール割り当てのための User Access Administrator など) が必要です。

```bash
az login
az account list -o table   # デプロイ先のサブスクリプションとテナントを確認する
az account set --subscription <subscription-id>
az account show --query '{name:name, tenant:tenantId, user:user.name}' -o table
```

`az` CLI と VS Code の Azure 拡張は、別々にログインしています。**デプロイ先は、`az account show` の結果で必ず確認してください。** 複数のテナントやサブスクリプションにログインしている場合は、各コマンドに `--subscription <subscription-id>` を付けると、既定の設定に左右されません。

```bash
# 変更内容の確認 (何も作成しない)
az deployment sub what-if \
  --location japaneast \
  --template-file main.bicep \
  --parameters main.bicepparam

# デプロイ
az deployment sub create \
  --location japaneast \
  --name fizzbuzz-infra \
  --template-file main.bicep \
  --parameters main.bicepparam
```

## デプロイ後に必要なこと

- クラスターの管理者を使えるようにするため、`adminGroupObjectIds` に Entra ID グループを指定するか、**Azure Kubernetes Service RBAC Cluster Admin** などのロールを割り当ててください。ローカルアカウントを無効にしているため、ロールがないと `kubectl` で操作できません。
- GitHub Actions からデプロイするための ID (OIDC) とロールは含まれています。ただし、GitHub 側の Environment (`staging`) と Variables は、手動で作成する必要があります。

## Flux (GitOps)

各 AKS に、GitOps 拡張 (`microsoft.flux`) で Flux v2 を導入します。

- **Flux の導入は常に行います。** `fluxRepositoryUrl` が空の間は、導入だけです。
- **現在は、配備用リポジトリ [k-kosugi/fizzbuzz-deploy](https://github.com/k-kosugi/fizzbuzz-deploy) (public) を接続しています。** 各クラスターは `<fluxPathPrefix>/<環境名>` (既定は `./clusters/staging`) を監視し、変更を自動で反映します。マニフェストの構成と、配備するバージョンの変え方は、そのリポジトリの README を参照してください。
- 別のリポジトリを使うときは、リポジトリを作り、初期のマニフェストを push してから、`fluxRepositoryUrl` を指定して再デプロイします。
- 確認間隔は 60 秒です。Git から削除したリソースは、クラスターからも削除されます (`prune`)。

```bash
# 配備用リポジトリを接続する (public リポジトリの場合)
az deployment sub create --location japaneast --name fizzbuzz-infra \
  --template-file main.bicep --parameters main.bicepparam \
  --parameters fluxRepositoryUrl=https://github.com/<owner>/fizzbuzz-deploy

# private リポジトリの場合は、読み取り専用のトークンを環境変数で渡す (ファイルには保存しない)
export FLUX_HTTPS_USER=<ユーザー名>
export FLUX_HTTPS_KEY=<読み取り専用のトークン>
```

- トークンは、配備用リポジトリだけを読み取れる権限に絞ってください。
- Flux は、namespace `fizzbuzz` にもクラスターの管理者権限で反映します。GitOps で配備する場合、GitHub Actions のデプロイ用 ID (namespace への書き込み権限) は不要になります。
- `Microsoft.KubernetesConfiguration` と `Microsoft.ContainerService` のリソースプロバイダーが、サブスクリプションで登録されている必要があります。未登録の場合は、`az provider register --namespace Microsoft.KubernetesConfiguration` を実行してください。

## このテンプレートに含まれないもの

- 監視 (Container insights、Managed Prometheus、Log Analytics)
- Microsoft Defender for Containers の有効化 (サブスクリプション単位の設定)
- Ingress コントローラー、証明書、DNS
- 配備用リポジトリ (`fizzbuzz-deploy`) の作成と、初期のマニフェスト
- GitHub Actions のワークフローのうち、AKS へのデプロイと配備用リポジトリの更新 (ACR への push は `backend-ci` と `frontend-ci` が行います)
- 閉域化 (プライベートクラスター、ACR のプライベートエンドポイント)
