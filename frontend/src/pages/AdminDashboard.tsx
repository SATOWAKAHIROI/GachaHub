import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getScrapeStatus, getScrapeLogs, getScrapeConfigs } from '../services/adminApi';

interface ScrapeStatus {
  available: boolean;
  supportedSites: string[];
  lastExecution?: string;
  lastStatus?: string;
}

interface ScrapeLog {
  id: number;
  targetSite: string;
  status: string;
  productsFound: number;
  errorMessage?: string;
  executedAt: string;
}

interface ScrapeConfig {
  id: number;
  siteName: string;
  siteUrl: string;
  cronExpression: string;
  isEnabled: boolean;
  lastScrapedAt?: string;
}

function AdminDashboard() {
  const { user } = useAuth();
  const [status, setStatus] = useState<ScrapeStatus | null>(null);
  const [recentLogs, setRecentLogs] = useState<ScrapeLog[]>([]);
  const [configs, setConfigs] = useState<ScrapeConfig[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true);
      setError(null);
      try {
        const [statusData, logsData, configsData] = await Promise.all([
          getScrapeStatus(),
          getScrapeLogs(5),
          getScrapeConfigs(),
        ]);
        setStatus(statusData);
        setRecentLogs(logsData);
        setConfigs(configsData);
      } catch {
        setError('管理データの取得に失敗しました。');
      } finally {
        setLoading(false);
      }
    };
    fetchDashboardData();
  }, []);

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex justify-center items-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-6">
        <h1 className="text-2xl sm:text-3xl font-bold text-gray-800">管理ダッシュボード</h1>
        <span className="text-sm text-gray-500">ログイン中: {user?.username}</span>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      {/* ステータスカード */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        {/* スクレイピングステータス */}
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-sm font-medium text-gray-500 mb-2">スクレイピング状態</h3>
          <div className="flex items-center gap-2">
            <span className={`inline-block w-3 h-3 rounded-full ${status?.available ? 'bg-green-500' : 'bg-red-500'}`}></span>
            <span className="text-lg font-semibold text-gray-800">
              {status?.available ? '利用可能' : '停止中'}
            </span>
          </div>
          {status?.lastExecution && (
            <p className="text-xs text-gray-400 mt-2">
              最終実行: {new Date(status.lastExecution).toLocaleString('ja-JP')}
            </p>
          )}
          {status?.lastStatus && (
            <span className={`inline-block mt-1 text-xs px-2 py-0.5 rounded ${
              status.lastStatus === 'SUCCESS' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
            }`}>
              {status.lastStatus}
            </span>
          )}
        </div>

        {/* 対応サイト数 */}
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-sm font-medium text-gray-500 mb-2">登録サイト数</h3>
          <p className="text-3xl font-bold text-indigo-600">{configs.length}</p>
          <p className="text-xs text-gray-400 mt-2">
            有効: {configs.filter(c => c.isEnabled).length} / {configs.length}
          </p>
        </div>

        {/* 最新ログ結果 */}
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-sm font-medium text-gray-500 mb-2">直近のスクレイピング結果</h3>
          {recentLogs.length > 0 ? (
            <>
              <p className="text-3xl font-bold text-indigo-600">{recentLogs[0].productsFound}件</p>
              <p className="text-xs text-gray-400 mt-2">
                {recentLogs[0].targetSite} - {new Date(recentLogs[0].executedAt).toLocaleString('ja-JP')}
              </p>
            </>
          ) : (
            <p className="text-gray-400">データなし</p>
          )}
        </div>
      </div>

      {/* 管理メニュー */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        <Link to="/admin/scrape" className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition-shadow">
          <h3 className="text-lg font-semibold text-gray-800 mb-2">手動スクレイピング</h3>
          <p className="text-sm text-gray-500">スクレイピングを手動で実行し、結果を確認します。</p>
        </Link>

        <Link to="/admin/logs" className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition-shadow">
          <h3 className="text-lg font-semibold text-gray-800 mb-2">スクレイピング履歴</h3>
          <p className="text-sm text-gray-500">過去のスクレイピング実行履歴を確認します。</p>
        </Link>

        <Link to="/admin/users" className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition-shadow">
          <h3 className="text-lg font-semibold text-gray-800 mb-2">ユーザー管理</h3>
          <p className="text-sm text-gray-500">ユーザーの作成・削除を行います。</p>
        </Link>
      </div>

      {/* 最近のスクレイピングログ */}
      <div className="bg-white rounded-lg shadow p-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-lg font-semibold text-gray-800">最近のスクレイピングログ</h2>
          <Link to="/admin/logs" className="text-sm text-indigo-600 hover:text-indigo-800">
            すべて見る
          </Link>
        </div>

        {recentLogs.length === 0 ? (
          <p className="text-gray-400 text-center py-4">ログがありません。</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-200">
                  <th className="text-left py-2 px-3 text-gray-500 font-medium">実行日時</th>
                  <th className="text-left py-2 px-3 text-gray-500 font-medium">対象サイト</th>
                  <th className="text-left py-2 px-3 text-gray-500 font-medium">ステータス</th>
                  <th className="text-left py-2 px-3 text-gray-500 font-medium">取得件数</th>
                  <th className="text-left py-2 px-3 text-gray-500 font-medium">エラー</th>
                </tr>
              </thead>
              <tbody>
                {recentLogs.map((log) => (
                  <tr key={log.id} className="border-b border-gray-100">
                    <td className="py-2 px-3 text-gray-700">
                      {new Date(log.executedAt).toLocaleString('ja-JP')}
                    </td>
                    <td className="py-2 px-3 text-gray-700">{log.targetSite}</td>
                    <td className="py-2 px-3">
                      <span className={`px-2 py-0.5 rounded text-xs font-medium ${
                        log.status === 'SUCCESS' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                      }`}>
                        {log.status}
                      </span>
                    </td>
                    <td className="py-2 px-3 text-gray-700">{log.productsFound}件</td>
                    <td className="py-2 px-3 text-red-500 text-xs truncate max-w-xs">
                      {log.errorMessage || '-'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

    </div>
  );
}

export default AdminDashboard;
