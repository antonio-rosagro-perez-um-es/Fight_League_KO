import { AsyncPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { map, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { FighterAssetType, fighterAsset, fighterPlaceholder } from '../shared/asset-paths';
import { ComboNotationComponent } from '../shared/combo-notation.component';

@Component({
  selector: 'app-fighter-detail',
  imports: [AsyncPipe, ComboNotationComponent, RouterLink],
  template: `
    @if (fighter$ | async; as fighter) {
      <section class="detail-layout">
        <div class="info-side intro-panel">
          <p class="eyebrow">{{ fighter.archetype }}</p>
          <h1>{{ fighter.name }}</h1>
          @if (fighter.title) {
            <p class="fighter-title">{{ fighter.title }}</p>
          }
          <p class="description">{{ fighter.description }}</p>
          <div class="region-card">
            <span>Region</span>
            <strong>{{ fighter.region }}</strong>
          </div>
        </div>

        <div class="fighter-showcase" [style.--fighter-banner]="'url(' + fighterAsset(fighter.slug, 'banner') + ')'">
          <img class="fighter-hero-image" [src]="fighterAsset(fighter.slug, 'banner')" [alt]="fighter.name" (error)="setImageFallback($event, fighter.slug, 'banner')">
        </div>

        <div class="info-side detail-panel">
          <nav class="submenu">
            <button [class.active]="tab() === 'info'" (click)="tab.set('info')">Info</button>
            <button [class.active]="tab() === 'combos'" (click)="tab.set('combos')">Official Combos</button>
          </nav>

          @if (tab() === 'info') {
            <div class="stats-panel">
              <dl>
                <div><dt>Type</dt><dd>{{ fighter.archetype }}</dd></div>
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
      </section>

      <section>
        <p class="eyebrow">Other fighters</p>
        <div class="mini-grid">
          @if (fighters$ | async; as fighters) {
            @for (f of fighters; track f.id) {
              @if (f.id !== fighter.id) {
                <a class="mini-card" [routerLink]="['/fighters', f.id]">
                  <img class="mini-card-image" [src]="fighterAsset(f.slug, 'portrait')" [alt]="f.name" (error)="setImageFallback($event, f.slug, 'portrait')">
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
    .detail-layout { align-items: center; display: grid; gap: clamp(1rem, 3vw, 2.5rem); grid-template-columns: minmax(210px, .76fr) minmax(380px, 1.5fr) minmax(240px, .84fr); margin: 1.5rem 50% 2.5rem; max-width: 1560px; min-height: 760px; transform: translateX(-50%); width: min(98vw, 1560px); }
    .info-side { position: relative; z-index: 1; }
    .intro-panel { align-self: center; }
    h1 { font-size: clamp(2.5rem, 7vw, 6.5rem); line-height: .88; margin: .25rem 0 .75rem; text-transform: uppercase; }
    .fighter-title { color: #ffbd59; font-size: clamp(1rem, 2vw, 1.35rem); font-weight: 800; letter-spacing: .08em; margin: 0 0 1.25rem; text-transform: uppercase; }
    .description { color: #d7deef; font-size: 1rem; line-height: 1.65; margin: 0; }
    .region-card { background: rgba(255,255,255,.07); border-left: 3px solid #ff4655; border-radius: 0 14px 14px 0; margin-top: 1.5rem; padding: .9rem 1rem; }
    .region-card span { color: #8fa0c4; display: block; font-size: .72rem; font-weight: 800; text-transform: uppercase; }
    .region-card strong { display: block; font-size: 1.15rem; margin-top: .2rem; }
    .fighter-showcase { align-items: end; background: radial-gradient(circle at 50% 44%, rgba(255,70,85,.24), transparent 58%); display: flex; justify-content: center; min-height: 760px; overflow: hidden; pointer-events: none; position: relative; }
    .fighter-showcase::before { background: linear-gradient(180deg, rgba(255,255,255,.08), rgba(255,70,85,.08)); clip-path: polygon(20% 0, 100% 0, 80% 100%, 0 100%); content: ''; height: 88%; left: 50%; opacity: .55; position: absolute; top: 7%; transform: translateX(-50%); width: min(82%, 420px); }
    .fighter-showcase::after { background-image: var(--fighter-banner); background-position: center bottom; background-repeat: no-repeat; background-size: contain; bottom: 0; content: ''; height: 100%; left: 50%; max-width: 1060px; position: absolute; transform: translateX(-50%); width: 164%; z-index: 1; }
    .fighter-hero-image { height: 1px; opacity: 0; pointer-events: none; position: absolute; width: 1px; }
    .detail-panel { align-self: center; background: linear-gradient(145deg, rgba(12,18,34,.86), rgba(255,255,255,.045)); border: 1px solid rgba(255,255,255,.12); border-radius: 20px; box-shadow: 0 22px 55px rgba(0,0,0,.28); padding: 1.15rem; }
    .submenu { border-bottom: 1px solid rgba(255,255,255,.12); display: flex; gap: 0; margin-bottom: 1rem; }
    .submenu button { background: none; border: 0; border-bottom: 2px solid transparent; color: #b8c3df; cursor: pointer; font: inherit; padding: .7rem 1.2rem; transition: color .15s, border-color .15s; }
    .submenu button.active { border-bottom-color: #ff4655; color: #fff; font-weight: 700; }
    .submenu button:hover { color: #fff; }
    dl { display: grid; gap: .75rem; grid-template-columns: repeat(2, minmax(0, 1fr)); margin: 0; }
    dl div { background: rgba(255,255,255,.06); border-radius: 14px; padding: .85rem; }
    dt { color: #ffbd59; font-size: .75rem; text-transform: uppercase; }
    dd { margin: .2rem 0 0; }
    .combo-list { display: grid; gap: 1rem; margin-top: 1rem; }
    article { background: rgba(255,255,255,.06); border-radius: 16px; padding: 1rem; }
    .notation { color: #a8d2ff; font-family: ui-monospace, SFMono-Regular, Consolas, monospace; }
    .mini-grid { display: grid; gap: .85rem; grid-template-columns: repeat(auto-fill, minmax(100px, 1fr)); margin-top: .75rem; }
    .mini-card { aspect-ratio: 3 / 4; background: linear-gradient(160deg, rgba(255,70,85,.2), rgba(255,255,255,.05)); border: 1px solid rgba(255,255,255,.12); clip-path: polygon(13% 0, 100% 0, 87% 100%, 0 100%); color: white; display: grid; isolation: isolate; overflow: hidden; place-items: end center; position: relative; text-decoration: none; transition: transform .2s, border-color .2s; }
    .mini-card::after { background: linear-gradient(180deg, transparent 48%, rgba(0,0,0,.82)); content: ''; inset: 0; pointer-events: none; position: absolute; z-index: 1; }
    .mini-card:hover, .mini-card:focus-visible { border-color: #ff4655; outline: none; transform: translateY(-4px); }
    .mini-card-image { filter: grayscale(1) brightness(.78); height: 100%; inset: 0; object-fit: cover; opacity: .85; position: absolute; transform: scale(1.04); transition: filter .2s, transform .2s; width: 100%; }
    .mini-card:hover .mini-card-image, .mini-card:focus-visible .mini-card-image { filter: grayscale(0) brightness(1); transform: scale(1.08); }
    .mini-card span { font-size: .78rem; font-weight: 700; padding: .5rem; position: relative; text-align: center; width: 100%; z-index: 2; }
    @media (max-width: 1050px) { .detail-layout { grid-template-columns: 1fr; margin-top: 1rem; min-height: 0; } .intro-panel, .detail-panel { justify-self: center; max-width: 720px; width: 100%; } .fighter-showcase { min-height: 660px; order: 2; width: 100%; } .fighter-showcase::after { width: min(124vw, 900px); } .detail-panel { order: 3; } }
    @media (max-width: 620px) { .detail-layout { width: min(94vw, 1480px); } .fighter-showcase { min-height: 520px; } .fighter-showcase::after { width: min(142vw, 680px); } .submenu button { flex: 1; padding-inline: .6rem; } dl { grid-template-columns: 1fr; } .mini-card { clip-path: polygon(9% 0, 100% 0, 91% 100%, 0 100%); } }
  `]
})
export class FighterDetailComponent {
  private readonly api = inject(ApiService);
  private readonly fighterId$ = inject(ActivatedRoute).paramMap.pipe(map((params) => params.get('id')!));
  readonly fighter$ = this.fighterId$.pipe(switchMap((id) => this.api.getFighter(id)));
  readonly combos$ = this.fighterId$.pipe(switchMap((id) => this.api.getOfficialCombos(id)));
  readonly fighters$ = this.api.getFighterBanners();
  readonly tab = signal<'info' | 'combos'>('info');
  readonly fighterAsset = fighterAsset;

  setImageFallback(event: Event, slug: string, type: FighterAssetType): void {
    const image = event.target as HTMLImageElement;
    image.onerror = () => {
      image.onerror = null;
      image.src = fighterPlaceholder(type);
    };
    image.src = fighterAsset(slug, type === 'portrait' ? 'banner' : 'portrait');
  }
}
