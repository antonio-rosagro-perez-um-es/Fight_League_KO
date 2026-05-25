import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, combineLatest, map, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { Combo, ComboCreate, ComboFilters } from '../core/api.models';
import { ComboNotationComponent } from '../shared/combo-notation.component';

@Component({
  selector: 'app-community-combos',
  imports: [AsyncPipe, ComboNotationComponent, ReactiveFormsModule, RouterLink],
  template: `
    @if (!auth.authenticated()) {
      <section class="empty-state">
        <h1>Community combos are for registered users</h1>
        <p>Create an account or log in to browse, publish, and vote community combos.</p>
        <a routerLink="/login">Log In</a>
        <a routerLink="/register">Register</a>
      </section>
    } @else {
      <p class="eyebrow">Lab notes</p>
      <h1>Community Combos</h1>

      <section class="panel filters">
        <div class="filter-group">
          <select (change)="setFilter('pointFighterId', $any($event.target).value)">
            <option value="">Point fighter: All</option>
            @for (f of fighters$ | async; track f.id) {
              <option [value]="f.id">{{ f.name }}</option>
            }
          </select>
          <select (change)="setFilter('secondFighterId', $any($event.target).value)">
            <option value="">Second fighter: All</option>
            @for (f of fighters$ | async; track f.id) {
              <option [value]="f.id">{{ f.name }}</option>
            }
          </select>
          <select (change)="setFilter('comboDificulty', $any($event.target).value)">
            <option value="">Difficulty: All</option>
            <option value="BEGINNER">Beginner</option>
            <option value="INTERMEDIATE">Intermediate</option>
            <option value="ADVANCED">Advanced</option>
          </select>
          <select (change)="setFilter('fuse', $any($event.target).value)">
            <option value="">Fuse: All</option>
            <option value="DOUBLE_DOWN">Double Down</option>
            <option value="FREESTYLE">Freestyle</option>
            <option value="TWO_X_ASSIST">2X Assist</option>
            <option value="JUGGERNAUT">Juggernaut</option>
            <option value="SIDEKICK">Sidekick</option>
          </select>
        </div>
        <div class="filter-buttons">
          <button type="button" [class.active]="sortMode === 'latest'" (click)="setSort('latest')">Latest</button>
          <button type="button" [class.active]="sortMode === 'popular'" (click)="setSort('popular')">Most liked</button>
          <button type="button" [class.active]="sortMode === 'default'" (click)="setSort('default')">Default</button>
          <button type="button" class="view-toggle" [class.active]="viewMode === 'my-combos'" (click)="toggleViewMode()">
            {{ viewMode === 'my-combos' ? 'Show public combos' : 'Show my private combos' }}
          </button>
        </div>
      </section>

      <section class="layout">
        <div class="panel">
          <div class="form-heading">
            <h2>{{ editingId ? 'Edit combo' : 'Publish a combo' }}</h2>
            @if (editingId) {
              <button type="button" class="ghost" (click)="cancelEdit()">Cancel</button>
            }
          </div>
          <form [formGroup]="form" (ngSubmit)="saveCombo()">
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
            <label>Notation <input formControlName="textNotation" placeholder="M > H > S1"></label>
            <label>Description <textarea formControlName="description"></textarea></label>
            <div class="two-col">
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
              <label>Meter <input formControlName="metercost" type="number" min="0"></label>
              <label>Damage <input formControlName="damage" type="number" min="0"></label>
            </div>
            <label>Media URL <input formControlName="mediaUrl" placeholder="https://..."></label>
            @if (error) { <p class="error">{{ error }}</p> }
            <button type="submit" [disabled]="form.invalid">{{ editingId ? 'Save combo' : 'Publish private combo' }}</button>
          </form>

          <div class="my-combos">
            <h3>My combos</h3>
            @if (myCombos$ | async; as myCombos) {
              @for (combo of myCombos; track combo.id) {
                <article>
                  <strong>{{ combo.title }}</strong>
                  <span>{{ combo.privateCombo ? 'Private' : 'Public' }}</span>
                  <div class="actions compact">
                    <button type="button" (click)="startEdit(combo)">Edit</button>
                    @if (combo.privateCombo) {
                      <button type="button" (click)="setPublic(combo.id)">Make public</button>
                    } @else {
                      <button type="button" (click)="setPrivate(combo.id)">Make private</button>
                    }
                    <button type="button" (click)="deleteCombo(combo.id)">Delete</button>
                  </div>
                </article>
              } @empty {
                <p>No personal combos yet.</p>
              }
            }
          </div>
        </div>

        <div class="combo-list">
          @if (displayedCombos$ | async; as combos) {
            @for (combo of combos; track combo.id) {
              <article class="panel combo-card">
                <div class="combo-header">
                  <div>
                    <p class="eyebrow">{{ combo.comboDificulty }} · {{ combo.fuse }}</p>
                    <h2>{{ combo.title }}</h2>
                    <span>{{ combo.pointFighterName }} @if (combo.secondFighterName) { / {{ combo.secondFighterName }} }</span>
                  </div>
                  <div class="damage">{{ combo.damage }} dmg</div>
                </div>
                <app-combo-notation [notation]="combo.textNotation" />
                <p>{{ combo.description }}</p>
                <div class="actions">
                  <button type="button" (click)="vote(combo.id, 'LIKE')">Like {{ combo.likeCounter }}</button>
                  <button type="button" (click)="vote(combo.id, 'DISLIKE')">Dislike {{ combo.dislikeCounter }}</button>
                  <button type="button" (click)="unvote(combo.id)">Remove vote</button>
                   @if (combo.creatorUserId === auth.user()?.id) {
                    <button type="button" (click)="startEdit(combo)">Edit</button>
                    @if (combo.privateCombo) {
                      <button type="button" (click)="setPublic(combo.id)">Make public</button>
                    } @else {
                      <button type="button" (click)="setPrivate(combo.id)">Make private</button>
                    }
                    <button type="button" (click)="deleteCombo(combo.id)">Delete</button>
                  }
                </div>
              </article>
            } @empty {
              <div class="empty-state">No public community combos yet.</div>
            }
          }
        </div>
      </section>
    }
  `,
  styles: [`
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .4rem 0 1.5rem; text-transform: uppercase; }
    .filters { display: flex; flex-direction: column; gap: .7rem; margin-bottom: 1rem; }
    .filter-group { display: flex; flex-wrap: wrap; gap: .7rem; }
    .filter-group select { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 999px; color: white; cursor: pointer; padding: .55rem .85rem; }
    .filter-group option { color: #0b1020; }
    .filter-buttons { display: flex; flex-wrap: wrap; gap: .5rem; }
    .view-toggle.active { background: rgba(255,255,255,.22); color: #fff; }
    .layout { align-items: start; display: grid; gap: 1rem; grid-template-columns: 380px 1fr; }
    form { display: grid; gap: .85rem; }
    .form-heading { align-items: center; display: flex; gap: 1rem; justify-content: space-between; }
    label { color: #c8d3ed; display: grid; gap: .35rem; }
    input, select, textarea { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 12px; color: white; padding: .75rem; }
    option { color: #0b1020; }
    textarea { min-height: 92px; resize: vertical; }
    button { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.14); border-radius: 999px; color: white; cursor: pointer; padding: .7rem .95rem; }
    button.active, button[type='submit'] { background: #ff4655; }
    button:disabled { cursor: not-allowed; opacity: .55; }
    .two-col { display: grid; gap: .7rem; grid-template-columns: repeat(2, minmax(0, 1fr)); }
    .combo-list { display: grid; gap: 1rem; }
    .combo-card { display: grid; gap: 1rem; }
    .combo-header { align-items: start; display: flex; gap: 1rem; justify-content: space-between; }
    .combo-header h2 { margin: .2rem 0; }
    .damage { background: rgba(255,189,89,.16); border-radius: 16px; color: #ffbd59; font-weight: 900; padding: .8rem; white-space: nowrap; }
    .actions { display: flex; flex-wrap: wrap; gap: .6rem; }
    .compact { gap: .4rem; margin-top: .5rem; }
    .compact button { padding: .45rem .65rem; }
    .my-combos { border-top: 1px solid rgba(255,255,255,.12); display: grid; gap: .75rem; margin-top: 1rem; padding-top: 1rem; }
    .my-combos article { background: rgba(255,255,255,.05); border-radius: 14px; padding: .75rem; }
    .my-combos strong, .my-combos span { display: block; }
    .my-combos span { color: #c8d3ed; font-size: .85rem; margin-top: .2rem; }
    .error { color: #ff8a8a; }
    .empty-state a { color: #ffbd59; display: inline-block; margin: .75rem .4rem 0; }
    @media (max-width: 920px) { .layout { grid-template-columns: 1fr; } }
  `]
})
export class CommunityCombosComponent {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  readonly auth = inject(AuthService);
  readonly fighters$ = this.api.getFighterBanners();
  readonly refresh$ = new BehaviorSubject<void>(undefined);
  readonly myRefresh$ = new BehaviorSubject<void>(undefined);
  readonly filters$ = new BehaviorSubject<ComboFilters>({ latest: true });
  readonly viewMode$ = new BehaviorSubject<'public' | 'my-combos'>('public');
  viewMode: 'public' | 'my-combos' = 'public';

