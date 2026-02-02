import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route, Link, useNavigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Home from './pages/Home'
import About from './pages/About'
import Login from './pages/Login'
import AdminLogin from './pages/AdminLogin'
import Products from './pages/Products'
import AdminDashboard from './pages/AdminDashboard'
import AdminScrape from './pages/AdminScrape'
import AdminLogs from './pages/AdminLogs'
import AdminSites from './pages/AdminSites'
import AdminUsers from './pages/AdminUsers'
import AdminProtectedRoute from './components/AdminProtectedRoute'

function Navigation() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const isAdmin = isAuthenticated && user?.role === 'ADMIN';

  const handleLogout = () => {
    logout();
    setMenuOpen(false);
    navigate('/');
  };

  const closeMenu = () => setMenuOpen(false);

  return (
    <nav className="bg-white shadow-lg">
      <div className="container mx-auto px-4">
        <div className="flex justify-between items-center h-16">
          {/* ロゴ・サイト名 */}
          <Link to="/" className="text-xl font-bold text-indigo-600" onClick={closeMenu}>
            GachaHub
          </Link>

          {/* デスクトップナビ */}
          <div className="hidden md:flex space-x-6">
            <Link to="/" className="text-gray-700 hover:text-indigo-600 font-medium transition">
              ホーム
            </Link>
            <Link to="/products" className="text-gray-700 hover:text-indigo-600 font-medium transition">
              商品一覧
            </Link>
            <Link to="/about" className="text-gray-700 hover:text-indigo-600 font-medium transition">
              このサイトについて
            </Link>
            {isAdmin && (
              <Link to="/admin" className="text-gray-700 hover:text-indigo-600 font-medium transition">
                管理画面
              </Link>
            )}
          </div>

          {/* デスクトップ認証ボタン */}
          <div className="hidden md:flex items-center space-x-4">
            {isAuthenticated && user ? (
              <>
                <span className="text-gray-700 text-sm">
                  {user.username}
                  {isAdmin && (
                    <span className="ml-1 text-xs bg-purple-100 text-purple-800 px-2 py-0.5 rounded-full">
                      管理者
                    </span>
                  )}
                </span>
                <button
                  onClick={handleLogout}
                  className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition text-sm"
                >
                  ログアウト
                </button>
              </>
            ) : (
              <Link
                to="/login"
                className="text-gray-700 hover:text-indigo-600 font-medium transition"
              >
                ログイン
              </Link>
            )}
          </div>

          {/* モバイルハンバーガーボタン */}
          <button
            onClick={() => setMenuOpen(!menuOpen)}
            className="md:hidden p-2 rounded-lg text-gray-700 hover:bg-gray-100 transition"
            aria-label="メニュー"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              {menuOpen ? (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              ) : (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              )}
            </svg>
          </button>
        </div>

        {/* モバイルメニュー */}
        {menuOpen && (
          <div className="md:hidden border-t border-gray-200 py-3">
            <div className="flex flex-col space-y-2">
              <Link to="/" onClick={closeMenu} className="text-gray-700 hover:text-indigo-600 font-medium py-2 transition">
                ホーム
              </Link>
              <Link to="/products" onClick={closeMenu} className="text-gray-700 hover:text-indigo-600 font-medium py-2 transition">
                商品一覧
              </Link>
              <Link to="/about" onClick={closeMenu} className="text-gray-700 hover:text-indigo-600 font-medium py-2 transition">
                このサイトについて
              </Link>
              {isAdmin && (
                <Link to="/admin" onClick={closeMenu} className="text-gray-700 hover:text-indigo-600 font-medium py-2 transition">
                  管理画面
                </Link>
              )}

              <div className="border-t border-gray-200 pt-2 mt-2">
                {isAuthenticated && user ? (
                  <div className="flex flex-col space-y-2">
                    <span className="text-gray-500 text-sm">
                      {user.username}
                      {isAdmin && ' (管理者)'}
                    </span>
                    <button
                      onClick={handleLogout}
                      className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition text-sm text-center"
                    >
                      ログアウト
                    </button>
                  </div>
                ) : (
                  <Link to="/login" onClick={closeMenu} className="text-gray-700 hover:text-indigo-600 font-medium py-2 transition">
                    ログイン
                  </Link>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </nav>
  );
}

function AppContent() {
  return (
    <div className="app">
      <Navigation />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/products" element={<Products />} />
          <Route path="/about" element={<About />} />
          <Route path="/login" element={<Login />} />
          <Route path="/admin/login" element={<AdminLogin />} />
          <Route path="/admin" element={<AdminProtectedRoute><AdminDashboard /></AdminProtectedRoute>} />
          <Route path="/admin/scrape" element={<AdminProtectedRoute><AdminScrape /></AdminProtectedRoute>} />
          <Route path="/admin/logs" element={<AdminProtectedRoute><AdminLogs /></AdminProtectedRoute>} />
          <Route path="/admin/sites" element={<AdminProtectedRoute><AdminSites /></AdminProtectedRoute>} />
          <Route path="/admin/users" element={<AdminProtectedRoute><AdminUsers /></AdminProtectedRoute>} />
        </Routes>
      </main>
    </div>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </Router>
  )
}

export default App
