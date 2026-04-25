import { ArrowLeftRight, BookOpen, RotateCcw, Search } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { DataTable, type TableColumn } from '../../components/common/DataTable';
import { EmptyState } from '../../components/common/EmptyState';
import { ErrorState } from '../../components/common/ErrorState';
import { LoadingState } from '../../components/common/LoadingState';
import { PageHeader } from '../../components/common/PageHeader';
import { PaginationControls } from '../../components/common/PaginationControls';
import { StatusBadge } from '../../components/common/StatusBadge';
import { SurfaceCard } from '../../components/common/SurfaceCard';
import { SelectInput } from '../../components/forms/SelectInput';
import { TextInput } from '../../components/forms/TextInput';
import { runtimeConfig } from '../../config/runtime';
import { useAsyncValue } from '../../hooks/useAsyncValue';
import { useDebouncedValue } from '../../hooks/useDebouncedValue';
import { loanService } from '../../services/libraryService';
import type { Loan } from '../../types/entities';
import { formatDate, formatLoanStatusLabel } from '../../utils/format';
import { getErrorMessage } from '../../utils/errors';
import { matchesText } from '../../utils/library';
import { paginateItems } from '../../utils/pagination';

function resolveTone(label: string): 'success' | 'warning' | 'danger' {
  if (label === 'Returned') {
    return 'success';
  }

  if (label === 'Overdue') {
    return 'danger';
  }

  return 'warning';
}

export function LoansPage() {
  const { data, loading, error, reload } = useAsyncValue(() => loanService.list(), []);
  const [query, setQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [feedback, setFeedback] = useState<string | null>(null);
  const [returningLoanId, setReturningLoanId] = useState<number | null>(null);
  const [page, setPage] = useState(1);
  const debouncedQuery = useDebouncedValue(query);

  useEffect(() => {
    setPage(1);
  }, [debouncedQuery, statusFilter]);

  if (loading) {
    return <LoadingState title="Loading loans..." />;
  }

  if (error || !data) {
    return <ErrorState description={error || 'Loans could not be loaded.'} onRetry={reload} />;
  }

  const filteredLoans = data.filter((loan) => {
    const status = formatLoanStatusLabel(loan).toLowerCase();

    return (
      (matchesText(loan.bookTitle, debouncedQuery) || matchesText(loan.readerName, debouncedQuery)) &&
      (!statusFilter || status === statusFilter)
    );
  });
  const pagination = paginateItems(filteredLoans, page, runtimeConfig.pagination.pageSize);

  async function handleReturn(loan: Loan) {
    setFeedback(null);
    setReturningLoanId(loan.id);

    try {
      await loanService.returnBook(loan.id);
      setFeedback(`Loan #${loan.id} was updated with the return action.`);
      reload();
    } catch (returnError) {
      setFeedback(getErrorMessage(returnError));
    } finally {
      setReturningLoanId(null);
    }
  }

  const columns: TableColumn<Loan>[] = [
    {
      key: 'reader',
      header: 'Reader',
      cell: (loan) => (
        <div>
          <strong>{loan.readerName}</strong>
          <p className="table-subtext">Reader ID {loan.readerId}</p>
        </div>
      ),
    },
    {
      key: 'book',
      header: 'Book',
      cell: (loan) => (
        <div>
          <strong>{loan.bookTitle}</strong>
          <p className="table-subtext">Book ID {loan.bookId}</p>
        </div>
      ),
    },
    {
      key: 'borrow',
      header: 'Borrow date',
      cell: (loan) => formatDate(loan.loanDate),
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
        const status = formatLoanStatusLabel(loan);
        return <StatusBadge label={status} tone={resolveTone(status)} />;
      },
    },
    {
      key: 'actions',
      header: 'Actions',
      cell: (loan) => (
        <div className="table-actions">
          <Link to={`/loans/${loan.id}`} className="button button--ghost">
            Details
          </Link>
          {!loan.returned ? (
            <button
              type="button"
              className="button button--secondary"
              onClick={() => handleReturn(loan)}
              disabled={returningLoanId === loan.id}
            >
              {returningLoanId === loan.id ? 'Processing...' : 'Return book'}
            </button>
          ) : null}
        </div>
      ),
    },
  ];

  return (
    <div className="page-layout">
      <PageHeader
        eyebrow="Loans"
        title="Borrowing and returns made readable"
        description="Track who borrowed what, when it was due, and whether the item is active, overdue, or returned."
        actions={
          <Link to="/loans/new" className="button button--primary">
            <ArrowLeftRight size={16} />
            Create loan
          </Link>
        }
      />

      {feedback ? (
        <div className="alert-banner alert-banner--success">
          <strong>Loan action</strong>
          <p>{feedback}</p>
        </div>
      ) : null}

      <SurfaceCard className="toolbar-card">
        <div className="toolbar-grid">
          <div className="search-field">
            <Search size={16} />
            <TextInput
              type="search"
              value={query}
              placeholder="Search by reader or book"
              onChange={(event) => setQuery(event.target.value)}
            />
          </div>

          <SelectInput
            value={statusFilter}
            onChange={(event) => setStatusFilter(event.target.value)}
            options={[
              { value: '', label: 'All statuses' },
              { value: 'active', label: 'Active' },
              { value: 'overdue', label: 'Overdue' },
              { value: 'returned', label: 'Returned' },
            ]}
          />

          <button
            type="button"
            className="button button--ghost"
            onClick={() => {
              setQuery('');
              setStatusFilter('');
            }}
          >
            <RotateCcw size={14} />
            Reset filters
          </button>
        </div>
      </SurfaceCard>

      {filteredLoans.length ? (
        <SurfaceCard>
          <DataTable
            columns={columns}
            rows={pagination.items}
            getRowKey={(loan) => loan.id}
            caption="Loan records"
          />
          <PaginationControls pagination={pagination} onPageChange={setPage} />
        </SurfaceCard>
      ) : (
        <EmptyState
          icon={BookOpen}
          title="No loans found"
          description="Try another search or create a new loan."
          action={
            <Link to="/loans/new" className="button button--primary">
              Create loan
            </Link>
          }
        />
      )}
    </div>
  );
}
