import { AsyncPipe, DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { TournamentCreate, TournamentUpdate, TournamentView } from '../core/api.models';
import { ConfirmDialogComponent } from '../shared/confirm-dialog.component';

@Component({
  selector: 'app-admin-tournaments',
  imports: [AsyncPipe, ConfirmDialogComponent, DatePipe, ReactiveFormsModule, RouterLink],
  template: `
    <div class="admin-heading">
      <div>
        <p class="eyebrow">Admin</p>
        <h1>Tournament Management</h1>
      </div>
      <button type="button" (click)="startCreate()">New Tournament</button>
    </div>

    @if (showForm) {
      <section class="panel editor">
        <div class="editor-heading">
          <h2>{{ editingId ? 'Edit tournament' : 'Create tournament' }}</h2>
          <button type="button" class="ghost" (click)="cancelEdit()">Close</button>
        </div>
        <form [formGroup]="form" (ngSubmit)="save()">
          <label class="full">Title <input formControlName="title" [readonly]="!!editingId"></label>
          <label>Max players <input formControlName="maxPlayers" type="number" min="2"></label>
          <label>Start date <input formControlName="startDate" type="date"></label>
          <label>Registration close <input formControlName="inscriptionCloseDate" type="date"></label>
          @if (error) { <p class="error full">{{ error }}</p> }
          <button type="submit" [disabled]="form.invalid">Save</button>
        </form>
      </section>
    }

    <section class="panel table-wrap">
      @if (tournaments$ | async; as tournaments) {
        <table>
          <thead>
            <tr>
              <th>Title</th>
              <th>State</th>
              <th>Players</th>
              <th>Start</th>
              <th>Deleted</th>
              <th>Scored</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (tournament of tournaments; track tournament.id) {
              <tr [class.deleted]="tournament.deleted">
                <td><a [routerLink]="['/tournaments', tournament.id]">{{ tournament.title }}</a></td>
                <td>{{ tournament.state }}</td>
                <td>{{ tournament.playerCount }} / {{ tournament.maxPlayers }}</td>
                <td>{{ tournament.startDate | date:'mediumDate' }}</td>
                <td>{{ tournament.deleted ? 'Yes' : 'No' }}</td>
                <td>{{ tournament.scored ? 'Yes' : 'No' }}</td>
                <td>
                  <div class="row-actions">
                    <button type="button" (click)="startEdit(tournament)" [disabled]="tournament.state !== 'REGISTRATION'">Edit</button>
                    <button type="button" (click)="closeRegistrations(tournament.id)" [disabled]="tournament.state !== 'REGISTRATION'">Close</button>
                    <button type="button" (click)="generate(tournament.id)" [disabled]="tournament.state !== 'WAITING_START' && tournament.state !== 'IN_PROGRESS'">Generate</button>
                    @if (tournament.deleted) {
                      <button type="button" (click)="restore(tournament.id)">Restore</button>
                    } @else {
                      <button type="button" class="danger" (click)="requestDelete(tournament.id, tournament.title)">Delete</button>
                    }
                  </div>
                </td>
              </tr>
            }
          </tbody>
        </table>
      }
    </section>

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
    input { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 12px; color: white; padding: .75rem; }
    .full { grid-column: 1 / -1; }
    .table-wrap { overflow-x: auto; }
    table { border-collapse: collapse; min-width: 1260px; width: 100%; }
    th, td { border-bottom: 1px solid rgba(255,255,255,.1); padding: .8rem; text-align: left; }
    th { color: #7cff9f; font-size: .78rem; text-transform: uppercase; }
    a { color: #d9e5ff; }
    .deleted { opacity: .55; }
    .row-actions { display: flex; flex-wrap: wrap; gap: .5rem; }
    .error { color: #ff8a8a; }
    @media (max-width: 760px) { .admin-heading { align-items: stretch; flex-direction: column; } form { grid-template-columns: 1fr; } }
  `]
})
export class AdminTournamentsComponent {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  private readonly refresh$ = new BehaviorSubject<void>(undefined);
  readonly tournaments$ = this.refresh$.pipe(switchMap(() => this.api.getAllTournamentsForAdmin()));
  showForm = false;
  editingId: string | null = null;
  error = '';
  confirmDelete: { title: string; message: string; action: () => void } | null = null;

  readonly form = this.fb.nonNullable.group({
    title: ['', Validators.required],
    maxPlayers: [2, [Validators.required, Validators.min(2)]],
    startDate: ['', Validators.required],
    inscriptionCloseDate: ['', Validators.required],
  });

  startCreate(): void {
    this.editingId = null;
    this.error = '';
    this.form.reset({ title: '', maxPlayers: 2, startDate: '', inscriptionCloseDate: '' });
    this.form.controls.title.enable();
    this.showForm = true;
  }

  startEdit(tournament: TournamentView): void {
    this.editingId = tournament.id;
    this.error = '';
    this.form.setValue({
      title: tournament.title,
      maxPlayers: tournament.maxPlayers,
      startDate: tournament.startDate,
      inscriptionCloseDate: tournament.inscriptionCloseDate,
    });
    this.form.controls.title.disable();
    this.showForm = true;
  }

  cancelEdit(): void {
    this.showForm = false;
    this.editingId = null;
    this.form.controls.title.enable();
  }

  save(): void {
    this.error = '';
    const raw = this.form.getRawValue();

    if (this.editingId) {
      const update: TournamentUpdate = {
        maxPlayers: raw.maxPlayers,
        startDate: raw.startDate,
        inscriptionCloseDate: raw.inscriptionCloseDate,
      };
      this.api.updateTournament(this.editingId, update).subscribe({
        next: () => this.afterChange(),
        error: (err: { error?: { error?: string } }) => this.error = err.error?.error || 'Could not update tournament',
      });
      return;
    }

    const create: TournamentCreate = {
      title: raw.title,
      maxPlayers: raw.maxPlayers,
      starDate: raw.startDate,
      inscriptionCloseDate: raw.inscriptionCloseDate,
    };
    this.api.createTournament(create).subscribe({
      next: () => this.afterChange(),
      error: (err: { error?: { error?: string } }) => this.error = err.error?.error || 'Could not create tournament',
    });
  }

  closeRegistrations(id: string): void {
    this.api.closeTournamentRegistrations(id).subscribe(() => this.refresh$.next());
  }

  generate(id: string): void {
    this.api.generateTournamentMatchups(id).subscribe(() => this.refresh$.next());
  }

  requestDelete(id: string, title: string): void {
    this.confirmDelete = {
      title: `Delete tournament ${title}?`,
      message: 'This tournament will be cancelled and hidden from normal use.',
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
    this.api.deleteTournament(id).subscribe(() => this.refresh$.next());
  }

  restore(id: string): void {
    this.api.restoreTournament(id).subscribe(() => this.refresh$.next());
  }

  private afterChange(): void {
    this.cancelEdit();
    this.refresh$.next();
  }
}
