import { Component, computed, input } from '@angular/core';

import { controlGlyph } from './asset-paths';

type TokenKind = 'attack' | 'direction' | 'note' | 'separator' | 'text';

type NotationToken = {
  kind: TokenKind;
  label: string;
  className: string;
  aria: string;
  glyph?: string;
};

const ATTACK_TOKENS: Record<string, { label: string; aria: string; className: string }> = {
  l: { label: 'L', aria: 'Light attack', className: 'attack-light' },
  m: { label: 'M', aria: 'Medium attack', className: 'attack-medium' },
  h: { label: 'H', aria: 'Heavy attack', className: 'attack-heavy' },
  t: { label: 'T', aria: 'Throw', className: 'attack-throw' },
  s1: { label: 'S1', aria: 'Special one', className: 'attack-special-one' },
  s2: { label: 'S2', aria: 'Special two', className: 'attack-special-two' },
};

const DIRECTION_TOKENS: Record<string, { label: string; aria: string; className: string }> = {
  '1': { label: '↙', aria: 'Down-left', className: 'direction-diagonal' },
  '2': { label: '↓', aria: 'Down', className: 'direction-cardinal' },
  '3': { label: '↘', aria: 'Down-right', className: 'direction-diagonal' },
  '4': { label: '←', aria: 'Left', className: 'direction-cardinal' },
  '5': { label: '•', aria: 'Neutral', className: 'direction-neutral' },
  '6': { label: '→', aria: 'Right', className: 'direction-cardinal' },
  '7': { label: '↖', aria: 'Up-left', className: 'direction-diagonal' },
  '8': { label: '↑', aria: 'Up', className: 'direction-cardinal' },
  '9': { label: '↗', aria: 'Up-right', className: 'direction-diagonal' },
  dl: { label: '↙', aria: 'Down-left', className: 'direction-diagonal' },
  d: { label: '↓', aria: 'Down', className: 'direction-cardinal' },
  dr: { label: '↘', aria: 'Down-right', className: 'direction-diagonal' },
  lft: { label: '←', aria: 'Left', className: 'direction-cardinal' },
  left: { label: '←', aria: 'Left', className: 'direction-cardinal' },
  r: { label: '→', aria: 'Right', className: 'direction-cardinal' },
  right: { label: '→', aria: 'Right', className: 'direction-cardinal' },
  u: { label: '↑', aria: 'Up', className: 'direction-cardinal' },
  ul: { label: '↖', aria: 'Up-left', className: 'direction-diagonal' },
  ur: { label: '↗', aria: 'Up-right', className: 'direction-diagonal' },
};

const NOTE_TOKENS: Record<string, string> = {
  j: 'Jump',
  jump: 'Jump',
  air: 'Air',
  hold: 'Hold',
  delay: 'Delay',
  microdash: 'Microdash',
  dash: 'Dash',
  walk: 'Walk',
  assist: 'Assist',
};

const SEPARATORS: Record<string, string> = {
  '>': 'then',
  '+': 'with',
  ',': 'pause',
  '(': 'open note',
  ')': 'close note',
  '/': 'or',
};

@Component({
  selector: 'app-combo-notation',
  template: `
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
  `,
  styles: [`
    .notation { align-items: center; display: flex; flex-wrap: wrap; gap: .38rem; line-height: 1; }
    .input-token { align-items: center; border: 2px solid rgba(255,255,255,.34); box-shadow: inset 0 -3px 0 rgba(0,0,0,.22), 0 5px 14px rgba(0,0,0,.18); color: #070910; display: inline-flex; font-size: .78rem; font-weight: 950; height: 2rem; justify-content: center; min-width: 2rem; padding: 0 .5rem; text-transform: uppercase; }
    .glyph-token { background: #f4f7ff; padding: .18rem; }
    .glyph-token img { display: block; height: 1.45rem; object-fit: contain; width: 1.45rem; }
    .attack { border-radius: 999px; }
    .direction { border-radius: .55rem; font-size: 1rem; }
    .note { background: #ffcf66; border-radius: .45rem; font-size: .68rem; letter-spacing: .04em; }
    .attack-light { background: #6dff9f; }
    .attack-medium { background: #f4d35e; }
    .attack-heavy { background: #ff7d7d; }
    .attack-throw { background: #cbff5b; }
    .attack-special-one { background: #56c7ff; }
    .attack-special-two { background: #b879ff; }
    .direction-cardinal { background: #f4f7ff; }
    .direction-diagonal { background: linear-gradient(135deg, #ffffff, #b9c7ff); }
    .direction-neutral { background: #dce4f7; }
    .separator-token { color: #ffbd59; font-weight: 900; padding: 0 .08rem; }
    .text-token { background: rgba(255,255,255,.08); border: 1px solid rgba(255,255,255,.12); border-radius: 999px; color: #c8d3ed; font-family: ui-monospace, SFMono-Regular, Consolas, monospace; font-size: .78rem; padding: .38rem .55rem; }
  `]
})
export class ComboNotationComponent {
  readonly notation = input.required<string>();
  readonly tokens = computed(() => this.parseNotation(this.notation()));
  readonly readableNotation = computed(() => this.tokens().map((token) => token.aria).join(' '));

  private parseNotation(notation: string): NotationToken[] {
    return notation
      .split(/\s*(>|\+|,|\(|\)|\/)\s*|\s+/)
      .filter((token): token is string => !!token && token.trim().length > 0)
      .map((rawToken) => this.mapToken(rawToken.trim()));
  }

  private mapToken(token: string): NotationToken {
    const normalized = token.toLowerCase();
    const attack = ATTACK_TOKENS[normalized];
    if (attack) {
      return {
        kind: 'attack',
        label: attack.label,
        className: `attack ${attack.className}`,
        aria: attack.aria,
        glyph: controlGlyph(normalized) ?? undefined,
      };
    }

    const direction = DIRECTION_TOKENS[normalized];
    if (direction) {
      return {
        kind: 'direction',
        label: direction.label,
        className: `direction ${direction.className}`,
        aria: direction.aria,
        glyph: controlGlyph(normalized) ?? undefined,
      };
    }

    const note = NOTE_TOKENS[normalized];
    if (note) {
      return {
        kind: 'note',
        label: note,
        className: 'note',
        aria: note,
      };
    }

    const separator = SEPARATORS[token];
    if (separator) {
      return {
        kind: 'separator',
        label: token,
        className: '',
        aria: separator,
      };
    }

    return {
      kind: 'text',
      label: token,
      className: '',
      aria: token,
    };
  }
}
