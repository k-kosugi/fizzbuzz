---
applyTo: "**/*.java"
name: "Domain Driven Design"
description: "Java におけるドメイン駆動設計の基本方針を示す。"
---
# 開発思想
実装はドメイン駆動設計を用います。

# ユビキタス言語
* ユビキタス言語が登録されている場合(ubiquitous language)は、コード内でもその用語を使用してください。

# ドメインの分割
* ドメインは、ビジネス上の関心ごとに分割してください。
* 分割は Aggregate ごとを最小単位とします。Aggregate ごとで小さすぎる場合は複数の Aggregate をまとめて扱います。
* ドメイン間のデータのやり取りは以下のどちらかを使用します。
  * API
  * Domain Event (ドメインイベント)

## ドメイン駆動設計の戦術的 DDD
以下の要素を使って開発します

|用語|説明|例|
|:--|:--|:--|
|Domain Model|ドメインモデル|エンティティや値オブジェクトを含む|
|Entity|エンティティを表す。キーが同じであれば一意であると見なせるもの。Aggregate にもなりえる。|ISBN で一意性が決まる Book クラス|
|Value Object|値オブジェクト|タイトル(Title)、ISBN|
|Aggregate|集約|BookAggregate|
|Repository|リポジトリ|BookRepository|
|Domain Service|ドメインサービス|BookDomainService|
|Application Service|アプリケーションサービス|RegisterBookService|
|Infrastructure|インフラストラクチャー|JpaBookRepository|

* Domain Model には Spring Boot 等フレームワーク由来のアノテーションは付与しないでください。
* Domain Model は infrastructure 層、application 層に依存しないようにします。

## Web API
* HTTP API を新規作成・変更する際は、OpenAPI 形式の API 仕様を作成・更新してください。
* API 仕様には、パス、HTTP メソッド、リクエスト、レスポンス、ステータスコード、エラー形式を記述してください。
* API 仕様と実装の整合性を維持してください。
* OpenAPI 用のアノテーションを使用する場合は、Web Adapter の Controller や入出力 DTO に付与し、ドメインモデルには付与しないでください。
* OpenAPI 仕様の生成・公開には、プロジェクトで使用しているフレームワークとそのバージョンに対応したライブラリや標準機能を使用し、既存の導入・設定がある場合はそれを優先してください。