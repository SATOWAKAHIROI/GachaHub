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
    { value: 'BANDAI', label: 'バンダイ', sub: 'ガシャポン公式サイト' },
    { value: 'TAKARA_TOMY', label: 'タカラトミーアーツ', sub: '公式ガチャサイト' },
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
    <div style={{ minHeight: 'calc(100vh - 3.25rem)', padding: '2rem 1.5rem 4rem' }}>
      <div className="container mx-auto" style={{ maxWidth: '48rem' }}>

        {/* Breadcrumb */}
        <div className="breadcrumb">
          <Link to="/admin">ダッシュボード</Link>
          <span>/</span>
          <span style={{ color: 'var(--color-text)' }}>手動スクレイピング</span>
        </div>

        {/* Header */}
        <div style={{ marginBottom: '2rem' }}>
          <p className="section-label" style={{ marginBottom: '0.375rem' }}>Admin</p>
          <h1 className="page-title">手動スクレイピング</h1>
        </div>

        {/* Site select */}
        <div className="card" style={{ padding: '1.5rem', marginBottom: '1rem' }}>
          <p className="section-label" style={{ marginBottom: '1rem' }}>対象サイトを選択</p>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.625rem' }}>
            {sites.map((site) => (
              <button
                key={site.value}
                onClick={() => setSelectedSite(site.value)}
                disabled={loading}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '1rem',
                  padding: '1rem 1.25rem',
                  borderRadius: '8px',
                  border: `1px solid ${selectedSite === site.value ? 'var(--color-accent)' : 'var(--color-border)'}`,
                  backgroundColor: selectedSite === site.value ? 'var(--color-accent-dim)' : 'var(--color-surface-2)',
                  cursor: loading ? 'not-allowed' : 'pointer',
                  opacity: loading ? 0.5 : 1,
                  transition: 'all 0.15s ease',
                  textAlign: 'left',
                }}
              >
                <div style={{
                  width: '10px', height: '10px', borderRadius: '50%',
                  flexShrink: 0,
                  backgroundColor: selectedSite === site.value ? 'var(--color-accent)' : 'var(--color-border)',
                  boxShadow: selectedSite === site.value ? '0 0 8px var(--color-accent)' : 'none',
                  transition: 'all 0.15s',
                }} />
                <div>
                  <p style={{ margin: 0, fontSize: '0.875rem', fontWeight: 700, color: selectedSite === site.value ? 'var(--color-accent)' : 'var(--color-text)' }}>
                    {site.label}
                  </p>
                  <p style={{ margin: 0, fontSize: '0.72rem', color: 'var(--color-text-muted)' }}>
                    {site.sub}
                  </p>
                </div>
              </button>
            ))}
          </div>
        </div>

        {/* Execute */}
        <div className="card" style={{ padding: '1.5rem', marginBottom: '1rem' }}>
          <button
            onClick={handleExecute}
            disabled={!selectedSite || loading}
            className="btn-primary"
            style={{ padding: '0.75rem 2rem', fontSize: '0.875rem' }}
          >
            {loading ? (
              <>
                <span className="loader" style={{ width: '1rem', height: '1rem', borderWidth: '2px' }} />
                実行中...
              </>
            ) : '実行する →'}
          </button>
          {!selectedSite && !loading && (
            <p style={{ fontSize: '0.75rem', color: 'var(--color-text-dim)', marginTop: '0.75rem', marginBottom: 0 }}>
              サイトを選択してから実行してください
            </p>
          )}
        </div>

        {/* Running */}
        {loading && (
          <div className="alert-info" style={{ marginBottom: '1rem' }}>
            <p style={{ fontWeight: 700, margin: '0 0 0.25rem' }}>スクレイピング実行中...</p>
            <p style={{ margin: 0, opacity: 0.8, fontSize: '0.8rem' }}>
              対象サイトにアクセスしています。完了までしばらくお待ちください。
            </p>
          </div>
        )}

        {/* Result */}
        {result && (
          <div className={result.status === 'success' ? 'alert-success' : 'alert-error'} style={{ marginBottom: '1rem' }}>
            <p style={{ fontWeight: 700, margin: '0 0 0.25rem' }}>
              {result.status === 'success' ? '✓ 実行完了' : '✕ 実行失敗'}
            </p>
            <p style={{ margin: 0, opacity: 0.85, fontSize: '0.8rem' }}>{result.message}</p>
            {result.totalProducts !== undefined && (
              <div style={{ display: 'flex', gap: '1.5rem', marginTop: '1rem', flexWrap: 'wrap' }}>
                <div>
                  <span style={{ fontFamily: 'var(--font-display)', fontSize: '2rem', lineHeight: 1 }}>
                    {result.totalProducts}
                  </span>
                  <span style={{ fontSize: '0.75rem', marginLeft: '0.3rem', opacity: 0.8 }}>件取得</span>
                </div>
                {result.newProducts !== undefined && result.newProducts > 0 && (
                  <div>
                    <span style={{ fontFamily: 'var(--font-display)', fontSize: '2rem', lineHeight: 1, color: 'var(--color-accent)' }}>
                      {result.newProducts}
                    </span>
                    <span style={{ fontSize: '0.75rem', marginLeft: '0.3rem', opacity: 0.8 }}>件新着</span>
                  </div>
                )}
                {result.newProducts === 0 && (
                  <span style={{ fontSize: '0.75rem', opacity: 0.7, alignSelf: 'flex-end', paddingBottom: '0.25rem' }}>新着なし</span>
                )}
              </div>
            )}
          </div>
        )}

        {/* Error */}
        {error && (
          <div className="alert-error">{error}</div>
        )}
      </div>
    </div>
  );
}

export default AdminScrape;
