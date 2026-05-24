import { Component, input } from '@angular/core';

@Component({
  selector: 'app-simple-page',
  template: `
    <section class="panel">
      <p class="eyebrow">Pending view</p>
      <h1>{{ title() }}</h1>
      <p>{{ description() }}</p>
    </section>
  `,
  styles: [`h1 { font-size: clamp(2rem, 6vw, 4rem); margin: .4rem 0 1rem; text-transform: uppercase; }`]
})
export class SimplePageComponent {
  readonly title = input.required<string>();
  readonly description = input<string>('This screen will be expanded as the next frontend milestone.');
}
