import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Home() {
  const { user, isAuthenticated } = useAuth();

  return (
    <div style={{ minHeight: 'calc(100vh - 3.25rem)', backgroundColor: 'var(--color-bg)' }}>
      {/* Hero */}
      <section style={{
        padding: 'clamp(4rem, 10vw, 8rem) 1.5rem clamp(3rem, 8vw, 6rem)',
        position: 'relative',
        overflow: 'hidden',
        borderBottom: '1px solid var(--color-border)',
      }}>
        {/* Background decoration */}
        <div style={{
          position: 'absolute', top: '-20%', right: '-10%',
          width: 'clamp(300px, 50vw, 600px)', height: 'clamp(300px, 50vw, 600px)',
          background: 'radial-gradient(circle, rgba(255,77,0,0.06) 0%, transparent 70%)',
          pointerEvents: 'none',
        }} />
        <div style={{
          position: 'absolute', bottom: '-20%', left: '-5%',
          width: '300px', height: '300px',
          background: 'radial-gradient(circle, rgba(0,212,255,0.04) 0%, transparent 70%)',
          pointerEvents: 'none',
        }} />

        <div className="container mx-auto" style={{ maxWidth: '64rem', position: 'relative' }}>
          <p className="fade-up section-label" style={{ marginBottom: '1rem' }}>
            ガチャガチャ情報サービス
          </p>

          <h1 className="font-display fade-up-1" style={{
            fontSize: 'clamp(3.5rem, 12vw, 8rem)',
            lineHeight: 0.95,
            letterSpacing: '0.05em',
            marginBottom: '1.5rem',
            color: 'var(--color-text)',
          }}>
            GACHA<span style={{ color: 'var(--color-accent)' }}>HUB</span>
          </h1>

          <p className="fade-up-2" style={{
            fontSize: 'clamp(0.95rem, 2.5vw, 1.15rem)',
            color: 'var(--color-text-muted)',
            maxWidth: '36rem',
            lineHeight: 1.7,
            marginBottom: '2.5rem',
          }}>
            バンダイ・タカラトミーなどのメーカー公式サイトから
            ガチャガチャの最新情報を自動収集してお届けします。
          </p>

          {isAuthenticated && user && (
            <p className="fade-up-2" style={{ fontSize: '0.875rem', color: 'var(--color-accent)', marginBottom: '2rem' }}>
              ようこそ、{user.username} さん
            </p>
          )}

          <div className="fade-up-3" style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
            <Link to="/products" className="btn-primary" style={{ padding: '0.75rem 1.75rem', fontSize: '0.875rem' }}>
              商品一覧を見る →
            </Link>
            <Link to="/about" className="btn-secondary" style={{ padding: '0.75rem 1.75rem', fontSize: '0.875rem' }}>
              サービスについて
            </Link>
          </div>
        </div>
      </section>

      {/* Features */}
      <section style={{ padding: 'clamp(2.5rem, 6vw, 4rem) 1.5rem' }}>
        <div className="container mx-auto" style={{ maxWidth: '64rem' }}>
          <p className="section-label" style={{ marginBottom: '2rem' }}>主な機能</p>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '1px', backgroundColor: 'var(--color-border)', borderRadius: '10px', overflow: 'hidden' }}>
            {[
              {
                icon: '⚡',
                title: '自動スクレイピング',
                desc: 'バンダイ・タカラトミー公式サイトから定期的に最新情報を自動取得',
              },
              {
                icon: '🔔',
                title: 'メール通知',
                desc: '新着商品が見つかった際に登録メールアドレスへ自動通知',
              },
              {
                icon: '🔍',
                title: '検索・フィルタ',
                desc: 'メーカー別・価格別・発売日別のフィルタリングと全文検索',
              },
            ].map((item, i) => (
              <div key={i} style={{
                backgroundColor: 'var(--color-surface)',
                padding: '2rem',
              }}>
                <div style={{ fontSize: '1.5rem', marginBottom: '1rem' }}>{item.icon}</div>
                <h3 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'var(--color-text)', marginBottom: '0.5rem' }}>
                  {item.title}
                </h3>
                <p style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)', lineHeight: 1.65 }}>
                  {item.desc}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Manufacturers */}
      <section style={{ padding: '0 1.5rem clamp(2.5rem, 6vw, 4rem)' }}>
        <div className="container mx-auto" style={{ maxWidth: '64rem' }}>
          <div style={{
            backgroundColor: 'var(--color-surface)',
            border: '1px solid var(--color-border)',
            borderRadius: '10px',
            padding: '1.5rem 2rem',
            display: 'flex',
            flexWrap: 'wrap',
            alignItems: 'center',
            gap: '1rem',
            justifyContent: 'space-between',
          }}>
            <p style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)', margin: 0 }}>
              対応メーカー
            </p>
            <div style={{ display: 'flex', gap: '0.625rem', flexWrap: 'wrap' }}>
              <span className="badge badge-accent" style={{ padding: '0.3rem 0.875rem', fontSize: '0.72rem' }}>バンダイ</span>
              <span className="badge badge-cyan" style={{ padding: '0.3rem 0.875rem', fontSize: '0.72rem' }}>タカラトミーアーツ</span>
              <span style={{
                padding: '0.3rem 0.875rem',
                borderRadius: '999px',
                fontSize: '0.72rem',
                fontWeight: 700,
                textTransform: 'uppercase',
                letterSpacing: '0.06em',
                color: 'var(--color-text-dim)',
                border: '1px solid var(--color-border)',
              }}>順次追加予定</span>
            </div>
          </div>
        </div>
      </section>

      {/* Contact */}
      <section style={{ padding: '0 1.5rem clamp(3rem, 6vw, 5rem)', borderTop: '1px solid var(--color-border)' }}>
        <div className="container mx-auto" style={{ maxWidth: '64rem', paddingTop: '2rem' }}>
          <p style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>
            新しいサイトの追加などご要望は{' '}
            <a
              href="mailto:hitobussi@gmail.com"
              style={{ color: 'var(--color-accent)', textDecoration: 'none', fontWeight: 600 }}
            >
              hitobussi@gmail.com
            </a>
            {' '}までお問い合わせください。
          </p>
        </div>
      </section>
    </div>
  );
}

export default Home;
