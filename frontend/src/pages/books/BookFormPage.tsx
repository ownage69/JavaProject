import { ArrowLeft, ImagePlus, Save, Trash2 } from 'lucide-react';
import { useEffect, useState, type ChangeEvent, type FormEvent } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import { BookCover } from '../../components/books/BookCover';
import { AlertBanner } from '../../components/common/AlertBanner';
import { ErrorState } from '../../components/common/ErrorState';
import { LoadingState } from '../../components/common/LoadingState';
import { PageHeader } from '../../components/common/PageHeader';
import { SurfaceCard } from '../../components/common/SurfaceCard';
import { FormField } from '../../components/forms/FormField';
import { SearchableMultiSelect } from '../../components/forms/SearchableMultiSelect';
import { SearchableSelect } from '../../components/forms/SearchableSelect';
import { TextAreaInput } from '../../components/forms/TextAreaInput';
import { TextInput } from '../../components/forms/TextInput';
import { useAsyncValue } from '../../hooks/useAsyncValue';
import {
  getStoredBookCover,
  removeStoredBookCover,
  readImageFileAsDataUrl,
} from '../../services/bookCoverStorage';
import {
  authorService,
  bookService,
  categoryService,
  publisherService,
} from '../../services/libraryService';
import type { FormErrors } from '../../types/api';
import type { BookPayload } from '../../types/entities';
import { getErrorMessage } from '../../utils/errors';
import { isBlank } from '../../utils/validation';

interface BookFormValues {
  title: string;
  isbn: string;
  description: string;
  publishYear: string;
  totalCopies: string;
  publisherId: string;
  authorIds: string[];
  categoryIds: string[];
}

function validate(values: BookFormValues): FormErrors<BookFormValues> {
  const errors: FormErrors<BookFormValues> = {};

  if (isBlank(values.title)) {
    errors.title = 'Title is required.';
  }

  if (isBlank(values.isbn)) {
    errors.isbn = 'ISBN is required.';
  }

  if (values.publishYear && Number(values.publishYear) < 1000) {
    errors.publishYear = 'Publication year must be 1000 or greater.';
  }

  if (!values.totalCopies) {
    errors.totalCopies = 'Total copies is required.';
  } else if (Number(values.totalCopies) < 1) {
    errors.totalCopies = 'Total copies must be 1 or greater.';
  }

  if (!values.publisherId) {
    errors.publisherId = 'Select a publisher.';
  }

  if (!values.authorIds.length) {
    errors.authorIds = 'Select at least one author.';
  }

  if (!values.categoryIds.length) {
    errors.categoryIds = 'Select at least one category.';
  }

  return errors;
}

function toPayload(values: BookFormValues, coverImageUrl: string | null): BookPayload {
  return {
    title: values.title.trim(),
    isbn: values.isbn.trim(),
    description: values.description.trim(),
    coverImageUrl,
    publishYear: values.publishYear ? Number(values.publishYear) : null,
    totalCopies: Number(values.totalCopies),
    publisherId: values.publisherId ? Number(values.publisherId) : null,
    authorIds: values.authorIds.map(Number),
    categoryIds: values.categoryIds.map(Number),
  };
}

