import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-workout-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './workout-register.component.html',
})
export default class WorkoutRegisterComponent {
  private fb = inject(FormBuilder);

  form = this.fb.group({
    activityType: ['', Validators.required],
    duration: [null, [Validators.required, Validators.min(1)]],
    calories: [null, [Validators.required, Validators.min(1)]],
  });

  save() {
    if (this.form.valid) {
      console.log('Workout Data:', this.form.value);
      alert('Workout saved locally! (Check console)');
      this.form.reset();
    }
  }
}
