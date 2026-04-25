import { ArrowLeftRight, BookOpen, Building2, Shapes, Sparkles, Users } from 'lucide-react';
import { ErrorState } from '../../components/common/ErrorState';
import { LoadingState } from '../../components/common/LoadingState';
import { PageHeader } from '../../components/common/PageHeader';
import { StatCard } from '../../components/common/StatCard';
import { StatusBadge } from '../../components/common/StatusBadge';
import { SurfaceCard } from '../../components/common/SurfaceCard';
import { useAsyncValue } from '../../hooks/useAsyncValue';
import { dashboardService } from '../../services/libraryService';
import { formatDate, formatLoanStatusLabel } from '../../utils/format';

function getStatusTone(label: string): 'success' | 'warning' | 'danger' {
  if (label === 'Returned') {
    return 'success';
  }

  if (label === 'Overdue') {
    return 'danger';
  }

  return 'warning';
}

export function DashboardPage() {
  const { data, loading, error, reload } = useAsyncValue(() => dashboardService.getOverview(), []);

  if (loading) {
    return <LoadingState title="Loading dashboard overview..." />;
  }

  if (error || !data) {
    return <ErrorState description={error || 'The dashboard could not load the latest library metrics.'} onRetry={reload} />;
  }

  return (
    <div className="page-layout">
      <PageHeader
        eyebrow="Dashboard"
        title="Library overview"
        description="Current catalog, reader, and loan summary."
      />

      <div className="stat-grid">
        <StatCard title="Books" value={data.totalBooks} helper="Titles" icon={BookOpen} tone="green" />
        <StatCard title="Authors" value={data.totalAuthors} helper="Authors" icon={Sparkles} tone="green" />
        <StatCard title="Categories" value={data.totalCategories} helper="Categories" icon={Shapes} tone="green" />
        <StatCard title="Publishers" value={data.totalPublishers} helper="Publishers" icon={Building2} tone="green" />
        <StatCard title="Readers" value={data.totalReaders} helper="Readers" icon={Users} tone="green" />
        <StatCard title="Active Loans" value={data.activeLoansCount} helper="Open loans" icon={ArrowLeftRight} tone="green" />
      </div>

      <SurfaceCard
        header={
          <div>
            <p className="section-eyebrow">Latest activity</p>
            <h3 className="section-title">Recent loans</h3>
          </div>
        }
      >
        <div className="activity-list">
          {data.latestLoans.map((loan) => {
            const status = formatLoanStatusLabel(loan);

            return (
              <article key={loan.id} className="activity-item">
                <div className="activity-item__content">
                  <strong>{loan.bookTitle}</strong>
                  <p>Borrowed by {loan.readerName} on {formatDate(loan.loanDate)}</p>
                </div>
                <div className="activity-item__meta">
                  <StatusBadge label={status} tone={getStatusTone(status)} />
                  <small>Due {formatDate(loan.dueDate)}</small>
                </div>
              </article>
            );
          })}
        </div>
      </SurfaceCard>
    </div>
  );
}
