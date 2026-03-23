import type { Product } from '../types';

interface ProductCardProps {
  product: Product;
}

function ProductCard({ product }: ProductCardProps) {
  const manufacturerLabel = product.manufacturer === 'BANDAI' ? 'バンダイ' : 'タカラトミー';
  const manufacturerColor = product.manufacturer === 'BANDAI'
    ? { bg: 'rgba(255,77,0,0.08)', color: 'var(--color-accent)', border: 'rgba(255,77,0,0.2)' }
    : { bg: 'rgba(0,212,255,0.08)', color: 'var(--color-cyan)', border: 'rgba(0,212,255,0.2)' };

  return (
    <div style={{
      backgroundColor: 'var(--color-surface)',
      border: '1px solid var(--color-border)',
      borderRadius: '10px',
      overflow: 'hidden',
      transition: 'border-color 0.2s ease, transform 0.2s ease',
      display: 'flex',
      flexDirection: 'column',
    }}
      onMouseEnter={e => {
        (e.currentTarget as HTMLDivElement).style.borderColor = 'var(--color-accent)';
        (e.currentTarget as HTMLDivElement).style.transform = 'translateY(-3px)';
      }}
      onMouseLeave={e => {
        (e.currentTarget as HTMLDivElement).style.borderColor = 'var(--color-border)';
        (e.currentTarget as HTMLDivElement).style.transform = 'translateY(0)';
      }}
    >
      {/* Image */}
      <div style={{ position: 'relative', aspectRatio: '4/3', overflow: 'hidden', backgroundColor: 'var(--color-surface-2)' }}>
        {product.imageUrl ? (
          <img
            src={product.imageUrl}
            alt={product.productName}
            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
            onError={(e) => {
              (e.target as HTMLImageElement).style.display = 'none';
              (e.target as HTMLImageElement).nextElementSibling?.removeAttribute('style');
            }}
          />
        ) : null}
        <div style={{
          display: product.imageUrl ? 'none' : 'flex',
          width: '100%', height: '100%',
          alignItems: 'center', justifyContent: 'center',
          color: 'var(--color-text-dim)',
          fontSize: '0.75rem',
          position: 'absolute', inset: 0,
        }}>
          NO IMAGE
        </div>

        {/* Badges */}
        <div style={{ position: 'absolute', top: '0.6rem', left: '0.6rem', display: 'flex', gap: '0.375rem', flexWrap: 'wrap' }}>
          {product.isNew && (
            <span className="badge badge-new">NEW</span>
          )}
        </div>
        <div style={{ position: 'absolute', top: '0.6rem', right: '0.6rem' }}>
          <span className="badge" style={{
            backgroundColor: manufacturerColor.bg,
            color: manufacturerColor.color,
            border: `1px solid ${manufacturerColor.border}`,
          }}>
            {manufacturerLabel}
          </span>
        </div>
      </div>

      {/* Info */}
      <div style={{ padding: '0.875rem', display: 'flex', flexDirection: 'column', gap: '0.5rem', flex: 1 }}>
        <h3 style={{
          fontSize: '0.8rem',
          fontWeight: 600,
          color: 'var(--color-text)',
          lineHeight: 1.45,
          display: '-webkit-box',
          WebkitLineClamp: 2,
          WebkitBoxOrient: 'vertical',
          overflow: 'hidden',
          margin: 0,
        }}>
          {product.productName}
        </h3>

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', marginTop: 'auto' }}>
          <div>
            {product.price ? (
              <span style={{ fontFamily: 'var(--font-display)', fontSize: '1.4rem', color: 'var(--color-accent)', letterSpacing: '0.03em' }}>
                {product.price}<span style={{ fontSize: '0.7rem', fontFamily: 'var(--font-body)', color: 'var(--color-text-muted)', marginLeft: '0.15rem' }}>円</span>
              </span>
            ) : (
              <span style={{ fontSize: '0.72rem', color: 'var(--color-text-muted)' }}>価格未定</span>
            )}
          </div>
          {product.releaseDate && (
            <span style={{ fontSize: '0.68rem', color: 'var(--color-text-muted)' }}>{product.releaseDate}</span>
          )}
        </div>

        {product.sourceUrl && (
          <a
            href={product.sourceUrl}
            target="_blank"
            rel="noopener noreferrer"
            style={{
              display: 'block',
              textAlign: 'center',
              fontSize: '0.72rem',
              fontWeight: 700,
              letterSpacing: '0.06em',
              textTransform: 'uppercase',
              color: 'var(--color-text-muted)',
              border: '1px solid var(--color-border)',
              borderRadius: '5px',
              padding: '0.4rem',
              textDecoration: 'none',
              transition: 'color 0.15s, border-color 0.15s',
              marginTop: '0.25rem',
            }}
            onMouseEnter={e => {
              (e.currentTarget as HTMLAnchorElement).style.color = 'var(--color-accent)';
              (e.currentTarget as HTMLAnchorElement).style.borderColor = 'var(--color-accent)';
            }}
            onMouseLeave={e => {
              (e.currentTarget as HTMLAnchorElement).style.color = 'var(--color-text-muted)';
              (e.currentTarget as HTMLAnchorElement).style.borderColor = 'var(--color-border)';
            }}
          >
            詳細を見る →
          </a>
        )}
      </div>
    </div>
  );
}

export default ProductCard;
