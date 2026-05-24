import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BehaviorSubject, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { Combo, ComboCreate } from '../core/api.models';
import { ComboNotationComponent } from '../shared/combo-notation.component';

@Component({
  selector: 'app-admin-combos',
  imports: [AsyncPipe, ComboNotationComponent, ReactiveFormsModule],
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
          <label>Notation <input formControlName="textNotation"></label>
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
                      <button type="button" (click)="delete(combo.id)">Delete</button>
                    }
                  </div>
                </td>
              </tr>
            }
          </tbody>
        </table>
      }
    </section>
  `,
  styles: [`
    .admin-heading { align-items: center; display: flex; justify-content: space-between; margin-bottom: 1rem; }
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .3rem 0; text-transform: uppercase; }
    button { background: #ff4655; border: 0; border-radius: 999px; color: white; cursor: pointer; padding: .65rem .9rem; }
    button:disabled { cursor: not-allowed; opacity: .45; }
    .ghost, .row-actions button { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.14); }
    .editor { margin-bottom: 1rem; }
    .editor-heading { align-items: center; display: flex; justify-content: space-between; }
    form { display: grid; gap: .85rem; grid-template-columns: repeat(3, minmax(0, 1fr)); }
    label { color: #c8d3ed; display: grid; gap: .35rem; }
    input, select, textarea { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 12px; color: white; padding: .75rem; }
    option { color: #0b1020; }
    textarea { min-height: 100px; resize: vertical; }
    .full { grid-column: 1 / -1; }
    .table-wrap { overflow-x: auto; }
    table { border-collapse: collapse; min-width: 1100px; width: 100%; }
    th, td { border-bottom: 1px solid rgba(255,255,255,.1); padding: .8rem; text-align: left; vertical-align: top; }
    th { color: #ffbd59; font-size: .78rem; text-transform: uppercase; }
    td:first-child { min-width: 260px; }
    td:first-child strong { display: block; margin-bottom: .5rem; }
    .deleted { opacity: .55; }
    .row-actions { display: flex; flex-wrap: wrap; gap: .5rem; }
    .error { color: #ff8a8a; }
    @media (max-width: 760px) { .admin-heading { align-items: stretch; flex-direction: column; } form { grid-template-columns: 1fr; } }
  `]
})
export class AdminCombosComponent {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  private readonly refresh$ = new BehaviorSubject<void>(undefined);
  readonly combos$ = this.refresh$.pipe(switchMap(() => this.api.getAllCombosForAdmin()));
  readonly fighters$ = this.api.getFighterBanners();
  showForm = false;
  editingId: string | null = null;
  error = '';

  readonly form = this.fb.nonNullable.group({
    title: ['', Validators.required],
    pointFighter: ['', Validators.required],
    secondFighter: [''],
    textNotation: ['', Validators.required],
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

  save(): void {
    this.error = '';
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

  delete(id: string): void {
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
