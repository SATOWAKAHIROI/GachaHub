import type { Product } from '../types';

interface ProductCardProps {
  product: Product;
}

function ProductCard({ product }: ProductCardProps) {
  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
      {/* 商品画像 */}
      <div className="relative">
        {product.imageUrl ? (
          <img
            src={product.imageUrl}
            alt={product.productName}
            className="w-full h-48 object-cover"
            onError={(e) => {
              (e.target as HTMLImageElement).src = 'https://via.placeholder.com/300x200?text=No+Image';
            }}
          />
        ) : (
          <div className="w-full h-48 bg-gray-200 flex items-center justify-center">
            <span className="text-gray-400">画像なし</span>
          </div>
        )}

        {/* 新着バッジ */}
        {product.isNew && (
          <span className="absolute top-2 left-2 bg-red-500 text-white text-xs font-bold px-2 py-1 rounded">
            NEW
          </span>
        )}

        {/* メーカーバッジ */}
        <span className="absolute top-2 right-2 bg-indigo-600 text-white text-xs px-2 py-1 rounded">
          {product.manufacturer === 'BANDAI' ? 'バンダイ' : 'タカラトミー'}
        </span>
      </div>

      {/* 商品情報 */}
      <div className="p-4">
        <h3 className="text-sm font-semibold text-gray-800 mb-2 line-clamp-2">
          {product.productName}
        </h3>

        <div className="flex justify-between items-center mt-2">
          {product.price ? (
            <span className="text-lg font-bold text-indigo-600">{product.price}円</span>
          ) : (
            <span className="text-sm text-gray-400">価格未定</span>
          )}

          {product.releaseDate && (
            <span className="text-xs text-gray-500">{product.releaseDate}</span>
          )}
        </div>

        {/* 元サイトリンク */}
        {product.sourceUrl && (
          <a
            href={product.sourceUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="mt-3 block text-center text-sm text-indigo-600 hover:text-indigo-800 border border-indigo-600 rounded py-1 hover:bg-indigo-50 transition"
          >
            詳細を見る
          </a>
        )}
      </div>
    </div>
  );
}

export default ProductCard;
