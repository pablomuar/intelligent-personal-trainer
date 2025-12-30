import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
// import { environment } from '../../environments/environment'; // Bypassed for debugging

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
  // Forcing the correct URL to bypass local cache issues.
  private backendUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  generatePlan(request: TrainingPlanRequest): Observable<TrainingPlanResponse> {
    return this.http.post<TrainingPlanResponse>(`${this.backendUrl}/trainer/plan`, request);
  }
}
