import { Component, inject, signal, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MarkdownModule } from 'ngx-markdown';
import { AuthService } from '../../../core/auth/auth.service';
import { TrainerService, Conversation, ChatMessage, LlmResponse } from '../../../core/trainer.service';

@Component({
  selector: 'app-agentic-chat',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MarkdownModule],
  templateUrl: './agentic-chat.component.html',
})
export default class AgenticChatComponent implements OnInit, AfterViewChecked {
  authService = inject(AuthService);
  private trainerService = inject(TrainerService);
  private fb = inject(FormBuilder);

  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  user = this.authService.currentUser;
  conversations = signal<Conversation[]>([]);
  messages = signal<ChatMessage[]>([]);
  currentConversationId = signal<number | null>(null);

  loading = signal(false);
  error = signal<string | null>(null);

  form = this.fb.group({
    prompt: ['', Validators.required],
  });

  private shouldScrollToBottom = false;
  private userLocation: string | null = null;

  ngOnInit() {
    this.loadConversations();
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          this.userLocation = `${position.coords.latitude},${position.coords.longitude}`;
        },
        (error) => {
          console.warn('Geolocation permission denied or failed', error);
          this.userLocation = null;
        }
      );
    }
  }

  ngAfterViewChecked() {
    if (this.shouldScrollToBottom) {
      this.scrollToBottom();
      this.shouldScrollToBottom = false;
    }
  }

  scrollToBottom(): void {
    try {
      this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
    } catch(err) { }
  }

  loadConversations() {
    if (this.user()) {
      this.trainerService.getConversations(this.user()!.userId!)
        .subscribe({
          next: (data) => this.conversations.set(data),
          error: (err) => console.error('Failed to load conversations', err)
        });
    }
  }

  selectConversation(conversation: Conversation) {
    this.currentConversationId.set(conversation.id);
    this.loading.set(true);
    this.error.set(null);
    this.messages.set([]); // Clear previous while loading

    this.trainerService.getConversationMessages(conversation.id)
      .subscribe({
        next: (msgs) => {
          this.messages.set(msgs);
          this.loading.set(false);
          this.shouldScrollToBottom = true;
        },
        error: (err) => {
          console.error('Failed to load messages', err);
          this.error.set('Failed to load conversation.');
          this.loading.set(false);
        }
      });
  }

  deleteConversation(conversationId: number, event: Event) {
    event.stopPropagation();

    if (confirm('Are you sure you want to delete this conversation?')) {
        this.trainerService.deleteConversation(conversationId).subscribe({
            next: () => {
                this.conversations.update(prev => prev.filter(c => c.id !== conversationId));
                if (this.currentConversationId() === conversationId) {
                    this.startNewChat();
                }
            },
            error: (err) => console.error('Failed to delete conversation', err)
        });
    }
  }

  startNewChat() {
    this.currentConversationId.set(null);
    this.messages.set([]);
    this.form.reset();
    this.error.set(null);
  }

  sendMessage() {
    if (this.form.valid && this.user()) {
      const { prompt } = this.form.getRawValue();
      if (!prompt) return;

      this.loading.set(true);
      this.error.set(null);

      // Optimistic update for UI
      const optimisticMsg: ChatMessage = {
          id: -1,
          role: 'USER',
          content: prompt,
          createdAt: new Date().toISOString()
      };

      // We append to current messages
      this.messages.update(prev => [...prev, optimisticMsg]);
      this.form.reset();
      this.shouldScrollToBottom = true;

      this.trainerService
        .chatWithTrainer({
          userId: this.user()!.userId!,
          prompt: prompt,
          conversationId: this.currentConversationId() || undefined,
          userLocation: this.userLocation || undefined
        })
        .subscribe({
          next: (res) => {
            // If it was a new conversation, set ID
            if (!this.currentConversationId()) {
                this.currentConversationId.set(res.conversationId);
                this.loadConversations(); // Refresh sidebar for title
            } else {
                // If existing, just refresh sidebar to update "last message" timestamp/order
                this.loadConversations();
            }

            const aiMsg: ChatMessage = {
                id: Math.random(), // Temporary ID until reload
                role: 'ASSISTANT',
                content: JSON.stringify(res.response),
                createdAt: new Date().toISOString()
            };

            this.messages.update(prev => [...prev, aiMsg]);

            this.loading.set(false);
            this.shouldScrollToBottom = true;
          },
          error: (err) => {
            console.error('Failed to get chat response', err);
            this.error.set('Failed to get response. Please try again later.');
            this.loading.set(false);
          },
        });
    }
  }

  parseLlmResponse(content: string): LlmResponse | null {
    try {
      return JSON.parse(content);
    } catch (e) {
      console.error('Failed to parse LlmResponse', e);
      return null;
    }
  }

  formatIntensity(intensity: string): string {
    return intensity.replace(/_/g, ' ');
  }
}
