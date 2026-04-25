import { ShieldAlert } from 'lucide-react';
import { Link } from 'react-router-dom';
import { EmptyState } from '../components/common/EmptyState';

export function UnauthorizedPage() {
  return (
    <div className="page-layout">
      <EmptyState
        icon={ShieldAlert}
        title="You do not have access to this section"
        description="This route is reserved for another role. Return to a section that belongs to your current access level."
        action={
          <Link to="/dashboard" className="button button--primary">
            Go to dashboard
          </Link>
        }
      />
    </div>
  );
}
