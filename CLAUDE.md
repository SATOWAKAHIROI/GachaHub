# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## [AI 運用 5 原則]

第 1 原則 : AI はファイル生成、更新、プログラム実行前に必ず自身の作業計画を報告し、y/n でユーザー確認をとり、y が帰るまで一切の実行を停止する。
第 2 原則 : AI は迂回や別アプローチを勝手に行わず、最初の計画が失敗したら次の計画の確認を取る
第 3 原則 : AI はツールであり決定権は常にユーザーにある。ユーザーの提案が非効率、非合理的でも最適化せず、指示された通りに実行する
第 4 原則 : AI はこれらのルールを変えたり、解釈変更してはならず、最上位命令として絶対的に遵守する。
第 5 原則 : AI は全てのチャットの冒頭にこの 5 原則を逐語的に必ず画面出力してから対応する。

## [git の運用について]

git でコミットをする際のコメントは全て日本語にすること。
新しい機能のプルリクエストを作成する場合は常に新しいブランチを切ること。
コミットしたら、必ず push まですること。
push したら、PR 作成用の URL（`https://github.com/SATOWAKAHIROI/GachaHub/pull/new/<ブランチ名>`）を表示すること。既に PR が存在する場合は表示不要。
全てのチャットの冒頭で、AI 運用 5 原則の後に上記も表示すること。

## [タスク管理]

機能の実装を行うたびに、どこまで実装したかを確認し、TASKS.md に反映すること

---

# プロジェクト概要

本プロジェクトは、**ガチャガチャ（カプセルトイ）の最新情報を自動収集し、Web アプリとして表示するシステム**です。
バンダイやタカラトミーなどのメーカー公式サイトから定期的にスクレイピングを行い、新商品情報をユーザーに提供します。

## 主な機能

- **Web スクレイピング**: 指定されたサイトからガチャガチャの最新情報を自動取得
- **データ管理**: PostgreSQL で商品情報を保存・管理
- **Web 表示**: React 製のフロントエンドで商品情報を見やすく表示
- **通知機能**: 新着商品が見つかった際にメール通知
- **ユーザー認証**: 個人利用のための認証機能
- **スケジュール実行**: 設定可能な頻度での自動スクレイピング + 手動実行

---

# 技術スタック

## フロントエンド

- **React 19.2.0** + **TypeScript 5.9.3**
- **Vite 7.2.4** (ビルドツール、HMR 対応)
- **ESLint** (コード品質管理)

## バックエンド

- **Spring Boot 3.4.12** (Java 17)
- **Spring Data JPA** (データアクセス)
- **Spring Security** + **JWT 認証** (jjwt 0.12.3)
- **Spring Validation** (入力検証)
- **Spring Actuator** (監視)
- **PostgreSQL** (データベース)
- **Selenium/Playwright** (Web スクレイピング - 予定)
- **Spring Mail** (メール送信 - Gmail SMTP 使用予定)

## インフラ

- **Docker** + **Docker Compose**
- **Maven 3.9.11** (バックエンドビルド)
- **npm** (フロントエンドパッケージ管理)

---

# 開発コマンド

## Docker 環境での起動

### 全体起動（推奨）

```bash
docker-compose up --build
```

- バックエンド: http://localhost:8080
- フロントエンド: http://localhost:3000

### 個別サービス起動

```bash
# バックエンドのみ
docker-compose up backend

# フロントエンドのみ
docker-compose up frontend
```

### 停止

```bash
docker-compose down
```

## ローカル開発（Docker なし）

### バックエンド

```bash
cd backend

# 依存関係のダウンロード
mvn dependency:resolve

# ビルド
mvn clean package

# テスト実行
mvn test

# アプリケーション起動
mvn spring-boot:run

# 特定のテストのみ実行
mvn test -Dtest=クラス名#メソッド名
```

### フロントエンド

```bash
cd frontend

# 依存関係インストール
npm install

# 開発サーバー起動
npm run dev

# ビルド
npm run build

# Lint実行
npm run lint

# プレビュー（ビルド後の確認）
npm run preview
```

---

# プロジェクト構造

