import { ChevronLeft, ChevronRight } from 'lucide-react';
import type { PaginatedResult } from '../../types/api';
import { buildPaginationRange } from '../../utils/pagination';

interface PaginationControlsProps {
  pagination: PaginatedResult<unknown>;
  onPageChange: (page: number) => void;
}

export function PaginationControls({
  pagination,
  onPageChange,
}: PaginationControlsProps) {
  if (pagination.totalItems <= pagination.pageSize && pagination.totalPages <= 1) {
    return null;
  }

  const firstItemIndex = pagination.totalItems ? (pagination.page - 1) * pagination.pageSize + 1 : 0;
  const lastItemIndex = Math.min(pagination.page * pagination.pageSize, pagination.totalItems);
  const pageItems = buildPaginationRange(pagination.page, pagination.totalPages);

  return (
    <div className="pagination">
      <div className="pagination__meta">
        Showing {firstItemIndex}-{lastItemIndex} of {pagination.totalItems}
      </div>

      <div className="pagination__controls">
        <button
          type="button"
          className="pagination__button"
          onClick={() => onPageChange(pagination.page - 1)}
          disabled={!pagination.hasPreviousPage}
        >
          <ChevronLeft size={16} />
          Previous
        </button>

        <div className="pagination__pages">
          {pageItems.map((item, index) =>
            typeof item === 'string' ? (
              <span key={`ellipsis-${index}`} className="pagination__ellipsis">
                {item}
              </span>
            ) : (
              <button
                key={item}
                type="button"
                className={`pagination__page ${item === pagination.page ? 'pagination__page--active' : ''}`}
                onClick={() => onPageChange(item)}
              >
                {item}
              </button>
            ),
          )}
        </div>

        <button
          type="button"
          className="pagination__button"
          onClick={() => onPageChange(pagination.page + 1)}
          disabled={!pagination.hasNextPage}
        >
          Next
          <ChevronRight size={16} />
        </button>
      </div>
    </div>
  );
}
