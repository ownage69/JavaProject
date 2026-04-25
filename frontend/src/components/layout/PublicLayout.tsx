import { ShieldCheck } from 'lucide-react';
import { Link, Outlet } from 'react-router-dom';
import { runtimeConfig } from '../../config/runtime';

export function PublicLayout() {
  return (
    <div className="public-shell">
      <header className="public-header">
        <Link to="/" className="public-brand">
          <span className="brand-mark">LS</span>
          <span>
            <small>Public catalog</small>
            <strong>{runtimeConfig.appName}</strong>
          </span>
        </Link>

        <nav className="public-nav">
          {runtimeConfig.features.publicCatalogEnabled ? <Link to="/catalog">Catalog</Link> : null}
          <Link to="/dashboard" className="button button--primary">
            <ShieldCheck size={16} />
            Administration
          </Link>
        </nav>
      </header>

      <main className="public-main">
        <Outlet />
      </main>
    </div>
  );
}
