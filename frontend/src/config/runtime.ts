type LoanReturnMethod = 'post' | 'put' | 'patch';
type AuthMode = 'mock' | 'api';

function resolveLoanReturnMethod(value: string | undefined): LoanReturnMethod {
  const normalized = value?.toLowerCase();

  if (normalized === 'put' || normalized === 'patch') {
    return normalized;
  }

  return 'post';
}

function resolveAuthMode(value: string | undefined): AuthMode {
  return value?.toLowerCase() === 'api' ? 'api' : 'mock';
}

export const runtimeConfig = {
  appName: import.meta.env.VITE_APP_NAME || 'Library System',
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL || '/api',
  backendTarget: import.meta.env.VITE_BACKEND_TARGET || 'http://localhost:8080',
  pagination: {
    pageSize: Number(import.meta.env.VITE_DEFAULT_PAGE_SIZE || 10),
  },
  auth: {
    mode: resolveAuthMode(import.meta.env.VITE_AUTH_MODE),
    loginPath: import.meta.env.VITE_AUTH_LOGIN_PATH || '/auth/login',
    logoutPath: import.meta.env.VITE_AUTH_LOGOUT_PATH || '/auth/logout',
    currentUserPath: import.meta.env.VITE_AUTH_ME_PATH || '/auth/me',
    tokenStorageKey: import.meta.env.VITE_AUTH_TOKEN_STORAGE_KEY || 'library.auth.token',
    userStorageKey: import.meta.env.VITE_AUTH_USER_STORAGE_KEY || 'library.auth.user',
    useCredentials: import.meta.env.VITE_AUTH_USE_CREDENTIALS === 'true',
  },
  features: {
    publicCatalogEnabled: import.meta.env.VITE_ENABLE_PUBLIC_CATALOG !== 'false',
    loanReturnEnabled: import.meta.env.VITE_ENABLE_LOAN_RETURN !== 'false',
    loanReturnMethod: resolveLoanReturnMethod(import.meta.env.VITE_LOAN_RETURN_METHOD),
    loanReturnPath: import.meta.env.VITE_LOAN_RETURN_PATH || '/loans/:id/return',
    loanReturnDateField: import.meta.env.VITE_LOAN_RETURN_DATE_FIELD || 'returnDate',
  },
};
