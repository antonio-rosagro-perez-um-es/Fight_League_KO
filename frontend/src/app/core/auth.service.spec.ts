import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { PLATFORM_ID } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { AuthService } from './auth.service';
import { AuthResponse, User } from './api.models';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  const user: User = {
    id: 'user-1',
    username: 'player',
    email: 'player@example.test',
    role: 'REGISTERED',
    score: 0,
    tournamentWins: 0,
  };

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: PLATFORM_ID, useValue: 'browser' },
      ],
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it('stores token and user after login', () => {
    const response: AuthResponse = { token: 'jwt-token', user };

    service.login('player', 'password').subscribe();
    const request = http.expectOne('/api/auth/login');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ usernameOrEmail: 'player', password: 'password' });
    request.flush(response);

    expect(service.authenticated()).toBeTrue();
    expect(service.token()).toBe('jwt-token');
    expect(service.user()).toEqual(user);
    expect(localStorage.getItem('fight-league-token')).toBe('jwt-token');
  });

  it('clears session on logout', () => {
    localStorage.setItem('fight-league-token', 'jwt-token');
    localStorage.setItem('fight-league-user', JSON.stringify(user));

    service.logout();

    expect(service.authenticated()).toBeFalse();
    expect(localStorage.getItem('fight-league-token')).toBeNull();
    expect(localStorage.getItem('fight-league-user')).toBeNull();
  });
});
