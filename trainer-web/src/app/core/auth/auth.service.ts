import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { User } from './user.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly USER_INFO = 'USER_INFO';
  private backendUrl = environment.backendUrl;

  currentUser = signal<User | null>(null);

  constructor(private http: HttpClient, private router: Router) {
    const user = localStorage.getItem(this.USER_INFO);
    if (user) {
      this.currentUser.set(JSON.parse(user));
    }
  }

  login(credentials: { username: string; password: string }): Observable<User> {
    return this.http.post<User>(`${this.backendUrl}/users/login`, credentials).pipe(
      tap((user) => {
        localStorage.setItem(this.USER_INFO, JSON.stringify(user));
        this.currentUser.set(user);
      })
    );
  }

  register(user: User): Observable<User> {
    return this.http.post<User>(`${this.backendUrl}/users`, user);
  }

  logout() {
    localStorage.removeItem(this.USER_INFO);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return !!this.currentUser();
  }
}
