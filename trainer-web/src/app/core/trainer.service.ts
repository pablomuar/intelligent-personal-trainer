import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface TrainingPlanRequest {
  userId: string;
  prompt: string;
  daysHistory: number;
}

export interface PlanSession {
  day: string;
  duration: string;
  intensity: string;
  sessionDescription: string;
}

export interface TrainingPlanContent {
  recommendation: string;
  analysis: string;
  confidence: string;
  sessions: PlanSession[];
}

export interface TrainingPlanResponse {
  userId: string;
  originalPrompt: string;
  trainingPlan: TrainingPlanContent;
}

export interface LlmResponse {
  chatMessage: string;
  sessions: PlanSession[] | null;
  type: 'CHAT_ONLY' | 'PLAN_GENERATED';
}

export interface ChatRequest {
  userId: string;
  prompt: string;
  conversationId?: number;
}

export interface ChatResponse {
  conversationId: number;
  title: string;
  response: LlmResponse;
}

export interface Conversation {
  id: number;
  title: string;
  lastMessageAt: string;
}

export interface ChatMessage {
  id: number;
  role: 'USER' | 'ASSISTANT';
  content: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class TrainerService {
  private backendUrl = environment.backendUrl;

  constructor(private http: HttpClient) {}

  generatePlan(request: TrainingPlanRequest): Observable<TrainingPlanResponse> {
    return this.http.post<TrainingPlanResponse>(`${this.backendUrl}/trainer/plan`, request);
  }

  chatWithTrainer(request: ChatRequest): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(`${this.backendUrl}/trainer/chat`, request);
  }

  getConversations(userId: string): Observable<Conversation[]> {
    return this.http.get<Conversation[]>(`${this.backendUrl}/trainer/chat/conversations`, { params: { userId } });
  }

  getConversationMessages(conversationId: number): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.backendUrl}/trainer/chat/conversations/${conversationId}/messages`);
  }

  deleteConversation(conversationId: number): Observable<void> {
    return this.http.delete<void>(`${this.backendUrl}/trainer/chat/conversations/${conversationId}`);
  }
}
