import type {
  Author,
  AuthorPayload,
  Book,
  BookPage,
  BookPayload,
  Category,
  CategoryPayload,
  DashboardOverview,
  Loan,
  LoanPayload,
  Publisher,
  PublisherPayload,
  Reader,
  ReaderPayload,
} from '../types/entities';
import { runtimeConfig } from '../config/runtime';
import { endpoints } from './endpoints';
import { apiClient, getResponseData } from './http';

interface CrudEndpoints {
  list: string;
  detail: (id: number) => string;
}

function createCrudService<TEntity, TPayload>(endpoint: CrudEndpoints) {
  return {
    list: async (): Promise<TEntity[]> => apiClient.get(endpoint.list).then(getResponseData),
    getById: async (id: number): Promise<TEntity> =>
      apiClient.get(endpoint.detail(id)).then(getResponseData),
    create: async (payload: TPayload): Promise<TEntity> =>
      apiClient.post(endpoint.list, payload).then(getResponseData),
    update: async (id: number, payload: TPayload): Promise<TEntity> =>
      apiClient.put(endpoint.detail(id), payload).then(getResponseData),
    remove: async (id: number): Promise<void> => {
      await apiClient.delete(endpoint.detail(id));
    },
  };
}

const authorCrud = createCrudService<Author, AuthorPayload>(endpoints.authors);
const categoryCrud = createCrudService<Category, CategoryPayload>(endpoints.categories);
const publisherCrud = createCrudService<Publisher, PublisherPayload>(endpoints.publishers);
const readerCrud = createCrudService<Reader, ReaderPayload>(endpoints.readers);
const bookCrud = createCrudService<Book, BookPayload>(endpoints.books);
const loanCrud = createCrudService<Loan, LoanPayload>(endpoints.loans);

export const authorService = authorCrud;
export const categoryService = categoryCrud;
export const publisherService = publisherCrud;
export const readerService = readerCrud;

export const bookService = {
  ...bookCrud,
  searchByAuthor: async (author: string): Promise<Book[]> =>
    apiClient
      .get(endpoints.books.searchByAuthor, {
        params: {
          author,
        },
      })
      .then(getResponseData),
  filterJpql: async (params: Record<string, string | number | undefined>): Promise<BookPage> =>
    apiClient
      .get(endpoints.books.filterJpql, {
        params,
      })
      .then(getResponseData),
  filterNative: async (params: Record<string, string | number | undefined>): Promise<BookPage> =>
    apiClient
      .get(endpoints.books.filterNative, {
        params,
      })
      .then(getResponseData),
};

export const loanService = {
  ...loanCrud,
  createBulkWithoutTransaction: async (payload: LoanPayload[]): Promise<Loan[]> =>
    apiClient.post(endpoints.loans.bulkWithoutTransaction, payload).then(getResponseData),
  createBulkWithTransaction: async (payload: LoanPayload[]): Promise<Loan[]> =>
    apiClient.post(endpoints.loans.bulkWithTransaction, payload).then(getResponseData),
  returnBook: async (loanId: number): Promise<Loan | null> => {
    if (!runtimeConfig.features.loanReturnEnabled) {
      throw new Error(
        'Return action is not configured yet. Enable it in frontend/.env after adding a backend endpoint.',
      );
    }

    return apiClient
      .request({
        method: runtimeConfig.features.loanReturnMethod,
        url: endpoints.loans.returnLoan(loanId),
      })
      .then(getResponseData);
  },
};

export const dashboardService = {
  async getOverview(): Promise<DashboardOverview> {
    const [books, authors, categories, publishers, readers, loans] = await Promise.all([
      bookService.list(),
      authorService.list(),
      categoryService.list(),
      publisherService.list(),
      readerService.list(),
      loanService.list(),
    ]);

    const latestLoans = [...loans]
      .sort((left, right) => right.loanDate.localeCompare(left.loanDate))
      .slice(0, 6);

    return {
      totalBooks: books.length,
      totalAuthors: authors.length,
      totalCategories: categories.length,
      totalPublishers: publishers.length,
      totalReaders: readers.length,
      activeLoansCount: loans.filter((loan) => !loan.returned).length,
      latestLoans,
    };
  },
};
