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

  useEffect(() => { fetchLogs(); }, [limit]);

  const siteDisplayName = (site: string) => {
    switch (site) {
      case 'BANDAI_GASHAPON': return 'バンダイ';
      case 'TAKARA_TOMY_ARTS': return 'タカラトミーアーツ';
      default: return site;
    }
  };

  const successCount = logs.filter(l => l.status === 'SUCCESS').length;
  const failCount = logs.filter(l => l.status === 'FAILURE').length;

  return (
    <div style={{ minHeight: 'calc(100vh - 3.25rem)', padding: '2rem 1.5rem 4rem' }}>
      <div className="container mx-auto" style={{ maxWidth: '72rem' }}>

        {/* Breadcrumb */}
        <div className="breadcrumb">
          <Link to="/admin">ダッシュボード</Link>
          <span>/</span>
          <span style={{ color: 'var(--color-text)' }}>スクレイピング履歴</span>
        </div>

        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '2rem', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <p className="section-label" style={{ marginBottom: '0.375rem' }}>Admin</p>
            <h1 className="page-title">スクレイピング履歴</h1>
          </div>
          <div style={{ display: 'flex', gap: '0.625rem', alignItems: 'center' }}>
            <select
              value={limit}
              onChange={(e) => setLimit(Number(e.target.value))}
              className="input-field"
              style={{ width: 'auto' }}
            >
              <option value={10}>10件</option>
              <option value={20}>20件</option>
              <option value={50}>50件</option>
              <option value={100}>100件</option>
            </select>
            <button onClick={fetchLogs} className="btn-secondary">
              更新
            </button>
          </div>
        </div>

        {error && <div className="alert-error" style={{ marginBottom: '1.5rem' }}>{error}</div>}

        {loading ? (
          <div style={{ display: 'flex', justifyContent: 'center', padding: '5rem 0' }}>
            <div className="loader" />
          </div>
        ) : logs.length === 0 ? (
          <div className="card" style={{ padding: '3rem', textAlign: 'center', color: 'var(--color-text-dim)' }}>
            スクレイピング履歴がありません。
          </div>
        ) : (
          <>
            {/* Summary */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '1px', backgroundColor: 'var(--color-border)', borderRadius: '10px', overflow: 'hidden', marginBottom: '1.5rem' }}>
              {[
                { label: '合計実行回数', value: logs.length, suffix: '回', accent: false },
                { label: '成功', value: successCount, suffix: '回', accent: false, successColor: true },
                { label: '失敗', value: failCount, suffix: '回', accent: false, errorColor: true },
              ].map((item, i) => (
                <div key={i} style={{ backgroundColor: 'var(--color-surface)', padding: '1.25rem' }}>
                  <p className="section-label" style={{ marginBottom: '0.5rem' }}>{item.label}</p>
                  <p style={{
                    fontFamily: 'var(--font-display)',
                    fontSize: '2.25rem',
                    lineHeight: 1,
                    margin: 0,
                    color: item.successColor ? 'var(--color-success)' : item.errorColor ? 'var(--color-error)' : 'var(--color-text)',
                  }}>
                    {item.value}
                    <span style={{ fontSize: '0.9rem', fontFamily: 'var(--font-body)', color: 'var(--color-text-muted)', marginLeft: '0.25rem' }}>
                      {item.suffix}
                    </span>
                  </p>
                </div>
              ))}
            </div>

            {/* Table */}
            <div className="card" style={{ overflow: 'hidden' }}>
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
                    {logs.map((log) => (
                      <tr key={log.id}>
                        <td className="muted" style={{ whiteSpace: 'nowrap' }}>
                          {new Date(log.executedAt).toLocaleString('ja-JP')}
                        </td>
                        <td style={{ fontWeight: 600 }}>{siteDisplayName(log.targetSite)}</td>
                        <td>
                          <span className={`badge ${log.status === 'SUCCESS' ? 'badge-success' : 'badge-error'}`}>
                            {log.status === 'SUCCESS' ? '成功' : '失敗'}
                          </span>
                        </td>
                        <td style={{ color: 'var(--color-accent)', fontWeight: 700 }}>
                          {log.productsFound}
                        </td>
                        <td>
                          {log.errorMessage ? (
                            <div>
                              <button
                                onClick={() => setExpandedError(expandedError === log.id ? null : log.id)}
                                style={{
                                  color: 'var(--color-error)',
                                  fontSize: '0.7rem',
                                  fontWeight: 600,
                                  textDecoration: 'underline',
                                  background: 'none',
                                  border: 'none',
                                  cursor: 'pointer',
                                  padding: 0,
                                }}
                              >
                                {expandedError === log.id ? '閉じる' : 'エラー詳細'}
                              </button>
                              {expandedError === log.id && (
                                <div style={{
                                  marginTop: '0.5rem',
                                  padding: '0.625rem',
                                  backgroundColor: 'rgba(255,68,68,0.06)',
                                  border: '1px solid rgba(255,68,68,0.15)',
                                  borderRadius: '5px',
                                  fontSize: '0.7rem',
                                  color: 'var(--color-error)',
                                  maxWidth: '280px',
                                  wordBreak: 'break-word',
                                }}>
                                  {log.errorMessage}
                                </div>
                              )}
                            </div>
                          ) : (
                            <span style={{ color: 'var(--color-text-dim)' }}>—</span>
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
    </div>
  );
}

export default AdminLogs;
