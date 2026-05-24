import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ApiService } from '../core/api.service';

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
            <img [src]="'/assets/fighters/' + fighter.slug + '/banner.webp'" [alt]="fighter.name" (error)="setImageFallback($event, 'banner')">
            <strong>{{ fighter.name }}</strong>
          </a>
        }
      }
    </div>
  `,
  styles: [`
    h1 { font-size: clamp(2rem, 6vw, 4.5rem); margin: .4rem 0 1.5rem; text-transform: uppercase; }
    .roster { display: grid; gap: 1rem; grid-template-columns: repeat(auto-fit, minmax(170px, 1fr)); }
    .roster-card { background: #141a29; border: 1px solid rgba(255,255,255,.12); border-radius: 20px; color: white; min-height: 220px; overflow: hidden; position: relative; text-decoration: none; }
    img { height: 100%; object-fit: cover; opacity: .65; position: absolute; width: 100%; }
    strong { bottom: 0; font-size: 1.25rem; left: 0; padding: 1rem; position: absolute; right: 0; text-transform: uppercase; }
  `]
})
export class FightersComponent {
  readonly fighters$ = inject(ApiService).getFighterBanners();

  setImageFallback(event: Event, type: 'portrait' | 'banner' | 'full'): void {
    const image = event.target as HTMLImageElement;
    image.onerror = null;
    image.src = `/assets/placeholders/fighter-${type}.svg`;
  }
}
