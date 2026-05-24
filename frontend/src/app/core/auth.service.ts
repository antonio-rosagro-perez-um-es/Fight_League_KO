import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Injectable, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { tap } from 'rxjs';

import { AuthResponse, User } from './api.models';

const TOKEN_KEY = 'fight-league-token';
const USER_KEY = 'fight-league-user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly browser = isPlatformBrowser(this.platformId);

  readonly user = signal<User | null>(this.readUser());
  readonly token = signal<string | null>(this.browser ? localStorage.getItem(TOKEN_KEY) : null);
  readonly authenticated = computed(() => this.token() !== null && this.user() !== null);
  readonly role = computed(() => this.user()?.role ?? null);

  login(usernameOrEmail: string, password: string) {
    return this.http.post<AuthResponse>('/api/auth/login', { usernameOrEmail, password }).pipe(
      tap((response) => this.setSession(response))
    );
  }

  register(username: string, email: string, password: string) {
    return this.http.post<AuthResponse>('/api/auth/register', { username, email, password }).pipe(
      tap((response) => this.setSession(response))
    );
  }

  logout(): void {
    if (this.browser) {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
    }
    this.token.set(null);
    this.user.set(null);
  }

  updateLocalUser(user: User): void {
    if (this.browser) {
      localStorage.setItem(USER_KEY, JSON.stringify(user));
    }
    this.user.set(user);
  }

  private setSession(response: AuthResponse): void {
    if (this.browser) {
      localStorage.setItem(TOKEN_KEY, response.token);
      localStorage.setItem(USER_KEY, JSON.stringify(response.user));
    }
    this.token.set(response.token);
    this.user.set(response.user);
  }

  private readUser(): User | null {
    if (!this.browser) {
      return null;
    }

    const rawUser = localStorage.getItem(USER_KEY);
    return rawUser ? JSON.parse(rawUser) as User : null;
  }
}
