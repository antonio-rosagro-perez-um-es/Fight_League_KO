import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { authGuard } from './auth.guard';
import { AuthService } from './auth.service';
import { NotificationService } from './notification.service';

describe('authGuard', () => {
  const router = { navigateByUrl: jasmine.createSpy('navigateByUrl') };
  const notification = { showLoginPrompt: jasmine.createSpy('showLoginPrompt') };
  let authenticated = false;

  beforeEach(() => {
    router.navigateByUrl.calls.reset();
    notification.showLoginPrompt.calls.reset();
    TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: router },
        { provide: NotificationService, useValue: notification },
        { provide: AuthService, useValue: { authenticated: () => authenticated } },
      ],
    });
  });

  it('allows authenticated users', () => {
    authenticated = true;

    const result = TestBed.runInInjectionContext(() => authGuard({} as never, { url: '/profile' } as never));

    expect(result).toBeTrue();
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  it('redirects anonymous users to login', () => {
    authenticated = false;

    const result = TestBed.runInInjectionContext(() => authGuard({} as never, { url: '/profile' } as never));

    expect(result).toBeFalse();
    expect(notification.showLoginPrompt).toHaveBeenCalledWith('/profile');
    expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
  });
});
