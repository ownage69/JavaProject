import { Library, Plus, RotateCcw, Search } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';
import { Link, useLocation, useSearchParams } from 'react-router-dom';
import { BookCard } from '../../components/books/BookCard';
import { AlertBanner } from '../../components/common/AlertBanner';
import { ConfirmDialog } from '../../components/common/ConfirmDialog';
import { EmptyState } from '../../components/common/EmptyState';
import { ErrorState } from '../../components/common/ErrorState';
import { LoadingState } from '../../components/common/LoadingState';
import { PageHeader } from '../../components/common/PageHeader';
import { PaginationControls } from '../../components/common/PaginationControls';
import { SurfaceCard } from '../../components/common/SurfaceCard';
import { SelectInput } from '../../components/forms/SelectInput';
import { TextInput } from '../../components/forms/TextInput';
import { useAsyncValue } from '../../hooks/useAsyncValue';
import { useDebouncedValue } from '../../hooks/useDebouncedValue';
import { usePermissions } from '../../hooks/usePermissions';
import { removeStoredBookCover } from '../../services/bookCoverStorage';
import {
  authorService,
  bookService,
  categoryService,
  loanService,
  publisherService,
} from '../../services/libraryService';
import type { Book } from '../../types/entities';
import { getBookAvailability, matchesText, sortBooks } from '../../utils/library';
import { getErrorMessage } from '../../utils/errors';
import { paginateItems } from '../../utils/pagination';

interface BooksPageProps {
  variant?: 'app' | 'public';
}

const BOOKS_PAGE_SIZE = 12;

function parsePageParam(value: string | null) {
  const parsed = Number(value);
  return Number.isInteger(parsed) && parsed > 0 ? parsed : 1;
}

