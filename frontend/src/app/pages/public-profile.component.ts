import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { map, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';

@Component({
  selector: 'app-public-profile',
  imports: [AsyncPipe, RouterLink],
  template: `
    @if (profile$ | async; as profile) {
      <a class="back-link" routerLink="/ranking">Back to ranking</a>
      <section class="profile-hero panel">
        <div class="avatar"><span class="material-symbols-outlined">account_circle</span></div>
        <div>
          <p class="eyebrow">{{ profile.role }}</p>
          <h1>{{ profile.username }}</h1>
          <p>{{ profile.email }}</p>
        </div>
        <div class="score-card">
          <strong>{{ profile.score }}</strong>
          <span>ranking points</span>
        </div>
      </section>

      <section class="stats-grid">
        <article class="panel"><strong>{{ profile.tournamentWins }}</strong><span>Tournament wins</span></article>
        <article class="panel"><strong>{{ profile.gamesPlayed }}</strong><span>Games played</span></article>
        <article class="panel"><strong>{{ profile.gamesWon }}</strong><span>Wins</span></article>
        <article class="panel"><strong>{{ profile.gamesLost }}</strong><span>Losses</span></article>
      </section>
    }
  `,
  styles: [`
    .back-link { color: #7cff9f; display: inline-block; margin-bottom: 1rem; text-decoration: none; }
    .profile-hero { align-items: center; display: grid; gap: 1.5rem; grid-template-columns: auto 1fr auto; margin-bottom: 1rem; }
    .avatar span { color: #7cff9f; font-size: 5rem; }
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .2rem 0; text-transform: uppercase; }
    .score-card { background: rgba(32,217,100,.16); border-radius: 20px; padding: 1rem; text-align: center; }
    .score-card strong, .stats-grid strong { display: block; font-size: 2.2rem; }
    .stats-grid { display: grid; gap: 1rem; grid-template-columns: repeat(auto-fit, minmax(170px, 1fr)); }
    .stats-grid article { text-align: center; }
    @media (max-width: 700px) { .profile-hero { grid-template-columns: 1fr; } }
  `]
})
export class PublicProfileComponent {
  private readonly api = inject(ApiService);
  private readonly userId$ = inject(ActivatedRoute).paramMap.pipe(map((params) => params.get('id')!));
  readonly profile$ = this.userId$.pipe(switchMap((id) => this.api.getUserProfile(id)));
}
