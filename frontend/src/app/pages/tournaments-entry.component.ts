import { Component, inject } from '@angular/core';

import { AuthService } from '../core/auth.service';
import { AdminTournamentsComponent } from './admin-tournaments.component';
import { TournamentsComponent } from './tournaments.component';

@Component({
  selector: 'app-tournaments-entry',
  imports: [AdminTournamentsComponent, TournamentsComponent],
  template: `
    @if (auth.role() === 'ADMIN') {
      <app-admin-tournaments />
    } @else {
      <app-tournaments />
    }
  `
})
export class TournamentsEntryComponent {
  readonly auth = inject(AuthService);
}
