import { BookOpen, NotebookText } from 'lucide-react';
import { Link } from 'react-router-dom';
import { DataTable, type TableColumn } from '../../components/common/DataTable';
import { EmptyState } from '../../components/common/EmptyState';
import { ErrorState } from '../../components/common/ErrorState';
import { LoadingState } from '../../components/common/LoadingState';
import { PageHeader } from '../../components/common/PageHeader';
import { PaginationControls } from '../../components/common/PaginationControls';
import { StatusBadge } from '../../components/common/StatusBadge';
import { SurfaceCard } from '../../components/common/SurfaceCard';
import { runtimeConfig } from '../../config/runtime';
import { useAuth } from '../../hooks/useAuth';
import { useAsyncValue } from '../../hooks/useAsyncValue';
import { loanService } from '../../services/libraryService';
import type { Loan } from '../../types/entities';
import { formatDate, formatLoanStatusLabel } from '../../utils/format';
import { paginateItems } from '../../utils/pagination';
import { useState } from 'react';

function resolveTone(label: string): 'success' | 'warning' | 'danger' {
  if (label === 'Returned') {
    return 'success';
  }

  if (label === 'Overdue') {
    return 'danger';
  }

  return 'warning';
}

export function MyLoansPage() {
  const { user } = useAuth();
  const { data, loading, error, reload } = useAsyncValue(() => loanService.list(), []);
  const [page, setPage] = useState(1);

  if (loading) {
    return <LoadingState title="Loading your loans..." />;
  }

  if (error || !data) {
    return <ErrorState description={error || 'Your loan history could not be loaded.'} onRetry={reload} />;
  }

  if (!user?.readerId) {
    return (
      <div className="page-layout">
        <PageHeader
          eyebrow="My loans"
          title="Personal borrowing area"
          description="This page is ready, but it needs a reader-to-user mapping from your backend auth integration."
        />

        <EmptyState
          icon={NotebookText}
          title="Reader account mapping is not configured yet"
          description="Set `readerId` in the auth response or replace this page with a dedicated `/me/loans` endpoint later."
        />
      </div>
    );
  }

  const userLoans = data
    .filter((loan) => loan.readerId === user.readerId)
    .sort((left, right) => right.loanDate.localeCompare(left.loanDate));
  const pagination = paginateItems(userLoans, page, runtimeConfig.pagination.pageSize);

  const columns: TableColumn<Loan>[] = [
    {
      key: 'book',
      header: 'Book',
      cell: (loan) => (
        <div>
          <strong>{loan.bookTitle}</strong>
          <p className="table-subtext">Borrowed {formatDate(loan.loanDate)}</p>
        </div>
      ),
    },
    {
      key: 'due',
      header: 'Due date',
      cell: (loan) => formatDate(loan.dueDate),
    },
    {
      key: 'return',
      header: 'Return date',
      cell: (loan) => formatDate(loan.returnDate, loan.returned ? 'Returned date unavailable' : 'Not returned'),
    },
    {
      key: 'status',
      header: 'Status',
      cell: (loan) => {
        const label = formatLoanStatusLabel(loan);
        return <StatusBadge label={label} tone={resolveTone(label)} />;
      },
    },
  ];

  return (
    <div className="page-layout">
      <PageHeader
        eyebrow="My loans"
        title="Your personal borrowing history"
        description="See what is currently borrowed, what is overdue, and what has already been returned."
        actions={
          <Link to="/books" className="button button--secondary">
            <BookOpen size={16} />
            Browse books
          </Link>
        }
      />

      {pagination.totalItems ? (
        <SurfaceCard>
          <DataTable
            columns={columns}
            rows={pagination.items}
            getRowKey={(loan) => loan.id}
            caption="Current user loan history"
          />
          <PaginationControls pagination={pagination} onPageChange={setPage} />
        </SurfaceCard>
      ) : (
        <EmptyState
          icon={NotebookText}
          title="No loans yet"
          description="Borrowed books will appear here once the reader has active or historical loans."
        />
      )}
    </div>
  );
}
