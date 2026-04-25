import axios from 'axios';
import { runtimeConfig } from '../config/runtime';
import { getStoredAuthToken } from './authStorage';

export const apiClient = axios.create({
  baseURL: runtimeConfig.apiBaseUrl,
  timeout: 15000,
  withCredentials: runtimeConfig.auth.useCredentials,
});

apiClient.interceptors.request.use((config) => {
  const token = getStoredAuthToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export function getResponseData<T>(value: { data: T }): T {
  return value.data;
}
