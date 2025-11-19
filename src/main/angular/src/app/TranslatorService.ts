import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { TokenStats } from './TokenStats';

@Injectable({ providedIn: 'root' })
export class TranslatorService {
  readonly tokenStats = signal<TokenStats>({ session: 0, lastCall: 0 });
  readonly logs = signal<string[]>([]);

  private http = inject(HttpClient);

  private apiBaseUrl = `${window.location.protocol}//${window.location.hostname}:8080`;

  // Replace with real call to your backend / LLM
  translate(ramble: string): { issues: string[]; tokensUsed: number } {
    //let res = this.post_ramble(ramble);

    let res = { length: "107", status: "true", preview: "Develop a mock backend service that simulates LLM ...", title: "Create a Mock Backend for LLM Integration Testing" }
    console.log(res);

    const tokens = Math.ceil(ramble.trim().split(/\s+/).filter(Boolean).length * 1.3);
    const stats = this.tokenStats();
    this.tokenStats.set({ session: stats.session + tokens, lastCall: tokens });

    const issues = [`Mock issue created from: "${ramble.slice(0, 40)}${ramble.length > 40 ? '…' : ''}"`];
    this.prependLog(`POST /translate 200 OK | tokens=${tokens}`);

    return { issues, tokensUsed: tokens };
  }

  clearLogs() {
    this.logs.set([]);
  }

    private log_response(response : string) {
        console.log('Response: ', response);
        //this.prependLog(`Issues: ${response.join(', ')}`);
    }

  private post_ramble(ramble: string) {
    return this.http.post<string>(`${this.apiBaseUrl}/ramble`, ramble)
        .subscribe(res => { this.log_response(res); });
  }

  private prependLog(entry: string) {
    this.logs.update((prev) => [entry, ...prev]);
  }
}
