import { ArrowLeft, BookOpen, Bookmark } from 'lucide-react';
import { Link, useParams } from 'react-router-dom';
import { EmptyState } from '../../components/common/EmptyState';
import { ErrorState } from '../../components/common/ErrorState';
import { LoadingState } from '../../components/common/LoadingState';
import { PageHeader } from '../../components/common/PageHeader';
import { SurfaceCard } from '../../components/common/SurfaceCard';
import { usePermissions } from '../../hooks/usePermissions';
import { useAsyncValue } from '../../hooks/useAsyncValue';
import { bookService, categoryService } from '../../services/libraryService';
import { getBooksForCategory } from '../../utils/library';

export function CategoryDetailsPage() {
  const { canManageLibrary } = usePermissions();
  const id = Number(useParams().id);
  const { data, loading, error, reload } = useAsyncValue(async () => {
    const [category, books] = await Promise.all([categoryService.getById(id), bookService.list()]);
    return { category, books };
  }, [id]);

  if (loading) {
    return <LoadingState title="Loading category details..." />;
  }

  if (error || !data) {
    return <ErrorState description={error || 'Category details could not be loaded.'} onRetry={reload} />;
  }

  const relatedBooks = getBooksForCategory(data.books, data.category.id);

  return (
    <div className="page-layout">
      <PageHeader
        breadcrumbs={[
          { label: 'Categories', to: '/categories' },
          { label: data.category.name },
        ]}
        eyebrow="Category details"
        title={data.category.name}
        description="Review the books grouped in this category and keep the taxonomy understandable."
        actions={
          <div className="button-row">
            <Link to="/categories" className="button button--ghost">
              <ArrowLeft size={16} />
              Back
            </Link>
            {canManageLibrary ? (
              <Link to={`/categories/${data.category.id}/edit`} className="button button--primary">
                Edit category
              </Link>
            ) : null}
          </div>
        }
      />

      <div className="split-layout">
        <SurfaceCard className="profile-card">
          <div className="entity-card__icon">
            <Bookmark size={18} />
          </div>
          <h3>{data.category.name}</h3>
          <p>{relatedBooks.length} catalog items currently live in this category.</p>
        </SurfaceCard>

        <SurfaceCard
          header={
            <div>
              <p className="section-eyebrow">Related books</p>
              <h3 className="section-title">Titles grouped under this category</h3>
            </div>
          }
        >
          {relatedBooks.length ? (
            <div className="simple-list">
              {relatedBooks.map((book) => (
                <Link key={book.id} to={`/books/${book.id}`} className="simple-list__item">
                  <div className="simple-list__icon">
                    <BookOpen size={16} />
                  </div>
                  <div>
                    <strong>{book.title}</strong>
                    <p>{book.authorNames.join(', ')}</p>
                  </div>
                </Link>
              ))}
            </div>
          ) : (
            <EmptyState
              icon={BookOpen}
              title="No books in this category"
              description="Books assigned to this category will appear here."
            />
          )}
        </SurfaceCard>
      </div>
    </div>
  );
}
