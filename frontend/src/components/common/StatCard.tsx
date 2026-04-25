import type { LucideIcon } from 'lucide-react';

interface StatCardProps {
  title: string;
  value: string | number;
  helper: string;
  icon: LucideIcon;
  tone?: 'green' | 'burgundy' | 'blue' | 'gold';
}

export function StatCard({
  title,
  value,
  helper,
  icon: Icon,
  tone = 'green',
}: StatCardProps) {
  return (
    <article className={`stat-card stat-card--${tone}`}>
      <div className="stat-card__icon">
        <Icon size={20} />
      </div>
      <div>
        <p className="stat-card__title">{title}</p>
        <strong className="stat-card__value">{value}</strong>
        <span className="stat-card__helper">{helper}</span>
      </div>
    </article>
  );
}
