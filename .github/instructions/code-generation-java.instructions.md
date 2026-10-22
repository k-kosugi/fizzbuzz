---
name: "Java コード生成"
description: "Java コードの新規生成や機能実装を依頼された場合に使用する。コードの説明やレビューのみの場合にも使用する。"
applyTo: "**/*.java"
---

# コード生成
## パッケージ名
* コードを生成するにあたりパッケージ名は必ず確認する。

## アーキテクチャー
* 実装は [ドメイン駆動設計](./domain-driven-design.instructions.md) を用います。Entity、Value Object、Aggregate、Repository、Domain Service などの概念を使用してください。
* アーキテクチャーは [Clean Architecture](./clean-architecture.instructions.md) に従って実装します。詳細は以下の手順に従ってください。
* 自動実装を依頼された場合、以下の手順に従って実装内容を提案します。
  1. ドメインモデルとなるクラスの提示 ※ 本手順は Entity や Value Object の数だけ繰り返します.
    * Entity と Value Object を提示します。
    * Entity、Value Object ごとに Design by Contract を提示します。
  2. ドメインサービスの内容を提示します。
  3. ユースケースの内容を提示します。

## コメント
* クラスやメソッドにはコメントを必ず日本語で付与してください。

## 修飾子と型
* Entity は class として定義してください。
* Value Object は record として定義してください。
* Class に関して再利用しない場合は final 修飾子を付与してください。
* 引数の型に関しても再利用しない場合は final 修飾子を付与してください。

## 定数の扱い方
* メソッド内部で使用する定数は、値が変わらない場合に static final フィールドとして宣言してください。

## 定数の命名規則
* 定数の名前はすべて大文字で、単語の区切りはアンダースコア(_)を使用してください。
  * 例) `MAX_USER_COUNT`
  * 複数の単語を組み合わせる場合もアンダースコアで区切ります。
    * 例) `DEFAULT_TIMEOUT_SECONDS`
    * 例) `MAX_RETRY_COUNT`

## メソッドの命名規則
* メソッドの名前は動詞で始め、キャメルケースで記述してください。
  * 例) `calculateTotalPrice()`
  * 例) `findUserById()`
  * 例) `updateUserProfile()`
  * 例) `deleteUserAccount()`

