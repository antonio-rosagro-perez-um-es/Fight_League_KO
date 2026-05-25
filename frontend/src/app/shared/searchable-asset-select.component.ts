import { Component, computed, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

export type AssetSelectOption = {
  value: string;
  label: string;
  icon?: string | null;
};

@Component({
  selector: 'app-searchable-asset-select',
  imports: [FormsModule],
  template: `
    <div class="asset-select">
      <button type="button" class="select-trigger" (click)="toggle()">
        @if (selectedOption()?.icon; as icon) {
          <img [src]="icon" [alt]="selectedOption()?.label || placeholder()" (error)="hideBrokenIcon($event)">
        }
        <span>{{ selectedOption()?.label || placeholder() }}</span>
      </button>

      @if (open()) {
        <div class="select-menu">
          <input type="search" [(ngModel)]="query" [placeholder]="searchPlaceholder()">
          <div class="option-list">
            @if (allowEmpty()) {
              <button type="button" class="option" (click)="choose('')">
                <span>{{ emptyLabel() }}</span>
              </button>
            }
            @for (option of filteredOptions(); track option.value) {
              <button type="button" class="option" [class.active]="option.value === value()" (click)="choose(option.value)">
                @if (option.icon) {
                  <img [src]="option.icon" [alt]="option.label" (error)="hideBrokenIcon($event)">
                }
                <span>{{ option.label }}</span>
              </button>
            } @empty {
              <div class="empty-option">No matches</div>
            }
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .asset-select { position: relative; }
    .select-trigger { align-items: center; background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 12px; color: white; cursor: pointer; display: flex; gap: .55rem; justify-content: flex-start; min-height: 2.75rem; padding: .55rem .75rem; width: 100%; }
    .select-trigger img, .option img { background: rgba(255,255,255,.1); border-radius: 999px; height: 1.6rem; object-fit: cover; width: 1.6rem; }
    .select-menu { background: #12172b; border: 1px solid rgba(255,255,255,.18); border-radius: 14px; box-shadow: 0 18px 50px rgba(0,0,0,.36); left: 0; padding: .55rem; position: absolute; right: 0; top: calc(100% + .35rem); z-index: 50; }
    input { background: rgba(255,255,255,.09); border: 1px solid rgba(255,255,255,.18); border-radius: 10px; color: white; margin-bottom: .45rem; padding: .55rem .65rem; width: 100%; }
    .option-list { display: grid; gap: .2rem; max-height: 220px; overflow-y: auto; }
    .option { align-items: center; background: transparent; border: 0; border-radius: 10px; color: white; cursor: pointer; display: flex; gap: .55rem; padding: .5rem; text-align: left; width: 100%; }
    .option:hover, .option.active { background: rgba(255,70,85,.22); }
    .empty-option { color: #c8d3ed; padding: .7rem; text-align: center; }
  `]
})
export class SearchableAssetSelectComponent {
  readonly options = input<AssetSelectOption[]>([]);
  readonly value = input<string>('');
  readonly placeholder = input('Choose option');
  readonly searchPlaceholder = input('Search');
  readonly allowEmpty = input(false);
  readonly emptyLabel = input('None');
  readonly valueChange = output<string>();

  readonly open = signal(false);
  query = '';

  readonly selectedOption = computed(() => this.options().find((option) => option.value === this.value()));
  readonly filteredOptions = computed(() => {
    const query = this.query.trim().toLowerCase();
    if (!query) {
      return this.options();
    }
    return this.options().filter((option) => option.label.toLowerCase().includes(query));
  });

  toggle(): void {
    this.open.update((open) => !open);
  }

  choose(value: string): void {
    this.valueChange.emit(value);
    this.open.set(false);
    this.query = '';
  }

  hideBrokenIcon(event: Event): void {
    (event.target as HTMLImageElement).style.display = 'none';
  }
}
