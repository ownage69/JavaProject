import { runtimeConfig } from '../config/runtime';

function replaceId(pathTemplate: string, id: number): string {
  return pathTemplate.replace(':id', String(id));
}

export const endpoints = {
  auth: {
    login: runtimeConfig.auth.loginPath,
    logout: runtimeConfig.auth.logoutPath,
    currentUser: runtimeConfig.auth.currentUserPath,
  },
  books: {
    list: '/books',
    detail: (id: number) => `/books/${id}`,
    searchByAuthor: '/books/search',
    filterJpql: '/books/filter/jpql',
    filterNative: '/books/filter/native',
  },
  authors: {
    list: '/authors',
    detail: (id: number) => `/authors/${id}`,
  },
  categories: {
    list: '/categories',
    detail: (id: number) => `/categories/${id}`,
  },
  publishers: {
    list: '/publishers',
    detail: (id: number) => `/publishers/${id}`,
  },
  readers: {
    list: '/readers',
    detail: (id: number) => `/readers/${id}`,
  },
  loans: {
    list: '/loans',
    detail: (id: number) => `/loans/${id}`,
    bulkWithoutTransaction: '/loans/bulk/without-transaction',
    bulkWithTransaction: '/loans/bulk/with-transaction',
    // Backend placeholder: adjust the path once you implement an explicit return endpoint.
    returnLoan: (id: number) => replaceId(runtimeConfig.features.loanReturnPath, id),
  },
};
