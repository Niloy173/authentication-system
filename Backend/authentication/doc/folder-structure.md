# Folder Structure

## Root Layout

```
project-root/
├── doc/                        # Project documentation (updated as we build)
├── src/
│   ├── main/
│   │   ├── java/com/project/authentication/
│   │   └── resources/
│   └── test/
└── pom.xml
```

---

## Java Package Structure

```
com.project.authentication
│
├── config/
│   # All @Configuration classes
│   # Includes: app config, mail config, JWT config
│   # Security config will be added last (intentionally)
│
├── constant/
│   # Enums and constant values only — no logic
│   # Includes: TokenType, UserStatus, RoleName
│   # Rule: no magic strings anywhere in the project — always reference from here
│
├── controller/
│   # REST endpoints only
│   # Rule: zero business logic here — only receive request, call service, return response
│
├── service/
│   # Business logic lives here
│   # Rule: always define interface first, implementation goes in impl/
│   └── impl/
│       # Concrete implementations of service interfaces
│
├── repository/
│   # JPA repositories — database access only
│   # Rule: no business logic, no manual SQL unless absolutely necessary
│
├── entity/
│   # JPA entity classes mapping directly to Oracle DB tables
│   # Rule: entities are not exposed outside service layer — use DTOs
│
├── dto/
│   # Data Transfer Objects — what goes in and out of the API
│   ├── request/    # Incoming payloads (register, login, reset password etc)
│   └── response/   # Outgoing responses (user info, token response etc)
│
├── mapper/
│   # Converts Entity <-> DTO
│   # Rule: conversion logic does not belong in service or controller
│
├── exception/
│   # Custom exception classes + global exception handler (@RestControllerAdvice)
│   # Centralizes all error responses in one place
│
├── util/
│   # Stateless helper classes
│   # Includes: token generator, password utilities
│
└── mail/
    # Mail building and sending logic
    # Isolated as its own package — enough responsibility to stand alone
    # Not a separate service — same Spring Boot app, different package
```

---

## Resources Structure

```
src/main/resources/
│
├── application.yml             # Base config (shared across environments)
├── application-dev.yml         # Dev overrides (local DB, mail toggle etc)
├── application-prod.yml        # Prod overrides
│
├── db/
│   └── scripts/                # Oracle SQL scripts are maintained in the root-level DB/ folder
                                  not inside resources — kept separate from the Spring Boot project
│                               # Includes: table creation, seed roles, seed admin users
│
└── templates/
    └── mail/                   # HTML email templates
                                # Used for: verification email, forgot password, OTP
```

---

## Doc Folder

```
doc/
├── folder-structure.md         # This file
├── database-design.md          # Table designs + reasoning
├── api-endpoints.md            # All endpoints (filled as we build)
└── conventions.md              # Naming rules, response format, patterns
```

---

## Key Design Decisions

| Decision | Reason |
|---|---|
| `service/impl/` separation | Interface first — easier to mock in tests, easier to swap implementation |
| `mail/` as its own package | Mail has enough responsibility to be isolated without being a separate service |
| `constant/` for all enums | No magic strings anywhere in codebase |
| `mapper/` separate from service | Entity-to-DTO conversion is not business logic |
| Security config added last | All endpoints and business logic finalized first, then secured |
| `application-dev/prod.yml` split | Environment-specific config without touching base config |
| SQL scripts in `db/scripts/` | Seed data and schema versioned alongside code |

---

## Rules Summary

- Controller → calls Service only
- Service → calls Repository, Mapper, Util, Mail
- Repository → talks to DB only
- Entity → never leaves the service layer (always map to DTO before returning)
- Constants → always use enums, never hardcode strings