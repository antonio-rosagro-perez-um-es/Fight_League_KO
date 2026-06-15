# Fight League KO

Fight League KO is a university thesis project (`TFG`) for a 2XKO community hub. The repository is a monorepo with a Spring Boot backend, an Angular frontend, PostgreSQL persistence, and shared game/media assets.

## Stack

- Backend: Java 21, Spring Boot 4.0.6, Spring Web, Spring Security, JWT, Spring Data JPA, Hibernate, PostgreSQL.
- Frontend: Angular 19 standalone components, TypeScript, RxJS.
- Database: PostgreSQL 16 through Docker Compose.
- Assets: fighter portraits/banners/icons, fuse icons, backgrounds, brand assets, and combo control glyphs under `assets/`.

## Run Locally

```bash
# Database only
docker compose up -d postgres

# Backend with the local profile
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Frontend
cd frontend
npm start
```

Local URLs:

- Frontend: `http://localhost:4200`
- Backend: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

## Deploy With Docker

Create a root `.env` file if it does not exist:

```env
POSTGRES_DB=fight_league_db
POSTGRES_USER=user
POSTGRES_PASSWORD=password
```

Build and start the full stack:

```bash
docker compose up -d --build
```

Docker URLs and services:

- Frontend: `http://localhost`
- Backend: internal Compose service `backend` on port `8080`
- PostgreSQL: bound to `127.0.0.1:5432`

Useful Docker commands:

```bash
docker compose logs -f
docker compose down
```

## Build And Verify

```bash
# Backend tests/build
cd backend
./mvnw test
./mvnw package

# Frontend production build
cd frontend
npm run build
```

The frontend build can report Angular budget warnings for bundle/component style sizes while still completing successfully.

## Database

- Database: `fight_league_db`
- User/password: `user` / `password`
- Port: `5432`
- JPA is configured to update the schema automatically in development.

## Implemented Areas

- Authentication with register/login and JWT-backed sessions.
- Role-aware navigation and access for registered users, organizers, and admins.
- Fighter browsing, details, assets, admin management, stats, and ranking.
- Official combos with notation rendering and media display.
- Community combos with card/list browsing, filters, `Latest` and `Most liked` sorting, voting, private/public toggles, owner edit/delete actions, and media-only modal playback.
- Combo creation from `My Combos` using searchable fighter/fuse selectors and validated notation.
- Tournaments with creation, join/exit, owner controls, bracket generation, team assignment, winner selection/change, standings, and owner username display.
- Registered-user home recent matches with team fighter icons and fuse icons when team data exists.
- Admin management pages for fighters, combos, users, games, teams, and tournaments.

## Project Notes

- The main project context for agents is in `AGENTS.md`.
- Frontend requirements and design notes are in `ia/frontend.md`.
- Security and role notes are in `ia/spring-security.md`.
- There is no maintained root pending tracker at this time.
