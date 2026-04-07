# Демонстрация лабораторной 4

## Что демонстрируем
- bulk-операцию с бизнес-смыслом: массовая выдача книг;
- `Stream API` и `Optional` в сервисном слое (`LoanService`);
- разницу между bulk-операцией без общей транзакции и с `@Transactional`;
- unit-тесты сервисов на `Mockito`.

## 1. Подготовка
1. Создай БД:
```sql
CREATE DATABASE library;
```
2. Запусти приложение:
```bash
mvn spring-boot:run
```
3. При необходимости открой Swagger:
```text
http://localhost:8080/swagger-ui.html
```

## 2. Подготовка данных
Ниже приведены `curl`-запросы. Можно выполнить их в таком порядке и взять `id` из ответов.

Создать издательство:
```bash
curl -s -X POST http://localhost:8080/api/publishers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Demo Publisher",
    "country": "Belarus"
  }'
```

Создать автора:
```bash
curl -s -X POST http://localhost:8080/api/authors \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "George",
    "lastName": "Orwell"
  }'
```

Создать категорию:
```bash
curl -s -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Classic"
  }'
```

Создать читателя:
```bash
curl -s -X POST http://localhost:8080/api/readers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Ivan",
    "lastName": "Petrov",
    "email": "ivan.petrov.lab4@example.com"
  }'
```

Создать книгу `bookIdA`:
```bash
curl -s -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "1984 Demo A",
    "isbn": "9780306406157",
    "description": "Book for non-transaction bulk demo",
    "publishYear": 1949,
    "publisherId": <publisherId>,
    "authorIds": [<authorId>],
    "categoryIds": [<categoryId>]
  }'
```

Создать книгу `bookIdB`:
```bash
curl -s -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "1984 Demo B",
    "isbn": "9780306406158",
    "description": "Book for transactional bulk demo",
    "publishYear": 1949,
    "publisherId": <publisherId>,
    "authorIds": [<authorId>],
    "categoryIds": [<categoryId>]
  }'
```

## 3. Проверка состояния БД до bulk-операций
Проверь таблицу `loans`:
```bash
PGPASSWORD=190817 psql -h localhost -U postgres -d library -c \
"select id, book_id, reader_id, loan_date, due_date, returned from loans order by id;"
```

Ожидаемо: для `bookIdA` и `bookIdB` ещё нет записей о выдаче.

## 4. Bulk без `@Transactional`
Отправь список из двух объектов:
- первый объект валидный;
- второй объект содержит несуществующий `bookId`.

```bash
curl -i -X POST http://localhost:8080/api/loans/bulk/without-transaction \
  -H "Content-Type: application/json" \
  -d '[
    {
      "bookId": <bookIdA>,
      "readerId": <readerId>,
      "dueDate": "2026-05-01"
    },
    {
      "bookId": 999999,
      "readerId": <readerId>,
      "dueDate": "2026-05-01"
    }
  ]'
```

Ожидаемый результат:
- HTTP-ответ завершится ошибкой `404`;
- сообщение: `Book not found with id: 999999`;
- первая выдача успеет сохраниться, потому что общей транзакции нет.

Проверь БД повторно:
```bash
PGPASSWORD=190817 psql -h localhost -U postgres -d library -c \
"select id, book_id, reader_id, loan_date, due_date, returned from loans order by id;"
```

Ожидаемо: в таблице появилась запись с `book_id = <bookIdA>`.

## 5. Bulk с `@Transactional`
Теперь выполни похожий запрос, но уже на транзакционный endpoint и с другой валидной книгой:

```bash
curl -i -X POST http://localhost:8080/api/loans/bulk/with-transaction \
  -H "Content-Type: application/json" \
  -d '[
    {
      "bookId": <bookIdB>,
      "readerId": <readerId>,
      "dueDate": "2026-05-02"
    },
    {
      "bookId": 999998,
      "readerId": <readerId>,
      "dueDate": "2026-05-02"
    }
  ]'
```

Ожидаемый результат:
- HTTP-ответ снова будет `404`;
- сообщение: `Book not found with id: 999998`;
- запись для `bookIdB` в БД не появится, потому что вся bulk-операция выполнялась в одной транзакции и была откатана.

Снова проверь БД:
```bash
PGPASSWORD=190817 psql -h localhost -U postgres -d library -c \
"select id, book_id, reader_id, loan_date, due_date, returned from loans order by id;"
```

Ожидаемо:
- запись для `bookIdA` есть;
- записи для `bookIdB` нет.

Это и есть требуемая демонстрация разницы состояния БД.

## 6. Дополнительная демонстрация бизнес-правила
Одна и та же книга не может иметь две активные выдачи.

Повтори выдачу для уже занятой книги:
```bash
curl -i -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "bookId": <bookIdA>,
    "readerId": <readerId>,
    "dueDate": "2026-05-10"
  }'
```

Ожидаемый результат:
- HTTP-ответ `409`;
- сообщение: `Book is already loaned and not returned. Book id: <bookIdA>`.

## 7. Unit-тесты
Запуск тестов:
```bash
mvn test
```

Что проверяется:
- создание одиночной выдачи;
- запрет выдачи уже занятой книги;
- поэлементная обработка bulk-списка;
- обновление выдачи;
- базовые сценарии `ReaderService`.

## 8. Короткий вывод для защиты
Можно формулировать так:

1. Реализована bulk-операция `POST /api/loans/bulk/...` со списком объектов `LoanCreateDto`.
2. В `LoanService` использованы `Stream API` и `Optional`.
3. Одна и та же bulk-логика вызывается в двух режимах: без внешней транзакции и с `@Transactional`.
4. При ошибке на втором элементе списка без транзакции первая запись остаётся в БД, а с транзакцией все изменения откатываются.
5. Для сервисного слоя написаны unit-тесты на `Mockito`.
