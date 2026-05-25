import { Component, inject, input } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-auth-form',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <section class="auth-card panel">
      <p class="eyebrow">{{ mode() === 'login' ? 'Welcome back' : 'Join the league' }}</p>
      <h1>{{ mode() === 'login' ? 'Log In' : 'Register' }}</h1>
      <form [formGroup]="form" (ngSubmit)="submit()">
        @if (mode() === 'register') {
          <label>Username <input formControlName="username" autocomplete="username"></label>
          <label>Email <input formControlName="email" type="email" autocomplete="email"></label>
        } @else {
          <label>Username or email <input formControlName="usernameOrEmail" autocomplete="username"></label>
        }
        <label>Password <input formControlName="password" type="password" autocomplete="current-password"></label>
        @if (error) { <p class="error">{{ error }}</p> }
        <button type="submit" [disabled]="submitDisabled()">{{ mode() === 'login' ? 'Log In' : 'Create Account' }}</button>
      </form>
      @if (mode() === 'login') {
        <a routerLink="/register">Not registered yet? Try now</a>
      } @else {
        <a routerLink="/login">Already registered?</a>
      }
    </section>
  `,
  styles: [`
    .auth-card { margin: 2rem auto; max-width: 460px; }
    h1 { font-size: 2.5rem; margin: .3rem 0 1rem; }
    form { display: grid; gap: 1rem; }
    label { color: #c8d3ed; display: grid; gap: .4rem; }
    input { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 12px; color: white; padding: .85rem; }
    button { background: #ff4655; border: 0; border-radius: 999px; color: white; cursor: pointer; padding: .9rem; }
    button:disabled { cursor: not-allowed; opacity: .55; }
    .error { color: #ff8a8a; }
  `]
})
export class AuthFormComponent {
  readonly mode = input.required<'login' | 'register'>();
  private readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  error = '';

  readonly form = this.fb.group({
    username: [''],
    email: ['', Validators.email],
    usernameOrEmail: [''],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  submit(): void {
    this.error = '';
    if (this.submitDisabled()) {
      this.error = 'Please fill all required fields correctly';
      return;
    }
    const value = this.form.getRawValue();
    const request = this.mode() === 'login'
      ? this.auth.login(value.usernameOrEmail || '', value.password || '')
      : this.auth.register(value.username || '', value.email || '', value.password || '');

    request.subscribe({
      next: () => this.router.navigateByUrl('/'),
      error: (err) => this.error = err.error?.error || 'Authentication failed',
    });
  }

  submitDisabled(): boolean {
    const value = this.form.getRawValue();
    if (!value.password || value.password.length < 8) {
      return true;
    }

    if (this.mode() === 'login') {
      return !value.usernameOrEmail;
    }

    return !value.username || !value.email || this.form.controls.email.invalid;
  }
}
