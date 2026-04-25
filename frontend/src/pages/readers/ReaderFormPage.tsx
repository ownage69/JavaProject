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
import { readerService } from '../../services/libraryService';
import type { FormErrors } from '../../types/api';
import { getErrorMessage } from '../../utils/errors';
import { isBlank, isEmail } from '../../utils/validation';

interface ReaderFormValues {
  firstName: string;
  lastName: string;
  email: string;
}

function validate(values: ReaderFormValues): FormErrors<ReaderFormValues> {
  const errors: FormErrors<ReaderFormValues> = {};

  if (isBlank(values.firstName)) {
    errors.firstName = 'First name is required.';
  }

  if (isBlank(values.lastName)) {
    errors.lastName = 'Last name is required.';
  }

  if (isBlank(values.email)) {
    errors.email = 'Email is required.';
  } else if (!isEmail(values.email)) {
    errors.email = 'Enter a valid email address.';
  }

  return errors;
}

export function ReaderFormPage() {
  const navigate = useNavigate();
  const id = useParams().id ? Number(useParams().id) : null;
  const isEditMode = Boolean(id);
  const { data, loading, error } = useAsyncValue(
    () => (isEditMode && id ? readerService.getById(id) : Promise.resolve(null)),
    [id, isEditMode],
  );
  const [values, setValues] = useState<ReaderFormValues>({
    firstName: '',
    lastName: '',
    email: '',
  });
  const [errors, setErrors] = useState<FormErrors<ReaderFormValues>>({});
  const [apiError, setApiError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (data) {
      setValues({
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
      });
    }
  }, [data]);

  if (loading) {
    return <LoadingState title="Loading reader form..." />;
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
        await readerService.update(id, values);
        navigate(`/readers/${id}`);
      } else {
        const created = await readerService.create(values);
        navigate(`/readers/${created.id}`);
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
          { label: 'Readers', to: '/readers' },
          { label: isEditMode ? 'Edit reader' : 'New reader' },
        ]}
        eyebrow={isEditMode ? 'Edit reader' : 'Create reader'}
        title={isEditMode ? 'Update reader profile' : 'Create a reader record'}
        description="This form creates a reader entry in the library database. Separate user account registration still requires a backend auth module."
        actions={
          <Link to="/readers" className="button button--ghost">
            <ArrowLeft size={16} />
            Back
          </Link>
        }
      />

      <SurfaceCard className="form-card">
        {apiError ? <AlertBanner tone="danger" title="Unable to save reader" description={apiError} /> : null}

        <form className="form-layout" onSubmit={handleSubmit}>
          <div className="form-grid">
            <FormField label="First name" error={errors.firstName} required>
              <TextInput
                value={values.firstName}
                hasError={Boolean(errors.firstName)}
                placeholder="Emily"
                onChange={(event) => setValues({ ...values, firstName: event.target.value })}
              />
            </FormField>

            <FormField label="Last name" error={errors.lastName} required>
              <TextInput
                value={values.lastName}
                hasError={Boolean(errors.lastName)}
                placeholder="Carter"
                onChange={(event) => setValues({ ...values, lastName: event.target.value })}
              />
            </FormField>
          </div>

          <FormField label="Email" error={errors.email} required>
            <TextInput
              type="email"
              value={values.email}
              hasError={Boolean(errors.email)}
              placeholder="emily.carter@example.com"
              onChange={(event) => setValues({ ...values, email: event.target.value })}
            />
          </FormField>

          <div className="form-actions">
            <button type="submit" className="button button--primary" disabled={isSubmitting}>
              <Save size={16} />
              {isSubmitting ? 'Saving...' : 'Save reader'}
            </button>
          </div>
        </form>
      </SurfaceCard>
    </div>
  );
}
