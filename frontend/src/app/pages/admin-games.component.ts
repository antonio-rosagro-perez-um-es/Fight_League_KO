import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BehaviorSubject, combineLatest, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { AdminUser, FighterBanner, Game, GameCreate, Team } from '../core/api.models';
import { ConfirmDialogComponent } from '../shared/confirm-dialog.component';

@Component({
  selector: 'app-admin-games',
  imports: [AsyncPipe, ConfirmDialogComponent, ReactiveFormsModule],
  template: `
    <div class="admin-heading">
      <div>
        <p class="eyebrow">Admin</p>
        <h1>Game Management</h1>
      </div>
      <button type="button" (click)="startCreate()">New Game</button>
    </div>

    @if (vm$ | async; as vm) {
      @if (showForm) {
        <section class="panel editor">
          <div class="editor-heading">
            <h2>{{ editingId ? 'Edit game' : 'Create game' }}</h2>
            <button type="button" class="ghost" (click)="cancelEdit()">Close</button>
          </div>
          <form [formGroup]="form" (ngSubmit)="save()">
            <label>User 1
              <select formControlName="user1">
                <option value="">Select user</option>
                @for (user of vm.users; track user.id) {
                  <option [value]="user.id">{{ user.username }}</option>
                }
              </select>
            </label>
            <label>User 2
              <select formControlName="user2">
                <option value="">Select user</option>
                @for (user of vm.users; track user.id) {
                  <option [value]="user.id">{{ user.username }}</option>
                }
              </select>
            </label>
            @if (!editingId) {
              <label>Date <input formControlName="gameDate" type="date"></label>
            }
            @if (editingId) {
              <label>Team User 1
                <select formControlName="team1">
                  <option value="">No team</option>
                  @for (team of vm.teams; track team.id) {
                    <option [value]="team.id">{{ teamLabel(team.id, vm.teams, vm.fighters) }}</option>
                  }
                </select>
              </label>
              <label>Team User 2
                <select formControlName="team2">
                  <option value="">No team</option>
                  @for (team of vm.teams; track team.id) {
                    <option [value]="team.id">{{ teamLabel(team.id, vm.teams, vm.fighters) }}</option>
                  }
                </select>
              </label>
            }
            @if (error) { <p class="error full">{{ error }}</p> }
            <button type="submit" [disabled]="form.invalid">Save</button>
          </form>
        </section>
      }

      <section class="panel table-wrap">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Players</th>
              <th>Tournament</th>
              <th>Teams</th>
              <th>Winner</th>
              <th>Date</th>
              <th>Bracket</th>
              <th>Deleted</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (game of vm.games; track game.id) {
              <tr [class.deleted]="game.delete">
                <td>{{ game.id }}</td>
                <td>{{ username(game.user1Id, vm.users) }} vs {{ username(game.user2Id, vm.users) }}</td>
                <td>{{ game.tournament?.title || 'Exhibition' }}</td>
                <td>
                  <span>{{ teamLabel(game.teamUser1Id, vm.teams, vm.fighters) }}</span><br>
                  <span>{{ teamLabel(game.teamUser2Id, vm.teams, vm.fighters) }}</span>
                </td>
                <td>{{ game.winnerId ? username(game.winnerId, vm.users) : 'Pending' }}</td>
                <td>{{ game.gameDate }}</td>
                <td>R{{ game.roundNumber }} / #{{ game.bracketPosition }}</td>
                <td>{{ game.delete ? 'Yes' : 'No' }}</td>
                <td>
                  <div class="row-actions">
                    <button type="button" (click)="startEdit(game)">Edit</button>
                    @if (!game.winnerId) {
                      <button type="button" (click)="setWinner(game.id, game.user1Id)">User 1 wins</button>
                      <button type="button" (click)="setWinner(game.id, game.user2Id)">User 2 wins</button>
                    }
                    @if (game.delete) {
                      <button type="button" (click)="restore(game.id)">Restore</button>
                    } @else {
                      <button type="button" class="danger" (click)="requestDelete(game.id, username(game.user1Id, vm.users), username(game.user2Id, vm.users))">Delete</button>
                    }
                  </div>
                </td>
              </tr>
            }
          </tbody>
        </table>
      </section>
    }

    @if (confirmDelete) {
      <app-confirm-dialog
        [title]="confirmDelete.title"
        [message]="confirmDelete.message"
        confirmLabel="Delete"
        (confirmed)="confirmDeleteAction()"
        (cancelled)="cancelDelete()"
      />
    }
  `,
  styles: [`
    .admin-heading { align-items: center; display: flex; justify-content: space-between; margin-bottom: 1rem; }
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .3rem 0; text-transform: uppercase; }
    button { background: #20d964; border: 0; border-radius: 999px; color: white; cursor: pointer; padding: .65rem .9rem; }
    button:disabled { cursor: not-allowed; opacity: .45; }
    .ghost, .row-actions button { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.14); }
    .row-actions button.danger { background: #c7343f; border-color: rgba(255,122,132,.55); }
    .editor { margin-bottom: 1rem; }
    .editor-heading { align-items: center; display: flex; justify-content: space-between; }
    form { display: grid; gap: .85rem; grid-template-columns: repeat(3, minmax(0, 1fr)); }
    label { color: #c8d3ed; display: grid; gap: .35rem; }
    input, select { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 12px; color: white; padding: .75rem; }
    option { color: #001f0b; }
    .full { grid-column: 1 / -1; }
    .table-wrap { overflow-x: auto; }
    table { border-collapse: collapse; min-width: 1440px; width: 100%; }
    th, td { border-bottom: 1px solid rgba(255,255,255,.1); padding: .8rem; text-align: left; vertical-align: top; }
    th { color: #7cff9f; font-size: .78rem; text-transform: uppercase; }
    td:first-child { font-family: ui-monospace, SFMono-Regular, Consolas, monospace; font-size: .78rem; max-width: 190px; overflow: hidden; text-overflow: ellipsis; }
    .deleted { opacity: .55; }
    .row-actions { display: flex; flex-wrap: wrap; gap: .5rem; }
    .error { color: #ff8a8a; }
    @media (max-width: 760px) { .admin-heading { align-items: stretch; flex-direction: column; } form { grid-template-columns: 1fr; } }
  `]
})
export class AdminGamesComponent {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  private readonly refresh$ = new BehaviorSubject<void>(undefined);
  readonly vm$ = this.refresh$.pipe(switchMap(() => combineLatest({
    games: this.api.getAllGamesForAdmin(),
    users: this.api.getAllUsersForAdmin(),
    teams: this.api.getAllTeamsForAdmin(),
    fighters: this.api.getFighterBanners(),
  })));
  showForm = false;
  editingId: string | null = null;
  error = '';
  confirmDelete: { title: string; message: string; action: () => void } | null = null;

