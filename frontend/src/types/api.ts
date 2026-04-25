export interface ApiValidationError {
  field: string;
  rejectedValue?: unknown;
  message: string;
}

export interface ApiErrorResponse {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
  errors?: ApiValidationError[];
}

export interface AsyncValue<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
}

export interface BreadcrumbItem {
  label: string;
  to?: string;
}

export interface SelectOption {
  value: string;
  label: string;
  disabled?: boolean;
  hint?: string;
}

export interface PaginationState {
  page: number;
  pageSize: number;
}

export interface PaginatedResult<T> {
  items: T[];
  page: number;
  pageSize: number;
  totalItems: number;
  totalPages: number;
  hasPreviousPage: boolean;
  hasNextPage: boolean;
  mode: 'client' | 'server';
}

export interface SpringPageResponse<T> {
  content: T[];
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}

export type FormErrors<T extends object> = Partial<
  Record<keyof T | string, string>
>;
