import { AsyncPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { map, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { ComboNotationComponent } from '../shared/combo-notation.component';

@Component({
  selector: 'app-fighter-detail',
  imports: [AsyncPipe, ComboNotationComponent, RouterLink],
  template: `
    @if (fighter$ | async; as fighter) {
      <section class="detail-layout">
        <div class="info-side">
          <p class="eyebrow">{{ fighter.archetype }}</p>
          <h1>{{ fighter.name }}</h1>

          <nav class="submenu">
            <button [class.active]="tab() === 'info'" (click)="tab.set('info')">Info</button>
            <button [class.active]="tab() === 'combos'" (click)="tab.set('combos')">Official Combos</button>
          </nav>

          @if (tab() === 'info') {
            <div>
              <p>{{ fighter.description }}</p>
              <dl>
                <div><dt>Region</dt><dd>{{ fighter.region }}</dd></div>
                <div><dt>Likes</dt><dd>{{ fighter.itLikes }}</dd></div>
                <div><dt>Dislikes</dt><dd>{{ fighter.itDislike }}</dd></div>
                <div><dt>Winrate</dt><dd>{{ fighter.winRate }}%</dd></div>
              </dl>
            </div>
          } @else {
            <div class="combo-list">
              @if (combos$ | async; as combos) {
                @for (combo of combos; track combo.id) {
                  <article>
                    <h3>{{ combo.title }}</h3>
                    <app-combo-notation [notation]="combo.textNotation" />
                    <small>{{ combo.comboDificulty }} · {{ combo.damage }} damage · {{ combo.meterCost }} meter</small>
                  </article>
                } @empty {
                  <div class="empty-state">No official combos available yet.</div>
                }
              }
            </div>
          }
        </div>
        <img [src]="'/assets/fighters/' + fighter.slug + '/full.webp'" [alt]="fighter.name" (error)="setImageFallback($event, 'full')">
      </section>

      <section>
        <p class="eyebrow">Other fighters</p>
        <div class="mini-grid">
          @if (fighters$ | async; as fighters) {
            @for (f of fighters; track f.id) {
              @if (f.id !== fighter.id) {
                <a class="mini-card" [routerLink]="['/fighters', f.id]">
                  <img [src]="'/assets/fighters/' + f.slug + '/portrait.webp'" [alt]="f.name" (error)="setImageFallback($event, 'portrait')">
                  <span>{{ f.name }}</span>
                </a>
              }
            }
          }
        </div>
      </section>
    }
  `,
  styles: [`
    .detail-layout { align-items: center; display: grid; gap: 2rem; grid-template-columns: 1.1fr .9fr; margin-bottom: 1.5rem; }
    h1 { font-size: clamp(2rem, 6vw, 5rem); margin: .25rem 0 1rem; text-transform: uppercase; }
    img { max-height: 520px; max-width: 100%; object-fit: contain; }
    .submenu { border-bottom: 1px solid rgba(255,255,255,.12); display: flex; gap: 0; margin-bottom: 1rem; }
    .submenu button { background: none; border: 0; border-bottom: 2px solid transparent; color: #b8c3df; cursor: pointer; font: inherit; padding: .7rem 1.2rem; transition: color .15s, border-color .15s; }
    .submenu button.active { border-bottom-color: #ff4655; color: #fff; font-weight: 700; }
    .submenu button:hover { color: #fff; }
    dl { display: grid; gap: .75rem; grid-template-columns: repeat(2, minmax(0, 1fr)); margin-top: 1.5rem; }
    dt { color: #ffbd59; font-size: .75rem; text-transform: uppercase; }
    dd { margin: .2rem 0 0; }
    .combo-list { display: grid; gap: 1rem; margin-top: 1rem; }
    article { background: rgba(255,255,255,.06); border-radius: 16px; padding: 1rem; }
    .notation { color: #a8d2ff; font-family: ui-monospace, SFMono-Regular, Consolas, monospace; }
    .mini-grid { display: grid; gap: .85rem; grid-template-columns: repeat(auto-fill, minmax(100px, 1fr)); margin-top: .75rem; }
    .mini-card { aspect-ratio: 3 / 4; background: linear-gradient(160deg, rgba(255,70,85,.2), rgba(255,255,255,.05)); border: 1px solid rgba(255,255,255,.12); border-radius: 14px; color: white; display: grid; overflow: hidden; place-items: end center; position: relative; text-decoration: none; transition: transform .2s, border-color .2s; }
    .mini-card:hover { border-color: #ff4655; transform: translateY(-4px); }
    .mini-card img { height: 100%; inset: 0; object-fit: cover; opacity: .7; position: absolute; width: 100%; }
    .mini-card span { background: rgba(0,0,0,.6); font-size: .78rem; font-weight: 700; padding: .5rem; position: relative; text-align: center; width: 100%; }
    @media (max-width: 760px) { .detail-layout { grid-template-columns: 1fr; } }
  `]
})
export class FighterDetailComponent {
  private readonly api = inject(ApiService);
  private readonly fighterId$ = inject(ActivatedRoute).paramMap.pipe(map((params) => params.get('id')!));
  readonly fighter$ = this.fighterId$.pipe(switchMap((id) => this.api.getFighter(id)));
  readonly combos$ = this.fighterId$.pipe(switchMap((id) => this.api.getOfficialCombos(id)));
  readonly fighters$ = this.api.getFighterBanners();
  readonly tab = signal<'info' | 'combos'>('info');

  setImageFallback(event: Event, type: 'portrait' | 'banner' | 'full'): void {
    const image = event.target as HTMLImageElement;
    image.onerror = null;
    image.src = `/assets/placeholders/fighter-${type}.svg`;
  }
}
