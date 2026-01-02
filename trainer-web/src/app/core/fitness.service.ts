import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface FitnessData {
  id?: string;
  userId: string;
  timestamp: string;
  totalSteps: number;
  averageHeartRate: number;
  totalCaloriesBurned: number;
}

@Injectable({
  providedIn: 'root',
})
export class FitnessService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.backendUrl}/data-persistence/fitness-data`;

  getFitnessHistory(userId: string, from: string, to: string): Observable<FitnessData[]> {
    const params = new HttpParams()
      .set('from', from)
      .set('to', to);

    return this.http.get<FitnessData[]>(`${this.apiUrl}/${userId}`, { params });
  }
}
