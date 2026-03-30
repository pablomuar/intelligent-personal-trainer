import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, FormArray } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';

import { User } from '../core/auth/user.model';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [ReactiveFormsModule],
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css']
})
export default class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  error = signal<string | null>(null);

  form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
    name: ['', Validators.required],
    surname: ['', Validators.required],
    age: [null as number | null, [Validators.required, Validators.min(0)]],
    height: [null as number | null, [Validators.required, Validators.min(0)]],
    weight: [null as number | null, [Validators.required, Validators.min(0)]],
    gender: ['', Validators.required],
    lifestyle: ['', Validators.required],
    dataSourceId: [''],
    externalSourceUserId: [''],
    diseases: this.fb.array([])
  });

  get diseases() {
    return this.form.get('diseases') as FormArray;
  }

  addDisease() {
    this.diseases.push(this.fb.control('', Validators.required));
  }

  removeDisease(index: number) {
    this.diseases.removeAt(index);
  }

  goBack() {
    this.router.navigate(['/login']);
  }

  onSubmit() {
    if (this.form.valid) {
      this.error.set(null);
      const formValue = this.form.getRawValue();

      const user: User = {
        username: formValue.username!,
        password: formValue.password!,
        name: formValue.name!,
        surname: formValue.surname!,
        age: formValue.age!,
        height: formValue.height!,
        weight: formValue.weight!,
        gender: formValue.gender!,
        lifestyle: formValue.lifestyle!,
        dataSourceId: formValue.dataSourceId || undefined,
        externalSourceUserId: formValue.externalSourceUserId || undefined,
        diseases: (formValue.diseases as string[]).filter(d => !!d)
      };

      this.authService.register(user).subscribe({
        next: () => {
          // Auto login after successful registration
          this.authService.login({ username: user.username, password: user.password! }).subscribe({
            next: () => this.router.navigate(['/dashboard']),
            error: (err) => {
               console.error('Login after registration failed', err);
               this.error.set('Registration successful but auto-login failed. Please sign in manually.');
               setTimeout(() => this.router.navigate(['/login']), 2000);
            }
          });
        },
        error: (err) => {
          console.error('Registration failed', err);
          this.error.set('Registration failed. Please try again.');
        }
      });
    } else {
        this.form.markAllAsTouched();
    }
  }
}