export function BookFormPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const fromParam = searchParams.get('from');
  const fromSearch = fromParam ? `?from=${encodeURIComponent(fromParam)}` : '';
  const id = useParams().id ? Number(useParams().id) : null;
  const isEditMode = Boolean(id);
  const returnTo = fromParam || '/books';
  const { data, loading, error } = useAsyncValue(async () => {
    const [authors, categories, publishers, book] = await Promise.all([
      authorService.list(),
      categoryService.list(),
      publisherService.list(),
      isEditMode && id ? bookService.getById(id) : Promise.resolve(null),
    ]);

    return { authors, categories, publishers, book };
  }, [id, isEditMode]);
  const [values, setValues] = useState<BookFormValues>({
    title: '',
    isbn: '',
    description: '',
    publishYear: '',
    totalCopies: '3',
    publisherId: '',
    authorIds: [],
    categoryIds: [],
  });
  const [errors, setErrors] = useState<FormErrors<BookFormValues>>({});
  const [apiError, setApiError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [coverPreview, setCoverPreview] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    if (data?.book) {
      setValues({
        title: data.book.title,
        isbn: data.book.isbn,
        description: data.book.description || '',
        publishYear: data.book.publishYear ? String(data.book.publishYear) : '',
        totalCopies: String(data.book.totalCopies || 3),
        publisherId: String(data.book.publisherId),
        authorIds: data.book.authorIds.map(String),
        categoryIds: data.book.categoryIds.map(String),
      });
      const serverCoverUrl = data.book.coverImageUrl || null;
      setCoverPreview(serverCoverUrl);

      if (serverCoverUrl) {
        return () => {
          cancelled = true;
        };
      }

      getStoredBookCover(data.book.id)
        .then((storedCover) => {
          if (!cancelled) {
            setCoverPreview(storedCover);
          }
        })
        .catch(() => {
          if (!cancelled) {
            setCoverPreview(null);
          }
        });

      return () => {
        cancelled = true;
      };
    }

    setCoverPreview(null);
    return () => {
      cancelled = true;
    };
  }, [data]);

  if (loading) {
    return <LoadingState title="Loading book form..." />;
  }

  if (error || !data) {
    return <ErrorState description={error || 'The book form could not be prepared.'} />;
  }

  function toggleSelection(field: 'authorIds' | 'categoryIds', value: string) {
    setValues((current) => ({
      ...current,
      [field]: current[field].includes(value)
        ? current[field].filter((item) => item !== value)
        : [...current[field], value],
    }));
  }

  async function handleCoverChange(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];

    if (!file) {
      return;
    }

    try {
      const nextPreview = await readImageFileAsDataUrl(file);
      setCoverPreview(nextPreview);
    } catch (coverError) {
      setApiError(getErrorMessage(coverError));
    } finally {
      event.target.value = '';
    }
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const nextErrors = validate(values);

    setErrors(nextErrors);
    setApiError(null);

    if (Object.keys(nextErrors).length) {
      return;
    }

    setIsSubmitting(true);

    try {
      const payload = toPayload(values, coverPreview);

      if (isEditMode && id) {
        await bookService.update(id, payload);
        removeStoredBookCover(id).catch(() => undefined);
        navigate(`/books/${id}${fromSearch}`);
      } else {
        const created = await bookService.create(payload);
        navigate(`/books/${created.id}${fromSearch}`);
      }
    } catch (submitError) {
      setApiError(getErrorMessage(submitError));
    } finally {
      setIsSubmitting(false);
    }
  }

  const authorOptions = data.authors.map((author) => ({
    value: String(author.id),
    label: `${author.firstName} ${author.lastName}`,
  }));
  const categoryOptions = data.categories.map((category) => ({
    value: String(category.id),
    label: category.name,
  }));
  const publisherOptions = data.publishers.map((publisher) => ({
    value: String(publisher.id),
    label: publisher.name,
    hint: publisher.country,
  }));

  return (
    <div className="page-layout">
      <PageHeader
        breadcrumbs={[
          { label: 'Books', to: returnTo },
          { label: isEditMode ? 'Edit book' : 'New book' },
        ]}
        eyebrow={isEditMode ? 'Edit book' : 'Create book'}
        title={isEditMode ? 'Update catalog record' : 'Add a new catalog record'}
        description="Fill in catalog data, cover image, and the number of copies."
        actions={
          <Link to={returnTo} className="button button--ghost">
            <ArrowLeft size={16} />
            Back
          </Link>
        }
      />

      <SurfaceCard className="form-card">
        {apiError ? <AlertBanner tone="danger" title="Unable to save book" description={apiError} /> : null}

        <form className="form-layout" onSubmit={handleSubmit}>
          <div className="form-grid">
            <FormField label="Title" error={errors.title} required>
              <TextInput
                value={values.title}
                hasError={Boolean(errors.title)}
                placeholder="The Left Hand of Darkness"
                onChange={(event) => setValues({ ...values, title: event.target.value })}
              />
            </FormField>

            <FormField label="ISBN" error={errors.isbn} required>
              <TextInput
                value={values.isbn}
                hasError={Boolean(errors.isbn)}
                placeholder="9780441478125"
                onChange={(event) => setValues({ ...values, isbn: event.target.value })}
              />
            </FormField>
          </div>

          <FormField label="Description">
            <TextAreaInput
              rows={5}
              value={values.description}
              placeholder="Add a short catalog summary..."
              onChange={(event) => setValues({ ...values, description: event.target.value })}
            />
          </FormField>

          <div className="form-grid">
            <FormField label="Publication year" error={errors.publishYear}>
              <TextInput
                type="number"
                min="1000"
                value={values.publishYear}
                hasError={Boolean(errors.publishYear)}
                placeholder="1969"
                onChange={(event) => setValues({ ...values, publishYear: event.target.value })}
              />
            </FormField>

            <FormField label="Total copies" error={errors.totalCopies} required>
              <TextInput
                type="number"
                min="1"
                value={values.totalCopies}
                hasError={Boolean(errors.totalCopies)}
                placeholder="3"
                onChange={(event) => setValues({ ...values, totalCopies: event.target.value })}
              />
            </FormField>
          </div>

          <FormField
            label="Authors"
            error={errors.authorIds}
            required
            hint="Search and pick one or more authors."
          >
            <SearchableMultiSelect
              values={values.authorIds}
              onToggle={(value) => toggleSelection('authorIds', value)}
              options={authorOptions}
              searchPlaceholder="Search authors"
              emptyMessage="No authors match this search."
            />
          </FormField>

          <FormField
            label="Cover image"
            hint="Saved with the book and visible on every device."
          >
            <div className="cover-upload">
              <div className="cover-upload__preview">
                <BookCover title={values.title || 'Book preview'} coverUrl={coverPreview} />
              </div>

              <div className="cover-upload__actions">
                <label className="button button--secondary cover-upload__label">
                  <ImagePlus size={16} />
                  Upload cover
                  <input
                    type="file"
                    accept="image/*"
                    className="cover-upload__input"
                    onChange={handleCoverChange}
                  />
                </label>

                {coverPreview ? (
                  <button
                    type="button"
                    className="button button--ghost-danger"
                    onClick={() => {
                      setCoverPreview(null);
                    }}
                  >
                    <Trash2 size={16} />
                    Remove cover
                  </button>
                ) : null}
              </div>
            </div>
          </FormField>

          <FormField
            label="Categories"
            error={errors.categoryIds}
            required
            hint="Search and pick one or more categories."
          >
            <SearchableMultiSelect
              values={values.categoryIds}
              onToggle={(value) => toggleSelection('categoryIds', value)}
              options={categoryOptions}
              searchPlaceholder="Search categories"
              emptyMessage="No categories match this search."
            />
          </FormField>

          <FormField label="Publisher" error={errors.publisherId} required>
            <SearchableSelect
              value={values.publisherId}
              onChange={(value) => setValues({ ...values, publisherId: value })}
              hasError={Boolean(errors.publisherId)}
              options={publisherOptions}
              searchPlaceholder="Search publisher"
              emptyMessage="No publishers match this search."
            />
          </FormField>

          <div className="form-actions">
            <button type="submit" className="button button--primary" disabled={isSubmitting}>
              <Save size={16} />
              {isSubmitting ? 'Saving...' : 'Save book'}
            </button>
          </div>
        </form>
      </SurfaceCard>
    </div>
  );
}
