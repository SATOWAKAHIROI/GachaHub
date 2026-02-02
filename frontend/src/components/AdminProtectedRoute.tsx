import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import type { ReactNode } from 'react';

interface AdminProtectedRouteProps {
  children: ReactNode;
}

function AdminProtectedRoute({ children }: AdminProtectedRouteProps) {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated || !user) {
    return <Navigate to="/admin/login" replace />;
  }

  if (user.role !== 'ADMIN') {
    return <Navigate to="/admin/login" replace />;
  }

  return <>{children}</>;
}

export default AdminProtectedRoute;
