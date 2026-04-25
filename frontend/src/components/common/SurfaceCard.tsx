import type { PropsWithChildren, ReactNode } from 'react';

interface SurfaceCardProps extends PropsWithChildren {
  className?: string;
  header?: ReactNode;
}

export function SurfaceCard({ children, className = '', header }: SurfaceCardProps) {
  return (
    <section className={`surface-card ${className}`.trim()}>
      {header ? <div className="surface-card__header">{header}</div> : null}
      {children}
    </section>
  );
}
