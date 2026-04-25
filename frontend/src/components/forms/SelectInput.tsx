import type { SelectHTMLAttributes } from 'react';
import type { SelectOption } from '../../types/api';

interface SelectInputProps extends SelectHTMLAttributes<HTMLSelectElement> {
  hasError?: boolean;
  options: SelectOption[];
}

export function SelectInput({
  hasError = false,
  className = '',
  options,
  ...props
}: SelectInputProps) {
  return (
    <select
      {...props}
      className={`select-input ${hasError ? 'select-input--error' : ''} ${className}`.trim()}
    >
      {options.map((option) => (
        <option key={option.value} value={option.value} disabled={option.disabled}>
          {option.label}
        </option>
      ))}
    </select>
  );
}
