# API仕様書

## 概要

| 項目 | 内容 |
|---|---|
| ベースURL | `http://localhost:8080` |
| データ形式 | JSON |
| 認証方式 | JWT Bearer Token |
| 文字コード | UTF-8 |

## 認証

認証が必要なエンドポイントは、リクエストヘッダーに以下を付与してください。

```
Authorization: Bearer <token>
```

トークンはログインAPIのレスポンスから取得します。

---

## エンドポイント一覧

| メソッド | パス | 説明 | 認証 |
|---|---|---|---|
| POST | /api/auth/admin/login | 管理者ログイン | 不要 |
| GET | /api/products | 商品一覧取得 | 不要 |
| GET | /api/products/{id} | 商品詳細取得 | 不要 |
| GET | /api/products/new | 新着商品一覧取得 | 不要 |
| GET | /api/profile | 自分のプロフィール取得 | 必須 |
| PUT | /api/profile | 自分のプロフィール更新 | 必須 |
| GET | /api/admin/users | ユーザー一覧取得 | ADMIN |
| GET | /api/admin/users/{id} | ユーザー詳細取得 | ADMIN |
| POST | /api/admin/users | ユーザー作成 | ADMIN |
| PUT | /api/admin/users/{id} | ユーザー更新 | ADMIN |
| DELETE | /api/admin/users/{id} | ユーザー削除 | ADMIN |
| POST | /api/scrape/bandai | バンダイ手動スクレイピング実行 | ADMIN |
| POST | /api/scrape/takaratomy | タカラトミー手動スクレイピング実行 | ADMIN |
| GET | /api/scrape/status | スクレイピング状態確認 | ADMIN |
| GET | /api/scrape/logs | スクレイピングログ一覧取得 | ADMIN |
| GET | /api/scrape/logs/{site} | サイト別スクレイピングログ取得 | ADMIN |
| GET | /api/scrape/configs | スクレイピング設定一覧取得 | ADMIN |
| GET | /api/scrape/configs/{id} | スクレイピング設定詳細取得 | ADMIN |
| POST | /api/scrape/configs | スクレイピング設定作成 | ADMIN |
| PUT | /api/scrape/configs/{id} | スクレイピング設定更新 | ADMIN |
| PATCH | /api/scrape/configs/{id}/toggle | スクレイピング設定有効/無効切り替え | ADMIN |
| DELETE | /api/scrape/configs/{id} | スクレイピング設定削除 | ADMIN |
| POST | /api/notifications/test | テストメール送信 | ADMIN |
| PATCH | /api/notifications/users/{userId}/toggle | ユーザー通知設定切り替え | ADMIN |

---

## 認証

### 管理者ログイン

```
POST /api/auth/admin/login
```

**リクエストボディ**

```json
{
  "email": "admin@example.com",
  "password": "password123"
}
```

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | ログイン成功 |
| 401 | メールアドレスまたはパスワードが違う |
| 403 | 管理者権限がない |
| 500 | サーバーエラー |

