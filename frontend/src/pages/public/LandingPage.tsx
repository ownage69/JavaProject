import { ArrowRight, BookOpen, NotebookTabs, ScrollText } from 'lucide-react';
import { Link } from 'react-router-dom';
import { SurfaceCard } from '../../components/common/SurfaceCard';
import { runtimeConfig } from '../../config/runtime';

const showcaseItems = [
  {
    icon: BookOpen,
    title: 'Public browsing',
    description: 'Visitors can immediately see which books are available in the collection.',
  },
  {
    icon: NotebookTabs,
    title: 'Copy-aware catalog',
    description: 'Each title shows real copy counts instead of a vague available or unavailable state.',
  },
  {
    icon: ScrollText,
    title: 'Staff workspace',
    description: 'Administration keeps books, readers, and loans in one practical workflow.',
  },
];

const featureItems = [
  {
    icon: BookOpen,
    title: 'Browse the catalog',
    description: 'Visitors can browse books, authors, categories, and publishers.',
  },
  {
    icon: NotebookTabs,
    title: 'Track copies',
    description: 'Availability is shown through real copy counts for each title.',
  },
  {
    icon: ScrollText,
    title: 'Manage loans',
    description: 'The administration keeps reader records and borrowing history together.',
  },
];

export function LandingPage() {
  return (
    <div className="landing-page">
      <section className="landing-hero">
        <div className="landing-hero__content">
          <p className="page-header__eyebrow">Library System</p>
          <h1>Library catalog for visitors, administration for staff.</h1>
          <p>
            Browse the collection, check available copies, and let the library administration
            manage books, readers, and loans in one workspace.
          </p>

          <div className="button-row">
            {runtimeConfig.features.publicCatalogEnabled ? (
              <Link to="/catalog" className="button button--primary">
                Explore catalog
                <ArrowRight size={16} />
              </Link>
            ) : null}
            <Link to="/dashboard" className="button button--secondary">
              Open administration
            </Link>
          </div>
        </div>

        <SurfaceCard className="landing-showcase">
          <div className="landing-showcase__row">
            {showcaseItems.map(({ icon: Icon, title, description }) => (
              <div key={title} className="landing-showcase__metric">
                <Icon size={18} />
                <div>
                  <strong>{title}</strong>
                  <p>{description}</p>
                </div>
              </div>
            ))}
          </div>
        </SurfaceCard>
      </section>

      <section className="landing-grid">
        {featureItems.map(({ icon: Icon, title, description }) => (
          <SurfaceCard key={title} className="landing-feature-card">
            <div className="landing-feature-card__icon">
              <Icon size={20} />
            </div>
            <h2>{title}</h2>
            <p>{description}</p>
          </SurfaceCard>
        ))}
      </section>
    </div>
  );
}
