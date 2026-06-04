import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';

describe('authInterceptor', () => {
  let http: HttpClient;
  let controller: HttpTestingController;
  let token: string | null = null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: { token: () => token } },
      ],
    });
    http = TestBed.inject(HttpClient);
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controller.verify());

  it('adds bearer token when available', () => {
    token = 'jwt-token';

    http.get('/api/users/me').subscribe();

    const request = controller.expectOne('/api/users/me');
    expect(request.request.headers.get('Authorization')).toBe('Bearer jwt-token');
    request.flush({});
  });

  it('does not add authorization header without token', () => {
    token = null;

    http.get('/api/fighters/ranking').subscribe();

    const request = controller.expectOne('/api/fighters/ranking');
    expect(request.request.headers.has('Authorization')).toBeFalse();
    request.flush([]);
  });
});
