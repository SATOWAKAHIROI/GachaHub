import { useAuth } from '../context/AuthContext';

function Home() {
  const { user, isAuthenticated } = useAuth();

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="container mx-auto px-4 py-12">
        <div className="text-center mb-12">
          <h1 className="text-5xl font-bold text-gray-900 mb-4">GachaHub</h1>
          <p className="text-xl text-gray-700">ガチャガチャの最新情報をお届けします</p>
          {isAuthenticated && user && (
            <p className="mt-4 text-indigo-600 font-medium">
              ようこそ、{user.username}さん
            </p>
          )}
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div className="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition">
            <h2 className="text-2xl font-bold text-gray-800 mb-3">新着商品</h2>
            <p className="text-gray-600">最新のガチャガチャ商品をチェック</p>
          </div>

          <div className="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition">
            <h2 className="text-2xl font-bold text-gray-800 mb-3">人気商品</h2>
            <p className="text-gray-600">みんなが注目している商品</p>
          </div>

          <div className="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition">
            <h2 className="text-2xl font-bold text-gray-800 mb-3">メーカー別</h2>
            <p className="text-gray-600">バンダイ、タカラトミーなど</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Home;
