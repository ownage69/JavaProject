import { AlertTriangle } from 'lucide-react';
import { SurfaceCard } from './SurfaceCard';

interface ErrorStateProps {
  title?: string;
  description: string;
  onRetry?: () => void;
}

export function ErrorState({
  title = 'Unable to load data',
  description,
  onRetry,
}: ErrorStateProps) {
  return (
    <SurfaceCard className="error-state">
      <div className="error-state__icon">
        <AlertTriangle size={22} />
      </div>
      <h3>{title}</h3>
      <p>{description}</p>
      {onRetry ? (
        <button type="button" className="button button--secondary" onClick={onRetry}>
          Try again
        </button>
      ) : null}
    </SurfaceCard>
  );
}
