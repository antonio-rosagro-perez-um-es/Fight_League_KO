export type FighterAssetType = 'portrait' | 'banner' | 'icon';

const FUSE_ASSET_NAMES: Record<string, string> = {
  DOUBLE_DOWN: 'Double_Down',
  FREESTYLE: 'Freestyle',
  TWO_X_ASSIST: '2X_Assist',
  JUGGERNAUT: 'Juggernaut',
  SIDEKICK: 'Sidekick',
};

const CONTROL_GLYPH_NAMES: Record<string, string> = {
  l: 'Glyph-L.svg',
  m: 'Glyph-M.svg',
  h: 'Glyph-H.svg',
  t: 'Glyph-T.svg',
  s1: 'Glyph-S1.svg',
  s2: 'Glyph-S2.svg',
  parry: 'Glyph-parry.svg',
  d: 'Glyph-down.svg',
  down: 'Glyph-down.svg',
  '2': 'Glyph-down.svg',
  u: 'Glyph-up.svg',
  up: 'Glyph-up.svg',
  '8': 'Glyph-up.svg',
  left: 'Glyph-back.svg',
  lft: 'Glyph-back.svg',
  back: 'Glyph-back.svg',
  b: 'Glyph-back.svg',
  '4': 'Glyph-back.svg',
  right: 'Glyph-forward.svg',
  r: 'Glyph-forward.svg',
  forward: 'Glyph-forward.svg',
  f: 'Glyph-forward.svg',
  '6': 'Glyph-forward.svg',
  ff: 'Glyph-forward_forward.svg',
  dash: 'Glyph-forward_forward.svg',
};

export function fighterAsset(slug: string, type: FighterAssetType): string {
  return `/assets/fighters/${slug}/${slug}_${type}.webp`;
}

export function fighterPlaceholder(type: FighterAssetType): string {
  return `/assets/placeholders/fighter-${type}.svg`;
}

export function fuseAsset(fuse: string, extension: 'svg' | 'png' = 'svg'): string | null {
  const assetName = FUSE_ASSET_NAMES[fuse];
  return assetName ? `/assets/fuses/${assetName}.${extension}` : null;
}

export function controlGlyph(token: string): string | null {
  const glyphName = CONTROL_GLYPH_NAMES[token.toLowerCase()];
  return glyphName ? `/assets/controls/${glyphName}` : null;
}
