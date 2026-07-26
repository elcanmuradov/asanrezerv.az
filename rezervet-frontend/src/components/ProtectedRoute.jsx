import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Spinner from './Spinner';

export default function ProtectedRoute({ roles, children, loginPath = '/login' }) {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) return <Spinner full />;
  if (!user) return <Navigate to={loginPath} state={{ from: location }} replace />;
  if (roles && !roles.includes(user.role)) return <Navigate to="/" replace />;
  return children;
}
