# Frontend

For frontend development, Angular 19 has been chosen along with Angular Material for icons and components. TypeScript is used throughout. If other libraries are needed, permission must be requested first during the planning phase.

The application is a platform where users can view official fighter combos from the game 2XKO, organize community tournaments, and save, publish, and share combos. The views are described in the following sections.

> **Project context:** See `AGENTS.md` for overall architecture, `ia/spring-security.md` for detailed role permissions, and root `pending.md` for missing asset tracking. The frontend is already scaffolded — 20 page components exist under `frontend/src/app/pages/` with routing, core services (`api.service.ts`, `auth.service.ts`), and a shared `combo-notation.component.ts`.

Use the `frontend/` folder for development, and use `npx autoskills` to install the necessary skills.

Store any multimedia content using a proper file architecture so it can be used later. If you cannot obtain multimedia content, leave only the paths and create a separate file called `pending.md` indicating all pending operations or broken links throughout the project.

If you consider it necessary, create commits during development to save progress, using good practices such as descriptive names (add, create, fix, etc.).

Make all views responsive and adaptive.

---

## Authentication — Registration — Login

### Login Notification (any role)

When an unregistered user tries to access or perform an operation they don't have permissions for, show a notification saying "Want to log in?" with a button that navigates to the login view.

**Login form:** Two fields — `usernameOrEmail` and `password`. Below the password field, display a link saying "Not registered yet? Try now" that navigates to the register view.

**Register form:** Must satisfy the user creation endpoint at `POST /auth/register`. Required fields:

- `username` (String, max 50, unique)
- `email` (String, max 120, unique)
- `password` (String, BCrypt-hashed on backend)

See `backend/src/main/java/FightLeagueKO/auth/controller/AuthController.java` and `backend/src/main/java/FightLeagueKO/user/controller/UserController.java` for the full endpoint contracts and `RegisterRequest`/`LoginRequest` DTOs.

---

## Header

The header has four types, two of which are very similar.

1. **Unregistered user:** Fields Home, Fighters, Statistics, Ranking, Tournaments, and Calendar aligned left; Log In and Register aligned right.

   ![alt text](img/img_heather.png)

2. **Registered user:** Same as unregistered, except instead of Log In and Register, a user icon dropdown with options to go to profile and log out. The left side also includes Community Combos.

3. **Organizer:** Same fields as a registered user.

4. **Admin:** The menu shows system management entries: Combo, User, Games, Teams, Tournaments on the left; an admin user icon on the right.

   ![alt text](img/image_heather_1.png)

> **Role reference:** See `ia/spring-security.md` and `backend/src/main/java/FightLeagueKO/security/SecurityConfig.java` for the exact permission model.

---

## Footer

The footer contains links to social media (Twitter, Instagram), a Contact Me link (template email to be replaced later), and a Support Me link. Before the links, a disclaimer states that no rights are held over the products — they belong to Riot Games and this is for academic purposes only. Add a simplified sitemap.

![alt text](img/footer.png)

---

## User Profile

### Organizer — Registered User

The profile shows an image, the nickname, and personal statistics such as wins/losses, as seen on <https://2xkombo.gg/player/m80-hikari-1803850?season=season_0>, along with a button to modify allowed parameters.

**Backend endpoints:** `GET /users/me` returns `UserProfileDTO`; `PATCH /users/me` updates it via `UpdateUserProfileDTO`. See `UserController.java`. The `User` model fields include `username`, `email`, `role` (`REGISTERED`/`ORGANIZER`/`ADMIN`), `score`, and `tournamentWins`.

---

## Home

Three different view types depending on the user type.

### Unregistered

A grid of fighters ordered by creation date, with a design similar to <https://www.streetfighter.com/6/es-es/character>. Clicking on any fighter opens their character sheet. The hover highlight effect is also desired.

**Backend:** `GET /fighters/all-banners` returns `List<FighterBannerDTO>` with active fighters (not deleted). See `FighterController.java`.

**Frontend:** `home.component.ts` handles this view.

