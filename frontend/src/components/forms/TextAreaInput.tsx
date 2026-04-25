import type { TextareaHTMLAttributes } from 'react';

interface TextAreaInputProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  hasError?: boolean;
}

export function TextAreaInput({
  hasError = false,
  className = '',
  ...props
}: TextAreaInputProps) {
  return (
    <textarea
      {...props}
      className={`text-area ${hasError ? 'text-area--error' : ''} ${className}`.trim()}
    />
  );
}
