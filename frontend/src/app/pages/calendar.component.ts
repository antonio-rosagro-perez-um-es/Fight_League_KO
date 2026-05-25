import { AsyncPipe, DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, combineLatest, map } from 'rxjs';

import { ApiService } from '../core/api.service';
import { TournamentView } from '../core/api.models';

type CalendarDay = {
  date: Date;
  dayNumber: number;
  inCurrentMonth: boolean;
  isToday: boolean;
  tournaments: TournamentView[];
};

@Component({
  selector: 'app-calendar',
  imports: [AsyncPipe, DatePipe, RouterLink],
  template: `
    <div class="calendar-heading">
      <div>
        <p class="eyebrow">Schedule</p>
        <h1>Calendar</h1>
      </div>
      <div class="month-card">
        <span>Today</span>
        <strong>{{ today | date:'d' }}</strong>
        <small>{{ today | date:'MMMM y' }}</small>
      </div>
    </div>

    <section class="calendar panel">
      @if (calendar$ | async; as calendar) {
        <div class="calendar-title">
          <div>
            <h2>{{ calendar.monthDate | date:'MMMM y' }}</h2>
            <span>Upcoming tournaments by start date</span>
          </div>
          <div class="month-actions">
            <button type="button" (click)="changeMonth(-1)">Previous</button>
            <button type="button" (click)="goToToday()">Today</button>
            <button type="button" (click)="changeMonth(1)">Next</button>
          </div>
        </div>

        <div class="weekdays">
          @for (day of weekdays; track day) {
            <span>{{ day }}</span>
          }
        </div>

        <div class="month-grid">
          @for (day of calendar.days; track day.date.toISOString()) {
            <article class="day-cell" [class.muted]="!day.inCurrentMonth" [class.today]="day.isToday">
              <div class="day-number">{{ day.dayNumber }}</div>
              <div class="events">
                @for (tournament of day.tournaments; track tournament.id) {
                  <a [routerLink]="['/tournaments', tournament.id]" [title]="tournament.title">
                    <strong>{{ tournament.title }}</strong>
                    <span>{{ tournament.remainingSlots }} slots · {{ tournament.state }}</span>
                  </a>
                }
              </div>
            </article>
          }
        </div>
      }
    </section>
  `,
  styles: [`
    .calendar-heading { align-items: end; display: flex; justify-content: space-between; margin-bottom: 1.5rem; }
    h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .4rem 0 0; text-transform: uppercase; }
    h2 { font-size: clamp(1.4rem, 3vw, 2.4rem); margin: 0; text-transform: uppercase; }
    .month-card { background: #ff4655; border-radius: 22px; display: grid; min-width: 170px; padding: 1rem; place-items: center; text-align: center; }
    .month-card strong { font-size: 3.2rem; line-height: .9; }
    .calendar-title { align-items: center; display: flex; justify-content: space-between; margin-bottom: 1rem; }
    .calendar-title > div:first-child { display: grid; gap: .25rem; }
    .calendar-title span { color: #b8c3df; }
    .month-actions { display: flex; flex-wrap: wrap; gap: .5rem; }
    button { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.14); border-radius: 999px; color: white; cursor: pointer; padding: .6rem .85rem; }
    .weekdays, .month-grid { display: grid; grid-template-columns: repeat(7, minmax(0, 1fr)); }
    .weekdays { border-bottom: 1px solid rgba(255,255,255,.12); color: #ffbd59; font-size: .78rem; font-weight: 900; letter-spacing: .08em; padding-bottom: .6rem; text-align: center; text-transform: uppercase; }
    .month-grid { gap: .5rem; margin-top: .5rem; }
    .day-cell { background: rgba(255,255,255,.055); border: 1px solid rgba(255,255,255,.1); border-radius: 16px; display: grid; gap: .5rem; min-height: 132px; padding: .7rem; }
    .day-cell.muted { opacity: .38; }
    .day-cell.today { border-color: #ff4655; box-shadow: 0 0 0 1px rgba(255,70,85,.35), inset 0 0 28px rgba(255,70,85,.12); }
    .day-number { align-items: center; background: rgba(255,255,255,.08); border-radius: 999px; display: grid; font-weight: 900; height: 30px; place-items: center; width: 30px; }
    .today .day-number { background: #ff4655; color: white; }
    .events { display: grid; gap: .35rem; }
    .events a { background: rgba(255,189,89,.12); border: 1px solid rgba(255,189,89,.18); border-radius: 10px; color: white; display: grid; gap: .2rem; padding: .45rem; text-decoration: none; }
    .events strong { font-size: .78rem; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .events span { color: #c8d3ed; font-size: .7rem; }
    @media (max-width: 820px) { .calendar-heading, .calendar-title { align-items: stretch; flex-direction: column; gap: 1rem; } .month-grid { display: grid; grid-template-columns: 1fr; } .weekdays { display: none; } .day-cell { min-height: auto; } .day-cell.muted { display: none; } }
  `]
})
export class CalendarComponent {
  private readonly api = inject(ApiService);
  readonly today = new Date();
  readonly weekdays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  private readonly selectedMonth$ = new BehaviorSubject<Date>(new Date(this.today.getFullYear(), this.today.getMonth(), 1));
  readonly calendar$ = combineLatest([this.api.getTournaments(), this.selectedMonth$]).pipe(
    map(([tournaments, monthDate]) => ({
      monthDate,
      days: this.buildMonth(tournaments, monthDate),
    }))
  );

  changeMonth(delta: number): void {
    const current = this.selectedMonth$.value;
    this.selectedMonth$.next(new Date(current.getFullYear(), current.getMonth() + delta, 1));
  }

  goToToday(): void {
    this.selectedMonth$.next(new Date(this.today.getFullYear(), this.today.getMonth(), 1));
  }

  private buildMonth(tournaments: TournamentView[], monthDate: Date): CalendarDay[] {
    const year = monthDate.getFullYear();
    const month = monthDate.getMonth();
    const first = new Date(year, month, 1);
    const mondayOffset = (first.getDay() + 6) % 7;
    const gridStart = new Date(year, month, 1 - mondayOffset);

    return Array.from({ length: 42 }, (_, index) => {
      const date = new Date(gridStart);
      date.setDate(gridStart.getDate() + index);
      const isoDate = this.toIsoDate(date);
      return {
        date,
        dayNumber: date.getDate(),
        inCurrentMonth: date.getMonth() === month,
        isToday: this.toIsoDate(this.today) === isoDate,
        tournaments: tournaments
          .filter((tournament) => tournament.startDate === isoDate)
          .sort((a, b) => a.title.localeCompare(b.title)),
      };
    });
  }

  private toIsoDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
