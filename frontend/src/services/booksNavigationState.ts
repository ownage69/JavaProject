const BOOKS_RETURN_TO_KEY = 'library.books.return-to';
const BOOKS_CURRENT_PAGE_KEY = 'library.books.current-page';

function isBrowser() {
  return typeof window !== 'undefined';
}

export function rememberBooksReturnTo(path: string) {
  if (!isBrowser()) {
    return;
  }

  window.sessionStorage.setItem(BOOKS_RETURN_TO_KEY, path);
}

export function readBooksReturnTo() {
  if (!isBrowser()) {
    return null;
  }

  return window.sessionStorage.getItem(BOOKS_RETURN_TO_KEY);
}

export function rememberBooksCurrentPage(page: number) {
  if (!isBrowser()) {
    return;
  }

  window.sessionStorage.setItem(BOOKS_CURRENT_PAGE_KEY, String(page));
}

export function readBooksCurrentPage() {
  if (!isBrowser()) {
    return null;
  }

  const rawValue = window.sessionStorage.getItem(BOOKS_CURRENT_PAGE_KEY);
  const parsed = Number(rawValue);

  return Number.isInteger(parsed) && parsed > 0 ? parsed : null;
}
