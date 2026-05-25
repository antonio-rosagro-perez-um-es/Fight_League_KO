export type UserRole = 'REGISTERED' | 'ORGANIZER' | 'ADMIN';

export interface User {
  id: string;
  username: string;
  email: string;
  role: UserRole;
  score: number;
  tournamentWins: number;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface FighterBanner {
  id: string;
  name: string;
  slug: string;
}

export interface Fighter extends FighterBanner {
  description: string;
  region: string;
  archetype: string;
  title: string;
  itLikes: string;
  itDislike: string;
  deleted: boolean;
  health: number;
  range: number;
  power: number;
  vitality: number;
  mobility: number;
  easyOfUse: number;
  winCounter: number;
  loseCounter: number;
  playCounter: number;
  winRate: number;
}

export interface FighterWrite {
  name: string;
  description: string;
  region: string;
  archetype: string;
  title: string;
  itLikes: string;
  itDislike: string;
  slug: string;
  health: number;
  range: number;
  power: number;
  vitality: number;
  mobility: number;
  easyOfUse: number;
}

export interface OfficialCombo {
  id: string;
  title: string;
  textNotation: string;
  comboDificulty: string;
  fuse: string;
  mediaUrl: string;
  description: string;
  meterCost: number;
  damage: number;
  pointFighterId: string;
  pointFighterName: string;
  pointFighterSlug: string;
  secondFighterId?: string;
  secondFighterName?: string;
  secondFighterSlug?: string;
}

export interface Combo extends OfficialCombo {
  oficial: boolean;
  privateCombo: boolean;
  deleted: boolean;
  createdAt: string;
  upDateAt: string;
  likeCounter: number;
  dislikeCounter: number;
  creatorUserId?: string;
}

export interface ComboFilters {
  pointFighterId?: string | null;
  secondFighterId?: string | null;
  comboDificulty?: string | null;
  fuse?: string | null;
  latest?: boolean | null;
  mostLiked?: boolean | null;
}

export interface ComboCreate {
  title: string;
  pointFighter: string;
  secondFighter?: string | null;
  textNotation: string;
  comboDificulty: string;
  fuse: string;
  mediaUrl: string;
  description: string;
  metercost: number;
  damage: number;
}

export type ComboUpdate = Partial<ComboCreate>;

export interface UserRanking {
  id: string;
  username: string;
  score: number;
  tournamentWins: number;
}

export interface TournamentView {
  id: string;
  ownerId: string;
  title: string;
  state: string;
  maxPlayers: number;
  playerCount: number;
  remainingSlots: number;
  startDate: string;
  inscriptionCloseDate: string;
  winnerId?: string;
  deleted: boolean;
  scored: boolean;
  joinedByCurrentUser: boolean;
  ownedByCurrentUser: boolean;
}

export interface TournamentCreate {
  title: string;
  maxPlayers: number;
  starDate: string;
  inscriptionCloseDate: string;
}

export interface TournamentUpdate {
  maxPlayers: number;
  startDate: string;
  inscriptionCloseDate: string;
}

export interface RecentGame {
  id: string;
  tournamentId?: string;
  tournamentTitle?: string;
  user1Id: string;
  user1Username: string;
  user2Id: string;
  user2Username: string;
  teamUser1Id?: string;
  teamUser2Id?: string;
  winnerId?: string;
  gameDate: string;
  wonByCurrentUser: boolean;
}

export interface FighterStats {
  fighterId: string;
  fighterName: string;
  winRate: number;
  playRate: number;
  pickCounter: number;
  winsCounter: number;
  losesCounter: number;
}

export interface TeamStats {
  idTeam: string;
  pointFighterName: string;
  secondFighterName: string;
  fuse: string;
  winRate: number;
  playRate: number;
  pickCounter: number;
  winsCounter: number;
  losesCounter: number;
}

export interface UserProfile extends User {
  gamesPlayed: number;
  gamesWon: number;
  gamesLost: number;
}

export interface AdminUser extends User {
  deleted: boolean;
}

export interface TournamentGame {
  id: string;
  roundNumber: number;
  bracketPosition: number;
  user1Id: string;
  user1Username: string;
  user2Id: string;
  user2Username: string;
  teamUser1Id?: string;
  teamUser2Id?: string;
  winnerId?: string;
  gameDate: string;
}

export interface TournamentStanding {
  userId: string;
  username: string;
  placement: number;
  points: number;
}

export interface Team {
  id: string;
  pointFighterId: string;
  secondFighterId: string;
  fuse: string;
  playCounter: number;
  winCounter: number;
  loseCounter: number;
  deleted: boolean;
}

export interface TeamWrite {
  pointFighterId: string;
  secondFighterId: string;
  fuse: string;
}

export interface Game {
  id: string;
  tournament?: { id: string; title: string } | null;
  user1Id: string;
  user2Id: string;
  teamUser1Id?: string | null;
  teamUser2Id?: string | null;
  winnerId?: string | null;
  gameDate: string;
  roundNumber: number;
  bracketPosition: number;
  delete: boolean;
}

export interface GameCreate {
  user1: string;
  user2: string;
  gameDate: string;
}

export interface GameUpdate {
  user1?: string | null;
  user2?: string | null;
  team1?: string | null;
  team2?: string | null;
  winner?: string | null;
}
