import { AsyncPipe, DecimalPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { of } from 'rxjs';

import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { fighterAsset, fuseAsset } from '../shared/asset-paths';

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
    <section class="panel fighters-panel">
      <div class="section-heading">
        <div>
          <p class="eyebrow">Character performance</p>
          <h2>Top Fighters</h2>
        </div>
        <span>Winrate highlighted</span>
      </div>
        @if (fighters$ | async; as fighters) {
          <div class="fighter-grid">
            @for (fighter of fighters; track fighter.fighterId; let position = $index) {
              <article class="fighter-card">
                <img class="fighter-bg" [src]="fighterPortrait(fighter.fighterName)" [alt]="" aria-hidden="true" (error)="hideBrokenIcon($event)">
                <div class="fighter-copy">
                  <span class="rank">#{{ position + 1 }}</span>
                  <strong>{{ fighter.fighterName }}</strong>
                </div>
                <div class="winrate-badge">
                  <strong>{{ fighter.winRate | number:'1.0-1' }}%</strong>
                  <span>Win rate</span>
                </div>
                <div class="fighter-stats">
                  <span><strong>{{ fighter.pickCounter }}</strong> Times Played</span>
                  <span><strong>{{ fighter.winsCounter }}</strong> Wins</span>
                  <span><strong>{{ fighter.losesCounter }}</strong> Losses</span>
                </div>
              </article>
            }
          </div>
        }
    </section>

    <section class="panel teams-panel">
      <div class="section-heading">
        <div>
          <p class="eyebrow">Synergy picks</p>
          <h2>Top Teams</h2>
        </div>
        <span>Icons replace names</span>
      </div>
      @if (teams$ | async; as teams) {
        <div class="team-list">
          @for (team of teams; track team.idTeam) {
            <article class="team-card">
              <div class="team-icons" [attr.aria-label]="team.pointFighterName + ' and ' + team.secondFighterName">
                <img [src]="fighterIcon(team.pointFighterName)" [alt]="team.pointFighterName" (error)="hideBrokenIcon($event)">
                <img [src]="fighterIcon(team.secondFighterName)" [alt]="team.secondFighterName" (error)="hideBrokenIcon($event)">
                <img class="fuse-icon" [src]="fuseIcon(team.fuse)" [alt]="formatFuse(team.fuse)" (error)="hideBrokenIcon($event)">
              </div>
              <div>
                <strong>{{ team.winRate | number:'1.0-1' }}% WR</strong>
                <span>{{ team.pickCounter }} picks · {{ team.winsCounter }}W / {{ team.losesCounter }}L</span>
              </div>
            </article>
          }
        </div>
      }
    </section>
  `,
  styles: [`
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .4rem 0 1.5rem; text-transform: uppercase; }
    h2 { margin: .2rem 0; }
    :host { display: block; margin-inline: calc(50% - 48vw); width: 96vw; }
    .panel { margin-left: auto; margin-right: auto; max-width: 1520px; }
    .personal-panel { align-items: stretch; display: grid; gap: .85rem; grid-template-columns: minmax(180px, 1.2fr) repeat(6, minmax(112px, 1fr)); margin-bottom: 1rem; }
    .admin-note { margin-bottom: 1rem; }
    .admin-note p:last-child { color: #c8d3ed; margin-bottom: 0; }
    .personal-panel article { background: rgba(255,255,255,.06); border-radius: 16px; display: grid; gap: .2rem; min-width: 0; padding: .9rem; }
    .personal-panel strong { color: #7cff9f; font-size: clamp(1.35rem, 2vw, 1.8rem); overflow-wrap: anywhere; }
    .personal-panel span { color: #c8d3ed; font-size: .8rem; line-height: 1.2; }
    .fighters-panel { margin-bottom: 1rem; }
    .section-heading { align-items: end; display: flex; justify-content: space-between; margin-bottom: 1rem; }
    .section-heading span { color: #c8d3ed; font-size: .9rem; }
    .fighter-grid { display: grid; gap: 1rem; grid-template-columns: repeat(4, minmax(0, 1fr)); }
    .fighter-card { align-content: end; background: #06120a; border: 1px solid rgba(220,255,97,.26); border-radius: 22px; display: grid; gap: .9rem; min-height: 18rem; overflow: hidden; padding: 1rem; position: relative; }
    .fighter-card::before { background: linear-gradient(180deg, rgba(3,8,5,.18), rgba(3,8,5,.92)); content: ''; inset: 0; position: absolute; z-index: 1; }
    .fighter-bg { filter: blur(3px) saturate(1.1); height: 108%; inset: -4%; object-fit: cover; object-position: top center; opacity: .78; position: absolute; transform: scale(1.04); width: 108%; }
    .fighter-copy, .winrate-badge, .fighter-stats { position: relative; z-index: 2; }
    .fighter-copy { display: grid; gap: .25rem; left: 1rem; position: absolute; right: 1rem; top: 1rem; }
    .fighter-copy .rank { color: #dcff61; font-size: .82rem; font-weight: 950; letter-spacing: .08em; text-transform: uppercase; }
    .fighter-copy strong { font-size: 1.18rem; text-shadow: 0 2px 12px rgba(0,0,0,.65); text-transform: uppercase; }
    .team-card span { color: #c8d3ed; font-size: .86rem; }
    .winrate-badge { color: #dcff61; display: grid; gap: .15rem; text-shadow: 0 2px 16px rgba(0,0,0,.75); }
    .winrate-badge strong { font-size: clamp(2.1rem, 8vw, 3.2rem); line-height: .9; }
    .winrate-badge span { color: #f5ffd4; font-size: .78rem; font-weight: 900; letter-spacing: .08em; text-transform: uppercase; }
    .fighter-stats { color: #e8eefc; display: grid; gap: .32rem; font-size: .98rem; font-weight: 800; text-transform: uppercase; }
    .fighter-stats strong { color: #ffffff; font-size: 1.12rem; margin-right: .22rem; }
    .team-list { display: grid; gap: .75rem; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); }
    .team-card { align-items: center; background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.1); border-radius: 18px; display: flex; gap: 1rem; padding: .85rem; }
    .team-card > div:last-child { display: grid; gap: .2rem; }
    .team-card strong { color: #7cff9f; font-size: 1.15rem; }
    .team-icons { align-items: center; display: flex; flex-shrink: 0; }
    .team-icons img { background: rgba(255,255,255,.1); border: 2px solid #06120a; border-radius: 999px; height: 2.65rem; margin-right: -.55rem; object-fit: cover; width: 2.65rem; }
    .team-icons .fuse-icon { background: #ffffff; padding: .35rem; }
    @media (max-width: 1180px) { .personal-panel { grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); } .fighter-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
    @media (max-width: 640px) { :host { margin-inline: 0; width: 100%; } .section-heading { align-items: start; flex-direction: column; gap: .35rem; } .fighter-grid { grid-template-columns: 1fr; } }
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

  fighterPortrait(name: string): string {
    const slug = this.nameToSlug(name);
    return fighterAsset(slug, 'portrait');
  }

  fighterIcon(name: string): string {
    const slug = this.nameToSlug(name);
    return fighterAsset(slug, 'icon');
  }

  fuseIcon(fuse: string): string {
    return fuseAsset(fuse) || '';
  }

  formatFuse(fuse: string): string {
    return fuse.replace(/_/g, ' ');
  }

  hideBrokenIcon(event: Event): void {
    (event.target as HTMLImageElement).style.visibility = 'hidden';
  }

  private nameToSlug(name: string): string {
    return name.trim().toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
  }
}
