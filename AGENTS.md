# Fight League KO (2XKO)

Monorepo: **backend** (Java/Spring Boot 4.0.6) + **frontend** (Angular 19)
University thesis project (`# TFG`).

## Build & Run

```bash
# Backend (use Maven wrapper)
cd backend
./mvnw spring-boot:run    # dev server
./mvnw package             # build JAR

# Database (PostgreSQL)
docker-compose up -d

# Frontend
cd frontend
npm start              # dev server
npm run build          # production build
```

## Database

- **Name**: `fight_league_db`
- **User/Pass**: `user` / `password`
- **Port**: `5432`
- Start PostgreSQL before running the app (JPA auto-creates schema with `ddl-auto=update`)

## Backend Architecture

**8 domain/infra packages** (~58 Java source files), each following a layered pattern:

| Package       | Model          | Key Endpoints / Features |
|---------------|----------------|--------------------------|
| `auth/`       | —              | Register, login, JWT token generation via `AuthService` |
| `combo/`      | Combo          | CRUD, like/dislike, search/filters, public/private toggle, difficulty (`ComboDificulty`) & fuse (`FuseType`) enums |
| `fighter/`    | Fighter        | CRUD (renamed from `character`), banners, stats, ranking |
| `team/`       | Team           | CRUD, stats, ranking, point/second fighter same-fighter validation |
| `game/`       | Game           | CRUD, team assignment, stat-safe winner set/change, linked to tournament |
| `tournament/` | Tournament     | CRUD, join/exit, lifecycle scheduler (`@EnableScheduling`), states enum (`TournamentStates`) |
| `user/`       | User           | CRUD with username, email, password (BCrypt), role (`UserRole`), soft-delete |
| `security/`   | —              | `SecurityConfig` (JWT/BCrypt), `DefaultAdminSeeder`, `CurrentUserService` |
| `common/`     | —              | Global exception handler (`ApiExceptionHandler`) |

### Architecture Patterns

- **Layered**: Controller → Service (interface + impl) → Repository (+ custom `*Postgre` implementations) → Model
- **UUID PKs**, soft-delete (`deleted` boolean flag), DTOs + Mappers
- **Custom repositories** (`*RepositoryPostgre.java`) for complex queries
- **Enums**: `ComboDificulty` (BEGINNER/INTERMEDIATE/ADVANCED), `FuseType` (DOUBLE_DOWN/FREESTYLE/TWO_X_ASSIST/JUGGERNAUT/SIDEKICK), `TournamentStates` (REGISTRATION/WAITING_START/IN_PROGRESS/FINISHED)

### Key Refactors (recent git history)

- `fighter/` renamed from `character/` (avoid `java.lang.Character` conflict)
- Relations changed from object references to UUID IDs
- Same-fighter validation in teams/combos
- Like/dislike system for combos

## Security (implemented)

- `SecurityConfig` with JWT (HS256) auth via OAuth2 resource server + BCrypt password encoding
- `AuthService` handles register/login, issues JWT tokens
- Role hierarchy: `REGISTERED → ORGANIZER → ADMIN` (enforced via `@EnableMethodSecurity` + request matchers)
- Unauthenticated: only register, login, fighter/ranking/combos/tournament listing
- `DefaultAdminSeeder` creates an admin on first startup
- `CurrentUserService` extracts authenticated user from JWT subject

## Frontend

Angular frontend is scaffolded and implemented under `frontend/`.

- **Framework**: Angular 19 standalone components + TypeScript
- **Core services**: `frontend/src/app/core/api.service.ts`, `auth.service.ts`, route guards, notification service
- **Shared UI**: `frontend/src/app/shared/combo-notation.component.ts` renders control glyph combo notation with text fallback
- **Routes/pages**: auth, home, profile, fighters, statistics, ranking, calendar, tournaments, community combos, and admin management pages
- **Verification**: use `cd frontend && npm run build`

### Implemented Frontend Areas

- Role-aware header/footer, login/register, blocked-action login prompt
- Home variants for anonymous, registered/organizer, and admin users
- Fighter public detail and admin fighter management
- Global statistics rankings, personal statistics, read-only admin statistics, user ranking with clickable profiles, and calendar month grid
- Tournament public flow, creation, owner controls, custom bracket views, stat-safe winner selection/change, and searchable asset team assignment
- Community combo CRUD, filters, visibility, voting, searchable fighter/fuse asset dropdowns, and control glyph notation rendering
- Admin management for combos, users, games, teams, and tournaments

### Pending Frontend Work

Root `pending.md` is the canonical tracker for missing media assets, features limited by missing media, and partial frontend features. Keep detailed pending lists there instead of duplicating them in this file.

## Tech Stack

- **Backend**: Spring Boot 4.0.6, Java 21, Spring Data JPA, Hibernate, PostgreSQL
- **Frontend**: Angular 19+ with TypeScript
- **Build**: Maven (backend), npm (frontend)
- **Database**: PostgreSQL 16 via Docker
- **Testing**: Minimal backend context-load test + frontend production build verification
