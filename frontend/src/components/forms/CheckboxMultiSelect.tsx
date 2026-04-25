import type { SelectOption } from '../../types/api';

interface CheckboxMultiSelectProps {
  options: SelectOption[];
  values: string[];
  onToggle: (value: string) => void;
}

export function CheckboxMultiSelect({
  options,
  values,
  onToggle,
}: CheckboxMultiSelectProps) {
  return (
    <div className="checkbox-grid">
      {options.map((option) => {
        const checked = values.includes(option.value);

        return (
          <label key={option.value} className={`checkbox-card ${checked ? 'checkbox-card--active' : ''}`}>
            <input
              type="checkbox"
              checked={checked}
              onChange={() => onToggle(option.value)}
              disabled={option.disabled}
            />
            <span>
              <strong>{option.label}</strong>
              {option.hint ? <small>{option.hint}</small> : null}
            </span>
          </label>
        );
      })}
    </div>
  );
}
