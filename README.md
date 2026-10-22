# FizzBuzz

FizzBuzz を 1 件ずつ保存・取得する Web アプリケーションです。

| ディレクトリ | 内容 |
|---|---|
| [backend/](./backend) | REST API (Java 25 / Spring Boot 4 / Maven) |
| [frontend/](./frontend) | 画面 (React 19 / TypeScript / Vite) |
| [infrastructure/](./infrastructure) | Azure の基盤 (Bicep: ACR、AKS) |

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
