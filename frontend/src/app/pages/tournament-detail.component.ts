import { AsyncPipe, DatePipe, NgTemplateOutlet } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BehaviorSubject, combineLatest, map, Observable, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { FighterBanner, TournamentGame } from '../core/api.models';
import { AuthService } from '../core/auth.service';
import { fighterAsset, fuseAsset } from '../shared/asset-paths';
import { AssetSelectOption, SearchableAssetSelectComponent } from '../shared/searchable-asset-select.component';

type TeamDraft = {
  pointFighterId: string;
  secondFighterId: string;
  fuse: string;
};

@Component({
  selector: 'app-tournament-detail',
  imports: [AsyncPipe, DatePipe, FormsModule, NgTemplateOutlet, RouterLink, SearchableAssetSelectComponent],
  template: `
    @if (view$ | async; as view) {
      <section class="hero panel">
        <div>
          <p class="eyebrow">{{ view.state }}</p>
          <h1>{{ view.title }}</h1>
          <p>Registration closes {{ view.inscriptionCloseDate | date:'mediumDate' }}. Tournament starts {{ view.startDate | date:'mediumDate' }}.</p>
          <p>{{ view.playerCount }}/{{ view.maxPlayers }} players registered.</p>
        </div>
        <div class="join-card">
          <strong>{{ view.remainingSlots }}</strong>
          <span>slots left</span>
          @if (auth.authenticated()) {
            @if (view.ownedByCurrentUser) {
              <span class="owner-note">You organize this event</span>
            } @else if (view.joinedByCurrentUser) {
              <button type="button" (click)="exit(view.id)">Leave tournament</button>
            } @else {
              <button type="button" (click)="join(view.id)" [disabled]="view.remainingSlots === 0 || view.state !== 'REGISTRATION'">Join tournament</button>
            }
          } @else {
            <a routerLink="/register">Register to join</a>
          }
        </div>
      </section>

      @if (view.ownedByCurrentUser || auth.role() === 'ADMIN') {
        <section class="owner-panel panel">
          <div>
            <p class="eyebrow">Expanded organizer controls</p>
            <h2>Manage tournament bracket</h2>
            @if (actionError) { <p class="error">{{ actionError }}</p> }
          </div>
          <div class="owner-actions">
            <button type="button" (click)="closeRegistrations(view.id)" [disabled]="view.state !== 'REGISTRATION'">Close registrations</button>
            <button type="button" (click)="generateMatchups(view.id)" [disabled]="view.state !== 'WAITING_START' && view.state !== 'IN_PROGRESS'">Generate next round</button>
          </div>
        </section>
      }

      <section class="columns">
        <div class="panel bracket-panel">
          <div class="panel-heading">
            <div>
              <p class="eyebrow">Bracket graph</p>
              <h2>Matches</h2>
            </div>
            <span>Custom bracket, scroll horizontally on small screens</span>
          </div>
          @if (bracket$ | async; as games) {
            @if (games.length) {
              <div class="bracket-scroll">
                <div class="bracket-grid">
                  @for (round of rounds(games); track round.number) {
                    <div class="round-column">
                      <h3>Round {{ round.number }}</h3>
                      @for (game of round.games; track game.id) {
                        <button type="button" class="match-card" (click)="openGameModal(game)">
                          <small>Match {{ game.bracketPosition }}</small>
                          <span class="participant" [class.winner]="game.winnerId === game.user1Id" [class.placeholder]="!game.user1Username">{{ game.user1Username || 'Awaiting player' }}</span>
                          <em>vs</em>
                          <span class="participant" [class.winner]="game.winnerId === game.user2Id" [class.placeholder]="!game.user2Username">{{ game.user2Username || 'Awaiting player' }}</span>
                          @if (game.winnerId) {
                            <strong class="status-ready">Winner set</strong>
                          } @else {
                            <strong class="pending">Pending result</strong>
                          }
                        </button>
                      }
                    </div>
                  }
                </div>
              </div>
            } @else {
              <div class="empty-state bracket-empty">
                <span class="material-symbols-outlined">account_tree</span>
                <strong>Bracket has not been generated yet.</strong>
                <p>Missing match slots will appear once matchups are generated.</p>
              </div>
            }
          }
        </div>
        <div class="panel">
          <h2>Standings</h2>
          @if (standings$ | async; as standings) {
            @for (standing of standings; track standing.userId) {
              <article class="standing-row">
                <span>#{{ standing.placement }}</span>
                <strong>{{ standing.username }}</strong>
                <span>{{ standing.points }} pts</span>
              </article>
            } @empty {
              <div class="empty-state">Standings will appear as results are submitted.</div>
            }
          }
        </div>
      </section>

      @if (selectedGame) {
        <div class="modal-overlay" (click)="closeGameModal()">
          <div class="modal-content" (click)="$event.stopPropagation()">
            <div class="modal-heading">
              <div>
                <p class="eyebrow">Round {{ selectedGame.roundNumber }} · Match {{ selectedGame.bracketPosition }}</p>
                <h2>{{ selectedGame.user1Username }} vs {{ selectedGame.user2Username }}</h2>
              </div>
              <button type="button" class="ghost" (click)="closeGameModal()">Close</button>
            </div>

            <div class="winner-box">
              <span>Winner</span>
              <strong>{{ winnerName(selectedGame) }}</strong>
            </div>

            @if (view.ownedByCurrentUser || auth.role() === 'ADMIN') {
              <div class="winner-actions">
                <button type="button" (click)="setWinner(selectedGame.id, selectedGame.user1Id)">{{ winnerButtonLabel(selectedGame, selectedGame.user1Username, selectedGame.user1Id) }}</button>
                <button type="button" (click)="setWinner(selectedGame.id, selectedGame.user2Id)">{{ winnerButtonLabel(selectedGame, selectedGame.user2Username, selectedGame.user2Id) }}</button>
              </div>

              @if (fighters$ | async; as fighters) {
                <section class="team-editor">
                  <h3>Assign teams</h3>
                  <div class="team-grid">
                    <div>
                      <h4>{{ selectedGame.user1Username }}</h4>
                      <ng-container *ngTemplateOutlet="teamFields; context: { draft: team1Draft, fighters: fighters }" />
                    </div>
                    <div>
                      <h4>{{ selectedGame.user2Username }}</h4>
                      <ng-container *ngTemplateOutlet="teamFields; context: { draft: team2Draft, fighters: fighters }" />
                    </div>
                  </div>
                  <button type="button" (click)="assignTeams(selectedGame.id)" [disabled]="!teamDraftValid(team1Draft) || !teamDraftValid(team2Draft)">Save teams</button>
                </section>
              }
            }
          </div>
        </div>
      }
    }

    <ng-template #teamFields let-draft="draft" let-fighters="fighters">
      <label>Point fighter
        <app-searchable-asset-select
          [options]="fighterOptions(fighters)"
          [value]="draft.pointFighterId"
          placeholder="Choose fighter"
          searchPlaceholder="Search fighters"
          (valueChange)="draft.pointFighterId = $event"
        />
      </label>
      <label>Second fighter
        <app-searchable-asset-select
          [options]="fighterOptions(fighters)"
          [value]="draft.secondFighterId"
          placeholder="Choose fighter"
          searchPlaceholder="Search fighters"
          (valueChange)="draft.secondFighterId = $event"
        />
      </label>
      <label>Fuse
        <app-searchable-asset-select
          [options]="fuseOptions"
          [value]="draft.fuse"
          placeholder="Choose fuse"
          searchPlaceholder="Search fuses"
          (valueChange)="draft.fuse = $event"
        />
      </label>
    </ng-template>
  `,
  styles: [`
    .hero { align-items: center; display: grid; gap: 2rem; grid-template-columns: 1fr 220px; margin-bottom: 1rem; }
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .4rem 0 1rem; text-transform: uppercase; }
    h2 { margin: 0; }
    .join-card { background: linear-gradient(135deg, rgba(0,0,0,.5), rgba(0,34,14,.74)), url('/assets/Backgrounds/Background_Green.webp') center/cover; border: 1px solid rgba(124,255,159,.22); border-radius: 22px; display: grid; gap: .65rem; padding: 1.25rem; text-align: center; }
    .join-card strong { font-size: 3rem; }
    button, .join-card a { background: #20d964; border: 0; border-radius: 999px; color: white; cursor: pointer; padding: .8rem 1rem; text-decoration: none; }
    button:disabled { cursor: not-allowed; opacity: .55; }
    .ghost { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.14); }
    .owner-note { color: #7cff9f; font-weight: 700; }
    .owner-panel { align-items: center; display: flex; gap: 1rem; justify-content: space-between; margin-bottom: 1rem; }
    .owner-actions, .winner-actions { display: flex; flex-wrap: wrap; gap: .5rem; }
    .columns { display: grid; gap: 1rem; grid-template-columns: minmax(0, 1.4fr) minmax(280px, .6fr); }
    .panel-heading { align-items: center; display: flex; justify-content: space-between; margin-bottom: 1rem; }
    .panel-heading span { color: #c8d3ed; font-size: .9rem; }
    .bracket-scroll { overflow-x: auto; padding-bottom: .5rem; }
    .bracket-grid { align-items: start; display: flex; gap: 1rem; min-width: max-content; }
    .round-column { display: grid; gap: .85rem; min-width: 230px; }
    .round-column h3 { color: #7cff9f; font-size: .85rem; letter-spacing: .08em; margin: 0; text-transform: uppercase; }
    .match-card { background: linear-gradient(150deg, rgba(0,0,0,.58), rgba(10,36,18,.76)), url('/assets/Backgrounds/Background_Green.webp') center/cover; border: 1px solid rgba(255,255,255,.12); border-radius: 16px; display: grid; gap: .45rem; min-height: 150px; padding: 1rem; position: relative; text-align: left; }
    .match-card::after { background: linear-gradient(180deg, rgba(124,255,159,.5), transparent); bottom: -1rem; content: ''; position: absolute; right: -0.55rem; top: 50%; width: 1px; }
    .round-column:last-child .match-card::after { display: none; }
    .match-card small { color: #7cff9f; }
    .match-card em { color: #7f8aa8; font-style: normal; }
    .match-card strong { color: #78ffb0; font-size: .78rem; }
    .participant { background: rgba(255,255,255,.06); border-radius: 10px; padding: .45rem .55rem; }
    .participant.placeholder { border: 1px dashed rgba(255,255,255,.18); color: #7f8aa8; }
    .status-ready { background: rgba(120,255,176,.1); border-radius: 999px; padding: .35rem .55rem; width: fit-content; }
    .match-card .pending { color: #c8d3ed; }
    .bracket-empty { display: grid; gap: .4rem; place-items: center; }
    .bracket-empty .material-symbols-outlined { color: #7cff9f; font-size: 2.5rem; }
    .bracket-empty p { margin: 0; }
    .winner { color: #78ffb0; font-weight: 800; }
    .standing-row { border-bottom: 1px solid rgba(255,255,255,.1); display: grid; gap: .75rem; grid-template-columns: 60px 1fr auto; padding: .9rem 0; }
    .modal-overlay { align-items: center; background: rgba(0,0,0,.58); display: flex; inset: 0; justify-content: center; position: fixed; z-index: 100; }
    .modal-content { background: #06120a; border: 1px solid rgba(255,255,255,.12); border-radius: 20px; max-height: 84vh; max-width: 820px; overflow-y: auto; padding: 1.5rem; width: 92vw; }
    .modal-heading { align-items: center; display: flex; justify-content: space-between; margin-bottom: 1rem; }
    .winner-box { background: rgba(120,255,176,.1); border: 1px solid rgba(120,255,176,.18); border-radius: 18px; display: grid; gap: .25rem; margin-bottom: 1rem; padding: 1rem; }
    .winner-box span { color: #c8d3ed; }
    .team-editor { border-top: 1px solid rgba(255,255,255,.1); margin-top: 1rem; padding-top: 1rem; }
    .team-grid { display: grid; gap: 1rem; grid-template-columns: repeat(2, minmax(0, 1fr)); margin: 1rem 0; }
    label { color: #c8d3ed; display: grid; gap: .35rem; margin-bottom: .65rem; }
    .error { color: #ff8a8a; }
    @media (max-width: 820px) { .hero, .columns, .team-grid { grid-template-columns: 1fr; } .owner-panel, .panel-heading { align-items: stretch; flex-direction: column; } }
  `]
})
export class TournamentDetailComponent {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  readonly auth = inject(AuthService);
  private readonly refresh$ = new BehaviorSubject<void>(undefined);
  private readonly tournamentId$ = this.route.paramMap.pipe(map((params) => params.get('id')!));
  readonly fuses = ['DOUBLE_DOWN', 'FREESTYLE', 'TWO_X_ASSIST', 'JUGGERNAUT', 'SIDEKICK'];
  readonly fuseOptions = this.fuses.map((fuse) => ({
    value: fuse,
    label: this.formatFuse(fuse),
    icon: fuseAsset(fuse),
  }));
  readonly fighters$ = this.api.getFighterBanners();
  actionError = '';
  selectedGame: TournamentGame | null = null;
  team1Draft: TeamDraft = this.emptyTeamDraft();
  team2Draft: TeamDraft = this.emptyTeamDraft();

