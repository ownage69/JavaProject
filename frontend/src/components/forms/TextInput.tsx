import type { InputHTMLAttributes } from 'react';

interface TextInputProps extends InputHTMLAttributes<HTMLInputElement> {
  hasError?: boolean;
}

export function TextInput({ hasError = false, className = '', ...props }: TextInputProps) {
  return <input {...props} className={`text-input ${hasError ? 'text-input--error' : ''} ${className}`.trim()} />;
}
