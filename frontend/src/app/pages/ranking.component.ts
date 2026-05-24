import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ApiService } from '../core/api.service';

@Component({
  selector: 'app-ranking',
  imports: [AsyncPipe, RouterLink],
  template: `
    <p class="eyebrow">Leaderboard</p>
    <h1>Ranking</h1>
    <section class="panel ranking-panel">
      @if (ranking$ | async; as ranking) {
        @for (user of ranking; track user.id; let index = $index) {
          <a class="rank-row" [routerLink]="['/profile', user.id]" [class.podium]="index < 3">
            <span class="place">#{{ index + 1 }}</span>
            <span class="avatar material-symbols-outlined">account_circle</span>
            <strong>{{ user.username }}</strong>
            <span>{{ user.tournamentWins }} wins</span>
            <span>{{ user.score }} pts</span>
          </a>
        } @empty {
          <div class="empty-state">No ranking data yet.</div>
        }
      }
    </section>
  `,
  styles: [`
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .4rem 0 1.5rem; text-transform: uppercase; }
    .ranking-panel { display: grid; gap: .35rem; }
    .rank-row { align-items: center; border: 1px solid transparent; border-radius: 18px; color: white; display: grid; gap: 1rem; grid-template-columns: 70px auto 1fr auto auto; padding: 1rem; text-decoration: none; transition: background .18s, border-color .18s, transform .18s; }
    .rank-row:hover { background: rgba(255,255,255,.07); border-color: rgba(255,70,85,.55); transform: translateX(4px); }
    .rank-row.podium { background: linear-gradient(90deg, rgba(255,189,89,.16), rgba(255,255,255,.04)); }
    .place { color: #ffbd59; font-weight: 900; }
    .avatar { color: #ffbd59; font-size: 2rem; }
    .rank-row span:not(.place):not(.avatar) { color: #c8d3ed; }
    @media (max-width: 640px) { .rank-row { grid-template-columns: auto auto 1fr; } .rank-row span:last-child { grid-column: 3; } }
  `]
})
export class RankingComponent {
  readonly ranking$ = inject(ApiService).getUserRanking();
}
