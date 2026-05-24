import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';

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
      <section>
        <p class="eyebrow">Fighters</p>
        <div class="fighter-grid">
          @if (fighters$ | async; as fighters) {
            @for (fighter of fighters; track fighter.id) {
              <a class="fighter-card" [routerLink]="['/fighters', fighter.id]">
                <img [src]="'/assets/fighters/' + fighter.slug + '/portrait.webp'" [alt]="fighter.name" (error)="setImageFallback($event, 'portrait')">
                <span>{{ fighter.name }}</span>
              </a>
            }
          }
        </div>
      </section>
    }
  `,
  styles: [`
    .hero { margin-bottom: 1.5rem; padding: 3rem; }
    h1 { font-size: clamp(2.2rem, 7vw, 5rem); line-height: 0.95; margin: 0.5rem 0 1rem; max-width: 900px; text-transform: uppercase; }
    .cta { background: #ff4655; border-radius: 999px; color: white; display: inline-block; margin-top: 1rem; padding: 0.85rem 1.1rem; text-decoration: none; }
    .admin-cards { display: grid; gap: 1rem; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); }
    .admin-card { align-items: center; background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.12); border-radius: 18px; color: white; display: flex; flex-direction: column; gap: .5rem; padding: 1.5rem 1rem; text-decoration: none; transition: background .2s, border-color .2s; }
    .admin-card:hover { background: rgba(255,70,85,.14); border-color: #ff4655; }
    .admin-card span:first-child { font-size: 2.4rem; }
    .admin-card span:last-child { font-weight: 700; }
    .fighter-grid { display: grid; gap: 1rem; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); margin-top: 1rem; }
    .fighter-card { aspect-ratio: 3 / 4; background: linear-gradient(160deg, rgba(255,70,85,.28), rgba(255,255,255,.07)); border: 1px solid rgba(255,255,255,.14); border-radius: 18px; color: white; display: grid; overflow: hidden; place-items: end center; position: relative; text-decoration: none; transition: transform .2s ease, border-color .2s ease; }
    .fighter-card:hover { border-color: #ff4655; transform: translateY(-6px); }
    .fighter-card img { height: 100%; inset: 0; object-fit: cover; opacity: .78; position: absolute; width: 100%; }
    .fighter-card span { background: rgba(0,0,0,.62); font-weight: 800; padding: .75rem; position: relative; text-align: center; width: 100%; }
    .match-list { display: grid; gap: .75rem; margin-top: .75rem; }
    .match { align-items: center; border-radius: 18px; display: grid; gap: .6rem; grid-template-columns: auto 1fr auto 1fr auto; padding: .85rem 1rem; }
    .match small { grid-column: 1 / -1; opacity: .75; }
    .match-avatar { font-size: 1.6rem; }
    .vs-badge { background: rgba(255,255,255,.09); border-radius: 999px; font-size: .75rem; font-weight: 800; padding: .25rem .55rem; text-transform: uppercase; }
    .win { background: rgba(96, 255, 162, .16); }
    .loss { background: rgba(255, 96, 96, .16); }
  `]
})
export class HomeComponent {
  readonly api = inject(ApiService);
  readonly auth = inject(AuthService);
  readonly fighters$ = this.api.getFighterBanners();
  readonly recentGames$ = this.api.getRecentGames();

  setImageFallback(event: Event, type: 'portrait' | 'banner' | 'full'): void {
    const image = event.target as HTMLImageElement;
    image.onerror = null;
    image.src = `/assets/placeholders/fighter-${type}.svg`;
  }
}
