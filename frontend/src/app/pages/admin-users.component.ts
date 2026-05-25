import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { BehaviorSubject, switchMap } from 'rxjs';

import { ApiService } from '../core/api.service';

@Component({
  selector: 'app-admin-users',
  imports: [AsyncPipe],
  template: `
    <div class="admin-heading">
      <div>
        <p class="eyebrow">Admin</p>
        <h1>User Management</h1>
      </div>
    </div>

    <section class="panel table-wrap">
      @if (users$ | async; as users) {
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Username</th>
              <th>Email</th>
              <th>Role</th>
              <th>Score</th>
              <th>Tournament Wins</th>
              <th>Deleted</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (user of users; track user.id) {
              <tr [class.deleted]="user.deleted">
                <td>{{ user.id }}</td>
                <td>{{ user.username }}</td>
                <td>{{ user.email }}</td>
                <td>{{ user.role }}</td>
                <td>{{ user.score }}</td>
                <td>{{ user.tournamentWins }}</td>
                <td>{{ user.deleted ? 'Yes' : 'No' }}</td>
                <td>
                  @if (user.deleted) {
                    <button type="button" (click)="restore(user.id)">Restore</button>
                  } @else {
                    <button type="button" (click)="delete(user.id)">Delete</button>
                  }
                </td>
              </tr>
            }
          </tbody>
        </table>
      }
    </section>
  `,
  styles: [`
    .admin-heading { align-items: center; display: flex; justify-content: space-between; margin-bottom: 1rem; }
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .3rem 0; text-transform: uppercase; }
    .table-wrap { overflow-x: auto; }
    table { border-collapse: collapse; min-width: 980px; width: 100%; }
    th, td { border-bottom: 1px solid rgba(255,255,255,.1); padding: .8rem; text-align: left; }
    th { color: #ffbd59; font-size: .78rem; text-transform: uppercase; }
    td:first-child { font-family: ui-monospace, SFMono-Regular, Consolas, monospace; font-size: .78rem; max-width: 190px; overflow: hidden; text-overflow: ellipsis; }
    button { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.14); border-radius: 999px; color: white; cursor: pointer; padding: .65rem .9rem; }
    .deleted { opacity: .55; }
  `]
})
export class AdminUsersComponent {
  private readonly api = inject(ApiService);
  private readonly refresh$ = new BehaviorSubject<void>(undefined);
  readonly users$ = this.refresh$.pipe(switchMap(() => this.api.getAllUsersForAdmin()));

  delete(id: string): void {
    this.api.deleteUser(id).subscribe(() => this.refresh$.next());
  }

  restore(id: string): void {
    this.api.restoreUser(id).subscribe(() => this.refresh$.next());
  }
}
