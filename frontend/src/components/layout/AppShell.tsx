import { Menu, X } from 'lucide-react';
import { useState } from 'react';
import { Link, NavLink, Outlet } from 'react-router-dom';
import { navigationItems } from '../../app/navigation';
import { runtimeConfig } from '../../config/runtime';

function TopBar({
  onMenuToggle,
  isMenuOpen,
}: {
  onMenuToggle: () => void;
  isMenuOpen: boolean;
}) {
  const today = new Intl.DateTimeFormat('en', {
    weekday: 'long',
    month: 'long',
    day: 'numeric',
  }).format(new Date());

  return (
    <header className="topbar">
      <button
        className="icon-button mobile-only"
        type="button"
        onClick={onMenuToggle}
        aria-label={isMenuOpen ? 'Close navigation menu' : 'Open navigation menu'}
      >
        {isMenuOpen ? <X size={18} /> : <Menu size={18} />}
      </button>

      <Link to="/" className="topbar__brand" aria-label="Open public landing page">
        <p className="topbar__eyebrow">Library administration</p>
        <h1 className="topbar__title">{runtimeConfig.appName}</h1>
      </Link>

      <div className="topbar__actions">
        <div className="topbar__meta">
          <span className="status-dot" />
          <span>{today}</span>
        </div>
      </div>
    </header>
  );
}

function Sidebar({
  isOpen,
  closeMenu,
}: {
  isOpen: boolean;
  closeMenu: () => void;
}) {
  return (
    <>
      <aside className={`sidebar ${isOpen ? 'sidebar--open' : ''}`}>
        <Link to="/dashboard" className="sidebar__brand" onClick={closeMenu}>
          <div className="brand-mark">LS</div>
          <div>
            <p className="sidebar__eyebrow">Library System</p>
            <h2>Library workspace</h2>
          </div>
        </Link>

        <nav className="sidebar__nav" aria-label="Primary navigation">
          {navigationItems.map(({ icon: Icon, label, description, to }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `sidebar__link ${isActive ? 'sidebar__link--active' : ''}`
              }
              onClick={closeMenu}
            >
              <Icon size={18} />
              <span>
                <strong>{label}</strong>
                <small>{description}</small>
              </span>
            </NavLink>
          ))}
        </nav>
      </aside>

      {isOpen ? <button type="button" className="sidebar-backdrop" onClick={closeMenu} /> : null}
    </>
  );
}

export function AppShell() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <div className="app-shell">
      <Sidebar isOpen={isMenuOpen} closeMenu={() => setIsMenuOpen(false)} />

      <div className="app-shell__content">
        <TopBar
          isMenuOpen={isMenuOpen}
          onMenuToggle={() => setIsMenuOpen((current) => !current)}
        />

        <main className="page-stack">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