### Registered User

A list of the user's last played matches. If there are no matches played, show a motivational message saying it's time to join a tournament, with a button leading to tournaments. Matches won by the user are shown with a light green background, lost ones in red. Display using small circular images.

**Backend:** `GET /games/me/recent` returns `List<RecentGameDTO>`.

![alt text](img/inicio_registrado.png)

### Organizer

Same as registered user.

### Admin

A list of the different sections available in the header for direct navigation.

---

## Fighters

The fighter view is divided into two user types.

### Admin

A text header indicating the current section, with a button for creating new fighters located on the right, above a table with the main fighter attributes — only the most relevant ones such as id, name, type (archetype), slug, deleted as columns, with each row representing a fighter. At the end of each row there are buttons for edit, delete, restore, and view all information. At the beginning of each row there is a checkbox to delete multiple fighters at once via multiple delete requests.

![alt text](img/fighter_admin.png)

**Backend endpoints:**

- List: `GET /fighters`
- Create: `POST /fighters` via `CreateFighterDTO`
- Update: `PATCH /fighters/{id}` via `FighterUpdateDTO`
- Soft-delete: `PATCH /fighters/{id}/deactivate`
- Restore: `PATCH /fighters/{id}/restore`

See `backend/src/main/java/FightLeagueKO/fighter/controller/FighterController.java` for all endpoints and `Fighter.java` for the model fields (`name`, `description`, `region`, `archetype`, `title`, `slug`, `itLikes`, `itDislike`, plus stats: `health`, `range`, `power`, `vitality`, `mobility`, `easyOfUse`).

Forms are displayed via modal windows and submitted when the user presses submit.

**Frontend:** `admin-fighters.component.ts` handles this view.

### Unregistered — Registered — Organizer

A layout similar to <https://www.streetfighter.com/6/es-es/character/cammy>, keeping only the information that matches the fighter fields (description, likes, dislikes) and adding others like type (archetype) or region. A similar distribution is desired with information split left and right, a fighter image in the middle, and a submenu above the information on the right.

The submenu has two entries:

1. **Info** — the main view used to return.
2. **Official Combos** — shows the official combos for this fighter, with a distribution similar to <https://www.streetfighter.com/6/es-es/character/cammy/movelist>.

Below the fighter detail, include a mini-grid with other fighters.

**Backend:** `GET /fighters/{id}` returns `FighterDTO`. `GET /fighters/{fighterId}/official` returns `List<OfficialComboDTO>` (public, no auth required). See `ComboController.java`.

**Frontend:** `fighter-detail.component.ts` and `fighters-entry.component.ts` handle these views.

![alt text](img/image_1.png)

---

## Statistics

The statistics section is divided into 2 sections if the user is not registered and 3 if they are.

### Admin

Same layout as in Fighters (admin table with CRUD for stats entities).

### Registered — Organizer

1. The user's most played fighters with the best winrate (or one of the two).
2. The fighters with the best winrate (global).
3. The teams with the best winrate (global).

Style similar to <https://2xkombo.gg/characters>. Show the top 10 in each case.

**Backend:** `GET /fighters/ranking` returns `List<FighterStatsDTO>` (includes `winRate`). `GET /teams/ranking` returns `List<TeamStatsDTO>`.

### Unregistered

Same as registered but without showing personal user statistics — only the two global sections.

**Frontend:** `statistics.component.ts` handles this view.

---

## Ranking

The ranking shows users with the highest number of tournament wins. Clicking navigates to their profile. There is no admin ranking view. A format similar to <https://2xkombo.gg/rankings> is desired.

**Backend:** `GET /users/ranking` returns `List<UserRankingDTO>`.

**Score system:** Each `User` has a `score` field. Depending on their tournament placement:

- 1st place: 10 points
- 2nd place: 9 points
- ...
- 10th place: 1 point
- 11th+: 0 points

This scoring is assigned on the backend when the tournament finishes.

**Frontend:** `ranking.component.ts` handles this view.

---

## Calendar

