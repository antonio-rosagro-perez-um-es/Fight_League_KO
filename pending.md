# Pending Assets And Work

## Media Assets

- Placeholder fighter media has been added under `frontend/public/assets/placeholders/`:
  - `fighter-portrait.svg`
  - `fighter-banner.svg`
  - `fighter-full.svg`
- Fighter image usages now fall back to those placeholders when slug-based `.webp` files are missing.
- Final licensed fighter image assets are still pending.
- Expected final fighter paths:
  - `frontend/public/assets/fighters/{slug}/portrait.webp`
  - `frontend/public/assets/fighters/{slug}/banner.webp`
  - `frontend/public/assets/fighters/{slug}/full.webp`

## Features Blocked Or Limited By Missing Media

- Anonymous home fighter cards use placeholder portraits until final `portrait.webp` files are added.
- Fighter detail pages use placeholder full-body art until final `full.webp` files are added.
- Fighter mini-grids use placeholder portraits until final `portrait.webp` files are added.
- Banner-focused layouts use placeholder banners until final `banner.webp` files are added.
- Exact 2XKO-style combo button artwork is unavailable; the frontend currently uses CSS icon-style notation tokens.
- Final visual polish matching the reference screenshots is limited until the missing media assets are available.

## Combo Notation

- `frontend/src/app/shared/combo-notation.component.ts` now renders CSS icon-style tokens for:
  - Numeric directions: `1 2 3 4 5 6 7 8 9`
  - Direction aliases: `d`, `dl`, `dr`, `u`, `ul`, `ur`, `left`, `right`
  - Attack buttons: `L`, `M`, `H`, `T`, `S1`, `S2`
  - Notes: `jump`, `j`, `air`, `hold`, `delay`, `dash`, `microdash`, `walk`, `assist`
  - Separators: `>`, `+`, `,`, `(`, `)`, `/`
- Final artwork is still pending if exact 2XKO button image assets are required.
- A stricter notation grammar is still pending; unknown tokens intentionally render as fallback text chips.

## Partial Features Still Open

- Statistics personal section for registered and organizer users is not implemented yet.
- Admin statistics CRUD view is not implemented yet.
- Tournament winner-change/update flow after a winner has already been set is not implemented yet.
- Search inside tournament fighter/fuse dropdowns is not implemented yet.
- External bracket library integration is not used; the current bracket display is custom CSS/layout.

## Wired Features

- Auth, register, login, blocked-action prompt, role-aware header, and footer are wired.
- Home, profile, fighters, statistics, ranking, calendar, tournaments, and community combos are wired.
- Admin fighter, combo, game, team, user, and tournament management are wired.
- Community combo list, create, edit, delete, visibility, filters, sorting, and voting are wired.
- Tournament creation, owned-tournament controls, bracket preview, bracket detail, winner selection, and team assignment are wired.
- Calendar grid and clickable ranking profile rows are wired.
