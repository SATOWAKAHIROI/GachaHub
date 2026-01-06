# GachaHub

ガチャガチャ（カプセルトイ）の最新情報を自動収集し、Webアプリとして表示するシステム

## 概要

バンダイやタカラトミーなどのメーカー公式サイトから定期的にスクレイピングを行い、新商品情報をユーザーに提供します。

## 主な機能

- 🔍 **Webスクレイピング**: 指定されたサイトからガチャガチャの最新情報を自動取得
- 💾 **データ管理**: PostgreSQLで商品情報を保存・管理
- 🖥️ **Web表示**: React製のフロントエンドで商品情報を見やすく表示
- 📧 **通知機能**: 新着商品が見つかった際にメール通知
- 🔐 **ユーザー認証**: 個人利用のための認証機能
- ⏰ **スケジュール実行**: 設定可能な頻度での自動スクレイピング + 手動実行

## 技術スタック

### フロントエンド
- React 19.2.0 + TypeScript 5.9.3
- Vite 7.2.4 (ビルドツール、HMR対応)
- ESLint (コード品質管理)

### バックエンド
- Spring Boot 3.4.12 (Java 17)
- Spring Data JPA (データアクセス)
- Spring Security + JWT認証 (jjwt 0.12.3)
- Spring Validation (入力検証)
- Spring Actuator (監視)
- PostgreSQL (データベース)
- Selenium/Playwright (Webスクレイピング - 予定)
- Spring Mail (メール送信 - Gmail SMTP使用予定)

### インフラ
- Docker + Docker Compose
- Maven 3.9.11 (バックエンドビルド)
- npm (フロントエンドパッケージ管理)

## セットアップ

### 前提条件

- Docker
- Docker Compose

### インストール

1. リポジトリをクローン
```bash
git clone https://github.com/SATOWAKAHIROI/GachaHub.git
cd GachaHub
```

2. 環境変数を設定（オプション）
```bash
cp .env.example .env
# .envファイルを編集して必要な値を設定
```

3. Docker環境で起動
```bash
docker-compose up --build
```

4. アクセス
- バックエンド: http://localhost:8080
- フロントエンド: http://localhost:3000
- データベース: localhost:5432

## 開発コマンド

### 全体起動
```bash
docker-compose up --build
```

### 個別サービス起動
```bash
# バックエンドのみ
docker-compose up backend

# フロントエンドのみ
docker-compose up frontend

# データベースのみ
docker-compose up db
```

### 停止
```bash
docker-compose down
```

## プロジェクト構造

```
GachaHub/
├── docker-compose.yml          # Docker Compose設定
├── backend/                    # Spring Bootバックエンド
│   ├── Dockerfile
│   ├── pom.xml                 # Maven依存関係
│   └── src/
│       ├── main/
│       │   ├── java/com/example/capsuletoy/
│       │   │   ├── CapsuleToyHubApplication.java
│       │   │   ├── controller/
│       │   │   ├── model/                       # エンティティクラス
│       │   │   ├── repository/                  # JPA Repository
│       │   │   ├── service/                     # ビジネスロジック（予定）
│       │   │   └── config/                      # 設定クラス（予定）
│       │   └── resources/
│       │       └── application.properties
│       └── test/
└── frontend/                   # Reactフロントエンド
    ├── Dockerfile
    ├── package.json
    ├── vite.config.ts
    ├── tsconfig.json
    └── src/
        ├── main.tsx
        ├── App.tsx
        └── components/         # Reactコンポーネント（予定）
```

## データベーススキーマ

### Products（商品情報）
- 商品名、メーカー名、画像URL、発売日、価格、詳細説明
- ラインナップ情報、元サイトURL、新着フラグ

### ScrapeLog（スクレイピング履歴）
- 対象サイト、ステータス、取得商品数、エラーメッセージ、実行日時

### Users（ユーザー情報）
- ユーザー名、メールアドレス、パスワードハッシュ、通知有効化フラグ

### ScrapeConfig（スクレイピング設定）
- サイト名、サイトURL、cron式、有効/無効フラグ、最終実行日時

## 実装フェーズ

### Phase 1: 基盤構築 ✅
- [x] Docker環境整備
- [x] データベーススキーマ設計
- [x] Spring Boot基本設定
- [ ] React基本構成
- [ ] 認証機能実装

### Phase 2: コア機能実装（予定）
- [ ] スクレイピング機能
- [ ] データ保存機能
- [ ] REST API実装
- [ ] フロントエンド商品一覧画面

### Phase 3: 自動化・通知機能（予定）
- [ ] Spring Schedulerによる定期実行
- [ ] メール通知機能
- [ ] 新着判定ロジック

### Phase 4: 管理機能・UI改善（予定）
- [ ] 管理画面
- [ ] フィルタリング・検索機能
- [ ] レスポンシブデザイン

### Phase 5: 拡張・運用改善（予定）
- [ ] 追加サイト対応
- [ ] パフォーマンスチューニング
- [ ] エラーハンドリング強化

## ライセンス

MIT License

## 開発者

[@SATOWAKAHIROI](https://github.com/SATOWAKAHIROI)