  readonly view$ = combineLatest([this.tournamentId$, this.refresh$]).pipe(
    switchMap(([id]) => this.api.getTournament(id))
  );
  readonly bracket$ = combineLatest([this.tournamentId$, this.refresh$]).pipe(
    switchMap(([id]) => this.api.getTournamentBracket(id))
  );
  readonly standings$ = combineLatest([this.tournamentId$, this.refresh$]).pipe(
    switchMap(([id]) => this.api.getTournamentStandings(id))
  );

  join(id: string): void {
    this.api.joinTournament(id).subscribe(() => this.refresh$.next());
  }

  exit(id: string): void {
    this.api.exitTournament(id).subscribe(() => this.refresh$.next());
  }

  closeRegistrations(id: string): void {
    this.runAction(() => this.api.closeTournamentRegistrations(id));
  }

  generateMatchups(id: string): void {
    this.runAction(() => this.api.generateTournamentMatchups(id));
  }

  setWinner(gameId: string, userId: string): void {
    this.runAction(() => this.api.setGameWinner(gameId, userId));
  }

  assignTeams(gameId: string): void {
    this.runAction(() => this.api.setGameTeams(gameId, {
      team1: this.teamDraftToPayload(this.team1Draft),
      team2: this.teamDraftToPayload(this.team2Draft),
    }));
  }

