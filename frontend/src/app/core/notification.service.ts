import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  readonly blockedAction = signal<string | null>(null);

  showLoginPrompt(returnUrl?: string): void {
    this.blockedAction.set(returnUrl || null);
  }

  clearBlockedAction(): void {
    this.blockedAction.set(null);
  }
}
