import { AsyncPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { map, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { OfficialCombo } from '../core/api.models';
import { FighterAssetType, fighterAsset, fighterPlaceholder, fuseAsset } from '../shared/asset-paths';
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
            @if (combos$ | async; as combos) {
              <button (click)="openOfficialCombos(combos)">Official Combos</button>
            }
          </nav>

          <div class="stats-panel">
            <dl>
              <div><dt>Likes</dt><dd>{{ fighter.itLikes }}</dd></div>
              <div><dt>Dislikes</dt><dd>{{ fighter.itDislike }}</dd></div>
            </dl>
            <div class="radar-card">
              <h3>Fighter stats</h3>
              <svg viewBox="0 0 240 240" role="img" [attr.aria-label]="radarLabel(fighter)">
                <polygon class="radar-grid" points="120,28 207,74 207,166 120,212 33,166 33,74" />
                <polygon class="radar-grid inner" points="120,64 176,92 176,148 120,176 64,148 64,92" />
                <polygon class="radar-shape" [attr.points]="radarPoints(fighter)" />
                @for (point of radarAxisPoints(); track point.label) {
                  <line class="radar-axis" x1="120" y1="120" [attr.x2]="point.x" [attr.y2]="point.y" />
                  <text [attr.x]="point.labelX" [attr.y]="point.labelY" text-anchor="middle">{{ point.label }}</text>
                }
              </svg>
            </div>
          </div>
        </div>
      </section>

      @if (combos$ | async; as combos) {
        @if (openedCombo(combos); as combo) {
          <div class="combo-modal-backdrop" (click)="closeCombo()">
            <section class="combo-modal" role="dialog" aria-modal="true" [attr.aria-label]="combo.title" (click)="$event.stopPropagation()">
              <div class="combo-modal-top">
                <div>
                  <p class="eyebrow">{{ fighter.name }} official combo</p>
                  <h2>{{ combo.title }}</h2>
                </div>
                <button type="button" class="close-modal" (click)="closeCombo()">Close</button>
                <div class="combo-stat-grid">
                  <div><span>Difficulty</span><strong>{{ combo.comboDificulty }}</strong></div>
                  <div><span>Damage</span><strong>{{ combo.damage }}</strong></div>
                  <div><span>Bars</span><strong>{{ combo.meterCost }}</strong></div>
                  <div>
                    <span>Fuse</span>
                    <strong class="fuse-badge">
                      @if (fuseAsset(combo.fuse); as fuseIcon) {
                        <img [src]="fuseIcon" [alt]="combo.fuse">
                      }
                      {{ combo.fuse }}
                    </strong>
                  </div>
                </div>
                <app-combo-notation [notation]="combo.textNotation" />
              </div>

              <div class="combo-modal-body">
                <aside class="official-combo-list" aria-label="Official combo list">
                  @for (item of combos; track item.id) {
                    <button type="button" class="official-combo-item" [class.active]="item.id === combo.id" (click)="selectedComboId.set(item.id)">
                      <span class="combo-item-title">{{ item.title }}</span>
                      <app-combo-notation [notation]="item.textNotation" />
                      <span class="combo-item-meta">{{ item.comboDificulty }} · {{ item.damage }} dmg · {{ item.meterCost }} bars</span>
                    </button>
                  }
                </aside>

                <article class="combo-media-panel">
                  <div class="combo-video-card">
                    @if (youtubeEmbedUrl(combo.mediaUrl); as videoUrl) {
                      <iframe [src]="videoUrl" title="Official combo video" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>
                    } @else {
                      <div class="media-fallback">
                        <span>No embeddable YouTube preview available.</span>
                        @if (combo.mediaUrl) {
                          <a [href]="combo.mediaUrl" target="_blank" rel="noopener">Open media</a>
                        }
                      </div>
                    }
                  </div>
                  <p class="combo-description">{{ combo.description }}</p>
                </article>
              </div>
            </section>
          </div>
        }
      }

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
    .fighter-title { color: #7cff9f; font-size: clamp(1rem, 2vw, 1.35rem); font-weight: 800; letter-spacing: .08em; margin: 0 0 1.25rem; text-transform: uppercase; }
    .description { color: #d7deef; font-size: 1rem; line-height: 1.65; margin: 0; }
    .region-card { background: rgba(255,255,255,.07); border-left: 3px solid #20d964; border-radius: 0 14px 14px 0; margin-top: 1.5rem; padding: .9rem 1rem; }
    .region-card span { color: #8fa0c4; display: block; font-size: .72rem; font-weight: 800; text-transform: uppercase; }
    .region-card strong { display: block; font-size: 1.15rem; margin-top: .2rem; }
    .fighter-showcase { align-items: end; background: radial-gradient(circle at 50% 44%, rgba(32,217,100,.24), transparent 58%); display: flex; justify-content: center; min-height: 760px; overflow: hidden; pointer-events: none; position: relative; }
    .fighter-showcase::before { background: linear-gradient(180deg, rgba(255,255,255,.08), rgba(32,217,100,.08)); clip-path: polygon(20% 0, 100% 0, 80% 100%, 0 100%); content: ''; height: 88%; left: 50%; opacity: .55; position: absolute; top: 7%; transform: translateX(-50%); width: min(82%, 420px); }
    .fighter-showcase::after { background-image: var(--fighter-banner); background-position: center bottom; background-repeat: no-repeat; background-size: contain; bottom: 0; content: ''; height: 100%; left: 50%; max-width: 1060px; position: absolute; transform: translateX(-50%); width: 164%; z-index: 1; }
    .fighter-hero-image { height: 1px; opacity: 0; pointer-events: none; position: absolute; width: 1px; }
    .detail-panel { align-self: center; background: rgba(12,18,34,.86); border: 1px solid rgba(255,255,255,.12); border-radius: 20px; padding: 1.15rem; }
    .submenu { border-bottom: 1px solid rgba(255,255,255,.12); display: flex; gap: 0; margin-bottom: 1rem; }
    .submenu button { background: none; border: 0; border-bottom: 2px solid transparent; color: #b8c3df; cursor: pointer; font: inherit; padding: .7rem 1.2rem; }
    .submenu button.active { border-bottom-color: #20d964; color: #fff; font-weight: 700; }
    .submenu button:hover { color: #fff; }
    dl { display: grid; gap: .75rem; grid-template-columns: repeat(2, minmax(0, 1fr)); margin: 0 0 1rem; }
    dl div { background: rgba(255,255,255,.06); border-radius: 14px; padding: .85rem; }
    dt { color: #7cff9f; font-size: .75rem; text-transform: uppercase; }
    dd { margin: .2rem 0 0; }
    .radar-card { background: rgba(255,255,255,.05); border: 1px solid rgba(255,255,255,.1); border-radius: 18px; padding: 1rem; }
    .radar-card h3 { color: #7cff9f; font-size: .82rem; letter-spacing: .08em; margin: 0 0 .5rem; text-transform: uppercase; }
    .radar-card svg { display: block; margin: 0 auto; max-width: 280px; width: 100%; }
    .radar-grid { fill: rgba(255,255,255,.035); stroke: rgba(255,255,255,.22); stroke-width: 1; }
    .radar-grid.inner { fill: transparent; stroke: rgba(255,255,255,.13); }
    .radar-axis { stroke: rgba(255,255,255,.12); stroke-width: 1; }
    .radar-shape { fill: rgba(32,217,100,.34); stroke: #20d964; stroke-linejoin: round; stroke-width: 3; }
    text { fill: #d7deef; font-size: 10px; font-weight: 800; text-transform: uppercase; }
    .official-combo-item { background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.1); border-radius: 16px; color: white; cursor: pointer; display: grid; gap: .55rem; padding: .8rem; text-align: left; }
    .combo-modal-backdrop { align-items: flex-start; background: rgba(0,0,0,.72); display: flex; inset: 0; justify-content: center; padding: 5.5rem 1rem 1rem; position: fixed; z-index: 9; }
    .combo-modal { background: #06120a; border: 1px solid rgba(255,255,255,.14); border-radius: 24px; max-height: calc(100vh - 7rem); overflow: hidden; width: min(98vw, 1440px); }
    .combo-modal-top { background: linear-gradient(135deg, rgba(32,217,100,.18), rgba(255,255,255,.045)); border-bottom: 1px solid rgba(255,255,255,.12); display: grid; gap: .9rem; padding: 1.1rem; position: relative; }
    .combo-modal-top h2 { font-size: clamp(1.5rem, 4vw, 3.2rem); line-height: .94; margin: .2rem 5rem 0 0; text-transform: uppercase; }
    .close-modal { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.18); border-radius: 999px; color: white; cursor: pointer; font: inherit; padding: .62rem .9rem; position: absolute; right: 1rem; top: 1rem; }
    .close-modal:hover, .close-modal:focus-visible { background: #20d964; outline: none; }
    .combo-modal-body { display: grid; gap: 1rem; grid-template-columns: minmax(420px,1fr) minmax(620px,760px); padding: 1rem; }
    .official-combo-list { display: grid; gap: .7rem; max-height: calc(100vh - 24rem); overflow-y: auto; padding-right: .2rem; }
    .official-combo-item:hover, .official-combo-item:focus-visible, .official-combo-item.active { background: rgba(32,217,100,.15); border-color: rgba(32,217,100,.65); outline: none; transform: translateX(2px); }
    .combo-item-title { font-size: .92rem; font-weight: 900; }
    .combo-item-meta { color: #7cff9f; font-size: .68rem; font-weight: 850; text-transform: uppercase; }
    .combo-stat-grid { display: grid; gap: .55rem; grid-template-columns: repeat(4, minmax(0, 1fr)); }
    .combo-stat-grid div { background: rgba(0,0,0,.22); border-radius: 14px; padding: .7rem; }
    .combo-stat-grid span { color: #8fa0c4; display: block; font-size: .64rem; font-weight: 900; text-transform: uppercase; }
    .combo-stat-grid strong { display: block; font-size: .88rem; overflow-wrap: anywhere; }
    .fuse-badge { align-items: center; background: #35f28f; border-radius: 999px; color: #07120d; display: inline-flex !important; gap: .45rem; padding: .24rem .6rem; width: fit-content; }
    .fuse-badge img { background: #f4fff8; border-radius: 999px; height: 28px; object-fit: contain; padding: .2rem; width: 28px; }
    .combo-video-card { aspect-ratio: 16 / 9; background: #050814; border: 1px solid rgba(255,255,255,.12); border-radius: 18px; overflow: hidden; }
    .combo-video-card iframe { border: 0; display: block; height: 100%; width: 100%; }
    .combo-media-panel { display: grid; gap: .9rem; grid-template-rows: auto minmax(0, 1fr); min-height: 0; overflow: hidden; }
    .media-fallback { align-items: center; color: #c8d3ed; display: grid; gap: .75rem; height: 100%; justify-items: center; padding: 1rem; text-align: center; }
    .media-fallback a { background: #20d964; border-radius: 999px; color: white; font-weight: 800; padding: .65rem .9rem; text-decoration: none; }
    .combo-description { background: rgba(0,0,0,.18); border-left: 3px solid #7cff9f; color: #d7deef; line-height: 1.6; margin: 0; padding: .9rem 1rem; }
    .mini-grid { display: grid; gap: .85rem; grid-template-columns: repeat(auto-fill, minmax(100px, 1fr)); margin-top: .75rem; }
    .mini-card { aspect-ratio: 3 / 4; background: linear-gradient(160deg, rgba(32,217,100,.2), rgba(255,255,255,.05)); border: 1px solid rgba(255,255,255,.12); clip-path: polygon(13% 0, 100% 0, 87% 100%, 0 100%); color: white; display: grid; isolation: isolate; overflow: hidden; place-items: end center; position: relative; text-decoration: none; transition: transform .2s, border-color .2s; }
    .mini-card::after { background: linear-gradient(180deg, transparent 48%, rgba(0,0,0,.82)); content: ''; inset: 0; pointer-events: none; position: absolute; z-index: 1; }
    .mini-card:hover, .mini-card:focus-visible { border-color: #20d964; outline: none; transform: translateY(-4px); }
    .mini-card-image { filter: grayscale(1) brightness(.78); height: 100%; inset: 0; object-fit: cover; opacity: .85; position: absolute; transform: scale(1.04); transition: filter .2s, transform .2s; width: 100%; }
    .mini-card:hover .mini-card-image, .mini-card:focus-visible .mini-card-image { filter: grayscale(0) brightness(1); transform: scale(1.08); }
    .mini-card span { font-size: .78rem; font-weight: 700; padding: .5rem; position: relative; text-align: center; width: 100%; z-index: 2; }
    @media (max-width: 1050px) { .detail-layout { grid-template-columns: 1fr; margin-top: 1rem; min-height: 0; } .intro-panel, .detail-panel { justify-self: center; max-width: 720px; width: 100%; } .fighter-showcase { min-height: 660px; order: 2; width: 100%; } .fighter-showcase::after { width: min(124vw, 900px); } .detail-panel { order: 3; } }
    @media (max-width: 760px) { .combo-modal { overflow-y: auto; } .combo-modal-body { grid-template-columns: 1fr; max-height: none; overflow: visible; } .official-combo-list { max-height: 280px; padding-right: 0; } .combo-stat-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
    @media (max-width: 620px) { .detail-layout { width: min(94vw, 1480px); } .fighter-showcase { min-height: 520px; } .fighter-showcase::after { width: min(142vw, 680px); } .submenu button { flex: 1; padding-inline: .6rem; } dl, .combo-stat-grid { grid-template-columns: 1fr; } .mini-card { clip-path: polygon(9% 0, 100% 0, 91% 100%, 0 100%); } }
  `]
})
export class FighterDetailComponent {
  private readonly api = inject(ApiService);
  private readonly sanitizer = inject(DomSanitizer);
  private readonly fighterId$ = inject(ActivatedRoute).paramMap.pipe(map((params) => params.get('id')!));
  readonly fighter$ = this.fighterId$.pipe(switchMap((id) => this.api.getFighter(id)));
  readonly combos$ = this.fighterId$.pipe(switchMap((id) => this.api.getOfficialCombos(id)));
  readonly fighters$ = this.api.getFighterBanners();
  readonly tab = signal<'info' | 'combos'>('info');
  readonly selectedComboId = signal<string | null>(null);
  readonly fighterAsset = fighterAsset;
  readonly fuseAsset = fuseAsset;

  selectedCombo(combos: OfficialCombo[]): OfficialCombo | null {
    return combos.find((combo) => combo.id === this.selectedComboId()) ?? combos[0] ?? null;
  }

  openedCombo(combos: OfficialCombo[]): OfficialCombo | null {
    return this.selectedComboId() ? this.selectedCombo(combos) : null;
  }

  openCombo(comboId: string): void {
    this.selectedComboId.set(comboId);
  }

  openOfficialCombos(combos: OfficialCombo[]): void {
    if (combos.length > 0) {
      this.selectedComboId.set(combos[0].id);
    }
  }

  closeCombo(): void {
    this.selectedComboId.set(null);
  }

  youtubeEmbedUrl(mediaUrl: string): SafeResourceUrl | null {
    const videoId = this.youtubeVideoId(mediaUrl);
    return videoId ? this.sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube.com/embed/${videoId}`) : null;
  }

  radarPoints(fighter: { health: number; range: number; power: number; vitality: number; mobility: number; easyOfUse: number }): string {
    return this.radarValues(fighter)
      .map((stat, index) => this.radarPoint(index, this.normalizeStat(stat.value)))
      .map((point) => `${point.x},${point.y}`)
      .join(' ');
  }

  radarAxisPoints(): Array<{ label: string; x: number; y: number; labelX: number; labelY: number }> {
    return this.radarValues({ health: 0, range: 0, power: 0, vitality: 0, mobility: 0, easyOfUse: 0 })
      .map((stat, index) => {
        const axis = this.radarPoint(index, 1);
        const label = this.radarPoint(index, 1.16);
        return { label: stat.label, x: axis.x, y: axis.y, labelX: label.x, labelY: label.y };
      });
  }

  radarLabel(fighter: { health: number; range: number; power: number; vitality: number; mobility: number; easyOfUse: number }): string {
    return this.radarValues(fighter).map((stat) => `${stat.label} ${stat.value}`).join(', ');
  }

  setImageFallback(event: Event, slug: string, type: FighterAssetType): void {
    const image = event.target as HTMLImageElement;
    image.onerror = () => {
      image.onerror = null;
      image.src = fighterPlaceholder(type);
    };
    image.src = fighterAsset(slug, type === 'portrait' ? 'banner' : 'portrait');
  }

  private radarValues(fighter: { health: number; range: number; power: number; vitality: number; mobility: number; easyOfUse: number }) {
    return [
      { label: 'Health', value: fighter.health },
      { label: 'Range', value: fighter.range },
      { label: 'Power', value: fighter.power },
      { label: 'Vitality', value: fighter.vitality },
      { label: 'Mobility', value: fighter.mobility },
      { label: 'Ease', value: fighter.easyOfUse },
    ];
  }

  private normalizeStat(value: number): number {
    return Math.max(0, Math.min(1, Number(value || 0) / 10));
  }

  private radarPoint(index: number, scale: number): { x: number; y: number } {
    const angle = -Math.PI / 2 + index * (Math.PI * 2 / 6);
    const radius = 92 * scale;
    return {
      x: Math.round((120 + Math.cos(angle) * radius) * 100) / 100,
      y: Math.round((120 + Math.sin(angle) * radius) * 100) / 100,
    };
  }

  private youtubeVideoId(mediaUrl: string): string | null {
    if (!mediaUrl) {
      return null;
    }

    try {
      const url = new URL(mediaUrl);
      const host = url.hostname.replace(/^www\./, '');
      if (host === 'youtu.be') {
        return this.cleanYoutubeId(url.pathname.slice(1));
      }
      if (host === 'youtube.com' || host === 'm.youtube.com') {
        if (url.pathname.startsWith('/embed/')) {
          return this.cleanYoutubeId(url.pathname.split('/')[2]);
        }
        return this.cleanYoutubeId(url.searchParams.get('v') || '');
      }
    } catch {
      return null;
    }

    return null;
  }

  private cleanYoutubeId(value: string): string | null {
    const id = value.split(/[?&#/]/)[0];
    return /^[\w-]{11}$/.test(id) ? id : null;
  }
}
