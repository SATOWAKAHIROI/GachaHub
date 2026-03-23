import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getUsers, createUser, deleteUser } from '../services/adminApi';

interface UserInfo {
  id: number;
  username: string;
  email: string;
  role: string;
  notificationEnabled: boolean;
  createdAt: string;
}

function AdminUsers() {
  const [users, setUsers] = useState<UserInfo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({ username: '', email: '', password: '', role: 'USER' });
  const [formError, setFormError] = useState('');
  const [formLoading, setFormLoading] = useState(false);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const data = await getUsers();
      setUsers(data);
    } catch {
      setError('ユーザー一覧の取得に失敗しました');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchUsers(); }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormLoading(true);
    setFormError('');
    try {
      await createUser(formData);
      setFormData({ username: '', email: '', password: '', role: 'USER' });
      setShowForm(false);
      fetchUsers();
    } catch (err: any) {
      setFormError(err.response?.data?.error || 'ユーザー作成に失敗しました');
    } finally {
      setFormLoading(false);
    }
  };

  const handleDelete = async (id: number, username: string) => {
    if (!confirm(`ユーザー "${username}" を削除しますか？`)) return;
    try {
      await deleteUser(id);
      fetchUsers();
    } catch (err: any) {
      setError(err.response?.data?.error || 'ユーザー削除に失敗しました');
    }
  };

  return (
    <div style={{ minHeight: 'calc(100vh - 3.25rem)', padding: '2rem 1.5rem 4rem' }}>
      <div className="container mx-auto" style={{ maxWidth: '72rem' }}>

        {/* Breadcrumb */}
        <div className="breadcrumb">
          <Link to="/admin">ダッシュボード</Link>
          <span>/</span>
          <span style={{ color: 'var(--color-text)' }}>ユーザー管理</span>
        </div>

        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '2rem', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <p className="section-label" style={{ marginBottom: '0.375rem' }}>Admin</p>
            <h1 className="page-title">ユーザー管理</h1>
          </div>
          <button
            onClick={() => setShowForm(!showForm)}
            className={showForm ? 'btn-secondary' : 'btn-primary'}
            style={{ alignSelf: 'flex-end' }}
          >
            {showForm ? '✕ キャンセル' : '+ 新規ユーザー'}
          </button>
        </div>

        {error && <div className="alert-error" style={{ marginBottom: '1.5rem' }}>{error}</div>}

        {/* Create form */}
        {showForm && (
          <div className="card" style={{ padding: '1.5rem', marginBottom: '1.5rem' }}>
            <h2 style={{ fontSize: '0.875rem', fontWeight: 700, color: 'var(--color-text)', marginBottom: '1.25rem' }}>
              新規ユーザー作成
            </h2>
            {formError && <div className="alert-error" style={{ marginBottom: '1rem' }}>{formError}</div>}
            <form onSubmit={handleCreate}>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', marginBottom: '1.25rem' }}>
                <div>
                  <label className="input-label">ユーザー名</label>
                  <input
                    type="text"
                    value={formData.username}
                    onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                    required
                    className="input-field"
                    placeholder="username"
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
                    placeholder="email@example.com"
                  />
                </div>
                <div>
                  <label className="input-label">パスワード</label>
                  <input
                    type="password"
                    value={formData.password}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    required
                    minLength={8}
                    className="input-field"
                    placeholder="8文字以上"
                  />
                </div>
                <div>
                  <label className="input-label">ロール</label>
                  <select
                    value={formData.role}
                    onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                    className="input-field"
                  >
                    <option value="USER">一般ユーザー</option>
                    <option value="ADMIN">管理者</option>
                  </select>
                </div>
              </div>
              <button type="submit" disabled={formLoading} className="btn-primary">
                {formLoading ? '作成中...' : 'ユーザーを作成'}
              </button>
            </form>
          </div>
        )}

        {/* Table */}
        <div className="card" style={{ overflow: 'hidden' }}>
          <div style={{ padding: '1rem 1.5rem', borderBottom: '1px solid var(--color-border)' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>
              {users.length} 件のユーザー
            </span>
          </div>

          {loading ? (
            <div style={{ display: 'flex', justifyContent: 'center', padding: '3rem' }}>
              <div className="loader" />
            </div>
          ) : users.length === 0 ? (
            <p style={{ textAlign: 'center', padding: '3rem', color: 'var(--color-text-dim)', fontSize: '0.875rem' }}>
              ユーザーがいません
            </p>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>ユーザー名</th>
                    <th>メール</th>
                    <th>ロール</th>
                    <th>通知</th>
                    <th>作成日</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((user) => (
                    <tr key={user.id}>
                      <td className="muted" style={{ fontSize: '0.72rem' }}>{user.id}</td>
                      <td style={{ fontWeight: 600 }}>{user.username}</td>
                      <td className="muted" style={{ fontSize: '0.8rem' }}>{user.email}</td>
                      <td>
                        <span className={`badge ${user.role === 'ADMIN' ? 'badge-purple' : 'badge-cyan'}`}>
                          {user.role === 'ADMIN' ? '管理者' : '一般'}
                        </span>
                      </td>
                      <td>
                        <span className={`badge ${user.notificationEnabled ? 'badge-success' : ''}`}
                          style={!user.notificationEnabled ? { color: 'var(--color-text-dim)', border: '1px solid var(--color-border)' } : {}}>
                          {user.notificationEnabled ? 'ON' : 'OFF'}
                        </span>
                      </td>
                      <td className="muted" style={{ fontSize: '0.72rem', whiteSpace: 'nowrap' }}>
                        {new Date(user.createdAt).toLocaleDateString('ja-JP')}
                      </td>
                      <td>
                        <div style={{ display: 'flex', gap: '0.75rem' }}>
                          <Link
                            to={`/admin/users/${user.id}`}
                            style={{ fontSize: '0.75rem', color: 'var(--color-accent)', textDecoration: 'none', fontWeight: 600 }}
                          >
                            編集
                          </Link>
                          <button
                            onClick={() => handleDelete(user.id, user.username)}
                            className="btn-danger"
                            style={{ fontSize: '0.72rem', padding: '0.2rem 0.5rem' }}
                          >
                            削除
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default AdminUsers;
