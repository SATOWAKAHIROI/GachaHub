import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  getScrapeConfigs,
  updateScrapeConfig,
  toggleScrapeConfig,
} from '../services/adminApi';

interface ScrapeConfig {
  id: number;
  siteName: string;
  siteUrl: string;
  cronExpression: string;
  isEnabled: boolean;
  lastScrapedAt?: string;
}

interface ConfigForm {
  siteName: string;
  siteUrl: string;
  cronExpression: string;
  isEnabled: boolean;
}

const emptyForm: ConfigForm = {
  siteName: '',
  siteUrl: '',
  cronExpression: '0 0 6 * * *',
  isEnabled: true,
};

function AdminSites() {
  const [configs, setConfigs] = useState<ScrapeConfig[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form, setForm] = useState<ConfigForm>(emptyForm);
  const [formError, setFormError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const fetchConfigs = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getScrapeConfigs();
      setConfigs(data);
    } catch {
      setError('サイト設定の取得に失敗しました。');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchConfigs();
  }, []);

  const handleUpdate = async () => {
    if (editingId === null) return;
    if (!form.siteName || !form.siteUrl) {
      setFormError('サイト名とURLは必須です。');
      return;
    }
    setSubmitting(true);
    setFormError(null);
    try {
      await updateScrapeConfig(editingId, form);
      setEditingId(null);
      setForm(emptyForm);
      await fetchConfigs();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : '設定の更新に失敗しました。';
      setFormError(message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleToggle = async (id: number) => {
    try {
      await toggleScrapeConfig(id);
      await fetchConfigs();
    } catch {
      setError('設定の切り替えに失敗しました。');
    }
  };

  const startEdit = (config: ScrapeConfig) => {
    setEditingId(config.id);
    setForm({
      siteName: config.siteName,
      siteUrl: config.siteUrl,
      cronExpression: config.cronExpression || '0 0 6 * * *',
      isEnabled: config.isEnabled,
    });
    setFormError(null);
  };

  const cancelEdit = () => {
    setEditingId(null);
    setForm(emptyForm);
    setFormError(null);
  };

  const renderForm = () => (
    <div className="bg-gray-50 rounded-lg p-4 mb-4 border border-gray-200">
      <h3 className="text-sm font-semibold text-gray-700 mb-3">
        スケジュール設定を編集
      </h3>
      {formError && (
        <div className="bg-red-50 border border-red-200 text-red-700 text-sm px-3 py-2 rounded mb-3">
          {formError}
        </div>
      )}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        <div>
          <label className="block text-xs text-gray-500 mb-1">cron式</label>
          <input
            type="text"
            value={form.cronExpression}
            onChange={(e) => setForm({ ...form, cronExpression: e.target.value })}
            placeholder="例: 0 0 6 * * *"
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <p className="text-xs text-gray-400 mt-1">秒 分 時 日 月 曜日（例: 毎日6時 = 0 0 6 * * *）</p>
        </div>
        <div className="flex items-end">
          <label className="flex items-center gap-2 cursor-pointer">
            <input
              type="checkbox"
              checked={form.isEnabled}
              onChange={(e) => setForm({ ...form, isEnabled: e.target.checked })}
              className="w-4 h-4 text-indigo-600 rounded focus:ring-indigo-500"
            />
            <span className="text-sm text-gray-700">有効にする</span>
          </label>
        </div>
      </div>
      <div className="flex gap-2 mt-4">
        <button
          onClick={handleUpdate}
          disabled={submitting}
          className="bg-indigo-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-indigo-700 transition disabled:opacity-50"
        >
          {submitting ? '保存中...' : '更新'}
        </button>
        <button
          onClick={cancelEdit}
          className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg text-sm hover:bg-gray-300 transition"
        >
          キャンセル
        </button>
      </div>
    </div>
  );

  return (
    <div className="container mx-auto px-4 py-8">
      {/* パンくずリスト */}
      <div className="text-sm text-gray-500 mb-4">
        <Link to="/admin" className="hover:text-indigo-600">管理ダッシュボード</Link>
        <span className="mx-2">/</span>
        <span className="text-gray-800">サイト管理</span>
      </div>

      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-800">サイト管理</h1>
      </div>

      {/* お問い合わせ案内 */}
      <div className="bg-blue-50 border border-blue-200 text-blue-700 px-4 py-3 rounded-lg mb-6">
        新しいサイトの追加などご要望がありましたら、<a href="mailto:hitobussi@gmail.com" className="underline font-medium">hitobussi@gmail.com</a> まで連絡ください。
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
      ) : configs.length === 0 ? (
        <div className="bg-white rounded-lg shadow p-6 text-center text-gray-400">
          登録されているサイトはありません。
        </div>
      ) : (
        <div className="space-y-4">
          {configs.map((config) => (
            <div key={config.id}>
              {/* 編集フォーム */}
              {editingId === config.id && renderForm()}

              {/* サイトカード */}
              {editingId !== config.id && (
                <div className="bg-white rounded-lg shadow p-5">
                  <div className="flex flex-col sm:flex-row justify-between gap-4">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <h3 className="text-lg font-semibold text-gray-800">{config.siteName}</h3>
                        <span className={`px-2 py-0.5 rounded text-xs font-medium ${
                          config.isEnabled ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
                        }`}>
                          {config.isEnabled ? '有効' : '無効'}
                        </span>
                      </div>
                      <p className="text-sm text-gray-500 mb-1">{config.siteUrl}</p>
                      <div className="flex flex-wrap gap-4 text-xs text-gray-400">
                        <span>cron: {config.cronExpression || '未設定'}</span>
                        {config.lastScrapedAt && (
                          <span>最終実行: {new Date(config.lastScrapedAt).toLocaleString('ja-JP')}</span>
                        )}
                      </div>
                    </div>

                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => handleToggle(config.id)}
                        className={`px-3 py-1.5 rounded-lg text-xs font-medium transition ${
                          config.isEnabled
                            ? 'bg-yellow-100 text-yellow-700 hover:bg-yellow-200'
                            : 'bg-green-100 text-green-700 hover:bg-green-200'
                        }`}
                      >
                        {config.isEnabled ? '無効にする' : '有効にする'}
                      </button>
                      <button
                        onClick={() => startEdit(config)}
                        className="px-3 py-1.5 rounded-lg text-xs font-medium bg-gray-100 text-gray-700 hover:bg-gray-200 transition"
                      >
                        編集
                      </button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default AdminSites;
