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
import { publisherService } from '../../services/libraryService';
import type { FormErrors } from '../../types/api';
import { getErrorMessage } from '../../utils/errors';
import { isBlank } from '../../utils/validation';

interface PublisherFormValues {
  name: string;
  country: string;
}

function validate(values: PublisherFormValues): FormErrors<PublisherFormValues> {
  const errors: FormErrors<PublisherFormValues> = {};

  if (isBlank(values.name)) {
    errors.name = 'Publisher name is required.';
  }

  if (isBlank(values.country)) {
    errors.country = 'Country is required.';
  }

  return errors;
}

export function PublisherFormPage() {
  const navigate = useNavigate();
  const id = useParams().id ? Number(useParams().id) : null;
  const isEditMode = Boolean(id);
  const { data, loading, error } = useAsyncValue(
    () => (isEditMode && id ? publisherService.getById(id) : Promise.resolve(null)),
    [id, isEditMode],
  );
  const [values, setValues] = useState<PublisherFormValues>({
    name: '',
    country: '',
  });
  const [errors, setErrors] = useState<FormErrors<PublisherFormValues>>({});
  const [apiError, setApiError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (data) {
      setValues({
        name: data.name,
        country: data.country,
      });
    }
  }, [data]);

  if (loading) {
    return <LoadingState title="Loading publisher form..." />;
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
        await publisherService.update(id, values);
        navigate(`/publishers/${id}`);
      } else {
        const created = await publisherService.create(values);
        navigate(`/publishers/${created.id}`);
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
          { label: 'Publishers', to: '/publishers' },
          { label: isEditMode ? 'Edit publisher' : 'New publisher' },
        ]}
        eyebrow={isEditMode ? 'Edit publisher' : 'Create publisher'}
        title={isEditMode ? 'Update publisher' : 'Create publisher'}
        description="Keep publisher details polished and ready for catalog filtering."
        actions={
          <Link to="/publishers" className="button button--ghost">
            <ArrowLeft size={16} />
            Back
          </Link>
        }
      />

      <SurfaceCard className="form-card">
        {apiError ? <AlertBanner tone="danger" title="Unable to save publisher" description={apiError} /> : null}

        <form className="form-layout" onSubmit={handleSubmit}>
          <FormField label="Publisher name" error={errors.name} required>
            <TextInput
              value={values.name}
              hasError={Boolean(errors.name)}
              placeholder="Secker & Warburg"
              onChange={(event) => setValues({ ...values, name: event.target.value })}
            />
          </FormField>

          <FormField label="Country" error={errors.country} required>
            <TextInput
              value={values.country}
              hasError={Boolean(errors.country)}
              placeholder="United Kingdom"
              onChange={(event) => setValues({ ...values, country: event.target.value })}
            />
          </FormField>

          <div className="form-actions">
            <button type="submit" className="button button--primary" disabled={isSubmitting}>
              <Save size={16} />
              {isSubmitting ? 'Saving...' : 'Save publisher'}
            </button>
          </div>
        </form>
      </SurfaceCard>
    </div>
  );
}
