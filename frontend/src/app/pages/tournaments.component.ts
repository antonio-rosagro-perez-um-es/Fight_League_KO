import { AsyncPipe, DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, combineLatest, of, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { TournamentGame, TournamentView } from '../core/api.models';
import { AuthService } from '../core/auth.service';
import { ConfirmDialogComponent } from '../shared/confirm-dialog.component';

@Component({
  selector: 'app-tournaments',
  imports: [AsyncPipe, ConfirmDialogComponent, DatePipe, ReactiveFormsModule, RouterLink],
  template: `
    <div class="page-heading">
      <div>
        <p class="eyebrow">Upcoming events</p>
        <h1>Tournaments</h1>
      </div>
      @if (auth.authenticated()) {
        <button type="button" (click)="startCreate()">Create tournament</button>
      } @else {
        <a class="primary-link" routerLink="/register">Create tournament</a>
      }
    </div>

    @if (auth.authenticated()) {
      <section class="owned-section panel">
        <div class="section-title">
          <div>
            <p class="eyebrow">Organizer desk</p>
            <h2>Your tournaments</h2>
          </div>
          <span>{{ auth.role() === 'ORGANIZER' ? 'Organizer' : 'Ready to host' }}</span>
        </div>
        <div class="owned-grid">
          @if (ownedTournaments$ | async; as owned) {
            @for (tournament of owned; track tournament.id) {
              <button type="button" class="owned-card" (click)="openOwnerModal(tournament)">
                <strong>{{ tournament.title }}</strong>
                <span>{{ tournament.state }} · {{ tournament.playerCount }}/{{ tournament.maxPlayers }} players</span>
              </button>
            } @empty {
              <div class="empty-state">Create your first tournament to unlock organizer controls.</div>
            }
          }
        </div>
      </section>
    }

    <div class="grid">
      @if (tournaments$ | async; as tournaments) {
        @for (tournament of tournaments; track tournament.id) {
          <a class="panel tournament-card" [routerLink]="['/tournaments', tournament.id]">
            <div>
              <span class="state-pill">{{ tournament.state }}</span>
              <h2>{{ tournament.title }}</h2>
              <p>Starts {{ tournament.startDate | date:'mediumDate' }} · registration closes {{ tournament.inscriptionCloseDate | date:'mediumDate' }}</p>
            </div>
            <div class="slots">
              <strong>{{ tournament.remainingSlots }}</strong>
              <span>slots left</span>
            </div>
          </a>
        } @empty {
          <div class="empty-state">No active tournaments.</div>
        }
      }
    </div>

    @if (showCreateModal) {
      <div class="modal-overlay" (click)="closeCreate()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-heading">
            <div>
              <p class="eyebrow">New organizer event</p>
              <h2>Create tournament</h2>
            </div>
            <button type="button" class="ghost" (click)="closeCreate()">Close</button>
          </div>
          <form [formGroup]="createForm" (ngSubmit)="createTournament()">
            <label>Title <input formControlName="title"></label>
            <label>Max players <input type="number" formControlName="maxPlayers" min="2"></label>
            <label>Registration close date <input type="date" formControlName="inscriptionCloseDate"></label>
            <label>Start date <input type="date" formControlName="starDate"></label>
            @if (createError) { <p class="error full">{{ createError }}</p> }
            <button type="submit" [disabled]="createForm.invalid">Create and become organizer</button>
          </form>
        </div>
      </div>
    }

    @if (ownerModalTournament) {
      <div class="modal-overlay" (click)="closeOwnerModal()">
        <div class="modal-content wide" (click)="$event.stopPropagation()">
          <div class="modal-heading">
            <div>
              <p class="eyebrow">Owner controls</p>
              <h2>{{ ownerModalTournament.title }}</h2>
            </div>
            <button type="button" class="ghost" (click)="closeOwnerModal()">Close</button>
          </div>

          <form [formGroup]="ownerForm" (ngSubmit)="updateOwnerTournament(ownerModalTournament.id)">
            <label>Max players <input type="number" formControlName="maxPlayers" min="2"></label>
            <label>Registration close date <input type="date" formControlName="inscriptionCloseDate"></label>
            <label>Start date <input type="date" formControlName="startDate"></label>
            @if (ownerError) { <p class="error full">{{ ownerError }}</p> }
            <button type="submit" [disabled]="ownerForm.invalid || ownerModalTournament.state !== 'REGISTRATION'">Save changes</button>
          </form>

          <section class="bracket-preview">
            <div class="section-title compact">
              <div>
                <p class="eyebrow">Bracket preview</p>
                <h3>Generated games</h3>
              </div>
            </div>
            @if (ownerBracket$ | async; as games) {
              @if (games.length) {
                <div class="mini-bracket">
                  @for (round of rounds(games); track round.number) {
                    <div class="mini-round">
                      <strong>R{{ round.number }}</strong>
                      @for (game of round.games; track game.id) {
                        <a [routerLink]="['/tournaments', ownerModalTournament.id]">
                          {{ game.user1Username || 'TBD' }} vs {{ game.user2Username || 'TBD' }}
                        </a>
                      }
                    </div>
                  }
                </div>
              } @else {
                <div class="empty-state">No generated games yet.</div>
              }
            }
          </section>

          <div class="owner-actions">
            <button type="button" (click)="closeRegistrations(ownerModalTournament.id)" [disabled]="ownerModalTournament.state !== 'REGISTRATION'">Close registrations</button>
            <button type="button" class="danger" (click)="requestCancelTournament(ownerModalTournament.id, ownerModalTournament.title)">Cancel tournament</button>
            <a [routerLink]="['/tournaments', ownerModalTournament.id]">Open expanded bracket</a>
          </div>
        </div>
      </div>
    }

    @if (confirmDelete) {
      <app-confirm-dialog
        [title]="confirmDelete.title"
        [message]="confirmDelete.message"
        confirmLabel="Cancel tournament"
        (confirmed)="confirmDeleteAction()"
        (cancelled)="cancelDelete()"
      />
    }
  `,
  styles: [`
    .page-heading { align-items: center; display: flex; justify-content: space-between; margin-bottom: 1.5rem; }
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .4rem 0 0; text-transform: uppercase; }
    h2 { margin: 0 0 .35rem; }
    button, .primary-link, .owner-actions a { background: #20d964; border: 0; border-radius: 999px; color: white; cursor: pointer; padding: .75rem 1rem; text-decoration: none; }
    button:disabled { cursor: not-allowed; opacity: .5; }
    .ghost { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.14); }
    .danger { background: #c7343f; }
    .owned-section { margin-bottom: 1rem; }
    .section-title { align-items: center; display: flex; justify-content: space-between; margin-bottom: 1rem; }
    .section-title span, .state-pill { background: rgba(124,255,159,.16); border-radius: 999px; color: #7cff9f; padding: .35rem .65rem; }
    .state-pill { display: inline-flex; margin-bottom: .55rem; }
    .owned-grid { display: grid; gap: .75rem; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); }
    .owned-card { align-items: start; background: linear-gradient(135deg, rgba(0,0,0,.5), rgba(0,24,36,.72)), url('/assets/Backgrounds/Background_Blue.webp') center/cover; border: 1px solid rgba(124,255,159,.18); border-radius: 18px; display: grid; gap: .45rem; padding: 1rem; text-align: left; }
    .owned-card span { color: #c8d3ed; }
    .tournament-card { align-items: center; color: white; display: flex; gap: 1rem; justify-content: space-between; text-decoration: none; }
    .tournament-card p { color: #c8d3ed; margin: 0; }
    .slots { background: rgba(255, 70, 85, .18); border-radius: 18px; min-width: 110px; padding: 1rem; text-align: center; }
    .slots strong { display: block; font-size: 2rem; }
    .modal-overlay { align-items: center; background: rgba(0,0,0,.58); display: flex; inset: 0; justify-content: center; position: fixed; z-index: 100; }
    .modal-content { background: linear-gradient(135deg, rgba(0,0,0,.82), rgba(6,18,10,.9)), url('/assets/Backgrounds/Background_Purple.webp') center/cover; border: 1px solid rgba(255,255,255,.12); border-radius: 20px; max-height: 84vh; max-width: 640px; overflow-y: auto; padding: 1.5rem; width: 92vw; }
    .modal-content.wide { max-width: 860px; }
    .modal-heading { align-items: center; display: flex; justify-content: space-between; margin-bottom: 1rem; }
    form { display: grid; gap: .85rem; grid-template-columns: repeat(2, minmax(0, 1fr)); }
    label { color: #c8d3ed; display: grid; gap: .35rem; }
    input { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 12px; color: white; color-scheme: dark; padding: .75rem; }
    .full { grid-column: 1 / -1; }
    .owner-actions { border-top: 1px solid rgba(255,255,255,.1); display: flex; flex-wrap: wrap; gap: .75rem; margin-top: 1rem; padding-top: 1rem; }
    .bracket-preview { border-top: 1px solid rgba(255,255,255,.1); margin-top: 1rem; padding-top: 1rem; }
    .compact { margin-bottom: .75rem; }
    .mini-bracket { display: flex; gap: .75rem; overflow-x: auto; padding-bottom: .35rem; }
    .mini-round { display: grid; gap: .45rem; min-width: 180px; }
    .mini-round strong { color: #7cff9f; }
    .mini-round a { background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.1); border-radius: 12px; color: white; padding: .65rem; text-decoration: none; }
    .error { color: #ff8a8a; }
    @media (max-width: 720px) { .page-heading, .tournament-card, .section-title { align-items: stretch; flex-direction: column; } form { grid-template-columns: 1fr; } }
  `]
})
export class TournamentsComponent {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  readonly auth = inject(AuthService);
  private readonly refresh$ = new BehaviorSubject<void>(undefined);
  private readonly ownerBracketId$ = new BehaviorSubject<string | null>(null);

  readonly tournaments$ = this.refresh$.pipe(switchMap(() => this.api.getTournaments()));
  readonly ownedTournaments$ = combineLatest([this.refresh$]).pipe(
    switchMap(() => this.auth.authenticated() ? this.api.getOwnedTournaments() : of([]))
  );
  readonly ownerBracket$ = this.ownerBracketId$.pipe(
    switchMap((id) => id ? this.api.getTournamentBracket(id) : of([]))
  );

  showCreateModal = false;
  ownerModalTournament: TournamentView | null = null;
  createError = '';
  ownerError = '';
  confirmDelete: { title: string; message: string; action: () => void } | null = null;

  readonly createForm = this.fb.nonNullable.group({
    title: ['', Validators.required],
    maxPlayers: [8, [Validators.required, Validators.min(2)]],
    starDate: ['', Validators.required],
    inscriptionCloseDate: ['', Validators.required],
  });

  readonly ownerForm = this.fb.nonNullable.group({
    maxPlayers: [8, [Validators.required, Validators.min(2)]],
    startDate: ['', Validators.required],
    inscriptionCloseDate: ['', Validators.required],
  });

  startCreate(): void {
    this.createError = '';
    this.createForm.reset({ title: '', maxPlayers: 8, starDate: '', inscriptionCloseDate: '' });
    this.showCreateModal = true;
  }

  closeCreate(): void {
    this.showCreateModal = false;
  }

  createTournament(): void {
    this.createError = '';
    this.api.createTournament(this.createForm.getRawValue()).subscribe({
      next: () => {
        const user = this.auth.user();
        if (user && user.role === 'REGISTERED') {
          this.auth.updateLocalUser({ ...user, role: 'ORGANIZER' });
        }
        this.closeCreate();
        this.refresh$.next();
      },
      error: (err) => this.createError = err.error?.error || 'Could not create tournament',
    });
  }

  openOwnerModal(tournament: TournamentView): void {
    this.ownerError = '';
    this.ownerModalTournament = tournament;
    this.ownerBracketId$.next(tournament.id);
    this.ownerForm.setValue({
      maxPlayers: tournament.maxPlayers,
      startDate: tournament.startDate,
      inscriptionCloseDate: tournament.inscriptionCloseDate,
    });
  }

  closeOwnerModal(): void {
    this.ownerModalTournament = null;
    this.ownerBracketId$.next(null);
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

  updateOwnerTournament(id: string): void {
    this.ownerError = '';
    this.api.updateTournament(id, this.ownerForm.getRawValue()).subscribe({
      next: () => this.afterOwnerAction(),
      error: (err) => this.ownerError = err.error?.error || 'Could not update tournament',
    });
  }

  closeRegistrations(id: string): void {
    this.ownerError = '';
    this.api.closeTournamentRegistrations(id).subscribe({
      next: () => this.afterOwnerAction(),
      error: (err) => this.ownerError = err.error?.error || 'Could not close registrations',
    });
  }

  requestCancelTournament(id: string, title: string): void {
    this.confirmDelete = {
      title: `Cancel tournament ${title}?`,
      message: 'This tournament will be cancelled and hidden from normal tournament lists.',
      action: () => this.cancelTournament(id),
    };
  }

  confirmDeleteAction(): void {
    const action = this.confirmDelete?.action;
    this.confirmDelete = null;
    action?.();
  }

  cancelDelete(): void {
    this.confirmDelete = null;
  }

  private cancelTournament(id: string): void {
    this.ownerError = '';
    this.api.deleteTournament(id).subscribe({
      next: () => this.afterOwnerAction(),
      error: (err) => this.ownerError = err.error?.error || 'Could not cancel tournament',
    });
  }

  private afterOwnerAction(): void {
    this.closeOwnerModal();
    this.refresh$.next();
  }
}
