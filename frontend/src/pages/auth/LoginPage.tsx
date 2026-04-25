import { AlertCircle, LogIn } from 'lucide-react';
import { useState, type FormEvent } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { AlertBanner } from '../../components/common/AlertBanner';
import { PageHeader } from '../../components/common/PageHeader';
import { SurfaceCard } from '../../components/common/SurfaceCard';
import { FormField } from '../../components/forms/FormField';
import { TextInput } from '../../components/forms/TextInput';
import { runtimeConfig } from '../../config/runtime';
import { useAuth } from '../../hooks/useAuth';
import { authService } from '../../services/authService';
import type { FormErrors } from '../../types/api';
import { getErrorMessage } from '../../utils/errors';
import { isBlank } from '../../utils/validation';

interface LoginFormValues {
  identity: string;
  password: string;
}

function validate(values: LoginFormValues): FormErrors<LoginFormValues> {
  const errors: FormErrors<LoginFormValues> = {};

  if (isBlank(values.identity)) {
    errors.identity = 'Email or username is required.';
  }

  if (isBlank(values.password)) {
    errors.password = 'Password is required.';
  }

  return errors;
}

export function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();
  const [values, setValues] = useState<LoginFormValues>({
    identity: '',
    password: '',
  });
  const [errors, setErrors] = useState<FormErrors<LoginFormValues>>({});
  const [apiError, setApiError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const redirectTo = new URLSearchParams(location.search).get('redirect') || '/dashboard';

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
      await login(values);
      navigate(redirectTo, { replace: true });
    } catch (error) {
      setApiError(getErrorMessage(error));
    } finally {
      setIsSubmitting(false);
    }
  }

  const demoAccounts = authService.getDemoAccounts();

  return (
    <div className="auth-page">
      <div className="auth-page__intro">
        <PageHeader
          eyebrow="Authentication"
          title="Sign in to enter the library workspace"
          description="Use a clean role-aware login flow that can later switch from mock auth to your real Spring Boot security setup."
        />

        <SurfaceCard className="auth-note-card">
          <div className="auth-note-card__icon">
            <AlertCircle size={20} />
          </div>
          <div>
            <strong>Integration mode</strong>
            <p>
              {runtimeConfig.auth.mode === 'api'
                ? 'The login form is configured for backend authentication endpoints.'
                : 'The login form currently uses mock-safe demo accounts. Your current Spring Boot backend still has readers, but no dedicated users or registration API yet.'}
            </p>
          </div>
        </SurfaceCard>
      </div>

      <SurfaceCard className="auth-card">
        {apiError ? (
          <AlertBanner tone="danger" title="Unable to sign in" description={apiError} />
        ) : null}

        <form className="form-layout" onSubmit={handleSubmit}>
          <FormField label="Email or username" required error={errors.identity}>
            <TextInput
              value={values.identity}
              hasError={Boolean(errors.identity)}
              placeholder="admin@library.local"
              onChange={(event) => setValues({ ...values, identity: event.target.value })}
            />
          </FormField>

          <FormField label="Password" required error={errors.password}>
            <TextInput
              type="password"
              value={values.password}
              hasError={Boolean(errors.password)}
              placeholder="Enter your password"
              onChange={(event) => setValues({ ...values, password: event.target.value })}
            />
          </FormField>

          <button type="submit" className="button button--primary" disabled={isSubmitting}>
            <LogIn size={16} />
            {isSubmitting ? 'Signing in...' : 'Sign in'}
          </button>
        </form>

        {runtimeConfig.auth.mode === 'mock' ? (
          <div className="demo-accounts">
            <strong>Demo accounts</strong>
            {demoAccounts.map((account) => (
              <div key={account.identity} className="demo-accounts__item">
                <span>{account.role === 'admin' ? 'Admin' : 'User'}</span>
                <code>{account.identity}</code>
                <code>{account.password}</code>
              </div>
            ))}
          </div>
        ) : null}

        <p className="auth-card__footer">
          Need a public overview first? <Link to="/">Return to landing page</Link>
        </p>
      </SurfaceCard>
    </div>
  );
}
