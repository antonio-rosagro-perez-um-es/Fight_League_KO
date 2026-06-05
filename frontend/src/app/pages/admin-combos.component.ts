import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BehaviorSubject, map, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { Combo, ComboCreate } from '../core/api.models';
import { ComboNotationComponent } from '../shared/combo-notation.component';
import { comboNotationValidator } from '../shared/combo-notation';
import { ConfirmDialogComponent } from '../shared/confirm-dialog.component';

@Component({
  selector: 'app-admin-combos',
  imports: [AsyncPipe, ComboNotationComponent, ConfirmDialogComponent, ReactiveFormsModule],
  template: `
    <div class="admin-heading">
      <div>
        <p class="eyebrow">Admin</p>
        <h1>Combo Management</h1>
      </div>
      <button type="button" (click)="startCreate()">New Combo</button>
    </div>

    @if (showForm) {
      <section class="panel editor">
        <div class="editor-heading">
          <h2>{{ editingId ? 'Edit combo' : 'Create official combo' }}</h2>
          <button type="button" class="ghost" (click)="cancelEdit()">Close</button>
        </div>
        <form [formGroup]="form" (ngSubmit)="save()">
          <label>Title <input formControlName="title"></label>
          <label>Point fighter
            <select formControlName="pointFighter">
              <option value="">Select fighter</option>
              @if (fighters$ | async; as fighters) {
                @for (fighter of fighters; track fighter.id) {
                  <option [value]="fighter.id">{{ fighter.name }}</option>
                }
              }
            </select>
          </label>
          <label>Second fighter
            <select formControlName="secondFighter">
              <option value="">None</option>
              @if (fighters$ | async; as fighters) {
                @for (fighter of fighters; track fighter.id) {
                  <option [value]="fighter.id">{{ fighter.name }}</option>
                }
              }
            </select>
          </label>
          <label>Difficulty
            <select formControlName="comboDificulty">
              <option value="BEGINNER">Beginner</option>
              <option value="INTERMEDIATE">Intermediate</option>
              <option value="ADVANCED">Advanced</option>
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
          <label>
            <span class="label-with-help">
              Notation
              <button type="button" class="help-trigger" aria-label="Show combo notation help">?</button>
              <span class="notation-help" role="tooltip">
                <strong>Combo notation help</strong>
                <span>Use numpad directions: 7 8 9 / 4 5 6 / 1 2 3.</span>
                <span>Attacks: L, M, H, T, S1, S2.</span>
                <span>Examples: 5L &gt; 5M &gt; 2H &gt; j.S2, 5H(2), j.2HH.</span>
                <span>Modifiers: air, jump, jc, dash, microdash, walk, hold, delay, delayed, assist, cancel.</span>
                <span>Separators: &gt; = next, + = together, comma = pause, / = alternative.</span>
                <span>Not allowed: left/right/dl/dr, 236H, Super, free text.</span>
              </span>
            </span>
            <input formControlName="textNotation" placeholder="5L > 5M > 2H > j.S2">
          </label>
          @if (notationErrors.length) {
            <p class="field-error">{{ notationErrors[0] }}</p>
          }
          <label>Media URL <input formControlName="mediaUrl"></label>
          <label>Meter <input formControlName="metercost" type="number" min="0"></label>
          <label>Damage <input formControlName="damage" type="number" min="0"></label>
          <label class="full">Description <textarea formControlName="description"></textarea></label>
          @if (error) { <p class="error full">{{ error }}</p> }
          <button type="submit" [disabled]="form.invalid">Save</button>
        </form>
      </section>
    }

    <section class="panel table-wrap">
      @if (combos$ | async; as combos) {
        <table>
          <thead>
            <tr>
              <th>Title</th>
              <th>Fighters</th>
              <th>Difficulty</th>
              <th>Official</th>
              <th>Private</th>
              <th>Deleted</th>
              <th>Votes</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (combo of combos; track combo.id) {
              <tr [class.deleted]="combo.deleted">
                <td>
                  <strong>{{ combo.title }}</strong>
                  <app-combo-notation [notation]="combo.textNotation" />
                </td>
                <td>{{ combo.pointFighterName }} @if (combo.secondFighterName) { / {{ combo.secondFighterName }} }</td>
                <td>{{ combo.comboDificulty }}</td>
                <td>{{ combo.oficial ? 'Yes' : 'No' }}</td>
                <td>{{ combo.privateCombo ? 'Yes' : 'No' }}</td>
                <td>{{ combo.deleted ? 'Yes' : 'No' }}</td>
                <td>{{ combo.likeCounter }} / {{ combo.dislikeCounter }}</td>
                <td>
                  <div class="row-actions">
                    <button type="button" (click)="startEdit(combo)">Edit</button>
                    @if (combo.privateCombo) {
                      <button type="button" (click)="setPublic(combo.id)">Public</button>
                    } @else {
                      <button type="button" (click)="setPrivate(combo.id)">Private</button>
                    }
                    @if (combo.deleted) {
                      <button type="button" (click)="restore(combo.id)">Restore</button>
                    } @else {
                      <button type="button" class="danger" (click)="requestDelete(combo.id, combo.title)">Delete</button>
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
    .label-with-help { align-items: center; display: inline-flex; gap: .4rem; position: relative; width: fit-content; }
    .help-trigger { align-items: center; background: rgba(255,255,255,.12); border: 1px solid rgba(255,255,255,.24); border-radius: 999px; color: #7cff9f; display: inline-flex; font-size: .72rem; font-weight: 950; height: 1.25rem; justify-content: center; line-height: 1; padding: 0; width: 1.25rem; }
    .notation-help { background: #001f0b; border: 1px solid rgba(124,255,159,.55); border-radius: 12px; box-shadow: 0 18px 45px rgba(0,0,0,.4); color: #eef2ff; display: grid; font-size: .78rem; gap: .35rem; left: 100%; line-height: 1.35; margin-left: .55rem; max-width: min(340px, 78vw); opacity: 0; padding: .8rem; pointer-events: none; position: absolute; top: 50%; transform: translateY(-50%); transition: opacity .15s ease; width: max-content; z-index: 5; }
    .notation-help strong { color: #7cff9f; }
    .help-trigger:hover + .notation-help, .help-trigger:focus-visible + .notation-help { opacity: 1; }
    input, select, textarea { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 12px; color: white; padding: .75rem; }
    option { color: #001f0b; }
    textarea { min-height: 100px; resize: vertical; }
    .full { grid-column: 1 / -1; }
    .table-wrap { overflow-x: auto; }
    table { border-collapse: collapse; min-width: 1360px; width: 100%; }
    th, td { border-bottom: 1px solid rgba(255,255,255,.1); padding: .8rem; text-align: left; vertical-align: top; }
    th { color: #7cff9f; font-size: .78rem; text-transform: uppercase; }
    td:first-child { min-width: 260px; }
    td:first-child strong { display: block; margin-bottom: .5rem; }
    .deleted { opacity: .55; }
    .row-actions { display: flex; flex-wrap: wrap; gap: .5rem; }
    .error, .field-error { color: #ff8a8a; }
    .field-error { font-size: .85rem; margin: -.45rem 0 0; }
    @media (max-width: 760px) { .admin-heading { align-items: stretch; flex-direction: column; } form { grid-template-columns: 1fr; } .notation-help { left: 0; margin-left: 0; top: 100%; transform: translateY(.45rem); } }
  `]
})
export class AdminCombosComponent {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  private readonly refresh$ = new BehaviorSubject<void>(undefined);
  readonly combos$ = this.refresh$.pipe(switchMap(() => this.api.getAllCombosForAdmin().pipe(map((page) => page.content))));
  readonly fighters$ = this.api.getFighterBanners();
  showForm = false;
  editingId: string | null = null;
  error = '';
  confirmDelete: { title: string; message: string; action: () => void } | null = null;

