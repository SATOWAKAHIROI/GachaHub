import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route, Link, useNavigate, useLocation } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Home from './pages/Home'
import About from './pages/About'
import AdminLogin from './pages/AdminLogin'
import Products from './pages/Products'
import AdminDashboard from './pages/AdminDashboard'
import AdminScrape from './pages/AdminScrape'
import AdminLogs from './pages/AdminLogs'
import AdminUsers from './pages/AdminUsers'
import AdminUserDetail from './pages/AdminUserDetail'
import Profile from './pages/Profile'
import AdminProtectedRoute from './components/AdminProtectedRoute'

function Navigation() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);

  const isAdmin = isAuthenticated && user?.role === 'ADMIN';

  const handleLogout = () => {
    logout();
    setMenuOpen(false);
    navigate('/');
  };

  const closeMenu = () => setMenuOpen(false);

  const isActive = (path: string) =>
    path === '/' ? location.pathname === '/' : location.pathname.startsWith(path);

  return (
    <nav style={{
      backgroundColor: 'var(--color-surface)',
      borderBottom: '1px solid var(--color-border)',
      position: 'sticky',
      top: 0,
      zIndex: 50,
    }}>
      <div className="container mx-auto px-4 md:px-6">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', height: '3.25rem' }}>

          {/* Logo */}
          <Link to="/" onClick={closeMenu} style={{ textDecoration: 'none' }}>
            <span className="font-display" style={{ fontSize: '1.5rem', letterSpacing: '0.1em', color: 'var(--color-text)' }}>
              GACHA<span style={{ color: 'var(--color-accent)' }}>HUB</span>
            </span>
          </Link>

          {/* Desktop Nav Links */}
          <div className="hidden md:flex" style={{ gap: '2rem', alignItems: 'center' }}>
            <Link to="/" className={`nav-link ${isActive('/') ? 'active' : ''}`}>ホーム</Link>
            <Link to="/products" className={`nav-link ${isActive('/products') ? 'active' : ''}`}>商品一覧</Link>
            <Link to="/about" className={`nav-link ${isActive('/about') ? 'active' : ''}`}>このサイトについて</Link>
            {isAdmin && (
              <Link to="/admin" className={`nav-link ${isActive('/admin') ? 'active' : ''}`}>管理画面</Link>
            )}
          </div>

          {/* Desktop Auth */}
          <div className="hidden md:flex" style={{ alignItems: 'center', gap: '0.75rem' }}>
            {isAdmin && user ? (
              <>
                <Link to="/profile" style={{
                  display: 'flex', alignItems: 'center', gap: '0.5rem',
                  textDecoration: 'none', color: 'var(--color-text)',
                  fontSize: '0.8rem', fontWeight: 500,
                }}>
                  {user.username}
                  <span className="badge badge-purple">管理者</span>
                </Link>
                <button onClick={handleLogout} className="btn-secondary" style={{ padding: '0.35rem 0.875rem' }}>
                  ログアウト
                </button>
              </>
            ) : !isAuthenticated ? (
              <Link to="/admin/login" className="btn-primary" style={{ padding: '0.35rem 0.875rem' }}>
                管理者ログイン
              </Link>
            ) : null}
          </div>

          {/* Mobile Hamburger */}
          <button
            onClick={() => setMenuOpen(!menuOpen)}
            className="md:hidden"
            style={{ color: 'var(--color-text-muted)', background: 'none', border: 'none', cursor: 'pointer', padding: '0.25rem' }}
            aria-label="メニュー"
          >
            <svg width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              {menuOpen
                ? <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                : <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              }
            </svg>
          </button>
        </div>

        {/* Mobile Menu */}
        {menuOpen && (
          <div style={{ borderTop: '1px solid var(--color-border)', padding: '0.75rem 0 1rem' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.125rem' }}>
              {[
                { to: '/', label: 'ホーム', show: true },
                { to: '/products', label: '商品一覧', show: true },
                { to: '/about', label: 'このサイトについて', show: true },
                { to: '/admin', label: '管理画面', show: isAdmin },
              ].filter(i => i.show).map((item) => (
                <Link
                  key={item.to}
                  to={item.to}
                  onClick={closeMenu}
                  style={{
                    padding: '0.6rem 0.5rem',
                    color: isActive(item.to) ? 'var(--color-accent)' : 'var(--color-text-muted)',
                    textDecoration: 'none',
                    fontSize: '0.72rem',
                    fontWeight: 700,
                    textTransform: 'uppercase',
                    letterSpacing: '0.1em',
                  }}
                >
                  {item.label}
                </Link>
              ))}

              {isAdmin && user && (
                <div style={{ borderTop: '1px solid var(--color-border)', paddingTop: '0.75rem', marginTop: '0.5rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                  <Link to="/profile" onClick={closeMenu} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', textDecoration: 'none', color: 'var(--color-text)', fontSize: '0.8rem' }}>
                    {user.username}
                    <span className="badge badge-purple">管理者</span>
                  </Link>
                  <button onClick={handleLogout} className="btn-secondary" style={{ width: 'fit-content', padding: '0.35rem 0.875rem' }}>
                    ログアウト
                  </button>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </nav>
  );
}

function AppContent() {
  return (
    <div style={{ minHeight: '100vh', backgroundColor: 'var(--color-bg)' }}>
      <Navigation />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/products" element={<Products />} />
          <Route path="/about" element={<About />} />
          <Route path="/profile" element={<AdminProtectedRoute><Profile /></AdminProtectedRoute>} />
          <Route path="/admin/login" element={<AdminLogin />} />
          <Route path="/admin" element={<AdminProtectedRoute><AdminDashboard /></AdminProtectedRoute>} />
          <Route path="/admin/scrape" element={<AdminProtectedRoute><AdminScrape /></AdminProtectedRoute>} />
          <Route path="/admin/logs" element={<AdminProtectedRoute><AdminLogs /></AdminProtectedRoute>} />
          <Route path="/admin/users" element={<AdminProtectedRoute><AdminUsers /></AdminProtectedRoute>} />
          <Route path="/admin/users/:id" element={<AdminProtectedRoute><AdminUserDetail /></AdminProtectedRoute>} />
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
