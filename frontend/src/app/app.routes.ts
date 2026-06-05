import { Routes } from '@angular/router';

import { adminGuard } from './core/admin.guard';
import { authGuard } from './core/auth.guard';

import { AuthFormComponent } from './pages/auth-form.component';
import { AdminUsersComponent } from './pages/admin-users.component';
import { AdminCombosComponent } from './pages/admin-combos.component';
import { AdminGamesComponent } from './pages/admin-games.component';
import { AdminTeamsComponent } from './pages/admin-teams.component';
import { CalendarComponent } from './pages/calendar.component';
import { CommunityCombosComponent } from './pages/community-combos.component';
import { FighterDetailComponent } from './pages/fighter-detail.component';
import { FightersEntryComponent } from './pages/fighters-entry.component';
import { FightersComponent } from './pages/fighters.component';
import { HomeComponent } from './pages/home.component';
import { ProfileComponent } from './pages/profile.component';
import { PublicProfileComponent } from './pages/public-profile.component';
import { RankingComponent } from './pages/ranking.component';
import { StatisticsComponent } from './pages/statistics.component';
import { TournamentDetailComponent } from './pages/tournament-detail.component';
import { TournamentsEntryComponent } from './pages/tournaments-entry.component';
import { TournamentsComponent } from './pages/tournaments.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'fighters', component: FightersEntryComponent },
  { path: 'fighters/:id', component: FighterDetailComponent },
  { path: 'tournaments', component: TournamentsEntryComponent },
  { path: 'tournaments/:id', component: TournamentDetailComponent },
  { path: 'ranking', component: RankingComponent },
  { path: 'users', component: AdminUsersComponent, canActivate: [adminGuard] },
  { path: 'combos', component: AdminCombosComponent, canActivate: [adminGuard] },
  { path: 'games', component: AdminGamesComponent, canActivate: [adminGuard] },
  { path: 'login', component: AuthFormComponent, data: { mode: 'login' } },
  { path: 'register', component: AuthFormComponent, data: { mode: 'register' } },
  { path: 'statistics', component: StatisticsComponent },
  { path: 'calendar', component: CalendarComponent },
  {
    path: 'community-combos',
    component: CommunityCombosComponent,
    canActivate: [authGuard],
  },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'profile/:id', component: PublicProfileComponent, canActivate: [authGuard] },
  {
    path: 'teams',
    component: AdminTeamsComponent,
    canActivate: [adminGuard],
  },
  { path: '**', redirectTo: '' },
];
