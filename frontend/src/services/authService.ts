import { runtimeConfig } from '../config/runtime';
import type { AuthSession, AuthUser, LoginCredentials, UserRole } from '../types/auth';
import { getErrorMessage } from '../utils/errors';
import { clearStoredAuthSession, getStoredAuthSession, persistAuthSession } from './authStorage';
import { endpoints } from './endpoints';
import { apiClient, getResponseData } from './http';

interface RawAuthUser {
  id?: number | string;
  username?: string;
  email?: string;
  login?: string;
  role?: string;
  roles?: string[];
  authorities?: Array<string | { authority?: string }>;
  readerId?: number | null;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  displayName?: string;
}

interface RawAuthResponse {
  token?: string;
  accessToken?: string;
  jwt?: string;
  user?: RawAuthUser;
  account?: RawAuthUser;
}

const mockSessions: Array<AuthSession & { password: string }> = [
  {
    token: 'demo-admin-token',
    password: 'admin123',
    user: {
      id: 1,
      username: 'admin',
      email: 'admin@library.local',
      role: 'admin',
      displayName: 'Library Admin',
      readerId: null,
    },
  },
  {
    token: 'demo-user-token',
    password: 'user123',
    user: {
      id: 2,
      username: 'reader',
      email: 'reader@library.local',
      role: 'user',
      displayName: 'Reader Member',
      readerId: 1,
    },
  },
];

function normalizeRole(value: string | undefined): UserRole {
  return value?.toLowerCase() === 'admin' ? 'admin' : 'user';
}

function resolveRole(user: RawAuthUser | undefined): UserRole {
  const authorityValue = user?.authorities?.[0];
  const authority =
    typeof authorityValue === 'string' ? authorityValue : authorityValue?.authority;

  return normalizeRole(
    user?.role ||
      user?.roles?.[0] ||
      authority?.replace(/^ROLE_/, ''),
  );
}

function resolveDisplayName(user: RawAuthUser | undefined, fallbackIdentity: string): string {
  if (!user) {
    return fallbackIdentity;
  }

  return (
    user.displayName ||
    user.fullName ||
    [user.firstName, user.lastName].filter(Boolean).join(' ') ||
    user.username ||
    user.email ||
    fallbackIdentity
  );
}

function normalizeAuthSession(payload: RawAuthResponse | RawAuthUser, identity: string): AuthSession {
  const response = payload as RawAuthResponse;
  const user = ('user' in response && response.user) || ('account' in response && response.account)
    ? response.user || response.account
    : (payload as RawAuthUser);

  const normalizedUser: AuthUser = {
    id: user?.id || identity,
    username: user?.username || user?.login || user?.email || identity,
    email: user?.email || identity,
    role: resolveRole(user),
    displayName: resolveDisplayName(user, identity),
    readerId: user?.readerId ?? null,
  };

  return {
    user: normalizedUser,
    token: response.token || response.accessToken || response.jwt || null,
  };
}

function hasEmbeddedUser(payload: RawAuthResponse | RawAuthUser): boolean {
  const response = payload as RawAuthResponse;
  const user = response.user || response.account || (payload as RawAuthUser);

  return Boolean(
    user?.id ||
      user?.username ||
      user?.email ||
      user?.login ||
      user?.role ||
      user?.roles?.length ||
      user?.authorities?.length,
  );
}

async function fetchCurrentUserSession(identity: string): Promise<AuthSession | null> {
  try {
    const data = await apiClient.get(endpoints.auth.currentUser).then(getResponseData);
    return normalizeAuthSession(data as RawAuthResponse | RawAuthUser, identity);
  } catch {
    return null;
  }
}

async function loginWithMock(credentials: LoginCredentials): Promise<AuthSession> {
  const identity = credentials.identity.trim().toLowerCase();
  const session = mockSessions.find(
    (item) =>
      (item.user.email.toLowerCase() === identity || item.user.username.toLowerCase() === identity) &&
      item.password === credentials.password,
  );

  if (!session) {
    throw new Error('Invalid email or password. Try the demo accounts shown below the form.');
  }

  return {
    token: session.token,
    user: session.user,
  };
}

async function loginWithApi(credentials: LoginCredentials): Promise<AuthSession> {
  const payload = {
    username: credentials.identity,
    email: credentials.identity,
    login: credentials.identity,
    password: credentials.password,
  };

  const data = await apiClient.post(endpoints.auth.login, payload).then(getResponseData);
  const session = normalizeAuthSession(data as RawAuthResponse, credentials.identity);

  if (!hasEmbeddedUser(data as RawAuthResponse | RawAuthUser)) {
    const currentUserSession = await fetchCurrentUserSession(credentials.identity);

    if (currentUserSession) {
      return {
        ...currentUserSession,
        token: session.token || currentUserSession.token,
      };
    }
  }

  return session;
}

export const authService = {
  getDemoAccounts() {
    return mockSessions.map(({ password, user }) => ({
      identity: user.email,
      password,
      role: user.role,
      username: user.username,
    }));
  },
  getStoredSession() {
    return getStoredAuthSession();
  },
  async restoreSession(): Promise<AuthSession | null> {
    const storedSession = getStoredAuthSession();

    if (storedSession) {
      return storedSession;
    }

    if (runtimeConfig.auth.mode !== 'api' || !runtimeConfig.auth.useCredentials) {
      return null;
    }

    const session = await fetchCurrentUserSession('library.user');

    if (session) {
      persistAuthSession(session);
    }

    return session;
  },
  async login(credentials: LoginCredentials): Promise<AuthSession> {
    const session =
      runtimeConfig.auth.mode === 'api'
        ? await loginWithApi(credentials)
        : await loginWithMock(credentials);

    persistAuthSession(session);
    return session;
  },
  async logout() {
    try {
      if (runtimeConfig.auth.mode === 'api') {
        await apiClient.post(endpoints.auth.logout);
      }
    } catch (error) {
      // Prefer clearing local auth state even if the remote logout endpoint is absent.
      console.warn(getErrorMessage(error));
    } finally {
      clearStoredAuthSession();
    }
  },
};
