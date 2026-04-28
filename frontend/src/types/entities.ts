export interface Author {
  id: number;
  firstName: string;
  lastName: string;
  biography?: string | null;
}

export interface AuthorPayload {
  firstName: string;
  lastName: string;
}

export interface Category {
  id: number;
  name: string;
}

export interface CategoryPayload {
  name: string;
}

export interface Publisher {
  id: number;
  name: string;
  country: string;
  address?: string | null;
}

export interface PublisherPayload {
  name: string;
  country: string;
}

export interface Reader {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string | null;
}

export interface ReaderPayload {
  firstName: string;
  lastName: string;
  email: string;
}

export interface Book {
  id: number;
  title: string;
  isbn: string;
  description?: string | null;
  coverImageUrl?: string | null;
  publishYear?: number | null;
  totalCopies?: number | null;
  availableCopies?: number | null;
  publisherId: number;
  publisherName?: string | null;
  authorIds: number[];
  authorNames: string[];
  categoryIds: number[];
  categoryNames: string[];
  available?: boolean;
}

export interface BookPayload {
  title: string;
  isbn: string;
  description: string;
  coverImageUrl: string | null;
  publishYear: number | null;
  totalCopies: number;
  publisherId: number | null;
  authorIds: number[];
  categoryIds: number[];
}

export interface BookPage {
  content: Book[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  queryType: string;
}

export interface Loan {
  id: number;
  bookId: number;
  bookTitle: string;
  readerId: number;
  readerName: string;
  loanDate: string;
  dueDate: string;
  returned: boolean;
  // Optional extension point: add this field in the backend DTO when a return date becomes available.
  returnDate?: string | null;
  status?: string;
}

export interface LoanPayload {
  bookId: number | null;
  readerId: number | null;
  dueDate: string;
}

export interface DashboardOverview {
  totalBooks: number;
  totalAuthors: number;
  totalCategories: number;
  totalPublishers: number;
  totalReaders: number;
  activeLoansCount: number;
  latestLoans: Loan[];
}
