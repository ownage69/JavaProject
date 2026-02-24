# Библиотечная система

## Описание проекта
Данный проект представляет собой RESTful веб-приложение, разработанное с использованием Spring Boot.
Оно позволяет выполнять базовые CRUD-операции (создание, чтение, обновление, удаление) для сущности `Book`.

В качестве хранилища данных используется PostgreSQL.
Приложение построено по многослойной архитектуре: `Controller -> Service -> Repository`.
Для обмена данными между API и бизнес-слоем используются DTO.

## Выполняемые функции
Приложение предоставляет REST API для управления книгами:

### 1. Получение списка всех книг
- Метод: `GET`
- Эндпоинт: `/api/books`
- Описание: возвращает список всех книг.

### 2. Получение книги по ID
- Метод: `GET`
- Эндпоинт: `/api/books/{id}`
- Описание: возвращает книгу по уникальному идентификатору.

### 3. Поиск книг по автору (`@RequestParam`)
- Метод: `GET`
- Эндпоинт: `/api/books/search?author=...`
- Описание: возвращает книги, у которых в поле `authors` содержится переданное значение.

### 4. Добавление новой книги
- Метод: `POST`
- Эндпоинт: `/api/books`
- Описание: принимает JSON с новой книгой, сохраняет и возвращает созданную запись.

### 5. Обновление книги
- Метод: `PUT`
- Эндпоинт: `/api/books/{id}`
- Описание: обновляет книгу по ID.

### 6. Удаление книги
- Метод: `DELETE`
- Эндпоинт: `/api/books/{id}`
- Описание: удаляет книгу по ID.

## Реализация требований по эндпоинтам
- `GET` с `@PathVariable`: реализован (`/api/books/{id}`).
- `GET` с `@RequestParam`: реализован (`/api/books/search?author=...`).
- `PUT`: реализован (`/api/books/{id}`).
- `DELETE`: реализован (`/api/books/{id}`).

## Поля сущности Book
- `id: UUID`
- `title: String`
- `authors: String`
- `description: String`
- `publishYear: Integer`
- `categories: String`

## Запуск проекта
1. Создать БД в PostgreSQL:
```sql
CREATE DATABASE library;
```

2. Настроить подключение в `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/library
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:190817}
```

3. Запустить приложение:
```bash
mvn spring-boot:run
```

## Проверка Checkstyle
```bash
mvn checkstyle:check
```
