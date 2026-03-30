import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/auth/auth.service';
import { FitnessService, FitnessData } from '../../../core/fitness.service';

type DateRange = '24h' | '7d' | '30d';

interface TooltipData {
  attributes: { [key: string]: string };
  x: number;
  y: number;
}

@Component({
    selector: 'app-workout-history',
    imports: [CommonModule],
    templateUrl: './workout-history.component.html'
})
export default class WorkoutHistoryComponent implements OnInit {
  private fitnessService = inject(FitnessService);
  private authService = inject(AuthService);

  history = signal<FitnessData[]>([]);
  loading = signal(false);
  activeRange = signal<DateRange>('7d');

  tooltipData = signal<TooltipData | null>(null);

  user = this.authService.currentUser;

  ngOnInit() {
    this.loadData('7d');
  }

  setRange(range: DateRange) {
    this.activeRange.set(range);
    this.loadData(range);
  }

  loadData(range: DateRange) {
    if (!this.user()) return;

    this.loading.set(true);
    const { from, to } = this.calculateDateRange(range);

    this.fitnessService.getFitnessHistory(this.user()!.userId!, from, to)
      .subscribe({
        next: (data) => {
          const filteredData = this.filterDataForRange(data, range);
          this.history.set(filteredData);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Failed to load history', err);
          this.loading.set(false);
        }
      });
  }

  private filterDataForRange(data: FitnessData[], range: DateRange): FitnessData[] {
    const today = new Date();
    const cutoffDate = new Date(today);

    switch (range) {
      case '24h':
        cutoffDate.setDate(today.getDate() - 1);
        break;
      case '7d':
        cutoffDate.setDate(today.getDate() - 7);
        break;
      case '30d':
        cutoffDate.setDate(today.getDate() - 30);
        break;
    }

    const pad = (n: number) => n.toString().padStart(2, '0');
    const cutoffString = `${cutoffDate.getFullYear()}-${pad(cutoffDate.getMonth() + 1)}-${pad(cutoffDate.getDate())}`;

    // We only keep data where the local timestamp representation is >= cutoffString
    return data.filter(item => {
      // Create a local Date object from the UTC timestamp
      // Assuming item.timestamp is a valid ISO string like "2026-03-25T14:00:00.000Z"
      const localDate = new Date(item.timestamp);

      // We format it to local YYYY-MM-DD
      const localDateString = `${localDate.getFullYear()}-${pad(localDate.getMonth() + 1)}-${pad(localDate.getDate())}`;

      return localDateString >= cutoffString;
    });
  }

  getAttributesList(attributes: { [key: string]: string }): { key: string; value: string }[] {
    if (!attributes) return [];
    return Object.entries(attributes).map(([key, value]) => ({
      key: this.formatKey(key),
      value: value
    }));
  }

  showTooltip(event: MouseEvent, attributes: { [key: string]: string }) {
    const target = event.currentTarget as HTMLElement;
    const rect = target.getBoundingClientRect();

    this.tooltipData.set({
      attributes,
      x: rect.right,
      y: rect.bottom + 5
    });
  }

  hideTooltip() {
    this.tooltipData.set(null);
  }

  private formatKey(key: string): string {
    const result = key.replace(/([A-Z])/g, ' $1');
    return result.charAt(0).toUpperCase() + result.slice(1);
  }

  private calculateDateRange(range: DateRange): { from: string, to: string } {
    const today = new Date();

    const toDate = new Date(today);

    const fromDate = new Date(today);

    switch (range) {
      case '24h':
        fromDate.setDate(today.getDate() - 1);
        break;
      case '7d':
        fromDate.setDate(today.getDate() - 6);
        break;
      case '30d':
        fromDate.setDate(today.getDate() - 29);
        break;
    }

    const pad = (n: number) => n.toString().padStart(2, '0');
    const formatDate = (date: Date) => `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;

    return {
      from: formatDate(fromDate),
      to: formatDate(toDate)
    };
  }
}
