// 商品情報の型定義
export interface Product {
  id: number;
  productName: string;
  manufacturer: string;
  imageUrl?: string;
  releaseDate?: string;
  price?: number;
  description?: string;
  lineupInfo?: string;
  sourceUrl?: string;
  isNew: boolean;
  createdAt: string;
  updatedAt: string;
}

// ユーザー情報の型定義
export interface User {
  id: number;
  username: string;
  email: string;
  notificationEnabled: boolean;
  createdAt: string;
}

// ログインリクエストの型定義
export interface LoginRequest {
  username: string;
  password: string;
}

// ログインレスポンスの型定義
export interface LoginResponse {
  token: string;
  user: User;
}

// ユーザー登録リクエストの型定義
export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}
