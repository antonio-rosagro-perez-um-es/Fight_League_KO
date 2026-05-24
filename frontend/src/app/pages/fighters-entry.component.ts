import { Component, inject } from '@angular/core';

import { AuthService } from '../core/auth.service';
import { AdminFightersComponent } from './admin-fighters.component';
import { FightersComponent } from './fighters.component';

@Component({
  selector: 'app-fighters-entry',
  imports: [AdminFightersComponent, FightersComponent],
  template: `
    @if (auth.role() === 'ADMIN') {
      <app-admin-fighters />
    } @else {
      <app-fighters />
    }
  `
})
export class FightersEntryComponent {
  readonly auth = inject(AuthService);
}
