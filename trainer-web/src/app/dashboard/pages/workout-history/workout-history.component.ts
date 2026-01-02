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

  formatWorkoutAttributes(attributes: { [key: string]: string }): string {
    if (!attributes) return '';
    const parts = [];

    // Priorizamos duración y calorías si existen en el mapa
    if (attributes['durationMinutes']) {
      // Redondeamos los minutos para que quede limpio
      const mins = Math.round(parseFloat(attributes['durationMinutes']));
      parts.push(`${mins} min`);
    }
    if (attributes['caloriesBurned']) {
      parts.push(`${attributes['caloriesBurned']} kcal`);
    }

    return parts.length > 0 ? `(${parts.join(', ')})` : '';
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
