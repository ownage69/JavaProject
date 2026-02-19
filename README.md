# Library Service

Справочник книг — Spring Boot REST API.

## Лабораторные работы

### Лаба 1 — Basic REST Service
- Spring Boot приложение.
- REST API для сущности Book.
- GET с `@PathVariable` и `@RequestParam`.
- Слои: Controller → Service → Repository.
- DTO + Mapper (MapStruct).
- Checkstyle.

## Сущности

### Book (Книга)

| Поле | Тип | Описание |
|------|-----|----------|
| id | UUID | PK, auto-generated |
| title | String | Название книги |
| authors | String | Авторы |
| description | String | Описание |
| publishYear | Integer | Год издания |
| categories | String | Категории |

## Запуск

### Требования
- Java 17+
- Maven
- PostgreSQL

### Быстрый старт

1. Создать базу данных в PostgreSQL:
```sql
CREATE DATABASE library;
```

2. Указать параметры подключения в `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/library
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:your_password}
```

3. Запустить приложение:
```
mvn spring-boot:run
```

## API

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/books/{id}` | Получить книгу по ID |
| GET | `/api/books/search?title=...` | Поиск по названию |