  readonly form = this.fb.nonNullable.group({
    user1: ['', Validators.required],
    user2: ['', Validators.required],
    gameDate: [new Date().toISOString().slice(0, 10), Validators.required],
    team1: [''],
    team2: [''],
  });

  startCreate(): void {
    this.editingId = null;
    this.error = '';
    this.form.reset({ user1: '', user2: '', gameDate: new Date().toISOString().slice(0, 10), team1: '', team2: '' });
    this.showForm = true;
  }

  startEdit(game: Game): void {
    this.editingId = game.id;
    this.error = '';
    this.form.setValue({
      user1: game.user1Id,
      user2: game.user2Id,
      gameDate: game.gameDate,
      team1: game.teamUser1Id || '',
      team2: game.teamUser2Id || '',
    });
    this.showForm = true;
  }

  cancelEdit(): void {
    this.showForm = false;
    this.editingId = null;
  }

  save(): void {
    this.error = '';
    const raw = this.form.getRawValue();
    if (raw.user1 === raw.user2) {
      this.error = 'Players must be different';
      return;
    }

    if (this.editingId) {
      this.api.updateGame(this.editingId, {
        user1: raw.user1,
        user2: raw.user2,
        team1: raw.team1 || null,
        team2: raw.team2 || null,
      }).subscribe({
        next: () => this.afterSave(),
        error: (err: { error?: { error?: string } }) => this.error = err.error?.error || 'Could not save game',
      });
      return;
    }

    const game: GameCreate = { user1: raw.user1, user2: raw.user2, gameDate: raw.gameDate };
    this.api.createGame(game).subscribe({
      next: () => this.afterSave(),
      error: (err: { error?: { error?: string } }) => this.error = err.error?.error || 'Could not create game',
    });
  }

  setWinner(gameId: string, userId: string): void {
    this.api.setGameWinner(gameId, userId).subscribe(() => this.refresh$.next());
  }

  requestDelete(id: string, user1: string, user2: string): void {
    this.confirmDelete = {
      title: `Delete game ${user1} vs ${user2}?`,
      message: 'This game will be deactivated and excluded from normal admin lists.',
      action: () => this.delete(id),
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

  private delete(id: string): void {
    this.api.deleteGame(id).subscribe(() => this.refresh$.next());
  }

  restore(id: string): void {
    this.api.restoreGame(id).subscribe(() => this.refresh$.next());
  }

  username(id: string | null | undefined, users: AdminUser[]): string {
    return users.find(user => user.id === id)?.username || 'Unknown';
  }

  teamLabel(id: string | null | undefined, teams: Team[], fighters: FighterBanner[]): string {
    if (!id) {
      return 'No team';
    }
    const team = teams.find(item => item.id === id);
    if (!team) {
      return 'Unknown team';
    }
    return `${this.fighterName(team.pointFighterId, fighters)} / ${this.fighterName(team.secondFighterId, fighters)} (${team.fuse})`;
  }

  private fighterName(id: string, fighters: FighterBanner[]): string {
    return fighters.find(fighter => fighter.id === id)?.name || 'Unknown fighter';
  }

  private afterSave(): void {
    this.cancelEdit();
    this.refresh$.next();
  }
}
