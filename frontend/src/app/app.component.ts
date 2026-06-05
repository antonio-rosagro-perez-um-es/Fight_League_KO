import { Component, HostListener, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';

import { AuthService } from './core/auth.service';
import { NotificationService } from './core/notification.service';

@Component({
  selector: 'app-root',
  imports: [RouterLink, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  readonly auth = inject(AuthService);
  readonly notification = inject(NotificationService);
  readonly dropdownOpen = signal(false);
  readonly mobileMenuOpen = signal(false);
  private readonly router = inject(Router);
  readonly adminShell = computed(() => this.auth.role() === 'ADMIN');

  constructor() {
    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe(() => {
      this.closeMenus();
    });
  }

  toggleDropdown(): void {
    this.dropdownOpen.update((open) => !open);
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen.update((open) => !open);
  }

  @HostListener('document:click')
  closeMenus(): void {
    this.dropdownOpen.set(false);
    this.mobileMenuOpen.set(false);
  }

  closeDropdown(): void {
    this.dropdownOpen.set(false);
  }
}
