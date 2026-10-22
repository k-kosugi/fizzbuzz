---
name: "Clean Architecture"
applyTo: "**/*.java"
description: "クリーンアーキテクチャーに基づいた Java プロジェクトのパッケージ構成例を示します。"
---

# アーキテクチャー
* アーキテクチャーに関してはクリーンアーキテクチャーを採用している前提で回答を作ってください。
* 前提として、[ドメイン駆動設計](./domain-driven-design.instructions.md) を元に拡張した考え方です。

* ドメイン駆動設計にはない以下の概念が必要です。

* エンタープライズアプリケーションにおける関心の分離を明確にするための層構造
  * プレゼンテーション層（Adapter in）
  * アプリケーション層（Application）
  * インフラストラクチャ層（Adapter out）

# パッケージ体系

以下のパッケージ体系を推奨します。

```text
com.microsoft.japan.cinemacity.movie
├── catalog                         # 機能・境界づけられたコンテキスト
│   ├── domain                     # Domain Model
│   │   ├── Movie.java             # Entity / Aggregate Root
│   │   ├── Title.java             # Value Object
│   │   ├── MovieId.java            # Value Object
│   │   ├── MovieRepository.java    # Repository interface
│   │   └── MovieDomainService.java
│   │
│   ├── application                # Application Business Rules
│   │   ├── register
│   │   │   ├── RegisterMovieUseCase.java
│   │   │   ├── RegisterMovieService.java
│   │   │   ├── RegisterMovieCommand.java
│   │   │   └── RegisterMovieResult.java
│   │   └── find
│   │       └── ...
│   │
│   ├── adapter
│   │   ├── in                     # 外部からアプリへの入口
│   │   │   └── web
│   │   │       ├── MovieController.java
│   │   │       ├── RegisterMovieRequest.java
│   │   │       └── MovieResponse.java
│   │   └── out                    # アプリから外部への出口
│   │       └── persistence
│   │           ├── JpaMovieEntity.java
│   │           ├── JpaMovieRepository.java
│   │           └── MovieRepositoryAdapter.java
│   │
│   └── configuration
│       └── CatalogConfiguration.java
│
├── ticket
│   └── ...
│
└── MovieApplication.java
```
