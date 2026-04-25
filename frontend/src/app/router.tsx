import { createBrowserRouter } from 'react-router-dom';
import { AppShell } from '../components/layout/AppShell';
import { PublicLayout } from '../components/layout/PublicLayout';
import { AuthorDetailsPage } from '../pages/authors/AuthorDetailsPage';
import { AuthorFormPage } from '../pages/authors/AuthorFormPage';
import { AuthorsPage } from '../pages/authors/AuthorsPage';
import { BookDetailsPage } from '../pages/books/BookDetailsPage';
import { BookFormPage } from '../pages/books/BookFormPage';
import { BooksPage } from '../pages/books/BooksPage';
import { CategoryDetailsPage } from '../pages/categories/CategoryDetailsPage';
import { CategoryFormPage } from '../pages/categories/CategoryFormPage';
import { CategoriesPage } from '../pages/categories/CategoriesPage';
import { DashboardPage } from '../pages/dashboard/DashboardPage';
import { LoanDetailsPage } from '../pages/loans/LoanDetailsPage';
import { LoanFormPage } from '../pages/loans/LoanFormPage';
import { LoansPage } from '../pages/loans/LoansPage';
import { NotFoundPage } from '../pages/NotFoundPage';
import { PublisherDetailsPage } from '../pages/publishers/PublisherDetailsPage';
import { PublisherFormPage } from '../pages/publishers/PublisherFormPage';
import { PublishersPage } from '../pages/publishers/PublishersPage';
import { RouteErrorPage } from '../pages/RouteErrorPage';
import { LandingPage } from '../pages/public/LandingPage';
import { ReaderDetailsPage } from '../pages/readers/ReaderDetailsPage';
import { ReaderFormPage } from '../pages/readers/ReaderFormPage';
import { ReadersPage } from '../pages/readers/ReadersPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <PublicLayout />,
    errorElement: <RouteErrorPage />,
    children: [
      {
        index: true,
        element: <LandingPage />,
      },
      {
        path: 'catalog',
        children: [
          {
            index: true,
            element: <BooksPage variant="public" />,
          },
          {
            path: 'books/:id',
            element: <BookDetailsPage variant="public" />,
          },
        ],
      },
      {
        path: '*',
        element: <NotFoundPage />,
      },
    ],
  },
  {
    element: <AppShell />,
    errorElement: <RouteErrorPage />,
    children: [
      {
        path: 'dashboard',
        element: <DashboardPage />,
      },
      {
        path: 'books',
        children: [
          { index: true, element: <BooksPage /> },
          { path: 'new', element: <BookFormPage /> },
          { path: ':id', element: <BookDetailsPage /> },
          { path: ':id/edit', element: <BookFormPage /> },
        ],
      },
      {
        path: 'authors',
        children: [
          { index: true, element: <AuthorsPage /> },
          { path: 'new', element: <AuthorFormPage /> },
          { path: ':id', element: <AuthorDetailsPage /> },
          { path: ':id/edit', element: <AuthorFormPage /> },
        ],
      },
      {
        path: 'categories',
        children: [
          { index: true, element: <CategoriesPage /> },
          { path: 'new', element: <CategoryFormPage /> },
          { path: ':id', element: <CategoryDetailsPage /> },
          { path: ':id/edit', element: <CategoryFormPage /> },
        ],
      },
      {
        path: 'publishers',
        children: [
          { index: true, element: <PublishersPage /> },
          { path: 'new', element: <PublisherFormPage /> },
          { path: ':id', element: <PublisherDetailsPage /> },
          { path: ':id/edit', element: <PublisherFormPage /> },
        ],
      },
      {
        path: 'readers',
        children: [
          { index: true, element: <ReadersPage /> },
          { path: 'new', element: <ReaderFormPage /> },
          { path: ':id', element: <ReaderDetailsPage /> },
          { path: ':id/edit', element: <ReaderFormPage /> },
        ],
      },
      {
        path: 'loans',
        children: [
          { index: true, element: <LoansPage /> },
          { path: 'new', element: <LoanFormPage /> },
          { path: ':id', element: <LoanDetailsPage /> },
          { path: ':id/edit', element: <LoanFormPage /> },
        ],
      },
      {
        path: '*',
        element: <NotFoundPage />,
      },
    ],
  },
]);
