import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { UserProfile } from '../core/api.models';

@Component({
  selector: 'app-profile',
  imports: [AsyncPipe, ReactiveFormsModule, RouterLink],
  template: `
    @if (!auth.authenticated()) {
      <section class="empty-state">
        <h1>Profile locked</h1>
        <p>Log in to see your profile and match history.</p>
        <a routerLink="/login">Log In</a>
      </section>
    } @else {
      @if (profile$ | async; as profile) {
        <section class="profile panel">
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
          <button type="button" (click)="startEdit(profile)">Edit profile</button>
        </section>

        @if (showForm) {
          <section class="panel editor">
            <div class="editor-heading">
              <h2>Edit profile</h2>
              <button type="button" class="ghost" (click)="cancelEdit()">Cancel</button>
            </div>
            <form [formGroup]="form" (ngSubmit)="saveProfile()">
              <label>Username <input formControlName="username"></label>
              <label>Email <input formControlName="email" type="email"></label>
              @if (error) { <p class="error">{{ error }}</p> }
              <button type="submit" [disabled]="form.invalid">Save</button>
            </form>
          </section>
        }

        <section class="stats-grid">
          <article class="panel"><strong>{{ profile.tournamentWins }}</strong><span>Tournament wins</span></article>
          <article class="panel"><strong>{{ profile.gamesPlayed }}</strong><span>Games played</span></article>
          <article class="panel"><strong>{{ profile.gamesWon }}</strong><span>Wins</span></article>
          <article class="panel"><strong>{{ profile.gamesLost }}</strong><span>Losses</span></article>
        </section>

        <section class="panel">
          <p class="eyebrow">Recent matches</p>
          @if (recentGames$ | async; as games) {
            @for (game of games; track game.id) {
              <article class="match" [class.win]="game.wonByCurrentUser" [class.loss]="!game.wonByCurrentUser && game.winnerId">
                <strong>{{ game.user1Username }}</strong>
                <span>vs</span>
                <strong>{{ game.user2Username }}</strong>
                <small>{{ game.tournamentTitle || 'Friendly game' }} · {{ game.gameDate }}</small>
              </article>
            } @empty {
              <div class="empty-state">No matches played yet.</div>
            }
          }
        </section>
      }
    }
  `,
  styles: [`
    .profile { align-items: center; display: grid; gap: 1.5rem; grid-template-columns: auto 1fr auto auto; margin-bottom: 1rem; }
    .avatar span { color: #7cff9f; font-size: 5rem; }
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .2rem 0; }
    button { background: #20d964; border: 0; border-radius: 999px; color: white; cursor: pointer; padding: .65rem .9rem; }
    button:disabled { cursor: not-allowed; opacity: .45; }
    .ghost { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.14); }
    .score-card { background: rgba(32,217,100,.16); border-radius: 20px; padding: 1rem; text-align: center; }
    .score-card strong, .stats-grid strong { display: block; font-size: 2.2rem; }
    .stats-grid { display: grid; gap: 1rem; grid-template-columns: repeat(auto-fit, minmax(170px, 1fr)); margin-bottom: 1rem; }
    .stats-grid article { text-align: center; }
    .editor { margin-bottom: 1rem; }
    .editor-heading { align-items: center; display: flex; gap: 1rem; justify-content: space-between; }
    form { display: grid; gap: .85rem; grid-template-columns: minmax(0, 1fr) minmax(0, 1fr); }
    label { color: #c8d3ed; display: grid; gap: .35rem; }
    input { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 12px; color: white; padding: .75rem; }
    .match { align-items: center; border-radius: 16px; display: grid; gap: .5rem; grid-template-columns: 1fr auto 1fr; margin-top: .75rem; padding: 1rem; }
    .match small { grid-column: 1 / -1; opacity: .75; }
    .win { background: rgba(96, 255, 162, .16); }
    .loss { background: rgba(255, 96, 96, .16); }
    .error { color: #ff8a8a; }
    @media (max-width: 700px) { .profile { grid-template-columns: 1fr; } form { grid-template-columns: 1fr; } }
  `]
})
export class ProfileComponent {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  readonly auth = inject(AuthService);
  private readonly refresh$ = new BehaviorSubject<void>(undefined);
  readonly profile$ = this.refresh$.pipe(switchMap(() => this.api.getCurrentUserProfile()));
  readonly recentGames$ = this.api.getRecentGames();
  showForm = false;
  error = '';

  readonly form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
  });

  startEdit(profile: UserProfile): void {
    this.error = '';
    this.form.setValue({ username: profile.username, email: profile.email });
    this.showForm = true;
  }

  cancelEdit(): void {
    this.showForm = false;
    this.error = '';
  }

  saveProfile(): void {
    this.error = '';
    const raw = this.form.getRawValue();

    this.api.updateProfile(raw).subscribe({
      next: (updated) => {
        this.auth.updateLocalUser(updated);
        this.showForm = false;
        this.refresh$.next();
      },
      error: (err) => this.error = err.error?.error || 'Could not update profile',
    });
  }
}
