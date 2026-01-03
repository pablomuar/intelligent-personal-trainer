import {HttpClient, HttpParams} from '@angular/common/http';
import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';

export interface WorkoutData {
  workoutType: string;
  attributes: { [key: string]: string };
}

export interface FitnessData {
  id?: string;
  userId: string;
  timestamp: string;
  totalSteps: number;
  averageHeartRate: number;
  totalCaloriesBurned: number;
  totalDistance?: number;
  workoutDataList?: WorkoutData[];
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

  saveFitnessData(data: FitnessData): Observable<void> {
    return this.http.post<void>(this.apiUrl, data);
  }
}
