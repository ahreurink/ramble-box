import { Injectable, signal } from '@angular/core';
import { TokenStats } from './TokenStats';

@Injectable({ providedIn: 'root' })
export class TranslatorService {
  readonly tokenStats = signal<TokenStats>({ session: 0, lastCall: 0 });
  readonly logs = signal<string[]>([]);

  // Replace with real call to your backend / LLM
  translate(ramble: string): { issues: string[]; tokensUsed: number } {
    const tokens = Math.ceil(ramble.trim().split(/\s+/).filter(Boolean).length * 1.3);
    const stats = this.tokenStats();
    this.tokenStats.set({ session: stats.session + tokens, lastCall: tokens });

    const issues = [`Mock issue created from: "${ramble.slice(0, 40)}${ramble.length > 40 ? '…' : ''}"`];
    this.prependLog(`POST /translate 200 OK | tokens=${tokens}`);
    this.prependLog(`Issues: ${issues.join(', ')}`);
    return { issues, tokensUsed: tokens };
  }

  clearLogs() {
    this.logs.set([]);
  }

  private prependLog(entry: string) {
    this.logs.update((prev) => [entry, ...prev]);
  }
}
