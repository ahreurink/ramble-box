import { Component, computed, signal, Signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslatorService } from './TranslatorService';

@Component({
  standalone: true,
  selector: 'app-root',
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('ramblebox');

  ramble = signal<string>('');
  tokenStats!: Signal<{ session: number; lastCall: number }>;
  logs!: WritableSignal<string[]>;

  constructor(private svc: TranslatorService) {
    this.tokenStats = this.svc.tokenStats;
    this.logs = this.svc.logs;
  }

  // Adjust this to your real context/window limit if you want a gauge
  readonly lastCallSoftLimit = 2000;
  lastCallPct = computed(() => {
    const last = this.tokenStats().lastCall;
    return Math.min(100, Math.round((last / this.lastCallSoftLimit) * 100));
  });

  onTranslate() {
    const text = this.ramble().trim();
    if (!text) return;
    this.svc.translate(text);
    this.ramble.set('');
  }

  onClear() {
    this.ramble.set('');
  }
}
