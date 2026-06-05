import { AsyncPipe, DecimalPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BehaviorSubject, combineLatest, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { FighterBanner, Team, TeamWrite } from '../core/api.models';
import { ConfirmDialogComponent } from '../shared/confirm-dialog.component';

@Component({
  selector: 'app-admin-teams',
  imports: [AsyncPipe, ConfirmDialogComponent, DecimalPipe, ReactiveFormsModule],
  template: `
    <div class="admin-heading">
      <div>
        <p class="eyebrow">Admin</p>
        <h1>Team Management</h1>
      </div>
      <button type="button" (click)="startCreate()">New Team</button>
    </div>

    @if (vm$ | async; as vm) {
      @if (showForm) {
        <section class="panel editor">
          <div class="editor-heading">
            <h2>{{ editingId ? 'Edit team' : 'Create team' }}</h2>
            <button type="button" class="ghost" (click)="cancelEdit()">Close</button>
          </div>
          <form [formGroup]="form" (ngSubmit)="save()">
            <label>Point fighter
              <select formControlName="pointFighterId">
                <option value="">Select fighter</option>
                @for (fighter of vm.fighters; track fighter.id) {
                  <option [value]="fighter.id">{{ fighter.name }}</option>
                }
              </select>
            </label>
            <label>Second fighter
              <select formControlName="secondFighterId">
                <option value="">Select fighter</option>
                @for (fighter of vm.fighters; track fighter.id) {
                  <option [value]="fighter.id">{{ fighter.name }}</option>
                }
              </select>
            </label>
            <label>Fuse
              <select formControlName="fuse">
                <option value="DOUBLE_DOWN">Double Down</option>
                <option value="FREESTYLE">Freestyle</option>
                <option value="TWO_X_ASSIST">2X Assist</option>
                <option value="JUGGERNAUT">Juggernaut</option>
                <option value="SIDEKICK">Sidekick</option>
              </select>
            </label>
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
              <th>Point Fighter</th>
              <th>Second Fighter</th>
              <th>Fuse</th>
              <th>Plays</th>
              <th>Wins</th>
              <th>Losses</th>
              <th>Win Rate</th>
              <th>Deleted</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (team of vm.teams; track team.id) {
              <tr [class.deleted]="team.deleted">
                <td>{{ team.id }}</td>
                <td>{{ fighterName(team.pointFighterId, vm.fighters) }}</td>
                <td>{{ fighterName(team.secondFighterId, vm.fighters) }}</td>
                <td>{{ team.fuse }}</td>
                <td>{{ team.playCounter }}</td>
                <td>{{ team.winCounter }}</td>
                <td>{{ team.loseCounter }}</td>
                <td>{{ winRate(team) | number:'1.0-1' }}%</td>
                <td>{{ team.deleted ? 'Yes' : 'No' }}</td>
                <td>
                  <div class="row-actions">
                    <button type="button" (click)="startEdit(team)">Edit</button>
                    @if (team.deleted) {
                      <button type="button" (click)="restore(team.id)">Restore</button>
                    } @else {
                      <button type="button" class="danger" (click)="requestDelete(team.id, fighterName(team.pointFighterId, vm.fighters), fighterName(team.secondFighterId, vm.fighters))">Delete</button>
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
    select { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 12px; color: white; padding: .75rem; }
    option { color: #001f0b; }
    .full { grid-column: 1 / -1; }
    .table-wrap { overflow-x: auto; }
    table { border-collapse: collapse; min-width: 1300px; width: 100%; }
    th, td { border-bottom: 1px solid rgba(255,255,255,.1); padding: .8rem; text-align: left; }
    th { color: #7cff9f; font-size: .78rem; text-transform: uppercase; }
    td:first-child { font-family: ui-monospace, SFMono-Regular, Consolas, monospace; font-size: .78rem; max-width: 190px; overflow: hidden; text-overflow: ellipsis; }
    .deleted { opacity: .55; }
    .row-actions { display: flex; flex-wrap: wrap; gap: .5rem; }
    .error { color: #ff8a8a; }
    @media (max-width: 760px) { .admin-heading { align-items: stretch; flex-direction: column; } form { grid-template-columns: 1fr; } }
  `]
})
export class AdminTeamsComponent {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  private readonly refresh$ = new BehaviorSubject<void>(undefined);
  readonly vm$ = this.refresh$.pipe(switchMap(() => combineLatest({
    teams: this.api.getAllTeamsForAdmin(),
    fighters: this.api.getFighterBanners(),
  })));
  showForm = false;
  editingId: string | null = null;
  error = '';
  confirmDelete: { title: string; message: string; action: () => void } | null = null;

  readonly form = this.fb.nonNullable.group({
    pointFighterId: ['', Validators.required],
    secondFighterId: ['', Validators.required],
    fuse: ['DOUBLE_DOWN', Validators.required],
  });

  startCreate(): void {
    this.editingId = null;
    this.error = '';
    this.form.reset({ pointFighterId: '', secondFighterId: '', fuse: 'DOUBLE_DOWN' });
    this.showForm = true;
  }

  startEdit(team: Team): void {
    this.editingId = team.id;
    this.error = '';
    this.form.setValue({
      pointFighterId: team.pointFighterId,
      secondFighterId: team.secondFighterId,
      fuse: team.fuse,
    });
    this.showForm = true;
  }

  cancelEdit(): void {
    this.showForm = false;
    this.editingId = null;
  }

  save(): void {
    this.error = '';
    const team = this.form.getRawValue() satisfies TeamWrite;
    if (team.pointFighterId === team.secondFighterId) {
      this.error = 'Point and second fighter must be different';
      return;
    }

    if (this.editingId) {
      this.api.updateTeam(this.editingId, team).subscribe({
        next: () => this.afterSave(),
        error: (err: { error?: { error?: string } }) => this.error = err.error?.error || 'Could not save team',
      });
      return;
    }

    this.api.createTeam(team).subscribe({
      next: () => this.afterSave(),
      error: (err: { error?: { error?: string } }) => this.error = err.error?.error || 'Could not create team',
    });
  }

  requestDelete(id: string, pointFighter: string, secondFighter: string): void {
    this.confirmDelete = {
      title: `Delete team ${pointFighter} / ${secondFighter}?`,
      message: 'This team will be deactivated and hidden from normal use.',
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
    this.api.deleteTeam(id).subscribe(() => this.refresh$.next());
  }

  restore(id: string): void {
    this.api.restoreTeam(id).subscribe(() => this.refresh$.next());
  }

  fighterName(id: string, fighters: FighterBanner[]): string {
    return fighters.find(fighter => fighter.id === id)?.name || 'Unknown fighter';
  }

  winRate(team: Team): number {
    return team.playCounter > 0 ? team.winCounter * 100 / team.playCounter : 0;
  }

  private afterSave(): void {
    this.cancelEdit();
    this.refresh$.next();
  }
}