  openGameModal(game: TournamentGame): void {
    this.selectedGame = game;
    this.team1Draft = this.emptyTeamDraft();
    this.team2Draft = this.emptyTeamDraft();
  }

  closeGameModal(): void {
    this.selectedGame = null;
  }

  rounds(games: TournamentGame[]): Array<{ number: number; games: TournamentGame[] }> {
    const byRound = new Map<number, TournamentGame[]>();
    games.forEach((game) => {
      const roundGames = byRound.get(game.roundNumber) ?? [];
      roundGames.push(game);
      byRound.set(game.roundNumber, roundGames);
    });

    return [...byRound.entries()]
      .sort(([a], [b]) => a - b)
      .map(([number, roundGames]) => ({
        number,
        games: roundGames.sort((a, b) => a.bracketPosition - b.bracketPosition),
      }));
  }

  winnerName(game: TournamentGame): string {
    if (game.winnerId === game.user1Id) {
      return game.user1Username;
    }
    if (game.winnerId === game.user2Id) {
      return game.user2Username;
    }
    return 'Pending result';
  }

  winnerButtonLabel(game: TournamentGame, username: string, userId: string): string {
    if (game.winnerId === userId) {
      return `${username} is winner`;
    }

    return game.winnerId ? `Change to ${username}` : `${username} won`;
  }

  teamDraftValid(draft: TeamDraft): boolean {
    return !!draft.pointFighterId && !!draft.secondFighterId && !!draft.fuse;
  }

  fighterOptions(fighters: FighterBanner[]): AssetSelectOption[] {
    return fighters.map((fighter) => ({
      value: fighter.id,
      label: fighter.name,
      icon: fighterAsset(fighter.slug, 'icon'),
    }));
  }

  private formatFuse(fuse: string): string {
    return fuse
      .toLowerCase()
      .split('_')
      .map((part) => part === 'x' ? 'X' : part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ')
      .replace('Two X Assist', '2X Assist');
  }

  private runAction(action: () => Observable<void>): void {
    this.actionError = '';
    action().subscribe({
      next: () => {
        this.refresh$.next();
        this.closeGameModal();
      },
      error: (err) => this.actionError = err.error?.error || 'Action failed',
    });
  }

  private emptyTeamDraft(): TeamDraft {
    return { pointFighterId: '', secondFighterId: '', fuse: '' };
  }

  private teamDraftToPayload(draft: TeamDraft) {
    return {
      pointFighterId: draft.pointFighterId,
      secondFighterId: draft.secondFighterId,
      fuse: draft.fuse,
    };
  }
}
