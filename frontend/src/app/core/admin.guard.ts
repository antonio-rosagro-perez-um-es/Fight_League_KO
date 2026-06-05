import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { NotificationService } from './notification.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const notification = inject(NotificationService);

  if (auth.role() === 'ADMIN') {
    return true;
  }

  if (!auth.authenticated()) {
    notification.showLoginPrompt(state.url);
    router.navigateByUrl('/login');
  } else {
    router.navigateByUrl('/');
  }
  return false;
};
