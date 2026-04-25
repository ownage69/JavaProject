import { createContext, useEffect, useState, type PropsWithChildren } from 'react';
import { authService } from '../services/authService';
import type { AuthContextValue, AuthSession, LoginCredentials } from '../types/auth';

export const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: PropsWithChildren) {
  const [session, setSession] = useState<AuthSession | null>(null);
  const [status, setStatus] = useState<AuthContextValue['status']>('booting');

  useEffect(() => {
    let isActive = true;

    void (async () => {
      const restoredSession = await authService.restoreSession();

      if (!isActive) {
        return;
      }

      if (restoredSession) {
        setSession(restoredSession);
        setStatus('authenticated');
        return;
      }

      setStatus('guest');
    })();

    return () => {
      isActive = false;
    };
  }, []);

  async function login(credentials: LoginCredentials) {
    const nextSession = await authService.login(credentials);
    setSession(nextSession);
    setStatus('authenticated');
    return nextSession.user;
  }

  async function logout() {
    await authService.logout();
    setSession(null);
    setStatus('guest');
  }

  return (
    <AuthContext.Provider
      value={{
        user: session?.user || null,
        token: session?.token || null,
        status,
        isAuthenticated: status === 'authenticated',
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
