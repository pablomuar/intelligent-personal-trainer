import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';


@Component({
    selector: 'app-login',
    imports: [ReactiveFormsModule, RouterModule],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export default class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  error = signal<string | null>(null);

  login() {
    if (this.form.valid) {
      this.error.set(null);
      const { username, password } = this.form.getRawValue();
      this.authService.login({ username: username!, password: password! }).subscribe({
        next: () => this.router.navigate(['/dashboard']),
        error: (err) => {
          console.error('Login failed', err);
          this.error.set('Login failed. Please check your credentials and try again.');
        },
      });
    }
  }
}
