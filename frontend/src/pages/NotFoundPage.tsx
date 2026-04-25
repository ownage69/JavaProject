import { Compass } from 'lucide-react';
import { Link } from 'react-router-dom';
import { EmptyState } from '../components/common/EmptyState';

export function NotFoundPage() {
  return (
    <div className="page-layout">
      <EmptyState
        icon={Compass}
        title="This page is off the shelf"
        description="The route does not exist or was moved. Return to the dashboard and continue from there."
        action={
          <Link to="/dashboard" className="button button--primary">
            Go to dashboard
          </Link>
        }
      />
    </div>
  );
}
