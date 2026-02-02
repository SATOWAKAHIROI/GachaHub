import { useState } from 'react';
import { Link } from 'react-router-dom';
import { scrapeBandai, scrapeTakaraTomy } from '../services/adminApi';

interface ScrapeResult {
  status: string;
  site: string;
  totalProducts?: number;
  newProducts?: number;
  message: string;
}

function AdminScrape() {
  const [selectedSite, setSelectedSite] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<ScrapeResult | null>(null);
  const [error, setError] = useState<string | null>(null);

  const sites = [
    { value: 'BANDAI', label: 'バンダイ（ガシャポン）' },
    { value: 'TAKARA_TOMY', label: 'タカラトミーアーツ' },
  ];

  const handleExecute = async () => {
    if (!selectedSite) return;

    setLoading(true);
    setResult(null);
    setError(null);

    try {
      let data: ScrapeResult;
      if (selectedSite === 'BANDAI') {
        data = await scrapeBandai();
      } else {
        data = await scrapeTakaraTomy();
      }
      setResult(data);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'スクレイピングの実行に失敗しました。';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      {/* パンくずリスト */}
      <div className="text-sm text-gray-500 mb-4">
        <Link to="/admin" className="hover:text-indigo-600">管理ダッシュボード</Link>
        <span className="mx-2">/</span>
        <span className="text-gray-800">手動スクレイピング</span>
      </div>

      <h1 className="text-3xl font-bold text-gray-800 mb-6">手動スクレイピング実行</h1>

      {/* サイト選択 */}
      <div className="bg-white rounded-lg shadow p-6 mb-6">
        <h2 className="text-lg font-semibold text-gray-800 mb-4">対象サイトを選択</h2>
        <div className="flex flex-col sm:flex-row gap-3">
          {sites.map((site) => (
            <button
              key={site.value}
              onClick={() => setSelectedSite(site.value)}
              disabled={loading}
              className={`px-6 py-3 rounded-lg text-sm font-medium transition border-2 ${
                selectedSite === site.value
                  ? 'border-indigo-600 bg-indigo-50 text-indigo-700'
                  : 'border-gray-200 bg-white text-gray-700 hover:border-gray-300'
              } ${loading ? 'opacity-50 cursor-not-allowed' : ''}`}
            >
              {site.label}
            </button>
          ))}
        </div>
      </div>

      {/* 実行ボタン */}
      <div className="bg-white rounded-lg shadow p-6 mb-6">
        <button
          onClick={handleExecute}
          disabled={!selectedSite || loading}
          className={`w-full sm:w-auto px-8 py-3 rounded-lg text-white font-medium transition ${
            !selectedSite || loading
              ? 'bg-gray-400 cursor-not-allowed'
              : 'bg-indigo-600 hover:bg-indigo-700'
          }`}
        >
          {loading ? (
            <span className="flex items-center justify-center gap-2">
              <span className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></span>
              スクレイピング実行中...
            </span>
          ) : (
            'スクレイピングを実行'
          )}
        </button>

        {!selectedSite && !loading && (
          <p className="text-sm text-gray-400 mt-2">対象サイトを選択してください。</p>
        )}
      </div>

      {/* 実行中表示 */}
      {loading && (
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 mb-6">
          <div className="flex items-center gap-3">
            <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
            <div>
              <p className="text-blue-800 font-medium">スクレイピングを実行しています...</p>
              <p className="text-blue-600 text-sm mt-1">
                対象サイトにアクセスしてデータを取得中です。完了までお待ちください。
              </p>
            </div>
          </div>
        </div>
      )}

      {/* 実行結果 */}
      {result && (
        <div className={`border rounded-lg p-6 mb-6 ${
          result.status === 'success'
            ? 'bg-green-50 border-green-200'
            : 'bg-red-50 border-red-200'
        }`}>
          <h3 className={`text-lg font-semibold mb-2 ${
            result.status === 'success' ? 'text-green-800' : 'text-red-800'
          }`}>
            {result.status === 'success' ? '実行完了' : '実行失敗'}
          </h3>
          <p className={`text-sm ${
            result.status === 'success' ? 'text-green-700' : 'text-red-700'
          }`}>
            {result.message}
          </p>
          {result.totalProducts !== undefined && (
            <div className="mt-3 flex items-center gap-4">
              <div className="flex items-center gap-2">
                <span className="text-2xl font-bold text-green-700">{result.totalProducts}</span>
                <span className="text-green-600">件取得</span>
              </div>
              {result.newProducts !== undefined && result.newProducts > 0 && (
                <div className="flex items-center gap-2">
                  <span className="text-sm text-gray-400">うち新着</span>
                  <span className="text-xl font-bold text-orange-600">{result.newProducts}</span>
                  <span className="text-orange-500">件</span>
                </div>
              )}
              {result.newProducts !== undefined && result.newProducts === 0 && (
                <span className="text-sm text-gray-500">（新着なし）</span>
              )}
            </div>
          )}
        </div>
      )}

      {/* エラー表示 */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}
    </div>
  );
}

export default AdminScrape;
