import api from './api';
import { Product, PageResponse } from '../types';

export interface ProductSearchParams {
  page?: number;
  size?: number;
  sort?: string;
  direction?: 'asc' | 'desc';
  manufacturer?: string;
  keyword?: string;
}

// 商品一覧取得（ページネーション・フィルタ対応）
export const getProducts = async (params: ProductSearchParams = {}): Promise<PageResponse<Product>> => {
  const response = await api.get('/products', { params });
  return response.data;
};

// 商品詳細取得
export const getProductById = async (id: number): Promise<Product> => {
  const response = await api.get(`/products/${id}`);
  return response.data;
};

// 新着商品一覧取得
export const getNewProducts = async (page = 0, size = 20): Promise<PageResponse<Product>> => {
  const response = await api.get('/products/new', { params: { page, size } });
  return response.data;
};
