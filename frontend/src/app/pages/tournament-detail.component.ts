import { AsyncPipe, DatePipe, NgTemplateOutlet } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BehaviorSubject, combineLatest, map, Observable, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { FighterBanner, TournamentGame } from '../core/api.models';
import { AuthService } from '../core/auth.service';

type TeamDraft = {
  pointFighterId: string;
  secondFighterId: string;
  fuse: string;
};

@Component({
  selector: 'app-tournament-detail',
  imports: [AsyncPipe, DatePipe, FormsModule, NgTemplateOutlet, RouterLink],
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
            <span>Click a match for details</span>
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
                          <span [class.winner]="game.winnerId === game.user1Id">{{ game.user1Username || 'TBD' }}</span>
                          <em>vs</em>
                          <span [class.winner]="game.winnerId === game.user2Id">{{ game.user2Username || 'TBD' }}</span>
                          @if (game.winnerId) {
                            <strong>Winner set</strong>
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
              <div class="empty-state">Bracket has not been generated yet. Missing match slots will appear once matchups are generated.</div>
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
                <button type="button" (click)="setWinner(selectedGame.id, selectedGame.user1Id)">{{ selectedGame.user1Username }} won</button>
                <button type="button" (click)="setWinner(selectedGame.id, selectedGame.user2Id)">{{ selectedGame.user2Username }} won</button>
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
        <select [(ngModel)]="draft.pointFighterId" [ngModelOptions]="{ standalone: true }">
          <option value="">Choose fighter</option>
          @for (fighter of fighters; track fighter.id) {
            <option [value]="fighter.id">{{ fighter.name }}</option>
          }
        </select>
      </label>
      <label>Second fighter
        <select [(ngModel)]="draft.secondFighterId" [ngModelOptions]="{ standalone: true }">
          <option value="">Choose fighter</option>
          @for (fighter of fighters; track fighter.id) {
            <option [value]="fighter.id">{{ fighter.name }}</option>
          }
        </select>
      </label>
      <label>Fuse
        <select [(ngModel)]="draft.fuse" [ngModelOptions]="{ standalone: true }">
          <option value="">Choose fuse</option>
          @for (fuse of fuses; track fuse) {
            <option [value]="fuse">{{ fuse }}</option>
          }
        </select>
      </label>
    </ng-template>
  `,
  styles: [`
    .hero { align-items: center; display: grid; gap: 2rem; grid-template-columns: 1fr 220px; margin-bottom: 1rem; }
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .4rem 0 1rem; text-transform: uppercase; }
    h2 { margin: 0; }
    .join-card { background: rgba(255,70,85,.16); border-radius: 22px; display: grid; gap: .65rem; padding: 1.25rem; text-align: center; }
    .join-card strong { font-size: 3rem; }
    button, .join-card a { background: #ff4655; border: 0; border-radius: 999px; color: white; cursor: pointer; padding: .8rem 1rem; text-decoration: none; }
    button:disabled { cursor: not-allowed; opacity: .55; }
    .ghost { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.14); }
    .owner-note { color: #ffbd59; font-weight: 700; }
    .owner-panel { align-items: center; display: flex; gap: 1rem; justify-content: space-between; margin-bottom: 1rem; }
    .owner-actions, .winner-actions { display: flex; flex-wrap: wrap; gap: .5rem; }
    .columns { display: grid; gap: 1rem; grid-template-columns: minmax(0, 1.4fr) minmax(280px, .6fr); }
    .panel-heading { align-items: center; display: flex; justify-content: space-between; margin-bottom: 1rem; }
    .panel-heading span { color: #c8d3ed; font-size: .9rem; }
    .bracket-scroll { overflow-x: auto; padding-bottom: .5rem; }
    .bracket-grid { align-items: start; display: flex; gap: 1rem; min-width: max-content; }
    .round-column { display: grid; gap: .85rem; min-width: 230px; }
    .round-column h3 { color: #ffbd59; font-size: .85rem; letter-spacing: .08em; margin: 0; text-transform: uppercase; }
    .match-card { background: linear-gradient(150deg, rgba(255,70,85,.12), rgba(255,255,255,.06)); border: 1px solid rgba(255,255,255,.12); border-radius: 16px; display: grid; gap: .45rem; padding: 1rem; text-align: left; }
    .match-card small { color: #ffbd59; }
    .match-card em { color: #7f8aa8; font-style: normal; }
    .match-card strong { color: #78ffb0; font-size: .78rem; }
    .match-card .pending { color: #c8d3ed; }
    .winner { color: #78ffb0; font-weight: 800; }
    .standing-row { border-bottom: 1px solid rgba(255,255,255,.1); display: grid; gap: .75rem; grid-template-columns: 60px 1fr auto; padding: .9rem 0; }
    .modal-overlay { align-items: center; background: rgba(0,0,0,.58); display: flex; inset: 0; justify-content: center; position: fixed; z-index: 100; }
    .modal-content { background: #12172b; border: 1px solid rgba(255,255,255,.12); border-radius: 20px; max-height: 84vh; max-width: 820px; overflow-y: auto; padding: 1.5rem; width: 92vw; }
    .modal-heading { align-items: center; display: flex; justify-content: space-between; margin-bottom: 1rem; }
    .winner-box { background: rgba(120,255,176,.1); border: 1px solid rgba(120,255,176,.18); border-radius: 18px; display: grid; gap: .25rem; margin-bottom: 1rem; padding: 1rem; }
    .winner-box span { color: #c8d3ed; }
    .team-editor { border-top: 1px solid rgba(255,255,255,.1); margin-top: 1rem; padding-top: 1rem; }
    .team-grid { display: grid; gap: 1rem; grid-template-columns: repeat(2, minmax(0, 1fr)); margin: 1rem 0; }
    label { color: #c8d3ed; display: grid; gap: .35rem; margin-bottom: .65rem; }
    select { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 12px; color: white; padding: .75rem; }
    option { background: #12172b; color: white; }
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

  teamDraftValid(draft: TeamDraft): boolean {
    return !!draft.pointFighterId && !!draft.secondFighterId && !!draft.fuse;
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
