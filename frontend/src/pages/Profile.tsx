import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getProfile, updateProfile } from '../services/api';

function Profile() {
  const { user, updateUser } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
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
    const fetchProfile = async () => {
      try {
        setLoading(true);
        const data = await getProfile();
        setFormData({ username: data.username, email: data.email, password: '', notificationEnabled: data.notificationEnabled });
      } catch {
        setFormError('プロフィールの取得に失敗しました');
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
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

      const updatedUser = await updateProfile(updateData);
      updateUser(updatedUser);
      setFormData({ ...formData, password: '' });
      setSuccess('プロフィールを更新しました');
    } catch (err: any) {
      setFormError(err.response?.data?.error || 'プロフィールの更新に失敗しました');
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

  return (
    <div style={{ minHeight: 'calc(100vh - 3.25rem)', padding: '2rem 1.5rem 4rem' }}>
      <div className="container mx-auto" style={{ maxWidth: '42rem' }}>

        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '2rem', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <p className="section-label" style={{ marginBottom: '0.375rem' }}>Settings</p>
            <h1 className="page-title">プロフィール編集</h1>
            <p style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)', marginTop: '0.25rem' }}>
              {user?.username} さんの設定
            </p>
          </div>
          <button
            onClick={() => navigate(-1)}
            className="btn-secondary"
            style={{ alignSelf: 'flex-end' }}
          >
            ← 戻る
          </button>
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
                  style={{ width: '1rem', height: '1rem', accentColor: 'var(--color-accent)', cursor: 'pointer' }}
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
                <span className={`badge ${user?.role === 'ADMIN' ? 'badge-purple' : 'badge-cyan'}`}>
                  {user?.role === 'ADMIN' ? '管理者' : '一般'}
                </span>
              </div>
            </div>
          </form>

          <hr className="divider" />
          <p className="section-label" style={{ marginBottom: '0.5rem' }}>アカウント情報</p>
          <p style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>
            作成日: {user?.createdAt ? new Date(user.createdAt).toLocaleString('ja-JP') : '—'}
          </p>
        </div>
      </div>
    </div>
  );
}

export default Profile;
