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

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-gray-800 mb-6">商品一覧</h1>

      {/* フィルタ・検索バー */}
      <div className="bg-white rounded-lg shadow p-4 mb-6">
        <div className="flex flex-col gap-4">
          {/* メーカーフィルタ */}
          <div className="flex flex-wrap gap-2">
            <button
              onClick={() => handleManufacturerChange('')}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition ${
                manufacturer === '' ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              すべて
            </button>
            <button
              onClick={() => handleManufacturerChange('BANDAI')}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition ${
                manufacturer === 'BANDAI' ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              バンダイ
            </button>
            <button
              onClick={() => handleManufacturerChange('TAKARA_TOMY')}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition ${
                manufacturer === 'TAKARA_TOMY' ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              タカラトミー
            </button>
          </div>

          {/* キーワード検索 */}
          <form onSubmit={handleSearch} className="flex gap-2">
            <input
              type="text"
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
              placeholder="商品名で検索..."
              className="flex-1 min-w-0 border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <button
              type="submit"
              className="bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition whitespace-nowrap"
            >
              検索
            </button>
          </form>

          {/* ソート・クリア */}
          <div className="flex flex-col sm:flex-row sm:items-center gap-3">
          {/* ソート */}
          <div className="flex items-center gap-2 flex-1">
            <label className="text-sm text-gray-500 whitespace-nowrap">並び替え:</label>
            <select
              value={currentSortIndex}
              onChange={(e) => handleSortChange(e.target.value)}
              className="flex-1 sm:flex-none border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              {sortOptions.map((option, index) => (
                <option key={index} value={index}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>

          {/* フィルタクリア */}
          {(manufacturer || keyword || sortField !== 'createdAt' || sortDirection !== 'desc') && (
            <button
              onClick={handleClearFilters}
              className="text-sm text-gray-500 hover:text-gray-700 underline"
            >
              クリア
            </button>
          )}
          </div>
        </div>

        {/* 検索結果件数 */}
        <div className="mt-3 text-sm text-gray-500">
          {totalElements}件の商品が見つかりました
        </div>
      </div>

      {/* ローディング */}
      {loading && (
        <div className="flex justify-center items-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>
      )}

      {/* エラー */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      {/* 商品グリッド */}
      {!loading && !error && (
        <>
          {products.length === 0 ? (
            <div className="text-center py-12 text-gray-500">
              商品が見つかりませんでした。
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
              {products.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>
          )}

          {/* ページネーション */}
          {totalPages > 1 && (
            <div className="flex justify-center items-center gap-4 mt-8">
              <button
                onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
                disabled={currentPage === 0}
                className="px-4 py-2 rounded-lg bg-gray-100 text-gray-700 hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed transition"
              >
                前へ
              </button>

              <span className="text-sm text-gray-600">
                {currentPage + 1} / {totalPages} ページ
              </span>

              <button
                onClick={() => setCurrentPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={currentPage >= totalPages - 1}
                className="px-4 py-2 rounded-lg bg-gray-100 text-gray-700 hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed transition"
              >
                次へ
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default Products;
