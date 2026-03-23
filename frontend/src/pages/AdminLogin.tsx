import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';
import type { LoginRequest, LoginResponse } from '../types';

function AdminLogin() {
  const navigate = useNavigate();
  const { login, user, isAuthenticated } = useAuth();

  useEffect(() => {
    if (isAuthenticated && user?.role === 'ADMIN') {
      navigate('/admin');
    }
  }, [isAuthenticated, user, navigate]);

  const [formData, setFormData] = useState<LoginRequest>({ email: '', password: '' });
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const response = await api.post<LoginResponse>('/auth/admin/login', formData);
      const { token, user } = response.data;
      login(token, user);
      navigate('/admin');
    } catch (err: any) {
      if (err.response?.status === 403) {
        setError('管理者権限がありません。');
      } else if (err.response?.data?.error) {
        setError(err.response.data.error);
      } else {
        setError('ログインに失敗しました。もう一度お試しください。');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: 'calc(100vh - 3.25rem)',
      backgroundColor: 'var(--color-bg)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '2rem 1.5rem',
      position: 'relative',
      overflow: 'hidden',
    }}>
      {/* Background glow */}
      <div style={{
        position: 'absolute',
        top: '30%', left: '50%',
        transform: 'translate(-50%, -50%)',
        width: '600px', height: '600px',
        background: 'radial-gradient(circle, rgba(255,77,0,0.05) 0%, transparent 65%)',
        pointerEvents: 'none',
      }} />

      <div style={{
        width: '100%',
        maxWidth: '22rem',
        position: 'relative',
      }}>
        {/* Logo */}
        <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
          <h1 className="font-display" style={{
            fontSize: '2.5rem',
            letterSpacing: '0.1em',
            color: 'var(--color-text)',
            marginBottom: '0.375rem',
          }}>
            GACHA<span style={{ color: 'var(--color-accent)' }}>HUB</span>
          </h1>
          <p className="section-label">管理者ログイン</p>
        </div>

        {/* Card */}
        <div className="card" style={{ padding: '2rem' }}>
          {error && (
            <div className="alert-error" style={{ marginBottom: '1.25rem' }}>{error}</div>
          )}

          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <div>
              <label className="input-label" htmlFor="email">メールアドレス</label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
                className="input-field"
                placeholder="admin@example.com"
              />
            </div>

            <div>
              <label className="input-label" htmlFor="password">パスワード</label>
              <input
                type="password"
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                required
                className="input-field"
                placeholder="••••••••"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="btn-primary"
              style={{ width: '100%', justifyContent: 'center', padding: '0.7rem', fontSize: '0.875rem', marginTop: '0.25rem' }}
            >
              {loading ? (
                <>
                  <span className="loader" style={{ width: '1rem', height: '1rem', borderWidth: '2px' }} />
                  ログイン中...
                </>
              ) : 'ログイン'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default AdminLogin;
