#!/usr/bin/env sh
set -eu

DATABASE_URL="${1:-${DATABASE_URL:-}}"

if [ -z "${DATABASE_URL}" ]; then
    echo "Usage:"
    echo "  bash scripts/check-postgres-counts.sh 'postgresql://user:password@host:5432/library?sslmode=require'"
    echo "or:"
    echo "  DATABASE_URL='postgresql://user:password@host:5432/library' bash scripts/check-postgres-counts.sh"
    exit 1
fi

psql "${DATABASE_URL}" -v ON_ERROR_STOP=1 -c "
    select 'books' as table_name, count(*) from books
    union all select 'authors', count(*) from authors
    union all select 'categories', count(*) from categories
    union all select 'publishers', count(*) from publishers
    union all select 'readers', count(*) from readers
    union all select 'loans', count(*) from loans
    order by table_name;
"
