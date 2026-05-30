import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { map } from 'rxjs';

import { ApiService } from '../core/api.service';
import { FighterAssetType, fighterAsset, fighterPlaceholder } from '../shared/asset-paths';

@Component({
  selector: 'app-fighters',
  imports: [AsyncPipe, RouterLink],
  template: `
    <p class="eyebrow">Character select</p>
    <h1>Fighters</h1>
    <div class="roster">
      @if (fighters$ | async; as fighters) {
        @for (fighter of fighters; track fighter.id) {
          <a class="roster-card" [routerLink]="['/fighters', fighter.id]">
            <img [src]="fighterAsset(fighter.slug, 'portrait')" [alt]="fighter.name" (error)="setImageFallback($event, fighter.slug, 'portrait')">
            <strong>{{ fighter.name }}</strong>
          </a>
        }
      }
    </div>
  `,
  styles: [`
    h1 { font-size: clamp(2rem, 6vw, 4.5rem); margin: .4rem 0 1.5rem; text-transform: uppercase; }
    .roster { column-gap: .65rem; display: grid; grid-template-columns: repeat(6, minmax(0, 1fr)); margin-left: 50%; max-width: 1800px; row-gap: 1.15rem; transform: translateX(-50%); width: min(96vw, 1800px); }
    .roster-card { aspect-ratio: 2 / 3; background: linear-gradient(155deg, rgba(32,217,100,.34), rgba(255,255,255,.08)); border: 1px solid rgba(255,255,255,.16); clip-path: polygon(13% 0, 100% 0, 87% 100%, 0 100%); color: white; display: grid; isolation: isolate; overflow: hidden; place-items: end center; position: relative; text-decoration: none; transition: transform .22s ease, border-color .22s ease; }
    .roster-card::after { background: linear-gradient(180deg, transparent 48%, rgba(0,0,0,.82)); content: ''; inset: 0; pointer-events: none; position: absolute; z-index: 1; }
    .roster-card:hover, .roster-card:focus-visible { border-color: #20d964; outline: none; transform: translateY(-5px); }
    img { filter: grayscale(1) brightness(.78); height: 100%; inset: 0; object-fit: cover; position: absolute; transform: scale(1.04); transition: filter .22s ease, transform .22s ease; width: 100%; }
    .roster-card:hover img, .roster-card:focus-visible img { filter: grayscale(0) brightness(1); transform: scale(1.08); }
    strong { bottom: 0; font-size: 1rem; font-weight: 900; left: 0; letter-spacing: .08em; padding: .85rem 1rem 1rem; position: absolute; right: 0; text-align: center; text-transform: uppercase; z-index: 2; }
    @media (max-width: 1280px) { .roster { width: min(98vw, 1280px); } }
    @media (max-width: 1100px) { .roster { grid-template-columns: repeat(4, minmax(0, 1fr)); } }
    @media (max-width: 720px) { .roster { column-gap: .55rem; grid-template-columns: repeat(2, minmax(0, 1fr)); row-gap: .75rem; } .roster-card { clip-path: polygon(9% 0, 100% 0, 91% 100%, 0 100%); } }
  `]
})
export class FightersComponent {
  readonly fighters$ = inject(ApiService).getFighterBanners().pipe(
    map((fighters) => [...fighters].sort((a, b) => a.name.localeCompare(b.name)))
  );
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
