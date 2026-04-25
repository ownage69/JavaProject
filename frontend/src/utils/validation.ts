export function isBlank(value: string | null | undefined): boolean {
  return !value || !value.trim();
}

function parseCalendarDate(value: string): Date | null {
  const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(value);

  if (!match) {
    return null;
  }

  const [, year, month, day] = match;
  return new Date(Number(year), Number(month) - 1, Number(day));
}

export function isEmail(value: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
}

export function isTodayOrFuture(value: string): boolean {
  if (!value) {
    return false;
  }

  const today = new Date();
  const currentDate = new Date(today.getFullYear(), today.getMonth(), today.getDate());
  const candidate = parseCalendarDate(value);

  if (!candidate) {
    return false;
  }

  const normalizedCandidate = new Date(
    candidate.getFullYear(),
    candidate.getMonth(),
    candidate.getDate(),
  );

  return normalizedCandidate >= currentDate;
}
