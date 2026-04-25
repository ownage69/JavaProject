import type { PropsWithChildren, ReactNode } from 'react';

interface FormFieldProps extends PropsWithChildren {
  label: string;
  hint?: string;
  error?: string;
  required?: boolean;
  actions?: ReactNode;
}

export function FormField({
  label,
  hint,
  error,
  required,
  actions,
  children,
}: FormFieldProps) {
  return (
    <div className="form-field">
      <span className="form-field__label-row">
        <span className="form-field__label">
          {label}
          {required ? <strong> *</strong> : null}
        </span>
        {actions ? <span>{actions}</span> : null}
      </span>
      {hint ? <small className="form-field__hint">{hint}</small> : null}
      {children}
      {error ? <small className="form-field__error">{error}</small> : null}
    </div>
  );
}
