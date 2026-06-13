import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

import { controlGlyph } from './asset-paths';

export type TokenKind = 'attack' | 'direction' | 'note' | 'separator' | 'text';

export type NotationToken = {
  kind: TokenKind;
  label: string;
  className: string;
  aria: string;
  glyph?: string;
  invalid?: boolean;
};

export type NotationValidationResult = {
  valid: boolean;
  errors: string[];
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
};

const NOTE_TOKENS: Record<string, string> = {
  j: 'Air',
  jc: 'Jump cancel',
  jump: 'Jump',
  air: 'Air',
  hold: 'Hold',
  delay: 'Delay',
  delayed: 'Delayed',
  microdash: 'Microdash',
  walk: 'Walk',
  assist: 'Assist',
  cancel: 'Cancel',
};

const SEPARATORS: Record<string, string> = {
  '>': 'then',
  '+': 'with',
  ',': 'pause',
  '/': 'or',
};

const TOKEN_PATTERN = /[jJ]\.|[1-9]?[sS][12](?:\(\d+\))?|[1-9]?[lLmMhHtT](?:\(\d+\))?(?![A-Za-z])|[1-9]|[A-Za-z]+|[>+,/()]|\S/g;

export function parseComboNotation(notation: string): NotationToken[] {
  return tokenizeNotation(notation).flatMap((token) => mapToken(token));
}

export function validateComboNotation(notation: string): NotationValidationResult {
  const rawTokens = tokenizeNotation(notation);
  const errors: string[] = [];

  if (!notation.trim()) {
    return { valid: false, errors: ['Notation is required.'] };
  }

  rawTokens.forEach((token) => {
    if (!isValidToken(token)) {
      errors.push(`Invalid notation token: ${token}`);
    }
  });

  rawTokens.forEach((token, index) => {
    if (isSeparator(token) && (index === 0 || index === rawTokens.length - 1 || isSeparator(rawTokens[index - 1]) || isSeparator(rawTokens[index + 1]))) {
      errors.push(`Separator "${token}" must be between combo inputs.`);
    }
  });

  return { valid: errors.length === 0, errors: Array.from(new Set(errors)) };
}

export function comboNotationValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = String(control.value || '');
    const result = validateComboNotation(value);
    return result.valid ? null : { comboNotation: result.errors };
  };
}

function tokenizeNotation(notation: string): string[] {
  return notation.match(TOKEN_PATTERN) ?? [];
}

function isValidToken(token: string): boolean {
  if (isSeparator(token)) {
    return true;
  }

  if (token === '(' || token === ')') {
    return false;
  }

  const normalized = stripRepeat(token).toLowerCase();
  if (NOTE_TOKENS[normalized]) {
    return true;
  }

  if (/^[1-9]$/.test(normalized)) {
    return true;
  }

  if (/^[1-9]?(?:[lmht]|s[12])+$/.test(normalized)) {
    return true;
  }

  return false;
}

function isSeparator(token: string): boolean {
  return !!SEPARATORS[token];
}

function stripRepeat(token: string): string {
  return token.replace(/\(\d+\)$/, '');
}

function expandToken(token: string): string[] {
  const repeat = token.match(/^(.+)\((\d+)\)$/);
  if (repeat) {
    return Array.from({ length: Number(repeat[2]) }, () => repeat[1]);
  }

  return [token];
}

function mapToken(token: string): NotationToken[] {
  return expandToken(token).flatMap((expandedToken) => mapExpandedToken(expandedToken));
}

function mapExpandedToken(token: string): NotationToken[] {
  const normalized = token.toLowerCase();

  if (isSeparator(token)) {
    return [{ kind: 'separator', label: token, className: '', aria: SEPARATORS[token] }];
  }

  if (normalized === 'j.') {
    return mapExpandedToken('j');
  }

  if (normalized === 'dash') {
    return [{
      kind: 'direction',
      label: 'Dash',
      className: 'direction direction-cardinal',
      aria: 'Dash forward',
      glyph: controlGlyph('dash') ?? undefined,
    }];
  }

  const note = NOTE_TOKENS[normalized];
  if (note) {
    return [{ kind: 'note', label: note, className: 'note', aria: note }];
  }

  if (/^[1-9]$/.test(normalized)) {
    return mapDirection(normalized);
  }

  const direction = normalized.match(/^([1-9])(.+)$/);
  if (direction) {
    return [...mapDirection(direction[1]), ...mapAttacks(direction[2], token)];
  }

  if (/^(?:[lmht]|s[12])+$/.test(normalized)) {
    return mapAttacks(normalized, token);
  }

  return [{ kind: 'text', label: token, className: 'invalid-text', aria: `Invalid token ${token}`, invalid: true }];
}

function mapDirection(direction: string): NotationToken[] {
  if (direction === '5') {
    return [];
  }

  const value = DIRECTION_TOKENS[direction];
  return [{
    kind: 'direction',
    label: value.label,
    className: `direction ${value.className}`,
    aria: value.aria,
    glyph: controlGlyph(direction) ?? undefined,
  }];
}

function mapAttacks(attacks: string, fallbackToken: string): NotationToken[] {
  const parts = attacks.match(/s[12]|[lmht]/g);
  if (!parts || parts.join('') !== attacks) {
    return [{ kind: 'text', label: fallbackToken, className: 'invalid-text', aria: `Invalid token ${fallbackToken}`, invalid: true }];
  }

  return parts.map((part) => {
    const attack = ATTACK_TOKENS[part];
    return {
      kind: 'attack',
      label: attack.label,
      className: `attack ${attack.className}`,
      aria: attack.aria,
      glyph: controlGlyph(part) ?? undefined,
    };
  });
}
