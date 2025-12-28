import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { TokenStats } from './TokenStats';

type PostedTicket = {
    status: boolean;
    preview: string;
    title: string;
    length: number;
}

@Injectable({ providedIn: 'root' })
export class TranslatorService {
  readonly tokenStats = signal<TokenStats>({ session: 0, lastCall: 0 });
  readonly logs = signal<string[]>([]);

  private http = inject(HttpClient);

  private apiBaseUrl = `${window.location.protocol}//${window.location.hostname}:8080`;
  private API_URL = `${this.apiBaseUrl}/ramble`;

  // Replace with real call to your backend / LLM
    translate(ramble: string) {
    this.post_ramble(ramble);

    const tokens = Math.ceil(ramble.trim().split(/\s+/).filter(Boolean).length * 1.3);
    const stats = this.tokenStats();
    this.tokenStats.set({ session: stats.session + tokens, lastCall: tokens });
  }

  private clearLogs() {
    this.logs.set([]);
  }

  private update_ticket_log(response: PostedTicket) {
    console.log('Response from backend = ', response);
    let newTicketText = `Created ticket with title: "${response.title}".`;
    this.logs.update((prev) => [newTicketText, ...prev]);
  }

  private post_ramble(ramble: string) {
    this.http.post<PostedTicket>(this.API_URL, ramble)
        .subscribe((res : PostedTicket) => { this.update_ticket_log(res); });
  }
}
