# Database Design

**Database:** Oracle  
**Schema approach:** Minimal now, extensible later  
**Rule:** Nothing hardcoded — thresholds, lock durations, token expiry all driven by config

---

## Tables Overview

| Table | Purpose |
|---|---|
| USERS | Identity + account state |
| ROLE | Available roles in the system |
| USER_ROLE | Maps users to roles (many-to-many) |
| AUTH_TOKEN | Temporary tokens (reset, OTP, verification) |
| LOGIN_ATTEMPT | Login history + security tracking |

---

## 1. USERS

> Who the user is, how they log in, whether they are allowed to log in

| Column | Type | Constraint |
|---|---|---|
| USER_ID | NUMBER | PRIMARY KEY |
| USERNAME | VARCHAR2(100) | NOT NULL |
| EMAIL | VARCHAR2(150) | NOT NULL |
| PASSWORD_HASH | VARCHAR2(255) | NOT NULL |
| STATUS | VARCHAR2(20) | NOT NULL |
| FAILED_ATTEMPTS | NUMBER | DEFAULT 0 |
| LOCKED_UNTIL | TIMESTAMP | NULL |
| CREATED_AT | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| UPDATED_AT | TIMESTAMP | NULL |

### Column Reasoning

- **USER_ID** — internal identifier, never exposed in business logic
- **USERNAME + EMAIL** — both supported as login identifiers
- **PASSWORD_HASH** — raw password never stored
- **STATUS** — one of: `ACTIVE`, `LOCKED`, `DISABLED`
- **FAILED_ATTEMPTS** — increments on wrong password, resets on success
- **LOCKED_UNTIL** — set when failed attempt threshold is reached (threshold + duration from config, not hardcoded)
- **UPDATED_AT** — null until first update

### Design Decisions

- No separate lock table — login check needs fast single-table access
- Roles not stored here — one user can have many roles, handled via USER_ROLE

---

## 2. ROLE

> Defines what type of user someone is

| Column | Type | Constraint |
|---|---|---|
| ROLE_ID | NUMBER | PRIMARY KEY |
| ROLE_NAME | VARCHAR2(50) | NOT NULL |
| DESCRIPTION | VARCHAR2(200) | NULL |

### Notes

- Pre-seeded via SQL script — roles like `ADMIN`, `USER` exist from the start
- New users get `USER` role by default on registration
- Admins are pre-configured via seed script, not through the registration flow

---

## 3. USER_ROLE

> Connects users to roles — handles many-to-many

| Column | Type | Constraint |
|---|---|---|
| USER_ID | NUMBER | PRIMARY KEY (composite) |
| ROLE_ID | NUMBER | PRIMARY KEY (composite) |

### Notes

- One user can have multiple roles
- One role can belong to multiple users
- Admin can assign roles to users via management endpoint

---

## 4. AUTH_TOKEN

> Single reusable table for all temporary token needs

| Column | Type | Constraint |
|---|---|---|
| TOKEN_ID | NUMBER | PRIMARY KEY |
| USER_ID | NUMBER | NOT NULL |
| TOKEN | VARCHAR2(255) | NOT NULL |
| TOKEN_TYPE | VARCHAR2(30) | NOT NULL |
| EXPIRY_TIME | TIMESTAMP | NOT NULL |
| USED | CHAR(1) | DEFAULT 'N' |
| CREATED_AT | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

### TOKEN_TYPE Values

| Value | Use Case |
|---|---|
| `RESET_PASSWORD` | Forgot password flow |
| `VERIFY_EMAIL` | Email verification on registration |
| `OTP` | One-time password (suspicious login etc) |

### Token Format by Type

- `RESET_PASSWORD` / `VERIFY_EMAIL` → UUID-style random token
- `OTP` → 6-digit numeric code

### Expiry

- All token types → 5 minutes (driven by config, not hardcoded)

### Rules

- Only latest token per user per type is valid
- When a new token is generated → all previous tokens of same type for that user are invalidated
- Once used → `USED = 'Y'`, cannot be reused

### Design Decision

- Avoided separate OTP table, reset table, verification table
- Single table handles all cases via `TOKEN_TYPE` — clean and scalable

---

## 5. LOGIN_ATTEMPT

> Login history for security tracking and audit

| Column | Type | Constraint |
|---|---|---|
| ATTEMPT_ID | NUMBER | PRIMARY KEY |
| USER_ID | NUMBER | NOT NULL |
| ATTEMPT_TIME | TIMESTAMP | NOT NULL |
| SUCCESS | CHAR(1) | NOT NULL |
| IP_ADDRESS | VARCHAR2(50) | NULL |
| CREATED_AT | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

### Notes

- `SUCCESS` → `Y` for successful login, `N` for failed
- `USER_ID` is always stored — attempts for non-existent users are not logged
- `IP_ADDRESS` → useful for suspicious login detection (Phase 2)

### How It Works With USERS Table

| Table | Role |
|---|---|
| USERS.FAILED_ATTEMPTS | Current state — fast access during login check |
| LOGIN_ATTEMPT | Full history — audit, analytics, future risk scoring |

---

## Relationships

```
USERS ──< USER_ROLE >── ROLE
  │
  └──< AUTH_TOKEN
  │
  └──< LOGIN_ATTEMPT
```

---

## Phase 2 Additions (Not Now)

- Adaptive authentication
- Device tracking
- Geo tracking / risk scoring
- OTP on suspicious login