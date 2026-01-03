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
  standalone: true,
  imports: [CommonModule],
  templateUrl: './workout-history.component.html',
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

    this.fitnessService.getFitnessHistory(this.user()!.userId, from, to)
      .subscribe({
        next: (data) => {
          this.history.set(data);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Failed to load history', err);
          this.loading.set(false);
        }
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

    // Position fixed tooltip relative to the viewport.
    // We place it at the bottom-right of the target element.
    // CSS handles RTL transform if needed, but here we just set top/left.
    this.tooltipData.set({
      attributes,
      x: rect.right,
      y: rect.bottom + 5 // Small vertical gap
    });
  }

  hideTooltip() {
    this.tooltipData.set(null);
  }

  private formatKey(key: string): string {
    // Splits camelCase and capitalizes first letter
    const result = key.replace(/([A-Z])/g, ' $1');
    return result.charAt(0).toUpperCase() + result.slice(1);
  }

  private calculateDateRange(range: DateRange): { from: string, to: string } {
    const toDate = new Date();
    const fromDate = new Date();

    switch (range) {
      case '24h':
        fromDate.setDate(toDate.getDate() - 1);
        break;
      case '7d':
        fromDate.setDate(toDate.getDate() - 7);
        break;
      case '30d':
        fromDate.setDate(toDate.getDate() - 30);
        break;
    }

    return {
      from: fromDate.toISOString().split('T')[0],
      to: toDate.toISOString().split('T')[0]
    };
  }
}
