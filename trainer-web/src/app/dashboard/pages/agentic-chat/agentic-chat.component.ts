import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MarkdownModule } from 'ngx-markdown';
import { AuthService } from '../../../core/auth/auth.service';
import { TrainerService, ChatHistoryItem } from '../../../core/trainer.service';

@Component({
  selector: 'app-agentic-chat',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MarkdownModule],
  templateUrl: './agentic-chat.component.html',
})
export default class AgenticChatComponent implements OnInit {
  authService = inject(AuthService);
  private trainerService = inject(TrainerService);
  private fb = inject(FormBuilder);

  user = this.authService.currentUser;
  response = signal<string | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);
  history = signal<ChatHistoryItem[]>([]);
  isSidebarCollapsed = signal(false);

  form = this.fb.group({
    prompt: ['', Validators.required],
  });

  ngOnInit() {
    this.loadHistory();
  }

  loadHistory() {
    if (this.user()) {
      // Default last 30 days
      const to = new Date().toISOString().split('T')[0];
      const fromDate = new Date();
      fromDate.setDate(fromDate.getDate() - 30);
      const from = fromDate.toISOString().split('T')[0];

      this.trainerService.getChatHistory(this.user()!.userId!, from, to)
        .subscribe({
          next: (data) => this.history.set(data),
          error: (err) => console.error('Failed to load history', err)
        });
    }
  }

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
            this.loadHistory();
          },
          error: (err) => {
            console.error('Failed to get chat response', err);
            this.error.set('Failed to get response. Please try again later.');
            this.loading.set(false);
          },
        });
    }
  }

  selectHistoryItem(item: ChatHistoryItem) {
    this.form.patchValue({ prompt: item.prompt });
    this.response.set(item.response);
    this.error.set(null);
  }

  startNewChat() {
    this.form.reset();
    this.response.set(null);
    this.error.set(null);
  }

  toggleSidebar() {
    this.isSidebarCollapsed.update((v) => !v);
  }
}