A calendar view highlighting the current day, showing upcoming tournaments in calendar format using the tournament list sorted by date. A view similar to <https://2xkombo.gg/tournament-calendar> is desired.

**Backend:** `GET /tournaments/all-tournaments` returns `List<TournamentViewDTO>` with `startDate` and `inscriptionCloseDate` fields.

**Frontend:** `calendar.component.ts` handles this view.

---

## Tournament

### Admin

Same layout as in Fighters (admin table with CRUD). See `TournamentController.java` for the full set of admin endpoints.

**Frontend:** `admin-tournaments.component.ts` handles this view.

### Registered and Unregistered Users

A column list of tournaments ordered from nearest to farthest, as seen on <https://2xkombo.gg/tournaments>. Clicking opens a view showing public information: remaining slots, registration end date, status (`TournamentStates` enum: `REGISTRATION`, `WAITING_START`, `IN_PROGRESS`, `FINISHED`), and a button to join if possible. After joining, the button changes to allow leaving the tournament.

If an unregistered user presses the join button, they are taken to the registration view. This applies to all actions they are not permitted to perform.

**Backend:** `GET /tournaments/all-tournaments` (public list), `GET /tournaments/{id}/view` (public detail). Join: `PATCH /tournaments/{id}/join`. Exit: `PATCH /tournaments/{id}/exit`.

**Frontend:** `tournaments.component.ts` and `tournament-detail.component.ts`.

### Registered User Creating a Tournament

A registered user can create a tournament via a prominent button. When they press it:

- Their `UserRole` is upgraded to `ORGANIZER`
- They become the tournament owner (tracked by `userOwnerId`)
- They cannot register for their own tournaments

**Backend:** `POST /tournaments` via `CreateTournamentDTO`. See `TournamentController.java`.

### Owner (Organizer) User

For organizer users, their owned tournaments appear in a separate section. Clicking opens a floating/modal view with information fields and authorized actions: modification, close registrations, cancellation, etc.

**Backend:** `GET /tournaments/me/owned`, plus `PATCH /tournaments/{id}` (update), `PATCH /tournaments/{id}/close` (close registrations), `PATCH /tournaments/{id}/delete` (cancel). See `TournamentController.java`.

Inside the floating view, include a tournament bracket graph showing the tournament games. Use any library that can render tournament brackets. The graph may be incomplete (e.g., in the first phase when only the first matches exist) — use a default placeholder for missing slots. When the owner clicks any match slot, they are redirected to an expanded page with the same graph.

![alt text](img/image_3.png)

In the expanded tournament match view, the owner can click a game to open a floating window showing its information. Fighter and fuse fields are dropdown menus populated from the backend:

- Fighters: `GET /fighters/all-banners`
- Fuses: `FuseType` enum values (`DOUBLE_DOWN`, `FREESTYLE`, `TWO_X_ASSIST`, `JUGGERNAUT`, `SIDEKICK`)

If well implemented, add search to these dropdowns.

The winner is displayed in the middle and is set by selecting one of the two users. This can be changed by updating the game.

**Backend for game management:**

- Assign teams: `PATCH /games/{gameId}/teams` via `SetTeamsDTO`
- Set winner: `PATCH /games/{gameId}/winner/{userId}`
- Update game: `PATCH /games/{gameId}` via `UpdateGameDTO`

See `backend/src/main/java/FightLeagueKO/game/service/GameService.java` and `GameController.java`.

![alt text](img/image_4.png)

**Frontend:** `tournament-detail.component.ts` handles bracket display.

---

## Combos (Community)

### Admin

Same admin table layout as in Fighters. See `ComboController.java`.

**Frontend:** `admin-combos.component.ts` handles this view.

### Registered User and Owner (Organizer)

This view is similar to <https://2xkombo.gg/> but uses the search/filter endpoints from `ComboController.java`.

**Creating a combo:** By default, a new combo is created as private. Only public combos appear in the community view. Next to the upload combo button there is a toggle button to view private combos (user-owned only). The same button toggles back to public combos. In private view, users can perform allowed actions on their own combos: edit, delete, change visibility.

