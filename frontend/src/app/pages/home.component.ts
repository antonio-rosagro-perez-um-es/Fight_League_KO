import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { map } from 'rxjs';

import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { FighterAssetType, fighterAsset, fighterPlaceholder } from '../shared/asset-paths';

@Component({
  selector: 'app-home',
  imports: [AsyncPipe, RouterLink],
  template: `
    <section class="hero panel">
      <div>
        <p class="eyebrow">2XKO community hub</p>
        <h1>Combos, fighters and community tournaments in one league.</h1>
        <p>Browse official fighter data, track rankings and join events built from the Spring Boot API.</p>
        <a class="cta" routerLink="/tournaments">Find a tournament</a>
      </div>
    </section>

    @if (auth.role() === 'ADMIN') {
      <section class="admin-cards">
        <a class="admin-card" routerLink="/combos">
          <span class="material-symbols-outlined">library_books</span>
          <span>Combos</span>
        </a>
        <a class="admin-card" routerLink="/users">
          <span class="material-symbols-outlined">people</span>
          <span>Users</span>
        </a>
        <a class="admin-card" routerLink="/games">
          <span class="material-symbols-outlined">sports_esports</span>
          <span>Games</span>
        </a>
        <a class="admin-card" routerLink="/teams">
          <span class="material-symbols-outlined">groups</span>
          <span>Teams</span>
        </a>
        <a class="admin-card" routerLink="/tournaments">
          <span class="material-symbols-outlined">emoji_events</span>
          <span>Tournaments</span>
        </a>
        <a class="admin-card" routerLink="/fighters">
          <span class="material-symbols-outlined">sports_martial_arts</span>
          <span>Fighters</span>
        </a>
      </section>
    } @else if (auth.authenticated()) {
      <section class="panel">
        <p class="eyebrow">Recent matches</p>
        @if (recentGames$ | async; as games) {
          @if (games.length) {
            <div class="match-list">
              @for (game of games; track game.id) {
                <article class="match" [class.win]="game.wonByCurrentUser" [class.loss]="!game.wonByCurrentUser && game.winnerId">
                  <span class="match-avatar material-symbols-outlined">account_circle</span>
                  <strong>{{ game.user1Username }}</strong>
                  <span class="vs-badge">vs</span>
                  <strong>{{ game.user2Username }}</strong>
                  <span class="match-avatar material-symbols-outlined">account_circle</span>
                  <small>{{ game.tournamentTitle || 'Friendly game' }} · {{ game.gameDate }}</small>
                </article>
              }
            </div>
          } @else {
            <div class="empty-state">
              <p>No matches yet. Time to join a tournament.</p>
              <a class="cta" routerLink="/tournaments">Find a tournament</a>
            </div>
          }
        }
      </section>
    } @else {
      <section class="fighter-showcase">
        <p class="eyebrow">Fighters</p>
        <div class="fighter-grid">
          @if (fighters$ | async; as fighters) {
            @for (fighter of fighters; track fighter.id) {
              <a class="fighter-card" [routerLink]="['/fighters', fighter.id]">
                <img [src]="fighterAsset(fighter.slug, 'portrait')" [alt]="fighter.name" (error)="setImageFallback($event, fighter.slug, 'portrait')">
                <span>{{ fighter.name }}</span>
              </a>
            }
          }
        </div>
      </section>
    }
  `,
  styles: [`
    .hero { align-items: center; background: radial-gradient(circle at 88% 20%, rgba(32,217,100,.28), transparent 32%), linear-gradient(135deg, rgba(0,0,0,.42), rgba(0,28,12,.78)), url('/assets/Backgrounds/Background_Green.webp') center/cover; border: 1px solid rgba(124,255,159,.22); display: flex; gap: 2rem; justify-content: space-between; margin-bottom: .9rem; margin-left: 50%; max-width: 1800px; padding: 1.45rem 2rem; transform: translateX(-50%); width: min(96vw, 1800px); }
    .hero > div { width: 100%; }
    .hero p:not(.eyebrow) { max-width: 900px; }
    h1 { font-size: clamp(2rem, 5vw, 3.4rem); line-height: 0.95; margin: 0.35rem 0 .65rem; max-width: none; text-transform: uppercase; }
    .cta { background: #20d964; border-radius: 999px; color: white; display: inline-block; margin-top: 1rem; padding: 0.85rem 1.1rem; text-decoration: none; }
    .admin-cards { display: grid; gap: 1rem; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); }
    .admin-card { align-items: center; background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.12); border-radius: 18px; color: white; display: flex; flex-direction: column; gap: .5rem; padding: 1.5rem 1rem; text-decoration: none; transition: background .2s, border-color .2s; }
    .admin-card:hover { background: rgba(32,217,100,.14); border-color: #20d964; }
    .admin-card span:first-child { font-size: 2.4rem; }
    .admin-card span:last-child { font-weight: 700; }
    .fighter-showcase { margin-left: 50%; max-width: 1800px; transform: translateX(-50%); width: min(96vw, 1800px); }
    .fighter-showcase > .eyebrow { margin-left: 0; }
    .fighter-grid { column-gap: .65rem; display: grid; grid-template-columns: repeat(6, minmax(0, 1fr)); margin-top: .65rem; row-gap: 1.15rem; width: 100%; }
    .fighter-card { aspect-ratio: 2 / 3; background: linear-gradient(155deg, rgba(32,217,100,.34), rgba(255,255,255,.08)); border: 1px solid rgba(255,255,255,.16); clip-path: polygon(13% 0, 100% 0, 87% 100%, 0 100%); color: white; display: grid; isolation: isolate; overflow: hidden; place-items: end center; position: relative; text-decoration: none; transition: transform .22s ease, border-color .22s ease; }
    .fighter-card::after { background: linear-gradient(180deg, transparent 48%, rgba(0,0,0,.82)); content: ''; inset: 0; pointer-events: none; position: absolute; z-index: 1; }
    .fighter-card:hover, .fighter-card:focus-visible { border-color: #20d964; outline: none; transform: translateY(-5px); }
    .fighter-card img { filter: grayscale(1) brightness(.78); height: 100%; inset: 0; object-fit: cover; position: absolute; transform: scale(1.04); transition: filter .22s ease, transform .22s ease; width: 100%; }
    .fighter-card:hover img, .fighter-card:focus-visible img { filter: grayscale(0) brightness(1); transform: scale(1.08); }
    .fighter-card span { font-size: 1rem; font-weight: 900; letter-spacing: .08em; padding: .85rem 1rem 1rem; position: relative; text-align: center; text-transform: uppercase; width: 100%; z-index: 2; }
    .match-list { display: grid; gap: .75rem; margin-top: .75rem; }
    .match { align-items: center; border-radius: 18px; display: grid; gap: .6rem; grid-template-columns: auto 1fr auto 1fr auto; padding: .85rem 1rem; }
    .match small { grid-column: 1 / -1; opacity: .75; }
    .match-avatar { font-size: 1.6rem; }
    .vs-badge { background: rgba(255,255,255,.09); border-radius: 999px; font-size: .75rem; font-weight: 800; padding: .25rem .55rem; text-transform: uppercase; }
    .win { background: rgba(96, 255, 162, .16); }
    .loss { background: rgba(255, 96, 96, .16); }
    @media (max-width: 1280px) { .hero, .fighter-showcase { width: min(98vw, 1280px); } }
    @media (max-width: 1100px) { .fighter-grid { grid-template-columns: repeat(4, minmax(0, 1fr)); } }
    @media (max-width: 720px) { .hero { padding: 1.15rem 1.25rem; } .fighter-grid { column-gap: .55rem; grid-template-columns: repeat(2, minmax(0, 1fr)); row-gap: .75rem; } .fighter-card { clip-path: polygon(9% 0, 100% 0, 91% 100%, 0 100%); } }
  `]
})
export class HomeComponent {
  readonly api = inject(ApiService);
  readonly auth = inject(AuthService);
  readonly fighters$ = this.api.getFighterBanners().pipe(
    map((fighters) => [...fighters].sort((a, b) => a.name.localeCompare(b.name)))
  );
  readonly recentGames$ = this.api.getRecentGames();
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
