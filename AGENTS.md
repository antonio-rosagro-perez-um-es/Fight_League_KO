# Fight League KO

Monorepo: **backend** (Java/Spring Boot) + **frontend** (Angular + Angular Material)

## Build & Run

```bash
# Frontend (Angular + Angular Material)
cd frontend
npm install
npm start                  # dev server at http://localhost:4200
npm run build              # production build

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
- **Frontend**: Angular 19+, Angular Material
- **Build**: Maven (backend), npm (frontend)
- **Database**: PostgreSQL via Docker

## Notes

- `backend/src/main/resources/application.properties` - minimal config, add DB connection here if needed
- Backend structure: `character/`, `combo/` packages (controller, service, repository, model, dto, enums)
- Frontend: Angular 19+ with Angular Material for UI components
