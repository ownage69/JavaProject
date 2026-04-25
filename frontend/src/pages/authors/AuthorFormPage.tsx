import { ArrowLeft, Save } from 'lucide-react';
import { useEffect, useState, type FormEvent } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { AlertBanner } from '../../components/common/AlertBanner';
import { ErrorState } from '../../components/common/ErrorState';
import { LoadingState } from '../../components/common/LoadingState';
import { PageHeader } from '../../components/common/PageHeader';
import { SurfaceCard } from '../../components/common/SurfaceCard';
import { FormField } from '../../components/forms/FormField';
import { TextInput } from '../../components/forms/TextInput';
import { useAsyncValue } from '../../hooks/useAsyncValue';
import { authorService } from '../../services/libraryService';
import type { FormErrors } from '../../types/api';
import { isBlank } from '../../utils/validation';
import { getErrorMessage } from '../../utils/errors';

interface AuthorFormValues {
  firstName: string;
  lastName: string;
}

function validate(values: AuthorFormValues): FormErrors<AuthorFormValues> {
  const errors: FormErrors<AuthorFormValues> = {};

  if (isBlank(values.firstName)) {
    errors.firstName = 'First name is required.';
  }

  if (isBlank(values.lastName)) {
    errors.lastName = 'Last name is required.';
  }

  return errors;
}

export function AuthorFormPage() {
  const navigate = useNavigate();
  const id = useParams().id ? Number(useParams().id) : null;
  const isEditMode = Boolean(id);
  const { data, loading, error } = useAsyncValue(
    () => (isEditMode && id ? authorService.getById(id) : Promise.resolve(null)),
    [id, isEditMode],
  );
  const [values, setValues] = useState<AuthorFormValues>({
    firstName: '',
    lastName: '',
  });
  const [errors, setErrors] = useState<FormErrors<AuthorFormValues>>({});
  const [apiError, setApiError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (data) {
      setValues({
        firstName: data.firstName,
        lastName: data.lastName,
      });
    }
  }, [data]);

  if (loading) {
    return <LoadingState title="Loading author form..." />;
  }

  if (error) {
    return <ErrorState description={error} />;
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
      if (isEditMode && id) {
        await authorService.update(id, values);
        navigate(`/authors/${id}`);
      } else {
        const created = await authorService.create(values);
        navigate(`/authors/${created.id}`);
      }
    } catch (submitError) {
      setApiError(getErrorMessage(submitError));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="page-layout">
      <PageHeader
        breadcrumbs={[
          { label: 'Authors', to: '/authors' },
          { label: isEditMode ? 'Edit author' : 'New author' },
        ]}
        eyebrow={isEditMode ? 'Edit author' : 'Create author'}
        title={isEditMode ? 'Update author record' : 'Create a new author'}
        description="Use a simple, focused form so the author directory stays clean and easy to demonstrate."
        actions={
          <Link to="/authors" className="button button--ghost">
            <ArrowLeft size={16} />
            Back
          </Link>
        }
      />

      <SurfaceCard className="form-card">
        {apiError ? <AlertBanner tone="danger" title="Unable to save author" description={apiError} /> : null}

        <form className="form-layout" onSubmit={handleSubmit}>
          <FormField label="First name" error={errors.firstName} required>
            <TextInput
              value={values.firstName}
              hasError={Boolean(errors.firstName)}
              placeholder="George"
              onChange={(event) => setValues({ ...values, firstName: event.target.value })}
            />
          </FormField>

          <FormField label="Last name" error={errors.lastName} required>
            <TextInput
              value={values.lastName}
              hasError={Boolean(errors.lastName)}
              placeholder="Orwell"
              onChange={(event) => setValues({ ...values, lastName: event.target.value })}
            />
          </FormField>

          <div className="form-actions">
            <button type="submit" className="button button--primary" disabled={isSubmitting}>
              <Save size={16} />
              {isSubmitting ? 'Saving...' : 'Save author'}
            </button>
          </div>
        </form>
      </SurfaceCard>
    </div>
  );
}