  readonly form = this.fb.nonNullable.group({
    title: ['', Validators.required],
    pointFighter: ['', Validators.required],
    secondFighter: [''],
    textNotation: ['', [Validators.required, comboNotationValidator()]],
    comboDificulty: ['BEGINNER', Validators.required],
    fuse: ['DOUBLE_DOWN', Validators.required],
    mediaUrl: ['', Validators.required],
    description: ['', Validators.required],
    metercost: [0, Validators.required],
    damage: [0, Validators.required],
  });

  startCreate(): void {
    this.editingId = null;
    this.error = '';
    this.form.reset({ comboDificulty: 'BEGINNER', fuse: 'DOUBLE_DOWN', metercost: 0, damage: 0 });
    this.showForm = true;
  }

  startEdit(combo: Combo): void {
    this.editingId = combo.id;
    this.error = '';
    this.form.setValue({
      title: combo.title,
      pointFighter: combo.pointFighterId,
      secondFighter: combo.secondFighterId || '',
      textNotation: combo.textNotation,
      comboDificulty: combo.comboDificulty,
      fuse: combo.fuse,
      mediaUrl: combo.mediaUrl,
      description: combo.description,
      metercost: combo.meterCost,
      damage: combo.damage,
    });
    this.showForm = true;
  }

  cancelEdit(): void {
    this.showForm = false;
    this.editingId = null;
  }

  get notationErrors(): string[] {
    const errors = this.form.controls.textNotation.errors?.['comboNotation'];
    return this.form.controls.textNotation.touched && Array.isArray(errors) ? errors : [];
  }

  save(): void {
    this.error = '';
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      return;
    }

    const raw = this.form.getRawValue();
    const combo: ComboCreate = {
      title: raw.title,
      pointFighter: raw.pointFighter,
      secondFighter: raw.secondFighter || null,
      textNotation: raw.textNotation,
      comboDificulty: raw.comboDificulty,
      fuse: raw.fuse,
      mediaUrl: raw.mediaUrl,
      description: raw.description,
      metercost: Number(raw.metercost),
      damage: Number(raw.damage),
    };

    if (this.editingId) {
      this.api.updateCombo(this.editingId, combo).subscribe({
        next: () => this.afterChange(),
        error: (err: { error?: { error?: string } }) => this.error = err.error?.error || 'Could not update combo',
      });
      return;
    }

    this.api.createCombo(combo).subscribe({
      next: () => this.afterChange(),
      error: (err: { error?: { error?: string } }) => this.error = err.error?.error || 'Could not create combo',
    });
  }

  setPublic(id: string): void {
    this.api.setComboPublic(id).subscribe(() => this.refresh$.next());
  }

  setPrivate(id: string): void {
    this.api.setComboPrivate(id).subscribe(() => this.refresh$.next());
  }

  requestDelete(id: string, title: string): void {
    this.confirmDelete = {
      title: `Delete combo ${title}?`,
      message: 'This combo will be deactivated and removed from normal lists.',
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
    this.api.deleteCombo(id).subscribe(() => this.refresh$.next());
  }

  restore(id: string): void {
    this.api.restoreCombo(id).subscribe(() => this.refresh$.next());
  }

  private afterChange(): void {
    this.cancelEdit();
    this.refresh$.next();
  }
}