**Combo creation fields:**

- `title` (String, max 100)
- `textNotation` (String, the combo button notation)
- `description` (String, TEXT)
- `pointFighterId` (UUID, required — select from fighter dropdown)
- `secondFighterId` (UUID, nullable — select from fighter dropdown)
- `comboDificulty` (enum: `BEGINNER`, `INTERMEDIATE`, `ADVANCED`)
- `fuse` (enum: `DOUBLE_DOWN`, `FREESTYLE`, `TWO_X_ASSIST`, `JUGGERNAUT`, `SIDEKICK`)
- `mediaUrl` (String, URL to video/image)
- `meterCost` (int)
- `damage` (int)

The fighter and fuse fields are dropdowns populated with predefined names/IDs matching what the backend expects.

**Filtering:** Users can apply filters as dropdown menus in the view. See `POST /combos/search` via `ComboFiltersDTO`.

**Voting:** Users can like/dislike combos. See `PATCH /combos/{comboId}/vote?voteType=LIKE|DISLIKE` and `PATCH /combos/{comboId}/unvote`.

**Notation rendering:** Any displayed combo should be translatable from text notation to images (button icons), as seen on <https://2xkombo.gg/>.

![alt text](img/image_2.png)

**Frontend:** `community-combos.component.ts` handles the list/create/edit views. `combo-notation.component.ts` handles the text-to-image notation rendering. Fighter image asset paths follow `public/assets/fighters/{slug}/portrait.webp` (see `pending.md` for what's missing).

**Combo endpoints reference:**

| Method | Path | Auth | Purpose |
|--------|------|------|---------|
| `POST` | `/combos/search` | Authenticated | Search/filter combos |
| `POST` | `/combos` | Authenticated | Create combo |
| `PATCH` | `/combos/{id}` | Authenticated | Update combo |
| `PATCH` | `/combos/{id}/delete` | Authenticated | Soft-delete combo |
| `PATCH` | `/combos/{id}/restore` | Authenticated | Restore combo |
| `PATCH` | `/combos/{id}/public` | Authenticated | Set public |
| `PATCH` | `/combos/{id}/private` | Authenticated | Set private |
| `PATCH` | `/combos/{id}/vote` | Authenticated | Vote (LIKE/DISLIKE) |
| `PATCH` | `/combos/{id}/unvote` | Authenticated | Withdraw vote |

---

## Quick Reference: Backend API Groups

| Domain | Base Path | Public Endpoints | Authenticated | Admin Only |
|--------|-----------|-----------------|---------------|------------|
| Auth | `/auth` | `register`, `login` | — | — |
| Fighter | `/fighters` | `/{id}`, `/all-banners`, `/ranking` | — | CRUD, stats |
| Combo | `/combos` | `/{fighterId}/official` | CRUD, search, vote, visibility | List all, get by ID |
| Team | `/teams` | `/ranking` | — | CRUD, stats |
| Game | `/games` | — | `/me/recent`, `/{gameId}/winner/{userId}` | CRUD, team assignment |
| Tournament | `/tournaments` | `/all-tournaments`, `/{id}/view`, `/{id}/bracket`, `/{id}/standings` | CRUD (owned), join/exit, close, generate | Admin list |
| User | `/users` | `/ranking` | `/me`, `/{userId}` | `/admin/**`, create |

### Key Enums

| Enum | Values |
|------|--------|
| `UserRole` | `REGISTERED`, `ORGANIZER`, `ADMIN` |
| `ComboDificulty` | `BEGINNER`, `INTERMEDIATE`, `ADVANCED` |
| `FuseType` | `DOUBLE_DOWN`, `FREESTYLE`, `TWO_X_ASSIST`, `JUGGERNAUT`, `SIDEKICK` |
| `VoteType` | `LIKE`, `DISLIKE` |
| `TournamentStates` | `REGISTRATION`, `WAITING_START`, `IN_PROGRESS`, `FINISHED` |