```
CapsuleToyHub/
├── docker-compose.yml          # Docker Compose設定
├── backend/                    # Spring Bootバックエンド
│   ├── Dockerfile
│   ├── pom.xml                 # Maven依存関係
│   └── src/
│       ├── main/
│       │   ├── java/com/example/capsuletoy/
│       │   │   ├── CapsuleToyHubApplication.java  # メインクラス
│       │   │   ├── controller/                  # RESTコントローラー
│       │   │   ├── model/                       # エンティティクラス
│       │   │   ├── repository/                  # JPA Repository（予定）
│       │   │   ├── service/                     # ビジネスロジック（予定）
│       │   │   ├── config/                      # 設定クラス（予定）
│       │   │   └── scheduler/                   # スケジュール処理（予定）
│       │   └── resources/
│       │       └── application.properties       # Spring設定
│       └── test/                                # テストコード
└── frontend/                   # Reactフロントエンド
    ├── Dockerfile
    ├── package.json            # npm依存関係
    ├── vite.config.ts          # Vite設定
    ├── tsconfig.json           # TypeScript設定
    ├── eslint.config.js        # ESLint設定
    ├── index.html              # エントリーポイント
    └── src/
        ├── main.tsx            # Reactエントリーポイント
        ├── App.tsx             # ルートコンポーネント
        ├── components/         # Reactコンポーネント（予定）
        ├── services/           # API通信（予定）
        ├── types/              # TypeScript型定義（予定）
        └── assets/             # 静的リソース
```

## アーキテクチャ

### バックエンド構造（Spring Boot）

- **Controller 層**: REST API エンドポイント（`@RestController`）
- **Service 層**: ビジネスロジック、スクレイピング処理
- **Repository 層**: データアクセス（Spring Data JPA）
- **Model 層**: エンティティクラス（JPA `@Entity`）
- **Scheduler**: 定期実行タスク（Spring `@Scheduled`）
- **Config**: セキュリティ、CORS、メール設定等

### フロントエンド構造（React + TypeScript）

- **コンポーネント**: 再利用可能な UI パーツ
- **サービス**: API 通信ロジック（axios 等）
- **型定義**: TypeScript インターフェース
- **状態管理**: React Hooks（useState、useEffect 等）

### データフロー

```
[スケジューラー] → [スクレイピングサービス] → [パーサー] → [DB保存]
                                                           ↓
[React UI] ← [REST API] ← [Service層] ← [Repository] ← [PostgreSQL]
                ↓
           [メール通知サービス]
```

---

# 機能要件

## 1. スクレイピング機能

### 対象サイト

- **バンダイ公式** ガシャポン情報ページ
- **タカラトミーアーツ公式** ガチャ情報ページ
- **その他メーカー**: 運用しながら要望に応じて適宜追加

### スクレイピング方式

- **Selenium または Playwright** を使用（JavaScript 動的レンダリング対応）
- サイトごとに異なるスクレイピングロジックを実装
- 各サイトの構造変更に対応できる柔軟な設計

### 実行タイミング

- **自動実行**: Spring Scheduler で定期実行（頻度は設定可能）
- **手動実行**: 管理画面または API エンドポイントからのトリガー
- 実行頻度は管理画面から変更可能（1 日 1 回、1 週間 1 回、数時間ごと等）

### 取得データ

- 商品名
- 商品画像 URL
- 発売日・発売予定日
- 価格（1 回あたりの料金）
- 詳細説明・商品コピー
- ラインナップ情報（全何種類、各種類の詳細）
- 元サイトへのリンク URL

## 2. データ管理機能

### データベース

- **PostgreSQL** を使用
- 商品情報、スクレイピング履歴、ユーザー情報を管理

### データモデル（設計案）

#### Product テーブル（商品情報）

