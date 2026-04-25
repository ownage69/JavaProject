export type UserRole = 'admin' | 'user';

export interface AuthUser {
  id: number | string;
  username: string;
  email: string;
  role: UserRole;
  displayName: string;
  readerId?: number | null;
}

export interface AuthSession {
  user: AuthUser;
  token?: string | null;
}

export interface LoginCredentials {
  identity: string;
  password: string;
}

export interface AuthContextValue {
  user: AuthUser | null;
  token: string | null;
  status: 'booting' | 'authenticated' | 'guest';
  isAuthenticated: boolean;
  login: (credentials: LoginCredentials) => Promise<AuthUser>;
  logout: () => Promise<void>;
}
