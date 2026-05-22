# Fight League KO (2XKO)

Monorepo: **backend** (Java/Spring Boot 4.0.6) + **frontend** (Angular — not yet scaffolded)
University thesis project (`# TFG`).

## Build & Run

```bash
# Backend (use Maven wrapper)
cd backend
./mvnw spring-boot:run    # dev server
./mvnw package             # build JAR

# Database (PostgreSQL)
docker-compose up -d

# Frontend — NOT YET SCAFFOLDED (planned Angular app)
```

## Database

- **Name**: `fight_league_db`
- **User/Pass**: `user` / `password`
- **Port**: `5432`
- Start PostgreSQL before running the app (JPA auto-creates schema with `ddl-auto=update`)

## Backend Architecture

**6 domain packages** (57 Java source files), each following a layered pattern:

| Package   | Model       | Key Endpoints / Features |
|-----------|-------------|--------------------------|
| `combo/`  | Combo       | CRUD, like/dislike, search/filters, public/private toggle, difficulty (`ComboDificulty`) & fuse (`FuseType`) enums |
| `fighter/`| Fighter     | CRUD (renamed from `character`), banners, stats, ranking |
| `team/`   | Team        | CRUD, stats, ranking, point/second fighter same-fighter validation |
| `game/`   | Game        | CRUD, team assignment, set winner, linked to tournament |
| `tournament/` | Tournament | CRUD, join/exit, lifecycle scheduler (`@EnableScheduling`), states enum (`TournamentStates`) |
| `user/`   | User        | Basic CRUD — **name only, no password/roles yet** |

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

## Security (planned — not implemented)

- `spring-security.md` at project root documents the plan
- **No Spring Security dependency** in `pom.xml` yet
- Planned: JWT auth, password-based login, role hierarchy (Unregistered → Registered → Organizer → Admin)
- User model needs password, roles, etc.

## Frontend

**Not started.** `frontend/` contains only a `README.md` placeholder. No Angular project files, no `package.json`. All npm commands are premature.

## Tech Stack

- **Backend**: Spring Boot 4.0.6, Java 21, Spring Data JPA, Hibernate, PostgreSQL
- **Frontend**: Planned — Angular 19+ with Angular Material
- **Build**: Maven (backend), npm TBD (frontend)
- **Database**: PostgreSQL 16 via Docker
- **Testing**: Minimal — single context-load test
