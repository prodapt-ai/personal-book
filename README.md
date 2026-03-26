# Personal Book List with Google Books Integration

## Context
You have a Spring Boot project with a REST endpoint `/books` that returns
all books from an in-memory H2 database. The code includes a `Book` entity,
`BookRepository`, `BookController`, and a Google Books integration (`/books/google`)
exposing a search that returns the upstream Google schema via `GoogleBookService`.

## Using the Google Books API
* Base URL: `https://www.googleapis.com/books/v1`
* Search endpoint: `GET /volumes?q={query}` (e.g., `q=effective+java`, optional `maxResults`, `startIndex`).
* Volume details endpoint: `GET /volumes/{id}` to fetch a single book by Google volume ID.
* This project uses `GoogleBookService` with a configurable base URL. Set
  `google.books.base*url` in `application.properties` (or override in tests)
  to point to the real API or a mock server. The search route is exposed as
  `GET /google?q={query}` returning the upstream Google schema.

## Task
Implement the following, with accompanying tests for each change (tests are
mandatory):

1. Add a new REST endpoint that takes a Google Books volume ID as a parameter
   and adds the book to your personal list.

   * Endpoint: `POST /books/{googleId}` (path variable `googleId`).
   * Behavior:
       * Fetch the book details from the Google Books API (via `GoogleBookService`).
       * Map appropriate fields from `GoogleBook` to your `Book` entity
       (e.g., id, title, first author, pageCount).
       * Persist the mapped `Book` using `BookRepository`.
       * Return `201 Created` with the persisted `Book` in the response body.
   * Tests (Spring Boot tests):
       * Happy path: valid `googleId` returns 201 and persists the book with
       correct fields.
       * Error path: invalid or missing upstream data returns an appropriate
       error (e.g., `400 Bad Request`), and nothing is persisted.
       * Prefer mocking the downstream (e.g., MockWebServer/WireMock) to avoid
       flakiness; a smoke test hitting the real API is optional.

2. Keep existing functionality intact and verify:

   * The existing `GET /books` endpoint still returns all persisted books.
   * The Google search endpoint `/google` continues to return the
     Google schema payload as-is.
   * Tests should seed data where needed and assert on JSON responses and
     status codes.

You may refactor or add code as needed, but keep the existing structure.
Aim to complete within 30 minutes.

Here is the **Markdown source** you can directly copy and paste into your `README.md` 👇

````markdown
## 🚀 How to Run the Application

This application is a Spring Boot project and can be run using Maven or any IDE such as Eclipse or IntelliJ.

### ▶️ Run using Maven

Run the following commands:

```bash
mvn clean install
mvn spring-boot:run
````

### ▶️ Run from IDE (Eclipse / IntelliJ)

1. Import the project as a Maven project
2. Locate the main class (e.g., `DemoApplication`)
3. Right-click → Run as → Spring Boot App

### 🌐 API Endpoint

Once the application is running, you can access the API using the following endpoint:

```http
POST http://localhost:8080/api/google/{googleId}
```

### Example

```http
POST http://localhost:8080/api/google/BREwDwAAQBAJ
```

Replace `{googleId}` with a valid Google Books ID to fetch and store book details.

### 🧪 Running Tests

```bash
mvn test
```

> Note: Some tests use mock servers and do not call external APIs. Smoke tests are disabled by default.

---

## ⚠️ Assumptions

The following assumptions were made during development:

* The Google Books API is publicly accessible and does not require authentication.
* The first author from the API response is considered the primary author of the book.
* If the authors list is empty or null, the author field will be set to null.
* If `volumeInfo` is missing in the API response, default values (null or 0) are used for the book fields.
* It is assumed that duplicate book handling (based on book ID) is managed within the persistence layer.
* External API errors such as 4xx and 5xx responses are handled through a global exception handler.
* The structure of the Google Books API response is assumed to remain consistent.

```



