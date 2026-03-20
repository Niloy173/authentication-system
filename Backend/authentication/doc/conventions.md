# Conventions

> These rules apply throughout the entire project — no exceptions.
> Purpose: anyone reading the code should be able to predict structure and naming without guessing.

---

## Naming Conventions

### Classes

| Type | Convention | Example |
|---|---|---|
| Controller | `PascalCase` + `Controller` suffix | `AuthController` |
| Service Interface | `PascalCase` + `Service` suffix | `AuthService` |
| Service Implementation | interface name + `Impl` suffix | `AuthServiceImpl` |
| Repository | entity name + `Repository` suffix | `UserRepository` |
| Entity | `PascalCase`, singular | `User`, `Role`, `AuthToken` |
| DTO Request | action + target + `Request` | `RegisterRequest`, `LoginRequest` |
| DTO Response | target + `Response` | `UserResponse`, `TokenResponse` |
| Mapper | entity name + `Mapper` suffix | `UserMapper` |
| Exception | descriptive + `Exception` suffix | `UserNotFoundException`, `TokenExpiredException` |
| Enum | `PascalCase` | `UserStatus`, `TokenType`, `RoleName` |
| Util | descriptive + `Util` suffix | `TokenUtil`, `PasswordUtil` |
| Config | descriptive + `Config` suffix | `MailConfig`, `AppConfig` |

### Methods

| Type | Convention | Example |
|---|---|---|
| General | `camelCase`, verb-first | `findUserByEmail()`, `generateToken()` |
| Boolean return | `is` or `has` prefix | `isTokenExpired()`, `isAccountLocked()` |
| Repository finders | Spring Data convention | `findByEmail()`, `findByUsernameOrEmail()` |

### Variables and Fields

- `camelCase` always
- No abbreviations unless universally understood (`id`, `dto`, `ip`)
- Boolean fields prefixed with `is` where it adds clarity

### Constants and Enums

- Enum values → `UPPER_SNAKE_CASE` → `RESET_PASSWORD`, `VERIFY_EMAIL`
- No magic strings anywhere — always reference from `constant/` package

### Database / Entity Fields

- Entity field names → `camelCase` (JPA maps to Oracle columns via `@Column`)
- Oracle column names → `UPPER_SNAKE_CASE` (e.g. `PASSWORD_HASH`, `LOCKED_UNTIL`)
- Always explicit `@Column(name = "...")` — never rely on implicit mapping

---

## Package Conventions

- One responsibility per package — no mixing concerns
- Controller never imports Repository directly
- Entity never leaves the service layer — always convert to DTO before returning
- Mapper is called from Service, not from Controller

---

## API Conventions

### URL Structure

| Pattern | Example |
|---|---|
| Lowercase, hyphen-separated | `/api/v1/auth/forgot-password` |
| Resource plural where applicable | `/api/v1/users` |
| Version prefix always | `/api/v1/...` |

### HTTP Methods

| Action | Method |
|---|---|
| Fetch data | `GET` |
| Create resource | `POST` |
| Full update | `PUT` |
| Partial update | `PATCH` |
| Delete | `DELETE` |

### Response Structure

```json
{
  "status": "success",
  "code": 200,
  "message": "...",
  "data": {},
  "timestamp": "2025-01-01T00:00:00"
}
```

For validation errors, `data` is replaced with `errors`:

```json
{
  "status": "error",
  "code": 400,
  "message": "Validation failed",
  "errors": [
    { "field": "email", "message": "must not be blank" }
  ],
  "timestamp": "2025-01-01T00:00:00"
}
```

- `status` → always `"success"` or `"error"`
- `code` → mirrors HTTP status code
- `message` → human readable, always present
- `data` → present on success, absent on error
- `errors` → present on validation failure only
- `timestamp` → always ISO 8601 format

---

## Exception Handling Convention

- All exceptions handled in one place — global `@RestControllerAdvice`
- Custom exceptions extend `RuntimeException`
- Never return raw Spring error responses to the client
- HTTP status codes must be meaningful — no `200` for errors

---

## Configuration Convention

- Nothing hardcoded in code — thresholds, durations, toggles go in `application.yml`
- Environment-specific values go in `application-dev.yml` or `application-prod.yml`
- Custom properties grouped under `app:` prefix

```yaml
app:
  mail:
    enabled: true
  security:
    max-failed-attempts: 5
    lock-duration-minutes: 15
  token:
    expiry-minutes: 5
```

---

## General Rules

- No `System.out.println` — use proper logging (`@Slf4j` via Lombok)
- No raw `String` for status or type comparisons — always use enums
- Every service method has an interface definition before implementation
- DTOs are never reused across unrelated flows — create a new one if needed