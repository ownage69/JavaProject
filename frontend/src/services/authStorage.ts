import type { AuthSession, AuthUser } from '../types/auth';
import { runtimeConfig } from '../config/runtime';

function isBrowser() {
  return typeof window !== 'undefined';
}

export function getStoredAuthToken(): string | null {
  if (!isBrowser()) {
    return null;
  }

  return window.localStorage.getItem(runtimeConfig.auth.tokenStorageKey);
}

export function getStoredAuthUser(): AuthUser | null {
  if (!isBrowser()) {
    return null;
  }

  const value = window.localStorage.getItem(runtimeConfig.auth.userStorageKey);

  if (!value) {
    return null;
  }

  try {
    return JSON.parse(value) as AuthUser;
  } catch {
    return null;
  }
}

export function getStoredAuthSession(): AuthSession | null {
  const user = getStoredAuthUser();

  if (!user) {
    return null;
  }

  return {
    user,
    token: getStoredAuthToken(),
  };
}

export function persistAuthSession(session: AuthSession) {
  if (!isBrowser()) {
    return;
  }

  if (session.token) {
    window.localStorage.setItem(runtimeConfig.auth.tokenStorageKey, session.token);
  } else {
    window.localStorage.removeItem(runtimeConfig.auth.tokenStorageKey);
  }

  window.localStorage.setItem(runtimeConfig.auth.userStorageKey, JSON.stringify(session.user));
}

export function clearStoredAuthSession() {
  if (!isBrowser()) {
    return;
  }

  window.localStorage.removeItem(runtimeConfig.auth.tokenStorageKey);
  window.localStorage.removeItem(runtimeConfig.auth.userStorageKey);
}