  sortMode: 'latest' | 'popular' | 'default' = 'latest';
  editingId: string | null = null;
  error = '';

  readonly form = this.fb.group({
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

  readonly combos$ = combineLatest([this.filters$, this.refresh$]).pipe(
    switchMap(([filters]) => this.api.searchCommunityCombos(filters))
  );

  readonly myCombos$ = this.myRefresh$.pipe(
    switchMap(() => this.api.getMyCombos())
  );

  readonly displayedCombos$ = combineLatest([this.combos$, this.myCombos$, this.viewMode$]).pipe(
    map(([publicCombos, myCombos, mode]) => mode === 'my-combos'
      ? myCombos.filter((combo) => combo.privateCombo)
      : publicCombos)
  );

  setFilter(key: keyof ComboFilters, value: string): void {
    const current = this.filters$.value;
    this.filters$.next({ ...current, [key]: value || null });
  }

  setSort(sort: 'latest' | 'popular' | 'default'): void {
    this.sortMode = sort;
    const current = this.filters$.value;
    this.filters$.next({
      ...current,
      latest: sort === 'latest' ? true : null,
      mostLiked: sort === 'popular' ? true : null,
    });
  }

  toggleViewMode(): void {
    this.viewMode = this.viewMode === 'public' ? 'my-combos' : 'public';
    this.viewMode$.next(this.viewMode);
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
  }

  cancelEdit(): void {
    this.editingId = null;
    this.form.reset({ comboDificulty: 'BEGINNER', fuse: 'DOUBLE_DOWN', metercost: 0, damage: 0 });
  }

  saveCombo(): void {
    this.error = '';
    const raw = this.form.getRawValue();
    const combo: ComboCreate = {
      title: raw.title || '',
      pointFighter: raw.pointFighter || '',
      secondFighter: raw.secondFighter || null,
      textNotation: raw.textNotation || '',
      comboDificulty: raw.comboDificulty || 'BEGINNER',
      fuse: raw.fuse || 'DOUBLE_DOWN',
      mediaUrl: raw.mediaUrl || '',
      description: raw.description || '',
      metercost: Number(raw.metercost || 0),
      damage: Number(raw.damage || 0),
    };

    if (this.editingId) {
      this.api.updateCombo(this.editingId, combo).subscribe({
        next: () => this.afterSave(),
        error: (err) => this.error = err.error?.error || 'Could not update combo',
      });
      return;
    }

    this.api.createCombo(combo).subscribe({
      next: () => this.afterSave(),
      error: (err) => this.error = err.error?.error || 'Could not create combo',
    });
  }

  private afterSave(): void {
    this.editingId = null;
    this.form.reset({ comboDificulty: 'BEGINNER', fuse: 'DOUBLE_DOWN', metercost: 0, damage: 0 });
    this.refresh$.next();
    this.myRefresh$.next();
  }

  deleteCombo(comboId: string): void {
    this.api.deleteCombo(comboId).subscribe({
      next: () => {
        this.refresh$.next();
        this.myRefresh$.next();
      },
      error: (err) => this.error = err.error?.error || 'Could not delete combo',
    });
  }

  vote(comboId: string, voteType: 'LIKE' | 'DISLIKE'): void {
    this.api.voteCombo(comboId, voteType).subscribe(() => {
      this.refresh$.next();
      this.myRefresh$.next();
    });
  }

  unvote(comboId: string): void {
    this.api.unvoteCombo(comboId).subscribe(() => {
      this.refresh$.next();
      this.myRefresh$.next();
    });
  }

  setPublic(comboId: string): void {
    this.api.setComboPublic(comboId).subscribe(() => {
      this.refresh$.next();
      this.myRefresh$.next();
    });
  }

  setPrivate(comboId: string): void {
    this.api.setComboPrivate(comboId).subscribe(() => {
      this.refresh$.next();
      this.myRefresh$.next();
    });
  }
}
