import type { ReactNode } from 'react';

interface AlertBannerProps {
  tone?: 'info' | 'warning' | 'danger' | 'success';
  title: string;
  description: ReactNode;
}

export function AlertBanner({
  tone = 'info',
  title,
  description,
}: AlertBannerProps) {
  return (
    <div className={`alert-banner alert-banner--${tone}`}>
      <strong>{title}</strong>
      <p>{description}</p>
    </div>
  );
}
