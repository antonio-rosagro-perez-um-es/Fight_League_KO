# Fight League KO

Monorepo: **backend** (Java/Spring Boot) + **frontend** (placeholder)

## Build & Run

```bash
# Backend (use Maven wrapper)
cd backend
./mvnw spring-boot:run    # dev
./mvnw package             # build JAR

# Database (PostgreSQL)
docker-compose up -d
```

## Database

- **Name**: `fight_league_ko`
- **User/Pass**: `postgres` / `postgres`
- **Port**: `5432`
- Start PostgreSQL before running the app (JPA auto-creates schema if configured)

## Tech Stack

- **Backend**: Spring Boot 4.0.6, Java 21, Spring Data JPA
- **Build**: Maven (wrapper in `backend/mvnw`)
- **Database**: PostgreSQL via Docker

## Notes

- `backend/src/main/resources/application.properties` - minimal config, add DB connection here if needed
- Backend structure: `character/` package (controller, service, repository, model, dto)
- Frontend directory exists but is empty - likely a future React/Vue SPA
