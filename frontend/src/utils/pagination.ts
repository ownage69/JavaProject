import type { PaginatedResult, SpringPageResponse } from '../types/api';

export function paginateItems<T>(
  items: T[],
  page: number,
  pageSize: number,
): PaginatedResult<T> {
  const totalItems = items.length;
  const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
  const safePage = Math.min(Math.max(page, 1), totalPages);
  const startIndex = (safePage - 1) * pageSize;

  return {
    items: items.slice(startIndex, startIndex + pageSize),
    page: safePage,
    pageSize,
    totalItems,
    totalPages,
    hasPreviousPage: safePage > 1,
    hasNextPage: safePage < totalPages,
    mode: 'client',
  };
}

export function normalizeSpringPage<T>(
  payload: SpringPageResponse<T>,
): PaginatedResult<T> {
  const items = payload.content || [];
  const page = (payload.number || 0) + 1;
  const pageSize = payload.size || items.length || 1;
  const totalItems = payload.totalElements || items.length;
  const totalPages = payload.totalPages || Math.max(1, Math.ceil(totalItems / pageSize));

  return {
    items,
    page,
    pageSize,
    totalItems,
    totalPages,
    hasPreviousPage: page > 1,
    hasNextPage: page < totalPages,
    mode: 'server',
  };
}

export function buildPaginationRange(currentPage: number, totalPages: number): Array<number | string> {
  if (totalPages <= 7) {
    return Array.from({ length: totalPages }, (_, index) => index + 1);
  }

  if (currentPage <= 3) {
    return [1, 2, 3, 4, '...', totalPages];
  }

  if (currentPage >= totalPages - 2) {
    return [1, '...', totalPages - 3, totalPages - 2, totalPages - 1, totalPages];
  }

  return [1, '...', currentPage - 1, currentPage, currentPage + 1, '...', totalPages];
}
