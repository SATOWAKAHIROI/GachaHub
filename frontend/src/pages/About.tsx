function About() {
  return (
    <div style={{ minHeight: 'calc(100vh - 3.25rem)', padding: '3rem 1.5rem 5rem' }}>
      <div className="container mx-auto" style={{ maxWidth: '52rem' }}>

        {/* Header */}
        <div style={{ marginBottom: '3rem' }}>
          <p className="section-label" style={{ marginBottom: '0.5rem' }}>About</p>
          <h1 className="page-title" style={{ marginBottom: '1rem' }}>GachaHubについて</h1>
          <div className="accent-bar" />
        </div>

        {/* Description */}
        <div style={{
          backgroundColor: 'var(--color-surface)',
          border: '1px solid var(--color-border)',
          borderRadius: '10px',
          padding: '2rem',
          marginBottom: '1.5rem',
        }}>
          <p style={{ fontSize: '0.9rem', color: 'var(--color-text)', lineHeight: 1.8, margin: 0 }}>
            このアプリは、バンダイやタカラトミーなどのメーカー公式サイトから
            ガチャガチャの最新情報を自動収集し、Webアプリとして表示するシステムです。
            新商品情報をリアルタイムで追跡し、コレクターの皆さんに最新情報をお届けします。
          </p>
        </div>

        {/* Features */}
        <div style={{ marginBottom: '1.5rem' }}>
          <p className="section-label" style={{ marginBottom: '1rem' }}>主な機能</p>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            {[
              { icon: '⚡', text: 'メーカー公式サイトからの自動スクレイピング' },
              { icon: '🔔', text: '新商品の通知機能（メール）' },
              { icon: '🔍', text: 'メーカー別・価格別フィルタリング' },
              { icon: '📅', text: '発売日・更新日での並び替え' },
            ].map((item, i) => (
              <div key={i} style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.875rem',
                padding: '0.875rem 1rem',
                backgroundColor: 'var(--color-surface)',
                border: '1px solid var(--color-border)',
                borderRadius: '8px',
              }}>
                <span style={{ fontSize: '1rem' }}>{item.icon}</span>
                <span style={{ fontSize: '0.875rem', color: 'var(--color-text)' }}>{item.text}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Manufacturers */}
        <div>
          <p className="section-label" style={{ marginBottom: '1rem' }}>対応メーカー</p>
          <div style={{ display: 'flex', gap: '0.625rem', flexWrap: 'wrap' }}>
            <span className="badge badge-accent" style={{ padding: '0.4rem 1rem', fontSize: '0.75rem' }}>バンダイ</span>
            <span className="badge badge-cyan" style={{ padding: '0.4rem 1rem', fontSize: '0.75rem' }}>タカラトミーアーツ</span>
            <span style={{
              padding: '0.4rem 1rem', borderRadius: '999px', fontSize: '0.75rem', fontWeight: 700,
              textTransform: 'uppercase', letterSpacing: '0.06em',
              color: 'var(--color-text-dim)', border: '1px solid var(--color-border)',
            }}>
              その他追加予定
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}

export default About;
