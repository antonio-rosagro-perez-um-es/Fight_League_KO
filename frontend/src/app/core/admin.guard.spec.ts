import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { adminGuard } from './admin.guard';
import { AuthService } from './auth.service';
import { NotificationService } from './notification.service';
import { UserRole } from './api.models';

describe('adminGuard', () => {
  const router = { navigateByUrl: jasmine.createSpy('navigateByUrl') };
  const notification = { showLoginPrompt: jasmine.createSpy('showLoginPrompt') };
  let role: UserRole | null = null;
  let authenticated = false;

  beforeEach(() => {
    router.navigateByUrl.calls.reset();
    notification.showLoginPrompt.calls.reset();
    TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: router },
        { provide: NotificationService, useValue: notification },
        { provide: AuthService, useValue: { role: () => role, authenticated: () => authenticated } },
      ],
    });
  });

  it('allows admins', () => {
    role = 'ADMIN';
    authenticated = true;

    const result = TestBed.runInInjectionContext(() => adminGuard({} as never, { url: '/admin' } as never));

    expect(result).toBeTrue();
  });

  it('redirects authenticated non-admin users home', () => {
    role = 'REGISTERED';
    authenticated = true;

    const result = TestBed.runInInjectionContext(() => adminGuard({} as never, { url: '/admin' } as never));

    expect(result).toBeFalse();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/');
  });

  it('prompts anonymous users to login', () => {
    role = null;
    authenticated = false;

    const result = TestBed.runInInjectionContext(() => adminGuard({} as never, { url: '/admin' } as never));

    expect(result).toBeFalse();
    expect(notification.showLoginPrompt).toHaveBeenCalledWith('/admin');
    expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
  });
});
