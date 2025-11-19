import { Component, computed, signal, Signal, WritableSignal,
    ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslatorService } from './TranslatorService';

import { RambleBox } from './RambleBox'

@Component({
  standalone: true,
  selector: 'app-root',
  imports: [CommonModule, RambleBox],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
    tokenStats!: Signal<{ session: number; lastCall: number }>;
    logs!: WritableSignal<string[]>;
    @ViewChild(RambleBox) rambleBox!: RambleBox;

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

}
