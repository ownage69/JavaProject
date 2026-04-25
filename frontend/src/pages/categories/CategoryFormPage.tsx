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
import { categoryService } from '../../services/libraryService';
import type { FormErrors } from '../../types/api';
import { getErrorMessage } from '../../utils/errors';
import { isBlank } from '../../utils/validation';

interface CategoryFormValues {
  name: string;
}

function validate(values: CategoryFormValues): FormErrors<CategoryFormValues> {
  const errors: FormErrors<CategoryFormValues> = {};

  if (isBlank(values.name)) {
    errors.name = 'Category name is required.';
  }

  return errors;
}

export function CategoryFormPage() {
  const navigate = useNavigate();
  const id = useParams().id ? Number(useParams().id) : null;
  const isEditMode = Boolean(id);
  const { data, loading, error } = useAsyncValue(
    () => (isEditMode && id ? categoryService.getById(id) : Promise.resolve(null)),
    [id, isEditMode],
  );
  const [values, setValues] = useState<CategoryFormValues>({
    name: '',
  });
  const [errors, setErrors] = useState<FormErrors<CategoryFormValues>>({});
  const [apiError, setApiError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (data) {
      setValues({
        name: data.name,
      });
    }
  }, [data]);

  if (loading) {
    return <LoadingState title="Loading category form..." />;
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
        await categoryService.update(id, values);
        navigate(`/categories/${id}`);
      } else {
        const created = await categoryService.create(values);
        navigate(`/categories/${created.id}`);
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
          { label: 'Categories', to: '/categories' },
          { label: isEditMode ? 'Edit category' : 'New category' },
        ]}
        eyebrow={isEditMode ? 'Edit category' : 'Create category'}
        title={isEditMode ? 'Update category' : 'Create category'}
        description="Keep the shelf taxonomy concise, readable, and easy to maintain."
        actions={
          <Link to="/categories" className="button button--ghost">
            <ArrowLeft size={16} />
            Back
          </Link>
        }
      />

      <SurfaceCard className="form-card">
        {apiError ? <AlertBanner tone="danger" title="Unable to save category" description={apiError} /> : null}

        <form className="form-layout" onSubmit={handleSubmit}>
          <FormField label="Category name" error={errors.name} required>
            <TextInput
              value={values.name}
              hasError={Boolean(errors.name)}
              placeholder="Science Fiction"
              onChange={(event) => setValues({ name: event.target.value })}
            />
          </FormField>

          <div className="form-actions">
            <button type="submit" className="button button--primary" disabled={isSubmitting}>
              <Save size={16} />
              {isSubmitting ? 'Saving...' : 'Save category'}
            </button>
          </div>
        </form>
      </SurfaceCard>
    </div>
  );
}
