import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../core/auth/auth.service';
import { TrainerService, TrainingPlanResponse } from '../core/trainer.service';
import { User } from '../core/auth/user.model';
import { MarkdownComponent } from 'ngx-markdown';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [ReactiveFormsModule, MarkdownComponent, CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export default class DashboardComponent {
  authService = inject(AuthService);
  private trainerService = inject(TrainerService);
  private fb = inject(FormBuilder);

  user = this.authService.currentUser;
  plan = signal<string | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);

  form = this.fb.group({
    prompt: ['', Validators.required],
  });

  generatePlan() {
    if (this.form.valid && this.user()) {
      this.loading.set(true);
      this.error.set(null);
      const { prompt } = this.form.getRawValue();
      this.trainerService
        .generatePlan({
          userId: this.user()!.userId,
          prompt: prompt!,
          daysHistory: 7,
        })
        .subscribe({
          next: (response) => {
            this.plan.set(response.plan);
            this.loading.set(false);
          },
          error: (err) => {
            console.error('Failed to generate plan', err);
            this.error.set('Failed to generate plan. Please try again later.');
            this.loading.set(false);
          },
        });
    }
  }

  logout() {
    this.authService.logout();
  }
}
