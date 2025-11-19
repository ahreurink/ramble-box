import { Component, computed, signal, Signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslatorService } from './TranslatorService';

@Component({
  standalone: true,
  selector: 'ramble-box',
  imports: [CommonModule, FormsModule],
  templateUrl: './ramble-box.html',
  styleUrl: './ramble-box.css'
})
export class RambleBox {
    show = signal(false);

    public ramble = signal<string>('');

    protected readonly title = signal('ramblebox');

    constructor(private svc: TranslatorService) {}

    onTranslate() {
      const text = this.ramble().trim();
      if (!text) {
        console.log("Clicked send without any text present")
        return;
      }
      let res = this.svc.translate(text);

      console.log(res);

      this.ramble.set('');
    }

    onClear() {
      this.ramble.set('');
    }

    toggle() {
        this.show.update(v => !v);
    }
}