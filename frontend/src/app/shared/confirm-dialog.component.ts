import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  template: `
    <div class="confirm-overlay" (click)="cancelled.emit()">
      <section class="confirm-dialog" role="dialog" aria-modal="true" [attr.aria-label]="title" (click)="$event.stopPropagation()">
        <p class="eyebrow">Confirmation required</p>
        <h2>{{ title }}</h2>
        <p>{{ message }}</p>
        <div class="confirm-actions">
          <button type="button" class="ghost" (click)="cancelled.emit()">Cancel</button>
          <button type="button" class="danger" (click)="confirmed.emit()">{{ confirmLabel }}</button>
        </div>
      </section>
    </div>
  `,
  styles: [`
    .confirm-overlay { align-items: center; background: rgba(0,0,0,.68); display: flex; inset: 0; justify-content: center; padding: 1rem; position: fixed; z-index: 200; }
    .confirm-dialog { background: linear-gradient(135deg, rgba(0,0,0,.88), rgba(6,18,10,.94)); border: 1px solid rgba(255,122,132,.36); border-radius: 22px; box-shadow: 0 28px 80px rgba(0,0,0,.52); display: grid; gap: .8rem; max-width: 460px; padding: 1.35rem; width: min(94vw, 460px); }
    h2 { font-size: clamp(1.35rem, 5vw, 2.3rem); line-height: .96; margin: 0; text-transform: uppercase; }
    p { color: #d7deef; line-height: 1.5; margin: 0; }
    .confirm-actions { display: flex; flex-wrap: wrap; gap: .7rem; justify-content: flex-end; margin-top: .35rem; }
    button { border-radius: 999px; color: white; cursor: pointer; padding: .75rem 1rem; }
    .ghost { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.18); }
    .danger { background: #c7343f; border: 1px solid rgba(255,122,132,.58); }
  `]
})
export class ConfirmDialogComponent {
  @Input({ required: true }) title = '';
  @Input({ required: true }) message = '';
  @Input() confirmLabel = 'Confirm';
  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();
}
