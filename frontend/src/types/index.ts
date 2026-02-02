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
  role: 'ADMIN' | 'USER';
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

// ページネーションレスポンスの型定義
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
  hasNext: boolean;
  hasPrevious: boolean;
}
