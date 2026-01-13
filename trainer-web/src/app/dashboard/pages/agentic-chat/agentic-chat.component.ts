import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MarkdownModule } from 'ngx-markdown';
import { AuthService } from '../../../core/auth/auth.service';
import { TrainerService } from '../../../core/trainer.service';

@Component({
  selector: 'app-agentic-chat',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MarkdownModule],
  templateUrl: './agentic-chat.component.html',
})
export default class AgenticChatComponent {
  authService = inject(AuthService);
  private trainerService = inject(TrainerService);
  private fb = inject(FormBuilder);

  user = this.authService.currentUser;
  response = signal<string | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);

  form = this.fb.group({
    prompt: ['', Validators.required],
  });

  sendMessage() {
    if (this.form.valid && this.user()) {
      this.loading.set(true);
      this.error.set(null);
      this.response.set(null);
      const { prompt } = this.form.getRawValue();

      this.trainerService
        .chatWithTrainer({
          userId: this.user()!.userId!,
          prompt: prompt!,
        })
        .subscribe({
          next: (res) => {
            this.response.set(res);
            this.loading.set(false);
          },
          error: (err) => {
            console.error('Failed to get chat response', err);
            this.error.set('Failed to get response. Please try again later.');
            this.loading.set(false);
          },
        });
    }
  }
}
