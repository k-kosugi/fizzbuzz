# FizzBuzz

FizzBuzz を 1 件ずつ保存・取得する Web アプリケーションです。

| ディレクトリ | 内容 |
|---|---|
| [backend/](./backend) | REST API (Java 25 / Spring Boot 4 / Maven) |
| [frontend/](./frontend) | 画面 (React 19 / TypeScript / Vite) |
| [infrastructure/](./infrastructure) | Azure の基盤 (Bicep: ACR、AKS) |

## CI (GitHub Actions)

`backend-ci` と `frontend-ci` が、次の順に実行します。

1. ビルドとテスト (backend は `./mvnw verify`、frontend は lint とビルド)
2. イメージのビルドと Trivy のスキャン (修正版がある HIGH/CRITICAL があれば失敗)
3. **`main` への push のときだけ**、Azure に OIDC でログインし、スキャンしたイメージと同一のものを ACR に push (タグはコミット SHA)
4. **`main` への push のときだけ**、配備用リポジトリ [fizzbuzz-deploy](https://github.com/k-kosugi/fizzbuzz-deploy) の `clusters/staging/kustomization.yaml` の digest を、push したイメージのものに更新してコミット

push 先のイメージは `<ACR 名>.azurecr.io/fizzbuzz-backend:<コミット SHA>` と `fizzbuzz-frontend:<コミット SHA>` です。digest は、各ジョブのサマリーに表示されます。
PR では、スキャンまでで止まります。

### staging への自動配備

4 の更新後は、AKS 上の Flux が配備用リポジトリの変更を検知して (最大 60 秒)、クラスターに反映します。

```text
fizzbuzz の main に push
  -> CI: ビルド、テスト、スキャン、ACR に push
  -> CI: fizzbuzz-deploy の digest を更新 (コミット)
  -> Flux: 変更を検知して AKS に反映
```

- 更新の処理は、複合アクション [update-deploy-repo](./.github/actions/update-deploy-repo) です。digest が変わらないとき (イメージの中身が同一のとき) は、コミットしません。
- `fizzbuzz-deploy` への書き込みには、**そのリポジトリだけに登録した、読み書き可のデプロイキー**を使います。秘密鍵は、このリポジトリのシークレット `DEPLOY_REPO_SSH_KEY` に保存しています。
- backend と frontend が同時に更新して競合した場合は、最新を取り直して再試行します。
- 古いコミットの CI が、新しいコミットの CI より後に終わると、古い digest で上書きされることがあります。短時間に連続して push しないでください。
- 本番への昇格は、まだ含まれていません。

デプロイキーを作り直すときは、`fizzbuzz-deploy` の Settings > Deploy keys で、読み書き可の公開鍵を登録し、秘密鍵を `DEPLOY_REPO_SSH_KEY` に設定します。

必要なリポジトリの Variables (`AZURE_TENANT_ID`、`AZURE_SUBSCRIPTION_ID`、`ACR_NAME`、`AZURE_BUILD_CLIENT_ID`) は、[infrastructure/README.md](./infrastructure/README.md) を参照してください。

## 必要なもの

- JDK 25
- Node.js (npm 付き)

Maven は同梱の `mvnw` を使うため、インストール不要です。

## 起動方法

backend と frontend を別々のターミナルで起動します。

```bash
# backend (http://localhost:8080)
cd backend
./mvnw spring-boot:run

# frontend (http://localhost:5173)
cd frontend
npm install
npm run dev
```

ブラウザーで http://localhost:5173 を開きます。
開発中は、frontend の `/api` へのリクエストが backend に中継されます。
中継先を変える場合は、環境変数 `BACKEND_URL` を指定します。

```bash
BACKEND_URL=http://localhost:9090 npm run dev
```

## API

| メソッド | パス | 内容 |
|---|---|---|
| POST | `/api/fizzbuzz` | 数値を 1 件保存する。body は `{"number": 15}` (1 以上の整数)。成功時は `201` と `Location` ヘッダーを返す |
| GET | `/api/fizzbuzz/{number}` | 保存済みの数値を 1 件取得する。保存されていなければ `404` |

レスポンスの例です。

```json
{"number": 15, "value": "FizzBuzz"}
```

- 3 の倍数は `Fizz`、5 の倍数は `Buzz`、両方の倍数は `FizzBuzz`、それ以外は数値の文字列です。
- エラーは Problem Details (`application/problem+json`) で返します。
- 仕様は、backend の起動後に http://localhost:8080/openapi.yaml で確認できます。

```bash
curl -X POST http://localhost:8080/api/fizzbuzz \
  -H "Content-Type: application/json" \
  -d '{"number":15}'

curl http://localhost:8080/api/fizzbuzz/15
```

### 保存先について

現在はサーバーのメモリ上に保存します。再起動すると保存した内容は消えます。
複数の Pod に分けて動かす場合、Pod ごとに保存内容が異なる点に注意してください。

メモリ上には、数値に加えて `isFizz`、`isBuzz`、`isFizzBuzz`、`toString` の結果を列として保持します。
PostgreSQL に保存する `JdbcFizzBuzzRepository` も残していますが、現在は Spring の管理対象にしていません。

## テスト

```bash
# backend: 単体テストと結合テスト (*IT.java)
cd backend
./mvnw verify

# frontend: 静的解析とビルド
cd frontend
npm run lint
npm run build
```

## backend の構成

クリーンアーキテクチャーに基づいています。パッケージは `com.microsoft.japan.fizzbuzz` です。

```text
domain          FizzBuzz (Entity)、FizzBuzzRepository
application    register (登録)、find (取得) のユースケース
adapter.in.web  REST コントローラー、リクエスト/レスポンス、例外ハンドラー
adapter.out.persistence  メモリ版・JDBC 版の Repository
configuration   Bean の組み立て
```

ドメイン層とアプリケーション層には Spring のアノテーションを付けず、`configuration` で組み立てています。
