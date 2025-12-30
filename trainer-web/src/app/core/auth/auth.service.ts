import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { User } from './user.model';
// import { environment } from '../../../environments/environment'; // Bypassed for debugging

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly USER_INFO = 'USER_INFO';
  // Forcing the correct URL to bypass local cache issues.
  private backendUrl = 'http://localhost:8080';

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

  logout() {
    localStorage.removeItem(this.USER_INFO);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return !!this.currentUser();
  }
}
