import api from './api';

// スクレイピングステータス取得
export const getScrapeStatus = async () => {
  const response = await api.get('/scrape/status');
  return response.data;
};

// スクレイピングログ取得
export const getScrapeLogs = async (limit = 10) => {
  const response = await api.get('/scrape/logs', { params: { limit } });
  return response.data;
};

// スクレイピング設定一覧取得
export const getScrapeConfigs = async () => {
  const response = await api.get('/scrape/configs');
  return response.data;
};

// バンダイ手動スクレイピング実行
export const scrapeBandai = async () => {
  const response = await api.post('/scrape/bandai');
  return response.data;
};

// タカラトミー手動スクレイピング実行
export const scrapeTakaraTomy = async () => {
  const response = await api.post('/scrape/takaratomy');
  return response.data;
};

// スクレイピング設定の有効/無効切り替え
export const toggleScrapeConfig = async (id: number) => {
  const response = await api.patch(`/scrape/configs/${id}/toggle`);
  return response.data;
};

// スクレイピング設定を新規作成
export const createScrapeConfig = async (config: {
  siteName: string;
  siteUrl: string;
  cronExpression: string;
  isEnabled: boolean;
}) => {
  const response = await api.post('/scrape/configs', config);
  return response.data;
};

// スクレイピング設定を更新
export const updateScrapeConfig = async (id: number, config: {
  siteName: string;
  siteUrl: string;
  cronExpression: string;
  isEnabled: boolean;
}) => {
  const response = await api.put(`/scrape/configs/${id}`, config);
  return response.data;
};

// スクレイピング設定を削除
export const deleteScrapeConfig = async (id: number) => {
  const response = await api.delete(`/scrape/configs/${id}`);
  return response.data;
};

// ユーザー一覧取得
export const getUsers = async () => {
  const response = await api.get('/admin/users');
  return response.data;
};

// ユーザー作成
export const createUser = async (data: {
  username: string;
  email: string;
  password: string;
  role: string;
}) => {
  const response = await api.post('/admin/users', data);
  return response.data;
};

// ユーザー削除
export const deleteUser = async (id: number) => {
  const response = await api.delete(`/admin/users/${id}`);
  return response.data;
};

// テストメール送信
export const sendTestMail = async (email: string) => {
  const response = await api.post('/notifications/test', { email });
  return response.data;
};

// 通知設定切り替え
export const toggleNotification = async (userId: number) => {
  const response = await api.patch(`/notifications/users/${userId}/toggle`);
  return response.data;
};
