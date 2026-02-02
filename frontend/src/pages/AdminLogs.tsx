import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getScrapeLogs } from '../services/adminApi';

interface ScrapeLog {
  id: number;
  targetSite: string;
  status: string;
  productsFound: number;
  errorMessage?: string;
  executedAt: string;
}

function AdminLogs() {
  const [logs, setLogs] = useState<ScrapeLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [limit, setLimit] = useState(20);
  const [expandedError, setExpandedError] = useState<number | null>(null);

  const fetchLogs = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getScrapeLogs(limit);
      setLogs(data);
    } catch {
      setError('スクレイピング履歴の取得に失敗しました。');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, [limit]);

  const siteDisplayName = (site: string) => {
    switch (site) {
      case 'BANDAI_GASHAPON': return 'バンダイ';
      case 'TAKARA_TOMY_ARTS': return 'タカラトミーアーツ';
      default: return site;
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      {/* パンくずリスト */}
      <div className="text-sm text-gray-500 mb-4">
        <Link to="/admin" className="hover:text-indigo-600">管理ダッシュボード</Link>
        <span className="mx-2">/</span>
        <span className="text-gray-800">スクレイピング履歴</span>
      </div>

      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-800">スクレイピング履歴</h1>
        <div className="flex items-center gap-3">
          <label className="text-sm text-gray-500">表示件数:</label>
          <select
            value={limit}
            onChange={(e) => setLimit(Number(e.target.value))}
            className="border border-gray-300 rounded-lg px-3 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value={10}>10件</option>
            <option value={20}>20件</option>
            <option value={50}>50件</option>
            <option value={100}>100件</option>
          </select>
          <button
            onClick={fetchLogs}
            className="bg-indigo-600 text-white px-4 py-1.5 rounded-lg text-sm hover:bg-indigo-700 transition"
          >
            更新
          </button>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      {loading ? (
        <div className="flex justify-center items-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>
      ) : logs.length === 0 ? (
        <div className="bg-white rounded-lg shadow p-6 text-center text-gray-400">
          スクレイピング履歴がありません。
        </div>
      ) : (
        <>
          {/* サマリー */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
            <div className="bg-white rounded-lg shadow p-4">
              <p className="text-sm text-gray-500">合計実行回数</p>
              <p className="text-2xl font-bold text-gray-800">{logs.length}回</p>
            </div>
            <div className="bg-white rounded-lg shadow p-4">
              <p className="text-sm text-gray-500">成功</p>
              <p className="text-2xl font-bold text-green-600">
                {logs.filter(l => l.status === 'SUCCESS').length}回
              </p>
            </div>
            <div className="bg-white rounded-lg shadow p-4">
              <p className="text-sm text-gray-500">失敗</p>
              <p className="text-2xl font-bold text-red-600">
                {logs.filter(l => l.status === 'FAILURE').length}回
              </p>
            </div>
          </div>

          {/* ログテーブル */}
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="bg-gray-50 border-b border-gray-200">
                    <th className="text-left py-3 px-4 text-gray-500 font-medium">実行日時</th>
                    <th className="text-left py-3 px-4 text-gray-500 font-medium">対象サイト</th>
                    <th className="text-left py-3 px-4 text-gray-500 font-medium">ステータス</th>
                    <th className="text-left py-3 px-4 text-gray-500 font-medium">取得件数</th>
                    <th className="text-left py-3 px-4 text-gray-500 font-medium">エラー</th>
                  </tr>
                </thead>
                <tbody>
                  {logs.map((log) => (
                    <tr key={log.id} className="border-b border-gray-100 hover:bg-gray-50">
                      <td className="py-3 px-4 text-gray-700">
                        {new Date(log.executedAt).toLocaleString('ja-JP')}
                      </td>
                      <td className="py-3 px-4 text-gray-700">
                        {siteDisplayName(log.targetSite)}
                      </td>
                      <td className="py-3 px-4">
                        <span className={`px-2 py-0.5 rounded text-xs font-medium ${
                          log.status === 'SUCCESS'
                            ? 'bg-green-100 text-green-700'
                            : 'bg-red-100 text-red-700'
                        }`}>
                          {log.status === 'SUCCESS' ? '成功' : '失敗'}
                        </span>
                      </td>
                      <td className="py-3 px-4 text-gray-700">{log.productsFound}件</td>
                      <td className="py-3 px-4">
                        {log.errorMessage ? (
                          <div>
                            <button
                              onClick={() => setExpandedError(expandedError === log.id ? null : log.id)}
                              className="text-red-500 text-xs hover:text-red-700 underline"
                            >
                              {expandedError === log.id ? '閉じる' : 'エラー詳細'}
                            </button>
                            {expandedError === log.id && (
                              <div className="mt-2 p-2 bg-red-50 rounded text-xs text-red-700 max-w-md break-words">
                                {log.errorMessage}
                              </div>
                            )}
                          </div>
                        ) : (
                          <span className="text-gray-300">-</span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}
    </div>
  );
}

export default AdminLogs;
