import { AlertTriangle, Home, RefreshCcw } from 'lucide-react';
import { Link, isRouteErrorResponse, useRouteError } from 'react-router-dom';
import { SurfaceCard } from '../components/common/SurfaceCard';

function getErrorDescription(error: unknown) {
  if (isRouteErrorResponse(error)) {
    return error.statusText || `Route error ${error.status}`;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'Something went wrong while rendering this page.';
}

export function RouteErrorPage() {
  const error = useRouteError();

  return (
    <div className="route-error">
      <SurfaceCard className="route-error__card">
        <div className="route-error__icon">
          <AlertTriangle size={22} />
        </div>
        <div className="route-error__content">
          <p className="page-header__eyebrow">Application error</p>
          <h2>Something interrupted this page</h2>
          <p>{getErrorDescription(error)}</p>
        </div>
        <div className="button-row">
          <button
            type="button"
            className="button button--secondary"
            onClick={() => window.location.reload()}
          >
            <RefreshCcw size={16} />
            Reload page
          </button>
          <Link to="/" className="button button--primary">
            <Home size={16} />
            Back to home
          </Link>
        </div>
      </SurfaceCard>
    </div>
  );
}
