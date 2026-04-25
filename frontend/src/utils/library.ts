import type { Author, Book, Category, Loan, Publisher, Reader } from '../types/entities';

export function matchesText(value: string | number | null | undefined, query: string): boolean {
  if (!query.trim()) {
    return true;
  }

  return String(value || '')
    .toLowerCase()
    .includes(query.trim().toLowerCase());
}

function resolveTotalCopies(book: Pick<Book, 'totalCopies'>): number {
  if (typeof book.totalCopies === 'number' && book.totalCopies > 0) {
    return book.totalCopies;
  }

  return 3;
}

export function getAvailableCopies(
  book: Pick<Book, 'id' | 'totalCopies' | 'availableCopies'>,
  loans: Loan[] = [],
): number {
  if (typeof book.availableCopies === 'number') {
    return book.availableCopies;
  }

  const activeLoans = loans.filter((loan) => loan.bookId === book.id && !loan.returned).length;
  return Math.max(resolveTotalCopies(book) - activeLoans, 0);
}

export function getBookAvailability(
  book: Pick<Book, 'id' | 'totalCopies' | 'availableCopies'>,
  loans: Loan[] = [],
): 'available' | 'loaned' {
  return getAvailableCopies(book, loans) > 0 ? 'available' : 'loaned';
}

export function getBooksForAuthor(books: Book[], authorId: number): Book[] {
  return books.filter((book) => book.authorIds.includes(authorId));
}

export function getBooksForCategory(books: Book[], categoryId: number): Book[] {
  return books.filter((book) => book.categoryIds.includes(categoryId));
}

export function getBooksForPublisher(books: Book[], publisherId: number): Book[] {
  return books.filter((book) => book.publisherId === publisherId);
}

export function getLoansForReader(loans: Loan[], readerId: number): Loan[] {
  return loans
    .filter((loan) => loan.readerId === readerId)
    .sort((left, right) => right.loanDate.localeCompare(left.loanDate));
}

export function findById<T extends { id: number }>(
  collection: T[],
  id: number | null | undefined,
): T | undefined {
  if (!id) {
    return undefined;
  }

  return collection.find((item) => item.id === id);
}

export function sortBooks(books: Book[], sortBy: string): Book[] {
  const sorted = [...books];

  sorted.sort((left, right) => {
    if (sortBy === 'author') {
      return (left.authorNames[0] || '').localeCompare(right.authorNames[0] || '');
    }

    if (sortBy === 'year') {
      return (right.publishYear || 0) - (left.publishYear || 0);
    }

    return left.title.localeCompare(right.title);
  });

  return sorted;
}

export function countBooksForAuthor(books: Book[], author: Author): number {
  return getBooksForAuthor(books, author.id).length;
}

export function countBooksForCategory(books: Book[], category: Category): number {
  return getBooksForCategory(books, category.id).length;
}

export function countBooksForPublisher(books: Book[], publisher: Publisher): number {
  return getBooksForPublisher(books, publisher.id).length;
}

export function countActiveLoansForReader(loans: Loan[], reader: Reader): number {
  return loans.filter((loan) => loan.readerId === reader.id && !loan.returned).length;
}
