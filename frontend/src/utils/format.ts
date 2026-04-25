import type { Author, Loan, Reader } from '../types/entities';

const shortDateFormatter = new Intl.DateTimeFormat('en', {
  day: 'numeric',
  month: 'short',
  year: 'numeric',
});

function parseCalendarDate(value: string): Date | null {
  const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(value);

  if (!match) {
    return null;
  }

  const [, year, month, day] = match;
  return new Date(Number(year), Number(month) - 1, Number(day));
}

export function formatDate(value?: string | null, fallback = 'Not available'): string {
  if (!value) {
    return fallback;
  }

  const date = parseCalendarDate(value) || new Date(value);

  if (Number.isNaN(date.getTime())) {
    return fallback;
  }

  return shortDateFormatter.format(date);
}

export function getAuthorFullName(author: Author): string {
  return `${author.firstName} ${author.lastName}`.trim();
}

export function getReaderFullName(reader: Reader): string {
  return `${reader.firstName} ${reader.lastName}`.trim();
}

export function formatLoanStatusLabel(loan: Loan): string {
  if (loan.returned) {
    return 'Returned';
  }

  const today = new Date();
  const currentDate = new Date(today.getFullYear(), today.getMonth(), today.getDate());
  const dueDate = parseCalendarDate(loan.dueDate);

  if (dueDate && dueDate < currentDate) {
    return 'Overdue';
  }

  return 'Active';
}

export function formatCount(value: number, singular: string, plural = `${singular}s`): string {
  return `${value} ${value === 1 ? singular : plural}`;
}
