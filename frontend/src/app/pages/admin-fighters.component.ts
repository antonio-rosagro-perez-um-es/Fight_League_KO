import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BehaviorSubject, forkJoin, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { Fighter, FighterWrite } from '../core/api.models';
import { ConfirmDialogComponent } from '../shared/confirm-dialog.component';

@Component({
  selector: 'app-admin-fighters',
  imports: [AsyncPipe, ConfirmDialogComponent, ReactiveFormsModule],
  template: `
    <div class="admin-heading">
      <div>
        <p class="eyebrow">Admin</p>
        <h1>Fighter Management</h1>
      </div>
      <button type="button" (click)="startCreate()">New Fighter</button>
    </div>

    @if (selectedIds.size) {
      <div class="bulk-bar">
        <span>{{ selectedIds.size }} selected</span>
        <button type="button" class="danger" (click)="requestDeleteSelected()">Delete selected</button>
        <button type="button" class="ghost" (click)="clearSelection()">Clear</button>
      </div>
    }

    @if (showForm) {
      <div class="modal-overlay" (click)="cancelEdit()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="editor-heading">
            <h2>{{ editingId ? 'Edit fighter' : 'Create fighter' }}</h2>
            <button type="button" class="ghost" (click)="cancelEdit()">Close</button>
          </div>
          <form [formGroup]="form" (ngSubmit)="save()">
            <label>Name <input formControlName="name"></label>
            <label>Slug <input formControlName="slug"></label>
            <label>Title <input formControlName="title"></label>
            <label>Region <input formControlName="region"></label>
            <label>Archetype <input formControlName="archetype"></label>
            <label>Likes <input formControlName="itLikes"></label>
            <label>Dislikes <input formControlName="itDislike"></label>
            <label class="full">Description <textarea formControlName="description"></textarea></label>
            <div class="stat-grid full">
              <label>Health <input formControlName="health" type="number"></label>
              <label>Range <input formControlName="range" type="number"></label>
              <label>Power <input formControlName="power" type="number"></label>
              <label>Vitality <input formControlName="vitality" type="number"></label>
              <label>Mobility <input formControlName="mobility" type="number"></label>
              <label>Ease of use <input formControlName="easyOfUse" type="number"></label>
            </div>
            @if (error) { <p class="error full">{{ error }}</p> }
            <button type="submit" [disabled]="form.invalid">Save</button>
          </form>
        </div>
      </div>
    }

    <section class="panel table-wrap">
      @if (fighters$ | async; as fighters) {
        <table>
          <thead>
            <tr>
              <th><input type="checkbox" [checked]="allSelected(fighters)" (change)="toggleAll(fighters)"></th>
              <th>ID</th>
              <th>Name</th>
              <th>Archetype</th>
              <th>Slug</th>
              <th>Deleted</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (fighter of fighters; track fighter.id) {
              <tr [class.deleted]="fighter.deleted" [class.selected]="selectedIds.has(fighter.id)">
                <td><input type="checkbox" [checked]="selectedIds.has(fighter.id)" (change)="toggleSelect(fighter.id)"></td>
                <td>{{ fighter.id }}</td>
                <td>{{ fighter.name }}</td>
                <td>{{ fighter.archetype }}</td>
                <td>{{ fighter.slug }}</td>
                <td>{{ fighter.deleted ? 'Yes' : 'No' }}</td>
                <td>
                  <div class="row-actions">
                    <button type="button" (click)="startEdit(fighter)">Edit</button>
                    @if (fighter.deleted) {
                      <button type="button" (click)="restore(fighter.id)">Restore</button>
                    } @else {
                      <button type="button" class="danger" (click)="requestDeactivate(fighter.id, fighter.name)">Delete</button>
                    }
                    <button type="button" (click)="view(fighter)">View</button>
                  </div>
                </td>
              </tr>
            }
          </tbody>
        </table>
      }
    </section>

    @if (selectedFighter) {
      <section class="panel info-panel">
        <h2>{{ selectedFighter.name }}</h2>
        <p>{{ selectedFighter.description }}</p>
        <dl>
          <div><dt>ID</dt><dd>{{ selectedFighter.id }}</dd></div>
          <div><dt>Slug</dt><dd>{{ selectedFighter.slug }}</dd></div>
          <div><dt>Archetype</dt><dd>{{ selectedFighter.archetype }}</dd></div>
          <div><dt>Region</dt><dd>{{ selectedFighter.region }}</dd></div>
          <div><dt>Title</dt><dd>{{ selectedFighter.title }}</dd></div>
          <div><dt>Likes</dt><dd>{{ selectedFighter.itLikes }}</dd></div>
          <div><dt>Dislikes</dt><dd>{{ selectedFighter.itDislike }}</dd></div>
          <div><dt>Health</dt><dd>{{ selectedFighter.health }}</dd></div>
          <div><dt>Range</dt><dd>{{ selectedFighter.range }}</dd></div>
          <div><dt>Power</dt><dd>{{ selectedFighter.power }}</dd></div>
          <div><dt>Vitality</dt><dd>{{ selectedFighter.vitality }}</dd></div>
          <div><dt>Mobility</dt><dd>{{ selectedFighter.mobility }}</dd></div>
          <div><dt>Ease of use</dt><dd>{{ selectedFighter.easyOfUse }}</dd></div>
          <div><dt>Played</dt><dd>{{ selectedFighter.playCounter }}</dd></div>
          <div><dt>Wins</dt><dd>{{ selectedFighter.winCounter }}</dd></div>
          <div><dt>Losses</dt><dd>{{ selectedFighter.loseCounter }}</dd></div>
          <div><dt>Win rate</dt><dd>{{ selectedFighter.winRate }}%</dd></div>
          <div><dt>Deleted</dt><dd>{{ selectedFighter.deleted ? 'Yes' : 'No' }}</dd></div>
        </dl>
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
    button.danger, .row-actions button.danger { background: #c7343f; border-color: rgba(255,122,132,.55); }
    .bulk-bar { align-items: center; background: rgba(32,217,100,.12); border-radius: 999px; display: flex; gap: .75rem; margin-bottom: .75rem; padding: .55rem 1rem; }
    .bulk-bar span { font-weight: 700; }
    .modal-overlay { align-items: center; background: rgba(0,0,0,.55); bottom: 0; display: flex; inset: 0; justify-content: center; position: fixed; z-index: 100; }
    .modal-content { background: #06120a; border: 1px solid rgba(255,255,255,.12); border-radius: 18px; max-height: 80vh; max-width: 720px; overflow-y: auto; padding: 1.5rem; width: 92vw; }
    .editor-heading { align-items: center; display: flex; justify-content: space-between; margin-bottom: .5rem; }
    form { display: grid; gap: .85rem; grid-template-columns: repeat(2, minmax(0, 1fr)); }
    label { color: #c8d3ed; display: grid; gap: .35rem; }
    input, textarea { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 12px; color: white; padding: .75rem; }
    textarea { min-height: 120px; resize: vertical; }
    .full { grid-column: 1 / -1; }
    .stat-grid { display: grid; gap: .85rem; grid-template-columns: repeat(3, minmax(0, 1fr)); }
    .table-wrap { overflow-x: auto; }
    table { border-collapse: collapse; min-width: 1220px; width: 100%; }
    th, td { border-bottom: 1px solid rgba(255,255,255,.1); padding: .8rem; text-align: left; }
    th { color: #7cff9f; font-size: .78rem; text-transform: uppercase; }
    th:first-child, td:first-child { width: 40px; }
    th:first-child input, td:first-child input { width: 16px; height: 16px; cursor: pointer; }
    td:nth-child(2) { font-family: ui-monospace, SFMono-Regular, Consolas, monospace; font-size: .78rem; max-width: 190px; overflow: hidden; text-overflow: ellipsis; }
    .deleted { opacity: .55; }
    .selected { background: rgba(32,217,100,.08); }
    .row-actions { display: flex; flex-wrap: wrap; gap: .5rem; }
    .info-panel { margin-top: 1rem; }
    dl { display: grid; gap: .75rem; grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); }
    dt { color: #7cff9f; font-size: .75rem; text-transform: uppercase; }
    dd { margin: .2rem 0 0; }
    .error { color: #ff8a8a; }
    @media (max-width: 760px) { form, .stat-grid { grid-template-columns: 1fr; } }
  `]
})
export class AdminFightersComponent {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  private readonly refresh$ = new BehaviorSubject<void>(undefined);
  readonly fighters$ = this.refresh$.pipe(switchMap(() => this.api.getAllFighters()));
  readonly selectedIds = new Set<string>();
  showForm = false;
  editingId: string | null = null;
  selectedFighter: Fighter | null = null;
  error = '';
  confirmDelete: { title: string; message: string; action: () => void } | null = null;

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    description: ['', Validators.required],
    region: ['', Validators.required],
    archetype: ['', Validators.required],
    title: ['', Validators.required],
    itLikes: ['', Validators.required],
    itDislike: ['', Validators.required],
    slug: ['', Validators.required],
    health: [0, Validators.required],
    range: [0, Validators.required],
    power: [0, Validators.required],
    vitality: [0, Validators.required],
    mobility: [0, Validators.required],
    easyOfUse: [0, Validators.required],
  });

  toggleSelect(id: string): void {
    if (this.selectedIds.has(id)) {
      this.selectedIds.delete(id);
    } else {
      this.selectedIds.add(id);
    }
  }

  allSelected(fighters: Fighter[]): boolean {
    return fighters.length > 0 && fighters.every((f) => this.selectedIds.has(f.id));
  }

  toggleAll(fighters: Fighter[]): void {
    if (this.allSelected(fighters)) {
      this.selectedIds.clear();
    } else {
      fighters.forEach((f) => this.selectedIds.add(f.id));
    }
  }

  clearSelection(): void {
    this.selectedIds.clear();
  }

  requestDeleteSelected(): void {
    const count = this.selectedIds.size;
    if (!count) {
      return;
    }
    this.confirmDelete = {
      title: `Delete ${count} selected fighter${count === 1 ? '' : 's'}?`,
      message: 'Selected fighters will be deactivated and hidden from normal use.',
      action: () => this.deleteSelected(),
    };
  }

  private deleteSelected(): void {
    const ids = [...this.selectedIds];
    if (!ids.length) {
      return;
    }
    this.selectedIds.clear();
    forkJoin(ids.map((id) => this.api.deactivateFighter(id))).subscribe({
      next: () => this.refresh$.next(),
      error: (err: { error?: { error?: string } }) => this.error = err.error?.error || 'Could not delete selected fighters',
    });
  }

  startCreate(): void {
    this.editingId = null;
    this.error = '';
    this.form.reset({ health: 0, range: 0, power: 0, vitality: 0, mobility: 0, easyOfUse: 0 });
    this.showForm = true;
  }

  startEdit(fighter: Fighter): void {
    this.editingId = fighter.id;
    this.error = '';
    this.form.setValue({
      name: fighter.name,
      description: fighter.description,
      region: fighter.region,
      archetype: fighter.archetype,
      title: fighter.title,
      itLikes: fighter.itLikes,
      itDislike: fighter.itDislike,
      slug: fighter.slug,
      health: fighter.health,
      range: fighter.range,
      power: fighter.power,
      vitality: fighter.vitality,
      mobility: fighter.mobility,
      easyOfUse: fighter.easyOfUse,
    });
    this.showForm = true;
  }

  cancelEdit(): void {
    this.showForm = false;
    this.editingId = null;
  }

  save(): void {
    this.error = '';
    const fighter = this.form.getRawValue() satisfies FighterWrite;
    if (this.editingId) {
      this.api.updateFighter(this.editingId, fighter).subscribe({
        next: () => this.afterSave(),
        error: (err: { error?: { error?: string } }) => this.error = err.error?.error || 'Could not save fighter',
      });
      return;
    }

    this.api.createFighter(fighter).subscribe({
      next: () => this.afterSave(),
      error: (err: { error?: { error?: string } }) => this.error = err.error?.error || 'Could not save fighter',
    });
  }

  private afterSave(): void {
    this.cancelEdit();
    this.refresh$.next();
  }

  requestDeactivate(id: string, name: string): void {
    this.confirmDelete = {
      title: `Delete fighter ${name}?`,
      message: 'This fighter will be deactivated and hidden from normal use.',
      action: () => this.deactivate(id),
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

  private deactivate(id: string): void {
    this.api.deactivateFighter(id).subscribe(() => this.refresh$.next());
  }

  restore(id: string): void {
    this.api.restoreFighter(id).subscribe(() => this.refresh$.next());
  }

  view(fighter: Fighter): void {
    this.selectedFighter = fighter;
  }
}
