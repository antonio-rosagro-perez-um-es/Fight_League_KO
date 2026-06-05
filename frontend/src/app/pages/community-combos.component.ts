import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, combineLatest, map, shareReplay, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { Combo, ComboCreate, ComboFilters, FighterBanner } from '../core/api.models';
import { fighterAsset, fuseAsset } from '../shared/asset-paths';
import { ComboNotationComponent } from '../shared/combo-notation.component';
import { comboNotationValidator } from '../shared/combo-notation';
import { ConfirmDialogComponent } from '../shared/confirm-dialog.component';
import { AssetSelectOption, SearchableAssetSelectComponent } from '../shared/searchable-asset-select.component';

@Component({
  selector: 'app-community-combos',
  imports: [AsyncPipe, ComboNotationComponent, ConfirmDialogComponent, ReactiveFormsModule, RouterLink, SearchableAssetSelectComponent],
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
      <div class="page-heading">
        <h1>Community Combos</h1>
        <div class="page-actions">
          <button type="button" (click)="startCreate()">Create combo</button>
          <button type="button" class="view-toggle" [class.active]="viewMode === 'my-combos'" (click)="toggleViewMode()">
            {{ viewMode === 'my-combos' ? 'Community combos' : 'My combos' }}
          </button>
        </div>
      </div>

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
        </div>
      </section>

      <section class="layout">
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
                  @if (viewMode === 'public') {
                    <button type="button" (click)="vote(combo.id, 'LIKE')">Like {{ combo.likeCounter }}</button>
                    <button type="button" (click)="vote(combo.id, 'DISLIKE')">Dislike {{ combo.dislikeCounter }}</button>
                    <button type="button" (click)="unvote(combo.id)">Remove vote</button>
                  }
                  @if (viewMode === 'my-combos' || combo.creatorUserId === auth.user()?.id) {
                    <button type="button" (click)="startEdit(combo)">Edit</button>
                    @if (combo.privateCombo) {
                      <button type="button" (click)="setPublic(combo.id)">Make public</button>
                    } @else {
                      <button type="button" (click)="setPrivate(combo.id)">Make private</button>
                    }
                    <button type="button" class="danger" (click)="requestDeleteCombo(combo.id, combo.title)">Delete</button>
                  }
                </div>
              </article>
            } @empty {
              <div class="empty-state">{{ viewMode === 'my-combos' ? 'No personal combos yet.' : 'No public community combos yet.' }}</div>
            }
          }
        </div>
        @if (viewMode === 'public') {
          @if (combosPage$ | async; as page) {
            <div class="pagination">
              <button type="button" [disabled]="page.first" (click)="changePublicPage(page.number - 1)">Previous</button>
              <span>Page {{ page.number + 1 }} of {{ page.totalPages || 1 }} · {{ page.totalElements }} combos</span>
              <button type="button" [disabled]="page.last" (click)="changePublicPage(page.number + 1)">Next</button>
            </div>
          }
        }
      </section>

      @if (showFormModal) {
        <div class="modal-overlay" (click)="closeFormModal()">
          <div class="modal-content" (click)="$event.stopPropagation()">
            <div class="form-heading">
              <h2>{{ editingId ? 'Edit combo' : 'Publish a combo' }}</h2>
              <button type="button" class="ghost" (click)="closeFormModal()">Close</button>
            </div>
            <form [formGroup]="form" (ngSubmit)="saveCombo()">
            <label>Title <input formControlName="title"></label>
            <label>Point fighter
              @if (fighters$ | async; as fighters) {
                <app-searchable-asset-select
                  [options]="fighterOptions(fighters)"
                  [value]="form.controls.pointFighter.value || ''"
                  placeholder="Select fighter"
                  searchPlaceholder="Search fighters"
                  (valueChange)="form.controls.pointFighter.setValue($event); form.controls.pointFighter.markAsTouched()"
                />
              }
            </label>
            <label>Second fighter
              @if (fighters$ | async; as fighters) {
                <app-searchable-asset-select
                  [options]="fighterOptions(fighters)"
                  [value]="form.controls.secondFighter.value || ''"
                  placeholder="None"
                  searchPlaceholder="Search fighters"
                  [allowEmpty]="true"
                  emptyLabel="No second fighter"
                  (valueChange)="form.controls.secondFighter.setValue($event); form.controls.secondFighter.markAsTouched()"
                />
              }
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
                <app-searchable-asset-select
                  [options]="fuseOptions"
                  [value]="form.controls.fuse.value || ''"
                  placeholder="Select fuse"
                  searchPlaceholder="Search fuses"
                  (valueChange)="form.controls.fuse.setValue($event); form.controls.fuse.markAsTouched()"
                />
              </label>
              <label>Meter <input formControlName="metercost" type="number" min="0"></label>
              <label>Damage <input formControlName="damage" type="number" min="0"></label>
            </div>
            <label>Media URL <input formControlName="mediaUrl" placeholder="https://..."></label>
            @if (error) { <p class="error">{{ error }}</p> }
            <button type="submit" [disabled]="form.invalid">{{ editingId ? 'Save combo' : 'Publish private combo' }}</button>
          </form>
          </div>
        </div>
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
    }
  `,
  styles: [`
    .page-heading { align-items: center; display: flex; gap: 1rem; justify-content: space-between; margin-bottom: 1.5rem; }
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .4rem 0 0; text-transform: uppercase; }
    .page-actions { display: flex; flex-wrap: wrap; gap: .6rem; justify-content: flex-end; }
    .filters { display: flex; flex-direction: column; gap: .7rem; margin-bottom: 1rem; }
    .filter-group { display: flex; flex-wrap: wrap; gap: .7rem; }
    .filter-group select { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 999px; color: white; cursor: pointer; padding: .55rem .85rem; }
    .filter-group option { color: #001f0b; }
    .filter-buttons { display: flex; flex-wrap: wrap; gap: .5rem; }
    .view-toggle.active { background: rgba(255,255,255,.22); color: #fff; }
    .layout { display: grid; gap: 1rem; }
    form { display: grid; gap: .85rem; }
    .form-heading { align-items: center; display: flex; gap: 1rem; justify-content: space-between; }
    label { color: #c8d3ed; display: grid; gap: .35rem; }
    .label-with-help { align-items: center; display: inline-flex; gap: .4rem; position: relative; width: fit-content; }
    .help-trigger { align-items: center; background: rgba(255,255,255,.12); border: 1px solid rgba(255,255,255,.24); border-radius: 999px; color: #7cff9f; display: inline-flex; font-size: .72rem; font-weight: 950; height: 1.25rem; justify-content: center; line-height: 1; padding: 0; width: 1.25rem; }
    .notation-help { background: #001f0b; border: 1px solid rgba(124,255,159,.55); border-radius: 12px; box-shadow: 0 18px 45px rgba(0,0,0,.4); color: #eef2ff; display: grid; font-size: .78rem; gap: .35rem; left: 100%; line-height: 1.35; margin-left: .55rem; max-width: min(340px, 78vw); opacity: 0; padding: .8rem; pointer-events: none; position: absolute; top: 50%; transform: translateY(-50%); transition: opacity .15s ease; width: max-content; z-index: 5; }
    .notation-help strong { color: #7cff9f; }
    .help-trigger:hover + .notation-help, .help-trigger:focus-visible + .notation-help { opacity: 1; }
    input, select, textarea { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 12px; color: white; padding: .75rem; }
    option { color: #001f0b; }
    textarea { min-height: 92px; resize: vertical; }
    button { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.14); border-radius: 999px; color: white; cursor: pointer; padding: .7rem .95rem; }
    button.active, button[type='submit'] { background: #20d964; }
    button.danger { background: #c7343f; border-color: rgba(255,122,132,.55); color: white; }
    button:disabled { cursor: not-allowed; opacity: .55; }
    .two-col { display: grid; gap: .7rem; grid-template-columns: repeat(2, minmax(0, 1fr)); }
    .combo-list { display: grid; gap: 1rem; }
    .combo-card { background: linear-gradient(135deg, rgba(0,0,0,.64), rgba(0,29,12,.82)), url('/assets/Backgrounds/Background_Green.webp') center/cover; display: grid; gap: 1rem; }
    .combo-header { align-items: start; display: flex; gap: 1rem; justify-content: space-between; }
    .combo-header h2 { margin: .2rem 0; }
    .damage { background: rgba(124,255,159,.16); border-radius: 16px; color: #7cff9f; font-weight: 900; padding: .8rem; white-space: nowrap; }
    .actions { display: flex; flex-wrap: wrap; gap: .6rem; }
    .pagination { align-items: center; display: flex; flex-wrap: wrap; gap: .75rem; justify-content: center; }
    .pagination span { color: #cfe9d6; font-weight: 700; }
    .error, .field-error { color: #ff8a8a; }
    .field-error { font-size: .85rem; margin: -.45rem 0 0; }
    .empty-state a { color: #7cff9f; display: inline-block; margin: .75rem .4rem 0; }
    .modal-overlay { align-items: center; background: rgba(0,0,0,.62); display: flex; inset: 0; justify-content: center; position: fixed; z-index: 100; }
    .modal-content { background: #06120a; border: 1px solid rgba(255,255,255,.12); border-radius: 20px; max-height: 86vh; max-width: 760px; overflow-y: auto; padding: 1.35rem; width: 92vw; }
    @media (max-width: 920px) { .page-heading { align-items: stretch; flex-direction: column; } .page-actions { justify-content: flex-start; } .notation-help { left: 0; margin-left: 0; top: 100%; transform: translateY(.45rem); } }
  `]
})
export class CommunityCombosComponent {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  readonly auth = inject(AuthService);
  readonly fighters$ = this.api.getFighterBanners();
  readonly fuseOptions = ['DOUBLE_DOWN', 'FREESTYLE', 'TWO_X_ASSIST', 'JUGGERNAUT', 'SIDEKICK'].map((fuse) => ({
    value: fuse,
    label: this.formatFuse(fuse),
    icon: fuseAsset(fuse),
  }));
  readonly refresh$ = new BehaviorSubject<void>(undefined);
  readonly myRefresh$ = new BehaviorSubject<void>(undefined);
  readonly filters$ = new BehaviorSubject<ComboFilters>({ latest: true });
  readonly publicPage$ = new BehaviorSubject<number>(0);
  readonly viewMode$ = new BehaviorSubject<'public' | 'my-combos'>('public');
  viewMode: 'public' | 'my-combos' = 'public';

  sortMode: 'latest' | 'popular' | 'default' = 'latest';
  editingId: string | null = null;
  error = '';
  showFormModal = false;
  confirmDelete: { title: string; message: string; action: () => void } | null = null;

  readonly form = this.fb.group({
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

  readonly combosPage$ = combineLatest([this.filters$, this.refresh$, this.publicPage$]).pipe(
    switchMap(([filters, , page]) => this.api.searchCommunityCombos(filters, page)),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  readonly combos$ = this.combosPage$.pipe(
    map((page) => page.content)
  );

  readonly myCombos$ = this.myRefresh$.pipe(
    switchMap(() => this.api.getMyCombos())
  );

  readonly displayedCombos$ = combineLatest([this.combos$, this.myCombos$, this.viewMode$]).pipe(
    map(([publicCombos, myCombos, mode]) => mode === 'my-combos'
      ? myCombos
      : publicCombos)
  );

  get notationErrors(): string[] {
    const errors = this.form.controls.textNotation.errors?.['comboNotation'];
    return this.form.controls.textNotation.touched && Array.isArray(errors) ? errors : [];
  }

  setFilter(key: keyof ComboFilters, value: string): void {
    const current = this.filters$.value;
    this.publicPage$.next(0);
    this.filters$.next({ ...current, [key]: value || null });
  }

  setSort(sort: 'latest' | 'popular' | 'default'): void {
    this.sortMode = sort;
    const current = this.filters$.value;
    this.publicPage$.next(0);
    this.filters$.next({
      ...current,
      latest: sort === 'latest' ? true : null,
      mostLiked: sort === 'popular' ? true : null,
    });
  }

  changePublicPage(page: number): void {
    this.publicPage$.next(Math.max(0, page));
  }

  toggleViewMode(): void {
    this.viewMode = this.viewMode === 'public' ? 'my-combos' : 'public';
    this.viewMode$.next(this.viewMode);
  }

  startCreate(): void {
    this.editingId = null;
    this.error = '';
    this.form.reset({ comboDificulty: 'BEGINNER', fuse: 'DOUBLE_DOWN', metercost: 0, damage: 0 });
    this.showFormModal = true;
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
    this.showFormModal = true;
  }

  cancelEdit(): void {
    this.editingId = null;
    this.form.reset({ comboDificulty: 'BEGINNER', fuse: 'DOUBLE_DOWN', metercost: 0, damage: 0 });
  }

  closeFormModal(): void {
    this.showFormModal = false;
    this.cancelEdit();
    this.error = '';
  }

  saveCombo(): void {
    this.error = '';
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      return;
    }

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
    this.showFormModal = false;
    this.form.reset({ comboDificulty: 'BEGINNER', fuse: 'DOUBLE_DOWN', metercost: 0, damage: 0 });
    this.refresh$.next();
    this.myRefresh$.next();
  }

  requestDeleteCombo(comboId: string, title: string): void {
    this.confirmDelete = {
      title: `Delete combo ${title}?`,
      message: 'This combo will be removed from your normal combo lists.',
      action: () => this.deleteCombo(comboId),
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

  private deleteCombo(comboId: string): void {
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
}
