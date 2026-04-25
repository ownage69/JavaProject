import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { LoadingState } from '../common/LoadingState';

export function RequireGuest() {
  const { isAuthenticated, status } = useAuth();

  if (status === 'booting') {
    return <LoadingState title="Checking access..." />;
  }

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  return <Outlet />;
}
