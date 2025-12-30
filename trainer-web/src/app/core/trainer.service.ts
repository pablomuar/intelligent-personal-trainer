import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface TrainingPlanRequest {
  userId: string;
  prompt: string;
  daysHistory: number;
}

export interface TrainingPlanResponse {
  plan: string;
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
}