```json
// 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "管理者",
    "email": "admin@example.com",
    "role": "ADMIN",
    "notificationEnabled": true,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

```json
// 401 Unauthorized
{
  "error": "メールアドレスまたはパスワードが違います"
}
```

---

## 商品

### 商品一覧取得

```
GET /api/products
```

**クエリパラメータ**

| パラメータ | 型 | 必須 | デフォルト | 説明 |
|---|---|---|---|---|
| page | int | × | 0 | ページ番号（0始まり） |
| size | int | × | 20 | 1ページの件数 |
| sort | string | × | createdAt | ソート項目 |
| direction | string | × | desc | ソート方向（asc / desc） |
| manufacturer | string | × | - | メーカー名でフィルタ（例: BANDAI） |
| keyword | string | × | - | キーワードで検索 |

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 取得成功 |

```json
// 200 OK
{
  "content": [
    {
      "id": 1,
      "productName": "ワンピース ガシャポン Vol.1",
      "manufacturer": "BANDAI",
      "imageUrl": "https://example.com/image.jpg",
      "releaseDate": "2024-04-01",
      "price": 300,
      "description": "商品説明テキスト",
      "sourceUrl": "https://gashapon.jp/...",
      "isNew": true,
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "currentPage": 0,
  "size": 20,
  "hasNext": true,
  "hasPrevious": false
}
```

---

### 商品詳細取得

```
GET /api/products/{id}
```

**パスパラメータ**

| パラメータ | 型 | 説明 |
|---|---|---|
| id | Long | 商品ID |

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 取得成功 |
| 404 | 商品が見つからない |

```json
// 200 OK
{
  "id": 1,
  "productName": "ワンピース ガシャポン Vol.1",
  "manufacturer": "BANDAI",
  "imageUrl": "https://example.com/image.jpg",
  "releaseDate": "2024-04-01",
  "price": 300,
  "description": "商品説明テキスト",
  "sourceUrl": "https://gashapon.jp/...",
  "isNew": true,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

---

### 新着商品一覧取得

```
GET /api/products/new
```

**クエリパラメータ**

| パラメータ | 型 | 必須 | デフォルト | 説明 |
|---|---|---|---|---|
| page | int | × | 0 | ページ番号（0始まり） |
| size | int | × | 20 | 1ページの件数 |

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 取得成功 |

レスポンス形式は[商品一覧取得](#商品一覧取得)と同じ。`isNew: true` の商品のみ返却。

---

## プロフィール

### 自分のプロフィール取得

```
GET /api/profile
```

**認証:** 必須（Bearer Token）

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 取得成功 |
| 400 | 認証情報が無効 |

```json
// 200 OK
{
  "id": 1,
  "username": "田中太郎",
  "email": "tanaka@example.com",
  "role": "USER",
  "notificationEnabled": true,
  "createdAt": "2024-01-01T00:00:00"
}
```

---

### 自分のプロフィール更新

```
PUT /api/profile
```

**認証:** 必須（Bearer Token）

**リクエストボディ**

```json
{
  "username": "田中次郎",
  "email": "tanaka2@example.com",
  "password": "newpassword123",
  "notificationEnabled": false
}
```

※ 変更しない項目は省略可能（null可）

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 更新成功 |
| 400 | バリデーションエラーまたは認証情報が無効 |

```json
// 200 OK
{
  "id": 1,
  "username": "田中次郎",
  "email": "tanaka2@example.com",
  "role": "USER",
  "notificationEnabled": false,
  "createdAt": "2024-01-01T00:00:00"
}
```

---

## ユーザー管理（管理者専用）

### ユーザー一覧取得

```
GET /api/admin/users
```

**認証:** ADMIN権限必須

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 取得成功 |

```json
// 200 OK
[
  {
    "id": 1,
    "username": "田中太郎",
    "email": "tanaka@example.com",
    "role": "USER",
    "notificationEnabled": true,
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

---

### ユーザー詳細取得

```
GET /api/admin/users/{id}
```

**認証:** ADMIN権限必須

**パスパラメータ**

| パラメータ | 型 | 説明 |
|---|---|---|
| id | Long | ユーザーID |

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 取得成功 |
| 404 | ユーザーが見つからない |

---

### ユーザー作成

```
POST /api/admin/users
```

**認証:** ADMIN権限必須

**リクエストボディ**

```json
{
  "username": "田中太郎",
  "email": "tanaka@example.com",
  "password": "password123",
  "role": "USER"
}
```

| フィールド | 型 | 必須 | 説明 |
|---|---|---|---|
| username | string | ○ | ユーザー名 |
| email | string | ○ | メールアドレス |
| password | string | ○ | パスワード（8文字以上） |
| role | string | ○ | 権限（USER / ADMIN） |

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 201 | 作成成功 |
| 400 | バリデーションエラー |

```json
// 201 Created
{
  "id": 2,
  "username": "田中太郎",
  "email": "tanaka@example.com",
  "role": "USER",
  "notificationEnabled": false,
  "createdAt": "2024-01-01T00:00:00"
}
```

---

### ユーザー更新

```
PUT /api/admin/users/{id}
```

**認証:** ADMIN権限必須

**パスパラメータ**

| パラメータ | 型 | 説明 |
|---|---|---|
| id | Long | ユーザーID |

**リクエストボディ**

```json
{
  "username": "田中次郎",
  "email": "tanaka2@example.com",
  "password": "newpassword123",
  "notificationEnabled": true
}
```

※ 変更しない項目は省略可能（null可）

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 更新成功 |
| 400 | バリデーションエラー |

---

### ユーザー削除

```
DELETE /api/admin/users/{id}
```

**認証:** ADMIN権限必須

**パスパラメータ**

| パラメータ | 型 | 説明 |
|---|---|---|
| id | Long | ユーザーID |

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 削除成功 |
| 404 | ユーザーが見つからない |

```json
// 200 OK
{
  "message": "ユーザーを削除しました"
}
```

---

## スクレイピング（管理者専用）

### バンダイ手動スクレイピング実行

```
POST /api/scrape/bandai
```

**認証:** ADMIN権限必須

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 実行成功 |
| 500 | スクレイピングエラー |

```json
// 200 OK
{
  "status": "success",
  "site": "BANDAI",
  "totalProducts": 50,
  "newProducts": 3,
  "message": "スクレイピングが完了しました"
}
```

```json
// 500 Internal Server Error
{
  "status": "error",
  "site": "BANDAI",
  "message": "スクレイピング中にエラーが発生しました"
}
```

---

### タカラトミー手動スクレイピング実行

```
POST /api/scrape/takaratomy
```

**認証:** ADMIN権限必須

レスポンス形式は[バンダイ手動スクレイピング実行](#バンダイ手動スクレイピング実行)と同じ。`site: "TAKARATOMY"`。

---

### スクレイピング状態確認

```
GET /api/scrape/status
```

**認証:** ADMIN権限必須

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 取得成功 |

```json
// 200 OK
{
  "available": true,
  "supportedSites": ["BANDAI", "TAKARATOMY"],
  "lastExecution": "2024-01-01T10:00:00",
  "lastStatus": "SUCCESS"
}
```

---

### スクレイピングログ一覧取得

```
GET /api/scrape/logs
```

**認証:** ADMIN権限必須

**クエリパラメータ**

| パラメータ | 型 | 必須 | デフォルト | 説明 |
|---|---|---|---|---|
| limit | int | × | 10 | 取得件数 |

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 取得成功 |
| 500 | サーバーエラー |

```json
// 200 OK
[
  {
    "id": 1,
    "targetSite": "BANDAI",
    "status": "SUCCESS",
    "productsFound": 50,
    "errorMessage": null,
    "executedAt": "2024-01-01T10:00:00"
  }
]
```

---

### サイト別スクレイピングログ取得

```
GET /api/scrape/logs/{site}
```

**認証:** ADMIN権限必須

**パスパラメータ**

| パラメータ | 型 | 説明 |
|---|---|---|
| site | string | サイト名（例: BANDAI, TAKARATOMY） |

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 取得成功 |
| 500 | サーバーエラー |

レスポンス形式は[スクレイピングログ一覧取得](#スクレイピングログ一覧取得)と同じ。

---

## スクレイピング設定（管理者専用）

### スクレイピング設定一覧取得

```
GET /api/scrape/configs
```

**認証:** ADMIN権限必須

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 取得成功 |

```json
// 200 OK
[
  {
    "id": 1,
    "siteName": "BANDAI",
    "siteUrl": "https://gashapon.jp/...",
    "cronExpression": "0 0 9 * * *",
    "isEnabled": true,
    "lastScrapedAt": "2024-01-01T09:00:00"
  }
]
```

---

### スクレイピング設定詳細取得

```
GET /api/scrape/configs/{id}
```

**認証:** ADMIN権限必須

**パスパラメータ**

| パラメータ | 型 | 説明 |
|---|---|---|
| id | Long | 設定ID |

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 取得成功 |
| 404 | 設定が見つからない |

---

### スクレイピング設定作成

```
POST /api/scrape/configs
```

**認証:** ADMIN権限必須

**リクエストボディ**

```json
{
  "siteName": "BANDAI",
  "siteUrl": "https://gashapon.jp/...",
  "cronExpression": "0 0 9 * * *",
  "isEnabled": true
}
```

| フィールド | 型 | 必須 | 説明 |
|---|---|---|---|
| siteName | string | ○ | サイト名 |
| siteUrl | string | ○ | スクレイピング対象URL |
| cronExpression | string | ○ | 実行頻度（cron形式） |
| isEnabled | boolean | ○ | 有効/無効 |

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 201 | 作成成功 |
| 400 | バリデーションエラー |
| 409 | 同名の設定が既に存在する |

---

### スクレイピング設定更新

```
PUT /api/scrape/configs/{id}
```

**認証:** ADMIN権限必須

**パスパラメータ**

| パラメータ | 型 | 説明 |
|---|---|---|
| id | Long | 設定ID |

**リクエストボディ**

リクエスト形式は[スクレイピング設定作成](#スクレイピング設定作成)と同じ。

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 更新成功 |
| 400 | バリデーションエラー |
| 404 | 設定が見つからない |

---

### スクレイピング設定 有効/無効切り替え

```
PATCH /api/scrape/configs/{id}/toggle
```

**認証:** ADMIN権限必須

**パスパラメータ**

| パラメータ | 型 | 説明 |
|---|---|---|
| id | Long | 設定ID |

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 切り替え成功 |
| 404 | 設定が見つからない |

```json
// 200 OK（isEnabledが反転した状態で返る）
{
  "id": 1,
  "siteName": "BANDAI",
  "siteUrl": "https://gashapon.jp/...",
  "cronExpression": "0 0 9 * * *",
  "isEnabled": false,
  "lastScrapedAt": "2024-01-01T09:00:00"
}
```

---

### スクレイピング設定削除

```
DELETE /api/scrape/configs/{id}
```

**認証:** ADMIN権限必須

**パスパラメータ**

| パラメータ | 型 | 説明 |
|---|---|---|
| id | Long | 設定ID |

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 削除成功 |
| 404 | 設定が見つからない |

```json
// 200 OK
{
  "status": "success",
  "message": "設定を削除しました"
}
```

---

## 通知（管理者専用）

### テストメール送信

```
POST /api/notifications/test
```

**認証:** ADMIN権限必須

**リクエストボディ**

```json
{
  "email": "test@example.com"
}
```

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 送信成功 |
| 400 | メールアドレスが未指定 |
| 500 | 送信エラー |

```json
// 200 OK
{
  "status": "success",
  "message": "テストメールを送信しました"
}
```

```json
// 500 Internal Server Error
{
  "status": "error",
  "message": "メール送信に失敗しました"
}
```

---

### ユーザー通知設定切り替え

```
PATCH /api/notifications/users/{userId}/toggle
```

**認証:** ADMIN権限必須

**パスパラメータ**

| パラメータ | 型 | 説明 |
|---|---|---|
| userId | Long | ユーザーID |

**レスポンス**

| ステータスコード | 説明 |
|---|---|
| 200 | 切り替え成功 |
| 404 | ユーザーが見つからない |

```json
// 200 OK
{
  "status": "success",
  "userId": 1,
  "notificationEnabled": false,
  "message": "通知設定を更新しました"
}
```

---

## 共通エラーレスポンス

| ステータスコード | 説明 |
|---|---|
| 400 | リクエストが不正（バリデーションエラー等） |
| 401 | 認証が必要（トークンなし・期限切れ） |
| 403 | 権限不足（ADMIN権限が必要） |
| 404 | リソースが見つからない |
| 500 | サーバー内部エラー |

```json
// エラーレスポンス共通形式
{
  "status": "error",
  "message": "エラーの詳細メッセージ"
}
```
