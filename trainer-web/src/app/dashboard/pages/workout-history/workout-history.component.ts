import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/auth/auth.service';
import { FitnessService, FitnessData } from '../../../core/fitness.service';

type DateRange = '24h' | '7d' | '30d';

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

  private calculateDateRange(range: DateRange): { from: string, to: string } {
    const toDate = new Date();
    const fromDate = new Date();

    switch (range) {
      case '24h':
        // For 24h, we might want just today, or literally 24h ago.
        // Based on "YYYY-MM-DD" requirement, let's assume "today" or "yesterday + today".
        // Requirement says "Last 24h". Let's use today for simplicity as per common dashboard patterns,
        // or effectively same day if granularity is daily.
        // If granularity is daily, 'from' = today is safer for "today's stats".
        // Let's go with today for 'from' if it means "current status", or yesterday if it means strictly last 24h.
        // Let's assume 'from' = 1 day ago.
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
