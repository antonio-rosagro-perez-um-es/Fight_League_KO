import { AsyncPipe, DecimalPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { of } from 'rxjs';

import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-statistics',
  imports: [AsyncPipe, DecimalPipe],
  template: `
    <p class="eyebrow">Performance</p>
    <h1>Statistics</h1>
    @if (auth.role() === 'ADMIN') {
      <section class="panel admin-note">
        <p class="eyebrow">Admin dashboard</p>
        <h2>Statistics are read-only</h2>
        <p>Current backend endpoints expose rankings and calculated stats, not editable stat entities. Use this dashboard to inspect global performance until dedicated stats CRUD endpoints exist.</p>
      </section>
    }
    @if (profile$ | async; as profile) {
      <section class="panel personal-panel">
        <div>
          <p class="eyebrow">Personal stats</p>
          <h2>{{ profile.username }}</h2>
        </div>
        <article><strong>{{ profile.gamesPlayed }}</strong><span>Games played</span></article>
        <article><strong>{{ profile.gamesWon }}</strong><span>Wins</span></article>
        <article><strong>{{ profile.gamesLost }}</strong><span>Losses</span></article>
        <article><strong>{{ personalWinRate(profile.gamesWon, profile.gamesPlayed) | number:'1.0-1' }}%</strong><span>Win rate</span></article>
        <article><strong>{{ profile.tournamentWins }}</strong><span>Tournament wins</span></article>
        <article><strong>{{ profile.score }}</strong><span>Ranking points</span></article>
      </section>
    }
    <div class="stats-layout">
      <section class="panel">
        <h2>Top Fighters</h2>
        @if (fighters$ | async; as fighters) {
          @for (fighter of fighters; track fighter.fighterId) {
            <article class="row">
              <strong>{{ fighter.fighterName }}</strong>
              <span>{{ fighter.winRate | number:'1.0-1' }}% WR</span>
              <span>{{ fighter.pickCounter }} picks</span>
            </article>
          }
        }
      </section>
      <section class="panel">
        <h2>Top Teams</h2>
        @if (teams$ | async; as teams) {
          @for (team of teams; track team.idTeam) {
            <article class="row">
              <strong>{{ team.pointFighterName }} / {{ team.secondFighterName }}</strong>
              <span>{{ team.winRate | number:'1.0-1' }}% WR</span>
              <span>{{ team.fuse }}</span>
            </article>
          }
        }
      </section>
    </div>
  `,
  styles: [`
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .4rem 0 1.5rem; text-transform: uppercase; }
    h2 { margin: .2rem 0; }
    .personal-panel { align-items: stretch; display: grid; gap: 1rem; grid-template-columns: minmax(180px, 1.3fr) repeat(auto-fit, minmax(130px, 1fr)); margin-bottom: 1rem; }
    .admin-note { margin-bottom: 1rem; }
    .admin-note p:last-child { color: #c8d3ed; margin-bottom: 0; }
    .personal-panel article { background: rgba(255,255,255,.06); border-radius: 16px; display: grid; gap: .2rem; padding: 1rem; }
    .personal-panel strong { color: #7cff9f; font-size: 1.8rem; }
    .personal-panel span { color: #c8d3ed; font-size: .85rem; }
    .stats-layout { display: grid; gap: 1rem; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); }
    .row { align-items: center; border-bottom: 1px solid rgba(255,255,255,.1); display: grid; gap: .75rem; grid-template-columns: 1fr auto auto; padding: .9rem 0; }
    .row:last-child { border-bottom: 0; }
  `]
})
export class StatisticsComponent {
  private readonly api = inject(ApiService);
  readonly auth = inject(AuthService);
  readonly fighters$ = this.api.getFighterRanking();
  readonly teams$ = this.api.getTeamRanking();
  readonly profile$ = this.auth.authenticated() ? this.api.getCurrentUserProfile() : of(null);

  personalWinRate(wins: number, played: number): number {
    return played > 0 ? (wins / played) * 100 : 0;
  }
}
