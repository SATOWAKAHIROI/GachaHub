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
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 'calc(100vh - 3.25rem)' }}>
        <div className="loader" />
      </div>
    );
  }

  const menuItems = [
    {
      to: '/admin/scrape',
      label: '手動スクレイピング',
      desc: 'バンダイ・タカラトミーのデータを今すぐ取得',
      icon: '⚡',
    },
    {
      to: '/admin/logs',
      label: 'スクレイピング履歴',
      desc: '過去の実行ログと成否を確認',
      icon: '📋',
    },
    {
      to: '/admin/users',
      label: 'ユーザー管理',
      desc: 'ユーザーの作成・編集・削除',
      icon: '👤',
    },
  ];

  return (
    <div style={{ minHeight: 'calc(100vh - 3.25rem)', padding: '2rem 1.5rem 4rem' }}>
      <div className="container mx-auto" style={{ maxWidth: '72rem' }}>

        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '2rem', flexWrap: 'wrap', gap: '0.5rem' }}>
          <div>
            <p className="section-label" style={{ marginBottom: '0.375rem' }}>Admin</p>
            <h1 className="page-title">ダッシュボード</h1>
          </div>
          <span style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)', paddingTop: '0.5rem' }}>
            {user?.username}
          </span>
        </div>

        {error && <div className="alert-error" style={{ marginBottom: '1.5rem' }}>{error}</div>}

        {/* Stats */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1px', backgroundColor: 'var(--color-border)', borderRadius: '10px', overflow: 'hidden', marginBottom: '2rem' }}>
          {/* Status */}
          <div style={{ backgroundColor: 'var(--color-surface)', padding: '1.5rem' }}>
            <p className="section-label" style={{ marginBottom: '0.75rem' }}>スクレイピング状態</p>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.625rem', marginBottom: '0.5rem' }}>
              <span style={{
                width: '8px', height: '8px', borderRadius: '50%',
                backgroundColor: status?.available ? 'var(--color-success)' : 'var(--color-error)',
                boxShadow: status?.available ? '0 0 6px var(--color-success)' : '0 0 6px var(--color-error)',
                flexShrink: 0,
              }} />
              <span style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--color-text)' }}>
                {status?.available ? '稼働中' : '停止中'}
              </span>
            </div>
            {status?.lastExecution && (
              <p style={{ fontSize: '0.7rem', color: 'var(--color-text-muted)' }}>
                最終実行: {new Date(status.lastExecution).toLocaleString('ja-JP')}
              </p>
            )}
            {status?.lastStatus && (
              <span className={`badge ${status.lastStatus === 'SUCCESS' ? 'badge-success' : 'badge-error'}`} style={{ marginTop: '0.375rem' }}>
                {status.lastStatus}
              </span>
            )}
          </div>

          {/* Sites count */}
          <div style={{ backgroundColor: 'var(--color-surface)', padding: '1.5rem' }}>
            <p className="section-label" style={{ marginBottom: '0.75rem' }}>登録サイト数</p>
            <p className="stat-num-accent">{configs.length}</p>
            <p style={{ fontSize: '0.7rem', color: 'var(--color-text-muted)', marginTop: '0.5rem' }}>
              有効: {configs.filter(c => c.isEnabled).length} / {configs.length}
            </p>
          </div>

          {/* Latest result */}
          <div style={{ backgroundColor: 'var(--color-surface)', padding: '1.5rem' }}>
            <p className="section-label" style={{ marginBottom: '0.75rem' }}>直近の取得件数</p>
            {recentLogs.length > 0 ? (
              <>
                <p className="stat-num">{recentLogs[0].productsFound}<span style={{ fontSize: '1rem', fontFamily: 'var(--font-body)', color: 'var(--color-text-muted)', marginLeft: '0.25rem' }}>件</span></p>
                <p style={{ fontSize: '0.7rem', color: 'var(--color-text-muted)', marginTop: '0.5rem' }}>
                  {recentLogs[0].targetSite} — {new Date(recentLogs[0].executedAt).toLocaleString('ja-JP')}
                </p>
              </>
            ) : (
              <p style={{ fontSize: '0.875rem', color: 'var(--color-text-dim)' }}>データなし</p>
            )}
          </div>
        </div>

        {/* Menu */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '0.75rem', marginBottom: '2rem' }}>
          {menuItems.map((item) => (
            <Link key={item.to} to={item.to} style={{ textDecoration: 'none' }}>
              <div className="card-interactive" style={{ padding: '1.5rem' }}>
                <div style={{ fontSize: '1.25rem', marginBottom: '0.75rem' }}>{item.icon}</div>
                <h3 style={{ fontSize: '0.875rem', fontWeight: 700, color: 'var(--color-text)', marginBottom: '0.375rem' }}>
                  {item.label}
                </h3>
                <p style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)', lineHeight: 1.55, margin: 0 }}>
                  {item.desc}
                </p>
              </div>
            </Link>
          ))}
        </div>

        {/* Recent logs */}
        <div className="card">
          <div style={{ padding: '1.25rem 1.5rem', borderBottom: '1px solid var(--color-border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h2 style={{ fontSize: '0.875rem', fontWeight: 700, color: 'var(--color-text)', margin: 0 }}>最近のスクレイピングログ</h2>
            <Link to="/admin/logs" style={{ fontSize: '0.72rem', color: 'var(--color-accent)', textDecoration: 'none', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.06em' }}>
              すべて見る →
            </Link>
          </div>

          {recentLogs.length === 0 ? (
            <p style={{ textAlign: 'center', padding: '2.5rem', fontSize: '0.875rem', color: 'var(--color-text-dim)' }}>
              ログがありません。
            </p>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>実行日時</th>
                    <th>対象サイト</th>
                    <th>ステータス</th>
                    <th>取得件数</th>
                    <th>エラー</th>
                  </tr>
                </thead>
                <tbody>
                  {recentLogs.map((log) => (
                    <tr key={log.id}>
                      <td className="muted">{new Date(log.executedAt).toLocaleString('ja-JP')}</td>
                      <td>{log.targetSite}</td>
                      <td>
                        <span className={`badge ${log.status === 'SUCCESS' ? 'badge-success' : 'badge-error'}`}>
                          {log.status === 'SUCCESS' ? '成功' : '失敗'}
                        </span>
                      </td>
                      <td style={{ color: 'var(--color-accent)', fontWeight: 600 }}>{log.productsFound}</td>
                      <td className="muted" style={{ fontSize: '0.7rem', maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {log.errorMessage || '—'}
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

export default AdminDashboard;
