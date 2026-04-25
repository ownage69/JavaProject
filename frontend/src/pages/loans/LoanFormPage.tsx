import { ArrowLeft, Save } from 'lucide-react';
import { useEffect, useState, type FormEvent } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { AlertBanner } from '../../components/common/AlertBanner';
import { ErrorState } from '../../components/common/ErrorState';
import { LoadingState } from '../../components/common/LoadingState';
import { PageHeader } from '../../components/common/PageHeader';
import { SurfaceCard } from '../../components/common/SurfaceCard';
import { FormField } from '../../components/forms/FormField';
import { SearchableSelect } from '../../components/forms/SearchableSelect';
import { TextInput } from '../../components/forms/TextInput';
import { useAsyncValue } from '../../hooks/useAsyncValue';
import { bookService, loanService, readerService } from '../../services/libraryService';
import type { FormErrors } from '../../types/api';
import type { LoanPayload } from '../../types/entities';
import { getErrorMessage } from '../../utils/errors';
import { getAvailableCopies, getBookAvailability } from '../../utils/library';
import { isTodayOrFuture } from '../../utils/validation';

interface LoanFormValues {
  bookId: string;
  readerId: string;
  dueDate: string;
}

function validate(values: LoanFormValues): FormErrors<LoanFormValues> {
  const errors: FormErrors<LoanFormValues> = {};

  if (!values.bookId) {
    errors.bookId = 'Select a book.';
  }

  if (!values.readerId) {
    errors.readerId = 'Select a reader.';
  }

  if (!values.dueDate) {
    errors.dueDate = 'Due date is required.';
  } else if (!isTodayOrFuture(values.dueDate)) {
    errors.dueDate = 'Due date must be today or in the future.';
  }

  return errors;
}

function toPayload(values: LoanFormValues): LoanPayload {
  return {
    bookId: values.bookId ? Number(values.bookId) : null,
    readerId: values.readerId ? Number(values.readerId) : null,
    dueDate: values.dueDate,
  };
}

export function LoanFormPage() {
  const navigate = useNavigate();
  const id = useParams().id ? Number(useParams().id) : null;
  const isEditMode = Boolean(id);
  const { data, loading, error } = useAsyncValue(async () => {
    const [books, readers, loans, loan] = await Promise.all([
      bookService.list(),
      readerService.list(),
      loanService.list(),
      isEditMode && id ? loanService.getById(id) : Promise.resolve(null),
    ]);

    return { books, readers, loans, loan };
  }, [id, isEditMode]);
  const [values, setValues] = useState<LoanFormValues>({
    bookId: '',
    readerId: '',
    dueDate: '',
  });
  const [errors, setErrors] = useState<FormErrors<LoanFormValues>>({});
  const [apiError, setApiError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (data?.loan) {
      setValues({
        bookId: String(data.loan.bookId),
        readerId: String(data.loan.readerId),
        dueDate: data.loan.dueDate,
      });
    }
  }, [data]);

  if (loading) {
    return <LoadingState title="Loading loan form..." />;
  }

  if (error || !data) {
    return <ErrorState description={error || 'The loan form could not be prepared.'} />;
  }

  const availableBooks = data.books.filter(
    (book) =>
      (data.loan && data.loan.bookId === book.id) ||
      getBookAvailability(book, data.loans) === 'available',
  );

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
      const payload = toPayload(values);

      if (isEditMode && id) {
        await loanService.update(id, payload);
        navigate(`/loans/${id}`);
      } else {
        const created = await loanService.create(payload);
        navigate(`/loans/${created.id}`);
      }
    } catch (submitError) {
      setApiError(getErrorMessage(submitError));
    } finally {
      setIsSubmitting(false);
    }
  }

  const bookOptions = availableBooks.map((book) => {
    const availableCopies = getAvailableCopies(book, data.loans);
    const totalCopies = book.totalCopies || 3;

    return {
      value: String(book.id),
      label: book.title,
      hint: `${book.authorNames.join(', ')} • ${availableCopies}/${totalCopies} available`,
    };
  });

  const readerOptions = data.readers.map((reader) => ({
    value: String(reader.id),
    label: `${reader.firstName} ${reader.lastName}`,
    hint: reader.email,
  }));

  return (
    <div className="page-layout">
      <PageHeader
        breadcrumbs={[
          { label: 'Loans', to: '/loans' },
          { label: isEditMode ? 'Edit loan' : 'New loan' },
        ]}
        eyebrow={isEditMode ? 'Edit loan' : 'Create loan'}
        title={isEditMode ? 'Update loan record' : 'Create a new loan'}
        description="Choose a book with an available copy, link a reader, and set a due date."
        actions={
          <Link to="/loans" className="button button--ghost">
            <ArrowLeft size={16} />
            Back
          </Link>
        }
      />

      {!availableBooks.length && !isEditMode ? (
        <AlertBanner
          tone="warning"
          title="No available books right now"
          description="All copies are currently on loan. Return a book first or increase the copy count."
        />
      ) : null}

      <SurfaceCard className="form-card">
        {apiError ? <AlertBanner tone="danger" title="Unable to save loan" description={apiError} /> : null}

        <form className="form-layout" onSubmit={handleSubmit}>
          <FormField
            label="Book"
            error={errors.bookId}
            required
            hint="Search the catalog and choose a book with available copies."
          >
            <SearchableSelect
              value={values.bookId}
              onChange={(value) => setValues({ ...values, bookId: value })}
              hasError={Boolean(errors.bookId)}
              options={bookOptions}
              searchPlaceholder="Search book"
              emptyMessage="No books match this search."
            />
          </FormField>

          <FormField label="Reader" error={errors.readerId} required hint="Search reader by name or email.">
            <SearchableSelect
              value={values.readerId}
              onChange={(value) => setValues({ ...values, readerId: value })}
              hasError={Boolean(errors.readerId)}
              options={readerOptions}
              searchPlaceholder="Search reader"
              emptyMessage="No readers match this search."
            />
          </FormField>

          <FormField label="Due date" error={errors.dueDate} required>
            <TextInput
              type="date"
              value={values.dueDate}
              hasError={Boolean(errors.dueDate)}
              onChange={(event) => setValues({ ...values, dueDate: event.target.value })}
            />
          </FormField>

          <div className="form-actions">
            <button type="submit" className="button button--primary" disabled={isSubmitting}>
              <Save size={16} />
              {isSubmitting ? 'Saving...' : 'Save loan'}
            </button>
          </div>
        </form>
      </SurfaceCard>
    </div>
  );
}
