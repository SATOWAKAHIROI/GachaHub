import { useState, useEffect } from 'react';
import type { Product, PageResponse } from '../types';
import { getProducts } from '../services/productApi';
import ProductCard from '../components/ProductCard';

function Products() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [manufacturer, setManufacturer] = useState<string>('');
  const [keyword, setKeyword] = useState<string>('');
  const [searchInput, setSearchInput] = useState<string>('');
  const [sortField, setSortField] = useState<string>('createdAt');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('desc');

  const PAGE_SIZE = 12;

  const sortOptions = [
    { label: '新着順', field: 'createdAt', direction: 'desc' as const },
    { label: '古い順', field: 'createdAt', direction: 'asc' as const },
    { label: '発売日（新しい順）', field: 'releaseDate', direction: 'desc' as const },
    { label: '発売日（古い順）', field: 'releaseDate', direction: 'asc' as const },
    { label: '価格（安い順）', field: 'price', direction: 'asc' as const },
    { label: '価格（高い順）', field: 'price', direction: 'desc' as const },
    { label: '商品名（A→Z）', field: 'productName', direction: 'asc' as const },
    { label: '商品名（Z→A）', field: 'productName', direction: 'desc' as const },
  ];

  const fetchProducts = async () => {
    setLoading(true);
    setError(null);
    try {
      const params: Record<string, string | number> = {
        page: currentPage,
        size: PAGE_SIZE,
        sort: sortField,
        direction: sortDirection,
      };
      if (manufacturer) params.manufacturer = manufacturer;
      if (keyword) params.keyword = keyword;

      const data: PageResponse<Product> = await getProducts(params);
      setProducts(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch {
      setError('商品データの取得に失敗しました。');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, [currentPage, manufacturer, keyword, sortField, sortDirection]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(0);
    setKeyword(searchInput);
  };

  const handleManufacturerChange = (value: string) => {
    setCurrentPage(0);
    setManufacturer(value);
  };

  const handleSortChange = (value: string) => {
    const option = sortOptions[Number(value)];
    if (option) {
      setCurrentPage(0);
      setSortField(option.field);
      setSortDirection(option.direction);
    }
  };

  const currentSortIndex = sortOptions.findIndex(
    (o) => o.field === sortField && o.direction === sortDirection
  );

  const handleClearFilters = () => {
    setCurrentPage(0);
    setManufacturer('');
    setKeyword('');
    setSearchInput('');
    setSortField('createdAt');
    setSortDirection('desc');
  };

  const hasActiveFilters = manufacturer || keyword || sortField !== 'createdAt' || sortDirection !== 'desc';

  return (
    <div style={{ minHeight: 'calc(100vh - 3.25rem)', padding: '2rem 1.5rem 4rem' }}>
      <div className="container mx-auto" style={{ maxWidth: '80rem' }}>

        {/* Header */}
        <div style={{ marginBottom: '2rem' }}>
          <p className="section-label" style={{ marginBottom: '0.375rem' }}>カタログ</p>
          <h1 className="page-title">商品一覧</h1>
        </div>

        {/* Filter bar */}
        <div style={{
          backgroundColor: 'var(--color-surface)',
          border: '1px solid var(--color-border)',
          borderRadius: '10px',
          padding: '1.25rem',
          marginBottom: '1.5rem',
          display: 'flex',
          flexDirection: 'column',
          gap: '1rem',
        }}>
          {/* Manufacturer pills */}
          <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', alignItems: 'center' }}>
            <span className="section-label" style={{ marginRight: '0.25rem' }}>メーカー:</span>
            {[
              { value: '', label: 'すべて' },
              { value: 'BANDAI', label: 'バンダイ' },
              { value: 'TAKARA_TOMY', label: 'タカラトミー' },
            ].map((item) => (
              <button
                key={item.value}
                onClick={() => handleManufacturerChange(item.value)}
                className={`filter-pill ${manufacturer === item.value ? 'active' : ''}`}
              >
                {item.label}
              </button>
            ))}
          </div>

          {/* Search + Sort row */}
          <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap', alignItems: 'center' }}>
            <form onSubmit={handleSearch} style={{ display: 'flex', gap: '0.5rem', flex: 1, minWidth: '200px' }}>
              <input
                type="text"
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                placeholder="商品名で検索..."
                className="input-field"
                style={{ flex: 1 }}
              />
              <button type="submit" className="btn-primary" style={{ whiteSpace: 'nowrap' }}>
                検索
              </button>
            </form>

            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <span className="section-label">並び替え:</span>
              <select
                value={currentSortIndex}
                onChange={(e) => handleSortChange(e.target.value)}
                className="input-field"
                style={{ width: 'auto' }}
              >
                {sortOptions.map((option, index) => (
                  <option key={index} value={index}>{option.label}</option>
                ))}
              </select>
            </div>

            {hasActiveFilters && (
              <button onClick={handleClearFilters} className="btn-secondary" style={{ padding: '0.4rem 0.875rem', whiteSpace: 'nowrap' }}>
                ✕ クリア
              </button>
            )}
          </div>

          {/* Count */}
          <p style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)', margin: 0 }}>
            <span style={{ color: 'var(--color-accent)', fontWeight: 700 }}>{totalElements}</span> 件の商品
          </p>
        </div>

        {/* Loading */}
        {loading && (
          <div style={{ display: 'flex', justifyContent: 'center', padding: '5rem 0' }}>
            <div className="loader" />
          </div>
        )}

        {/* Error */}
        {error && (
          <div className="alert-error" style={{ marginBottom: '1.5rem' }}>{error}</div>
        )}

        {/* Grid */}
        {!loading && !error && (
          <>
            {products.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '5rem 0', color: 'var(--color-text-muted)', fontSize: '0.875rem' }}>
                商品が見つかりませんでした。
              </div>
            ) : (
              <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
                gap: '1rem',
              }}>
                {products.map((product) => (
                  <ProductCard key={product.id} product={product} />
                ))}
              </div>
            )}

            {/* Pagination */}
            {totalPages > 1 && (
              <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '1rem', marginTop: '3rem' }}>
                <button
                  onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
                  disabled={currentPage === 0}
                  className="btn-secondary"
                >
                  ← 前へ
                </button>
                <span style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>
                  <span style={{ color: 'var(--color-text)', fontWeight: 600 }}>{currentPage + 1}</span>
                  {' / '}{totalPages}
                </span>
                <button
                  onClick={() => setCurrentPage((p) => Math.min(totalPages - 1, p + 1))}
                  disabled={currentPage >= totalPages - 1}
                  className="btn-secondary"
                >
                  次へ →
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

export default Products;