```
- id (BIGINT, PK)
- product_name (VARCHAR, 商品名)
- manufacturer (VARCHAR, メーカー名: "BANDAI", "TAKARA_TOMY"等)
- image_url (TEXT, 画像URL)
- release_date (DATE, 発売日)
- price (INTEGER, 価格)
- description (TEXT, 詳細説明)
- lineup_info (TEXT/JSON, ラインナップ情報)
- source_url (TEXT, 元サイトURL)
- is_new (BOOLEAN, 新着フラグ)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

#### ScrapeLog テーブル（スクレイピング履歴）

```
- id (BIGINT, PK)
- target_site (VARCHAR, 対象サイト)
- status (VARCHAR, "SUCCESS", "FAILURE")
- products_found (INTEGER, 取得商品数)
- error_message (TEXT, エラー内容)
- executed_at (TIMESTAMP, 実行日時)
```

#### User テーブル（ユーザー情報）

```
- id (BIGINT, PK)
- username (VARCHAR, UNIQUE)
- email (VARCHAR, UNIQUE)
- password_hash (VARCHAR, ハッシュ化パスワード)
- notification_enabled (BOOLEAN, 通知有効化)
- created_at (TIMESTAMP)
```

#### ScrapeConfig テーブル（スクレイピング設定）

```
- id (BIGINT, PK)
- site_name (VARCHAR, サイト名)
- site_url (TEXT, URL)
- cron_expression (VARCHAR, 実行頻度: cron形式)
- is_enabled (BOOLEAN, 有効/無効)
- last_scraped_at (TIMESTAMP)
```

## 3. Web 表示機能

### 商品一覧画面

- グリッド形式で商品カードを表示
- 商品画像、商品名、価格、発売日を表示
- 新着バッジ表示（is_new=true の商品）
- フィルタリング機能（メーカー別、発売日順、価格順）
- ページネーションまたは無限スクロール

### 商品詳細画面

- 商品の全情報を表示
- ラインナップ詳細
- 元サイトへのリンクボタン

### 管理画面（認証必須）

- スクレイピング手動実行ボタン
- スクレイピング履歴表示
- 実行頻度設定
- サイト追加/編集機能

## 4. 通知機能

### メール通知

- **Gmail SMTP** を使用
- 新着商品が見つかった際に登録ユーザーへメール送信
- 通知内容: 商品名、画像、リンク、発見日時
- 通知の ON/OFF 設定が可能

### 通知タイミング

- スクレイピング実行後、新規商品（`is_new=true`）が存在する場合

## 5. 認証機能

### ユーザー認証

- **Spring Security + JWT** による認証
- ログイン/ログアウト機能
- ユーザー登録機能（メールアドレス、パスワード）
- パスワードは BCrypt でハッシュ化

### アクセス制御

- 管理画面は認証必須
- 商品閲覧は認証不要（公開情報として表示可能）

---

# 技術要件

## セキュリティ

- CORS 設定: フロントエンド（localhost:3000）からのアクセスを許可
- JWT 有効期限: 24 時間（設定可能）
- パスワードポリシー: 8 文字以上（実装時に強化可能）

## パフォーマンス

- スクレイピング時はサイトへの負荷を考慮（適切な間隔を設ける）
- 画像は URL のみ保存（実際の画像ファイルは保存しない）
- データベースインデックス: `manufacturer`, `release_date`, `is_new`

## エラーハンドリング

- スクレイピング失敗時はログに記録し、次回実行を継続
- サイト構造変更検知時は管理者へアラート（メール）
- API 通信エラー時は適切な HTTP ステータスコードを返す

## ログ

- スクレイピング実行ログ（成功/失敗、取得件数）
- エラーログ（スタックトレース含む）
- ユーザー認証ログ

---

# 実装フェーズ計画

## Phase 1: 基盤構築

1. データベーススキーマ設計・作成
2. Spring Boot 基本設定（JPA、PostgreSQL 接続）
3. React 基本構成（ルーティング、API 通信設定）
4. 認証機能実装（JWT、ログイン/ログアウト）
5. Docker Compose 環境の整備（PostgreSQL 追加）

## Phase 2: コア機能実装

1. スクレイピング機能の実装
   - Selenium/Playwright 環境構築
   - バンダイサイトのスクレイピングロジック
   - タカラトミーサイトのスクレイピングロジック
   - パーサー実装
2. データ保存機能（Repository、Service 実装）
3. REST API 実装（商品取得、一覧表示）
4. フロントエンド商品一覧画面

## Phase 3: 自動化・通知機能

1. Spring Scheduler による定期実行
2. スクレイピング設定機能（cron 設定）
3. メール通知機能（Gmail SMTP 設定）
4. 新着判定ロジック

## Phase 4: 管理機能・UI 改善

1. 管理画面の実装
2. 手動スクレイピング実行機能
3. スクレイピング履歴表示
4. サイト追加/編集機能
5. フィルタリング・検索機能
6. レスポンシブデザイン対応

## Phase 5: 拡張・運用改善

1. 追加サイト対応（運用しながら）
2. パフォーマンスチューニング
3. エラーハンドリング強化
4. ログ監視・アラート機能
5. ユーザーフィードバック対応

---

# 開発時の注意事項

## API 通信

- バックエンドのベース URL: `http://localhost:8080/api`
- CORS 設定が必要: `@CrossOrigin(origins = "http://localhost:3000")`

## 環境変数

以下の環境変数を設定すること（`.env`ファイルまたは Docker 環境変数）:

- `DATABASE_URL`: PostgreSQL 接続 URL
- `JWT_SECRET`: JWT 署名用シークレットキー
- `MAIL_USERNAME`: Gmail SMTP ユーザー名
- `MAIL_PASSWORD`: Gmail SMTP パスワード（アプリパスワード）

## スクレイピング倫理

- robots.txt を尊重する
- 適切な User-Agent を設定
- リクエスト間隔を適切に設ける（数秒以上）
- サイト利用規約を遵守

## テスト

- スクレイピングロジックは単体テスト必須
- モックを使用してサイトへの実アクセスを避ける
- REST API のエンドポイントテスト

---

# 参考リンク

- Spring Boot 公式: https://spring.io/projects/spring-boot
- React 公式: https://react.dev/
- Vite 公式: https://vitejs.dev/
- Selenium 公式: https://www.selenium.dev/
- Playwright 公式: https://playwright.dev/
