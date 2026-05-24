import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { NotificationService } from './notification.service';

export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const notification = inject(NotificationService);

  if (auth.authenticated()) {
    return true;
  }

  notification.showLoginPrompt(state.url);
  router.navigateByUrl('/login');
  return false;
};