export function BooksPage({ variant = 'app' }: BooksPageProps) {
  const { canManageLibrary } = usePermissions();
  const location = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();
  const { data, loading, error, reload } = useAsyncValue(async () => {
    const [books, authors, categories, publishers, loans] = await Promise.all([
      bookService.list(),
      authorService.list(),
      categoryService.list(),
      publisherService.list(),
      loanService.list(),
    ]);

    return { books, authors, categories, publishers, loans };
  }, []);
  const [query, setQuery] = useState('');
  const [authorFilter, setAuthorFilter] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [publisherFilter, setPublisherFilter] = useState('');
  const [availabilityFilter, setAvailabilityFilter] = useState('');
  const [sortBy, setSortBy] = useState('title');
  const [bookToDelete, setBookToDelete] = useState<Book | null>(null);
  const [feedback, setFeedback] = useState<string | null>(null);
  const filtersInitializedRef = useRef(false);
  const debouncedQuery = useDebouncedValue(query);
  const pageParam = searchParams.get('page');
  const page = pageParam ? parsePageParam(pageParam) : 1;
  const returnSearch = page > 1 ? `?page=${page}` : '';
  const returnTo = `${location.pathname}${returnSearch}`;

  function setPage(nextPage: number) {
    const nextSearchParams = new URLSearchParams(searchParams);

    if (nextPage > 1) {
      nextSearchParams.set('page', String(nextPage));
    } else {
      nextSearchParams.delete('page');
    }

    setSearchParams(nextSearchParams, { replace: true });
  }

  useEffect(() => {
    if (!filtersInitializedRef.current) {
      filtersInitializedRef.current = true;
      return;
    }

    setPage(1);
  }, [debouncedQuery, authorFilter, categoryFilter, publisherFilter, availabilityFilter, sortBy]);

  useEffect(() => {
    window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
  }, [page]);

  if (loading) {
    return <LoadingState title="Loading books..." />;
  }

  if (error || !data) {
    return <ErrorState description={error || 'Books could not be loaded.'} onRetry={reload} />;
  }

  const filteredBooks = sortBooks(
    data.books.filter((book) => {
      const availability = getBookAvailability(book, data.loans);

      return (
        matchesText(book.title, debouncedQuery) &&
        (!authorFilter || book.authorIds.includes(Number(authorFilter))) &&
        (!categoryFilter || book.categoryIds.includes(Number(categoryFilter))) &&
        (!publisherFilter || book.publisherId === Number(publisherFilter)) &&
        (!availabilityFilter || availability === availabilityFilter)
      );
    }),
    sortBy,
  );
  const pagination = paginateItems(filteredBooks, page, BOOKS_PAGE_SIZE);
  const basePath = variant === 'public' ? '/catalog/books' : '/books';

  async function handleDelete() {
    if (!bookToDelete) {
      return;
    }

    try {
      await bookService.remove(bookToDelete.id);
      await removeStoredBookCover(bookToDelete.id);
      setFeedback(`Book "${bookToDelete.title}" was removed from the catalog.`);
      setBookToDelete(null);
      reload();
    } catch (deleteError) {
      setFeedback(getErrorMessage(deleteError));
    }
  }

  return (
    <div className="page-layout">
      <PageHeader
        eyebrow={variant === 'public' ? 'Catalog' : 'Books'}
        title={variant === 'public' ? 'Library catalog' : 'Book records'}
        description="Search the collection, filter related records, and check copy availability."
        actions={
          canManageLibrary ? (
            <div className="button-row">
              <Link
                to={`/books/new?from=${encodeURIComponent(returnTo)}`}
                className="button button--primary"
              >
                <Plus size={16} />
                Add book
              </Link>
            </div>
          ) : null
        }
      />

      <SurfaceCard className="toolbar-card">
        <div className="toolbar-grid toolbar-grid--wide">
          <div className="search-field">
            <Search size={16} />
            <TextInput
              type="search"
              value={query}
              placeholder="Search by title"
              onChange={(event) => setQuery(event.target.value)}
            />
          </div>

          <SelectInput
            value={authorFilter}
            onChange={(event) => setAuthorFilter(event.target.value)}
            options={[
              { value: '', label: 'All authors' },
              ...data.authors.map((author) => ({
                value: String(author.id),
                label: `${author.firstName} ${author.lastName}`,
              })),
            ]}
          />

          <SelectInput
            value={categoryFilter}
            onChange={(event) => setCategoryFilter(event.target.value)}
            options={[
              { value: '', label: 'All categories' },
              ...data.categories.map((category) => ({
                value: String(category.id),
                label: category.name,
              })),
            ]}
          />

          <SelectInput
            value={publisherFilter}
            onChange={(event) => setPublisherFilter(event.target.value)}
            options={[
              { value: '', label: 'All publishers' },
              ...data.publishers.map((publisher) => ({
                value: String(publisher.id),
                label: publisher.name,
              })),
            ]}
          />

          <SelectInput
            value={availabilityFilter}
            onChange={(event) => setAvailabilityFilter(event.target.value)}
            options={[
              { value: '', label: 'All statuses' },
              { value: 'available', label: 'Available' },
              { value: 'loaned', label: 'On loan' },
            ]}
          />

          <SelectInput
            value={sortBy}
            onChange={(event) => setSortBy(event.target.value)}
            options={[
              { value: 'title', label: 'Sort by title' },
              { value: 'author', label: 'Sort by author' },
              { value: 'year', label: 'Sort by publication year' },
            ]}
          />

          <div className="toolbar-actions">
            <button
              type="button"
              className="button button--ghost"
              onClick={() => {
                setQuery('');
                setAuthorFilter('');
                setCategoryFilter('');
                setPublisherFilter('');
                setAvailabilityFilter('');
                setSortBy('title');
              }}
            >
              <RotateCcw size={14} />
              Reset filters
            </button>
          </div>
        </div>
      </SurfaceCard>

      {feedback ? <AlertBanner title="Catalog update" description={feedback} /> : null}

      {filteredBooks.length ? (
        <>
          <div className="book-grid">
            {pagination.items.map((book) => (
              <BookCard
                key={book.id}
                book={book}
                availability={getBookAvailability(book, data.loans)}
                onDelete={setBookToDelete}
                basePath={basePath}
                returnTo={returnTo}
                canManage={canManageLibrary}
              />
            ))}
          </div>

          <PaginationControls pagination={pagination} onPageChange={setPage} />
        </>
      ) : (
        <EmptyState
          icon={Library}
          title="No books match the current filters"
          description={
            canManageLibrary
              ? 'Try resetting the filters or add another book.'
              : 'Try resetting the filters or browse another part of the catalog.'
          }
          action={
            canManageLibrary ? (
              <Link
                to={`/books/new?from=${encodeURIComponent(returnTo)}`}
                className="button button--primary"
              >
                Add book
              </Link>
            ) : null
          }
        />
      )}

      <ConfirmDialog
        open={Boolean(bookToDelete)}
        title="Delete book"
        description="This action permanently removes the book from the catalog."
        confirmLabel="Delete book"
        onCancel={() => setBookToDelete(null)}
        onConfirm={handleDelete}
      />
    </div>
  );
}
