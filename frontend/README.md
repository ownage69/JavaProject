# Library System Frontend

Modern SPA frontend for the existing Spring Boot backend in this repository.

## Stack

- React
- TypeScript
- Vite
- React Router
- Axios
- Lucide React
- Role-based auth-ready architecture
- Public landing page + authenticated dashboard shell

## Dependencies

Runtime:

- `react`
- `react-dom`
- `react-router-dom`
- `axios`
- `lucide-react`

Dev:

- `typescript`
- `vite`
- `@vitejs/plugin-react-swc`
- `@types/react`
- `@types/react-dom`

## Project structure

```text
frontend/
├── .env.example
├── index.html
├── package.json
├── README.md
├── tsconfig.json
├── vite.config.ts
└── src
    ├── app
    │   ├── navigation.ts
    │   └── router.tsx
    ├── components
    │   ├── auth
    │   ├── books
    │   ├── common
    │   ├── forms
    │   └── layout
    ├── config
    │   └── runtime.ts
    ├── hooks
    │   ├── useAsyncValue.ts
    │   └── useDebouncedValue.ts
    │   ├── useAuth.ts
    │   └── usePermissions.ts
    ├── pages
    │   ├── auth
    │   ├── authors
    │   ├── books
    │   ├── categories
    │   ├── dashboard
    │   ├── loans
    │   ├── public
    │   ├── publishers
    │   ├── readers
    │   ├── NotFoundPage.tsx
    │   └── UnauthorizedPage.tsx
    ├── providers
    │   └── AuthProvider.tsx
    ├── services
    │   ├── authService.ts
    │   ├── authStorage.ts
    │   ├── endpoints.ts
    │   ├── http.ts
    │   └── libraryService.ts
    ├── styles
    │   └── main.css
    ├── types
    │   ├── api.ts
    │   ├── auth.ts
    │   └── entities.ts
    ├── utils
    │   ├── errors.ts
    │   ├── format.ts
    │   ├── library.ts
    │   ├── pagination.ts
    │   └── validation.ts
    ├── main.tsx
    └── vite-env.d.ts
```

## Install and run

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server proxies `/api` to `http://localhost:8080` by default.

Demo auth in `mock` mode:

- Admin: `admin@library.local` / `admin123`
- User: `reader@library.local` / `user123`

## Backend integration

1. Start the Spring Boot backend on `http://localhost:8080`.
2. Copy `.env.example` to `.env` if you need custom API, pagination, or auth settings.
3. Keep `VITE_API_BASE_URL=/api` for local development with the Vite proxy.
4. Switch `VITE_AUTH_MODE=api` when your backend auth endpoint is ready.
5. For session-based Spring Security, set `VITE_AUTH_USE_CREDENTIALS=true` and point `VITE_AUTH_ME_PATH` to a current-user endpoint.
6. For JWT-based auth, keep `VITE_AUTH_USE_CREDENTIALS=false` and return token + role/user data from the login response.

## Backend contract notes

- The current backend already matches the CRUD endpoints for:
  - `books`
  - `authors`
  - `categories`
  - `publishers`
  - `readers`
  - `loans`
- Some richer UI fields are ready for extension:
  - `Loan.returnDate` is optional in the TypeScript model because the current backend does not send it yet.
  - `Return book` is wired through a configurable endpoint flag because the current backend does not expose a dedicated return action yet.
  - Pagination UI is already implemented on list pages. The frontend also contains a Spring-style page normalizer in `src/utils/pagination.ts` so you can switch from array responses to pageable responses in one place later.
  - Auth is centralized for both mock/demo mode and API mode. Session restore is ready for `GET /auth/me` style integration.
- If your DTO field names change later, update:
  - `src/types/entities.ts`
  - `src/types/auth.ts`
  - `src/services/endpoints.ts`
  - `src/config/runtime.ts`
