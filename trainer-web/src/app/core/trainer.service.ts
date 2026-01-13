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

export interface ChatRequest {
  userId: string;
  prompt: string;
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

  chatWithTrainer(request: ChatRequest): Observable<string> {
    return this.http.post(`${this.backendUrl}/trainer/chat`, request, { responseType: 'text' });
  }
}
