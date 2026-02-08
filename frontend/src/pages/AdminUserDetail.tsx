import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getUser, updateUser } from '../services/adminApi';

interface UserInfo {
  id: number;
  username: string;
  email: string;
  role: string;
  notificationEnabled: boolean;
  createdAt: string;
}

function AdminUserDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [user, setUser] = useState<UserInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    notificationEnabled: false,
  });
  const [formError, setFormError] = useState('');
  const [formLoading, setFormLoading] = useState(false);
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const fetchUser = async () => {
      if (!id) return;
      try {
        setLoading(true);
        const data = await getUser(parseInt(id));
        setUser(data);
        setFormData({
          username: data.username,
          email: data.email,
          password: '',
          notificationEnabled: data.notificationEnabled,
        });
      } catch (err) {
        setError('ユーザー情報の取得に失敗しました');
      } finally {
        setLoading(false);
      }
    };
    fetchUser();
  }, [id]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id) return;

    setFormLoading(true);
    setFormError('');
    setSuccess('');

    try {
      const updateData: {
        username?: string;
        email?: string;
        password?: string;
        notificationEnabled?: boolean;
      } = {
        username: formData.username,
        email: formData.email,
        notificationEnabled: formData.notificationEnabled,
      };

      // パスワードは入力された場合のみ送信
      if (formData.password) {
        updateData.password = formData.password;
      }

      const updatedUser = await updateUser(parseInt(id), updateData);
      setUser(updatedUser);
      setFormData({
        ...formData,
        password: '',
      });
      setSuccess('ユーザー情報を更新しました');
    } catch (err: any) {
      setFormError(err.response?.data?.error || 'ユーザー情報の更新に失敗しました');
    } finally {
      setFormLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-gray-500">読み込み中...</div>
      </div>
    );
  }

  if (error || !user) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="container mx-auto px-4 py-8 max-w-2xl">
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4">
            {error || 'ユーザーが見つかりません'}
          </div>
          <Link
            to="/admin/users"
            className="text-indigo-600 hover:text-indigo-800 text-sm font-medium"
          >
            ユーザー一覧に戻る
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8 max-w-2xl">
        {/* ヘッダー */}
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 gap-4">
          <div>
            <h1 className="text-2xl sm:text-3xl font-bold text-gray-900">ユーザー編集</h1>
            <p className="text-gray-600 mt-1">ID: {user.id}</p>
          </div>
          <Link
            to="/admin/users"
            className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300 transition text-sm font-medium"
          >
            ユーザー一覧に戻る
          </Link>
        </div>

        {/* フォーム */}
        <div className="bg-white rounded-lg shadow-md p-6">
          {formError && (
            <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
              {formError}
            </div>
          )}
          {success && (
            <div className="mb-4 bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg text-sm">
              {success}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                ユーザー名
              </label>
              <input
                type="text"
                value={formData.username}
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none text-sm"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                メールアドレス
              </label>
              <input
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none text-sm"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                パスワード
              </label>
              <input
                type="password"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                placeholder="変更する場合のみ入力（8文字以上）"
                minLength={8}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none text-sm"
              />
              <p className="mt-1 text-xs text-gray-500">
                空欄の場合、パスワードは変更されません
              </p>
            </div>

            <div>
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  checked={formData.notificationEnabled}
                  onChange={(e) => setFormData({ ...formData, notificationEnabled: e.target.checked })}
                  className="w-5 h-5 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
                />
                <span className="text-sm font-medium text-gray-700">
                  メール通知を有効にする
                </span>
              </label>
              <p className="mt-1 text-xs text-gray-500 ml-8">
                有効にすると、スクレイピング完了時にメール通知が届きます
              </p>
            </div>

            <div className="flex items-center gap-4 pt-4 border-t border-gray-200">
              <button
                type="submit"
                disabled={formLoading}
                className="bg-indigo-600 text-white px-6 py-2 rounded-lg hover:bg-indigo-700 transition text-sm font-medium disabled:opacity-50"
              >
                {formLoading ? '更新中...' : '更新する'}
              </button>
              <span className="text-sm text-gray-500">
                ロール: <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                  user.role === 'ADMIN'
                    ? 'bg-purple-100 text-purple-800'
                    : 'bg-green-100 text-green-800'
                }`}>
                  {user.role === 'ADMIN' ? '管理者' : '一般'}
                </span>
              </span>
            </div>
          </form>

          {/* 追加情報 */}
          <div className="mt-6 pt-6 border-t border-gray-200">
            <h3 className="text-sm font-medium text-gray-700 mb-2">その他の情報</h3>
            <dl className="text-sm text-gray-600 space-y-1">
              <div className="flex gap-2">
                <dt className="font-medium">作成日:</dt>
                <dd>{new Date(user.createdAt).toLocaleString('ja-JP')}</dd>
              </div>
            </dl>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AdminUserDetail;
