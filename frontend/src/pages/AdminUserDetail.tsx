import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
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
        setFormData({ username: data.username, email: data.email, password: '', notificationEnabled: data.notificationEnabled });
      } catch {
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
      const updateData: { username?: string; email?: string; password?: string; notificationEnabled?: boolean } = {
        username: formData.username,
        email: formData.email,
        notificationEnabled: formData.notificationEnabled,
      };
      if (formData.password) updateData.password = formData.password;

      const updatedUser = await updateUser(parseInt(id), updateData);
      setUser(updatedUser);
      setFormData({ ...formData, password: '' });
      setSuccess('ユーザー情報を更新しました');
    } catch (err: any) {
      setFormError(err.response?.data?.error || 'ユーザー情報の更新に失敗しました');
    } finally {
      setFormLoading(false);
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 'calc(100vh - 3.25rem)' }}>
        <div className="loader" />
      </div>
    );
  }

  if (error || !user) {
    return (
      <div style={{ padding: '2rem 1.5rem' }}>
        <div className="container mx-auto" style={{ maxWidth: '42rem' }}>
          <div className="alert-error" style={{ marginBottom: '1rem' }}>{error || 'ユーザーが見つかりません'}</div>
          <Link to="/admin/users" style={{ fontSize: '0.8rem', color: 'var(--color-accent)', textDecoration: 'none' }}>← ユーザー一覧に戻る</Link>
        </div>
      </div>
    );
  }

  return (
    <div style={{ minHeight: 'calc(100vh - 3.25rem)', padding: '2rem 1.5rem 4rem' }}>
      <div className="container mx-auto" style={{ maxWidth: '42rem' }}>

        {/* Breadcrumb */}
        <div className="breadcrumb">
          <Link to="/admin">ダッシュボード</Link>
          <span>/</span>
          <Link to="/admin/users">ユーザー管理</Link>
          <span>/</span>
          <span style={{ color: 'var(--color-text)' }}>{user.username}</span>
        </div>

        {/* Header */}
        <div style={{ marginBottom: '2rem' }}>
          <p className="section-label" style={{ marginBottom: '0.375rem' }}>Admin</p>
          <h1 className="page-title">ユーザー編集</h1>
          <p style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)', marginTop: '0.25rem' }}>ID: {user.id}</p>
        </div>

        <div className="card" style={{ padding: '1.75rem' }}>
          {formError && <div className="alert-error" style={{ marginBottom: '1.25rem' }}>{formError}</div>}
          {success && <div className="alert-success" style={{ marginBottom: '1.25rem' }}>{success}</div>}

          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <div>
              <label className="input-label">ユーザー名</label>
              <input
                type="text"
                value={formData.username}
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                required
                className="input-field"
              />
            </div>

            <div>
              <label className="input-label">メールアドレス</label>
              <input
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                required
                className="input-field"
              />
            </div>

            <div>
              <label className="input-label">パスワード</label>
              <input
                type="password"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                placeholder="変更する場合のみ入力（8文字以上）"
                minLength={8}
                className="input-field"
              />
              <p style={{ fontSize: '0.7rem', color: 'var(--color-text-muted)', marginTop: '0.375rem' }}>
                空欄の場合、パスワードは変更されません
              </p>
            </div>

            <div>
              <label style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', cursor: 'pointer' }}>
                <input
                  type="checkbox"
                  checked={formData.notificationEnabled}
                  onChange={(e) => setFormData({ ...formData, notificationEnabled: e.target.checked })}
                  style={{
                    width: '1rem', height: '1rem',
                    accentColor: 'var(--color-accent)',
                    cursor: 'pointer',
                  }}
                />
                <span style={{ fontSize: '0.875rem', color: 'var(--color-text)', fontWeight: 500 }}>
                  メール通知を有効にする
                </span>
              </label>
              <p style={{ fontSize: '0.7rem', color: 'var(--color-text-muted)', marginTop: '0.3rem', marginLeft: '1.75rem' }}>
                有効にすると、スクレイピング完了時にメール通知が届きます
              </p>
            </div>

            <hr className="divider" style={{ margin: '0.25rem 0' }} />

            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              <button type="submit" disabled={formLoading} className="btn-primary">
                {formLoading ? '更新中...' : '更新する'}
              </button>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>
                ロール:
                <span className={`badge ${user.role === 'ADMIN' ? 'badge-purple' : 'badge-cyan'}`}>
                  {user.role === 'ADMIN' ? '管理者' : '一般'}
                </span>
              </div>
            </div>
          </form>

          <hr className="divider" />
          <p className="section-label" style={{ marginBottom: '0.5rem' }}>アカウント情報</p>
          <p style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>
            作成日: {new Date(user.createdAt).toLocaleString('ja-JP')}
          </p>
        </div>
      </div>
    </div>
  );
}

export default AdminUserDetail;
