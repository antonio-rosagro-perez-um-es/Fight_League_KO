import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import {
  AdminUser,
  Combo,
  ComboCreate,
  ComboFilters,
  ComboUpdate,
  Fighter,
  FighterBanner,
  FighterStats,
  FighterWrite,
  Game,
  GameCreate,
  GameUpdate,
  OfficialCombo,
  PageResponse,
  RecentGame,
  Team,
  TeamStats,
  TeamWrite,
  TournamentGame,
  TournamentCreate,
  TournamentStanding,
  TournamentUpdate,
  TournamentView,
  UserProfile,
  UserRanking,
} from './api.models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);

  getFighterBanners() {
    return this.http.get<FighterBanner[]>('/api/fighters/all-banners');
  }

  getFighter(id: string) {
    return this.http.get<Fighter>(`/api/fighters/${id}`);
  }

  getAllFighters() {
    return this.http.get<Fighter[]>('/api/fighters');
  }

  createFighter(fighter: FighterWrite) {
    return this.http.post<Fighter>('/api/fighters', fighter);
  }

  uploadFighterMedia(id: string, files: Partial<Record<'portrait' | 'banner' | 'icon', File>>) {
    const formData = new FormData();
    Object.entries(files).forEach(([type, file]) => {
      if (file) {
        formData.append(type, file);
      }
    });
    return this.http.post<void>(`/api/fighters/${id}/media`, formData);
  }

  updateFighter(id: string, fighter: Partial<FighterWrite>) {
    return this.http.patch<void>(`/api/fighters/${id}`, fighter);
  }

  deactivateFighter(id: string) {
    return this.http.patch<void>(`/api/fighters/${id}/deactivate`, null);
  }

  restoreFighter(id: string) {
    return this.http.patch<void>(`/api/fighters/${id}/restore`, null);
  }

  getOfficialCombos(fighterId: string) {
    return this.http.get<OfficialCombo[]>(`/api/combos/${fighterId}/official`);
  }

  searchCommunityCombos(filters: ComboFilters, page = 0, size = 10) {
    return this.http.post<PageResponse<Combo>>('/api/combos/search', filters, { params: { page, size } });
  }

  getAllCombosForAdmin(page = 0, size = 100) {
    return this.http.get<PageResponse<Combo>>('/api/combos', { params: { page, size } });
  }

  getMyCombos() {
    return this.http.get<Combo[]>('/api/combos/me');
  }

  createCombo(combo: ComboCreate) {
    return this.http.post<Combo>('/api/combos', combo);
  }

  updateCombo(comboId: string, combo: ComboUpdate) {
    return this.http.patch<void>(`/api/combos/${comboId}`, combo);
  }

  deleteCombo(comboId: string) {
    return this.http.patch<void>(`/api/combos/${comboId}/delete`, null);
  }

  restoreCombo(comboId: string) {
    return this.http.patch<void>(`/api/combos/${comboId}/restore`, null);
  }

  voteCombo(comboId: string, voteType: 'LIKE' | 'DISLIKE') {
    return this.http.patch<void>(`/api/combos/${comboId}/vote`, null, { params: { voteType } });
  }

  unvoteCombo(comboId: string) {
    return this.http.patch<void>(`/api/combos/${comboId}/unvote`, null);
  }

  setComboPublic(comboId: string) {
    return this.http.patch<void>(`/api/combos/${comboId}/public`, null);
  }

  setComboPrivate(comboId: string) {
    return this.http.patch<void>(`/api/combos/${comboId}/private`, null);
  }

  getTournaments() {
    return this.http.get<TournamentView[]>('/api/tournaments/all-tournaments');
  }

  getTournament(id: string) {
    return this.http.get<TournamentView>(`/api/tournaments/${id}/view`);
  }

  getAllTournamentsForAdmin() {
    return this.http.get<TournamentView[]>('/api/tournaments/admin/all');
  }

  getOwnedTournaments() {
    return this.http.get<TournamentView[]>('/api/tournaments/me/owned');
  }

  createTournament(tournament: TournamentCreate) {
    return this.http.post<TournamentView>('/api/tournaments', tournament);
  }

  updateTournament(id: string, tournament: TournamentUpdate) {
    return this.http.patch<void>(`/api/tournaments/${id}`, tournament);
  }

  deleteTournament(id: string) {
    return this.http.patch<void>(`/api/tournaments/${id}/delete`, null);
  }

  restoreTournament(id: string) {
    return this.http.patch<void>(`/api/tournaments/${id}/restore`, null);
  }

  getTournamentBracket(id: string) {
    return this.http.get<TournamentGame[]>(`/api/tournaments/${id}/bracket`);
  }

  getTournamentStandings(id: string) {
    return this.http.get<TournamentStanding[]>(`/api/tournaments/${id}/standings`);
  }

  joinTournament(id: string) {
    return this.http.patch<void>(`/api/tournaments/${id}/join`, null);
  }

  exitTournament(id: string) {
    return this.http.patch<void>(`/api/tournaments/${id}/exit`, null);
  }

  closeTournamentRegistrations(id: string) {
    return this.http.patch<void>(`/api/tournaments/${id}/close`, null);
  }

  generateTournamentMatchups(id: string) {
    return this.http.patch<void>(`/api/tournaments/${id}/generate-matchups`, null);
  }

  setGameWinner(gameId: string, userId: string) {
    return this.http.patch<void>(`/api/games/${gameId}/winner/${userId}`, null);
  }

  setGameTeams(gameId: string, teams: { team1: TeamWrite; team2: TeamWrite }) {
    return this.http.patch<void>(`/api/games/${gameId}/teams`, teams);
  }

  getAllGamesForAdmin() {
    return this.http.get<Game[]>('/api/games');
  }

  createGame(game: GameCreate) {
    return this.http.post<Game>('/api/games', game);
  }

  updateGame(gameId: string, game: GameUpdate) {
    return this.http.patch<void>(`/api/games/${gameId}`, game);
  }

  deleteGame(gameId: string) {
    return this.http.patch<void>(`/api/games/${gameId}/delete`, null);
  }

  restoreGame(gameId: string) {
    return this.http.patch<void>(`/api/games/${gameId}/restore`, null);
  }

  getUserRanking() {
    return this.http.get<UserRanking[]>('/api/users/ranking');
  }

  getAllUsersForAdmin() {
    return this.http.get<AdminUser[]>('/api/users/admin/all');
  }

  deleteUser(id: string) {
    return this.http.patch<void>(`/api/users/admin/${id}/delete`, null);
  }

  restoreUser(id: string) {
    return this.http.patch<void>(`/api/users/admin/${id}/restore`, null);
  }

  getCurrentUserProfile() {
    return this.http.get<UserProfile>('/api/users/me');
  }

  getUserProfile(id: string) {
    return this.http.get<UserProfile>(`/api/users/${id}`);
  }

  updateProfile(profile: { username: string; email: string }) {
    return this.http.patch<UserProfile>('/api/users/me', profile);
  }

  getRecentGames() {
    return this.http.get<RecentGame[]>('/api/games/me/recent');
  }

  getFighterRanking() {
    return this.http.get<FighterStats[]>('/api/fighters/ranking');
  }

  getTeamRanking() {
    return this.http.get<TeamStats[]>('/api/teams/ranking');
  }

  getAllTeamsForAdmin() {
    return this.http.get<Team[]>('/api/teams');
  }

  createTeam(team: TeamWrite) {
    return this.http.post<Team>('/api/teams', team);
  }

  updateTeam(teamId: string, team: Partial<TeamWrite>) {
    return this.http.patch<void>(`/api/teams/${teamId}`, team);
  }

  deleteTeam(teamId: string) {
    return this.http.patch<void>(`/api/teams/${teamId}/delete`, null);
  }

  restoreTeam(teamId: string) {
    return this.http.patch<void>(`/api/teams/${teamId}/restore`, null);
  }
}
