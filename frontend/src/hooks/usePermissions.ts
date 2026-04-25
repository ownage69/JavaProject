import { useLocation } from 'react-router-dom';

export function usePermissions() {
  const location = useLocation();
  const isPublicRoute = location.pathname === '/' || location.pathname.startsWith('/catalog');
  const isAdmin = !isPublicRoute;
  const isUser = false;

  return {
    isAuthenticated: false,
    isAdmin,
    isUser,
    canManageLibrary: isAdmin,
    canManageReaders: isAdmin,
    canManageLoans: isAdmin,
    canViewReaderDirectory: isAdmin,
    canViewAdminPanels: isAdmin,
  };
}
