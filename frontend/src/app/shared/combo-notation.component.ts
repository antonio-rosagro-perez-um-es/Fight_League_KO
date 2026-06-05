import { Component, computed, input, signal } from '@angular/core';

import { parseComboNotation } from './combo-notation';

@Component({
  selector: 'app-combo-notation',
  template: `
    <div class="notation-box">
      <button type="button" class="mode-toggle" (click)="showText.set(!showText())">
        {{ showText() ? 'Images' : 'Text' }}
      </button>

      @if (showText()) {
        <p class="text-notation">{{ notation() }}</p>
      } @else {
        <div class="notation" [attr.aria-label]="readableNotation()">
          @for (token of tokens(); track $index) {
            @if (token.kind === 'separator') {
              <span class="separator-token" [attr.aria-label]="token.aria">{{ token.label }}</span>
            } @else if (token.kind === 'text') {
              <span class="text-token">{{ token.label }}</span>
            } @else if (token.glyph) {
              <span class="input-token glyph-token" [class]="token.className" [attr.aria-label]="token.aria">
                <img [src]="token.glyph" [alt]="token.aria">
              </span>
            } @else {
              <span class="input-token" [class]="token.className" [attr.aria-label]="token.aria">
                {{ token.label }}
              </span>
            }
          }
        </div>
        }
    </div>
  `,
  styles: [`
    .notation-box { background: #364153; border: 1px solid #4b5568; border-radius: 12px; box-shadow: inset 0 1px 0 rgba(255,255,255,.12); color: #f8fafc; padding: .75rem 4.6rem .75rem .85rem; position: relative; }
    .mode-toggle { background: rgba(255,255,255,.1); border: 1px solid rgba(255,255,255,.22); border-radius: 7px; color: #f8fafc; cursor: pointer; font-size: .68rem; font-weight: 800; line-height: 1; padding: .38rem .52rem; position: absolute; right: .55rem; text-transform: uppercase; top: .5rem; }
    .mode-toggle:hover, .mode-toggle:focus-visible { background: rgba(255,255,255,.18); border-color: rgba(255,255,255,.42); color: #ffffff; outline: none; }
    .notation { align-items: center; display: flex; flex-wrap: wrap; gap: .42rem; line-height: 1; }
    .input-token { align-items: center; background: transparent; border: 0; box-shadow: none; color: #111827; display: inline-flex; font-size: .78rem; font-weight: 950; height: 1.85rem; justify-content: center; min-width: 1.85rem; padding: 0 .12rem; text-transform: uppercase; }
    .glyph-token { background: transparent; padding: 0; }
    .glyph-token img { display: block; height: 1.65rem; object-fit: contain; width: 1.65rem; }
    .attack { border-radius: 999px; }
    .direction { border-radius: .55rem; font-size: 1rem; }
    .note { background: #fff7ed; border: 1px solid #fdba74; border-radius: 999px; color: #9a3412; font-size: .68rem; letter-spacing: .04em; padding: 0 .55rem; }
    .separator-token { color: #e2e8f0; font-weight: 950; padding: 0 .08rem; }
    .text-token, .text-notation { font-family: ui-monospace, SFMono-Regular, Consolas, monospace; }
    .text-token { background: #f8fafc; border: 1px solid #cbd5e1; border-radius: 999px; color: #334155; font-size: .78rem; padding: .38rem .55rem; }
    .text-token.invalid-text { background: #fee2e2; border-color: #fca5a5; color: #991b1b; }
    .text-notation { color: #f8fafc; font-size: .88rem; line-height: 1.55; margin: 0; overflow-wrap: anywhere; white-space: pre-wrap; }
    @media (max-width: 520px) { .notation-box { padding: 2.35rem .75rem .75rem; } }
  `]
})
export class ComboNotationComponent {
  readonly notation = input.required<string>();
  readonly showText = signal(false);
  readonly tokens = computed(() => parseComboNotation(this.notation()));
  readonly readableNotation = computed(() => this.tokens().map((token) => token.aria).join(' '));
}
