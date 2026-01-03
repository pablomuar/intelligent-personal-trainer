import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FitnessService, FitnessData, WorkoutData } from '../../../core/fitness.service';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-workout-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './workout-register.component.html',
})
export default class WorkoutRegisterComponent {
  private fb = inject(FormBuilder);
  private fitnessService = inject(FitnessService);
  private authService = inject(AuthService);

  // Main form for FitnessData
  form = this.fb.group({
    timestamp: [new Date().toISOString().substring(0, 10), Validators.required],
    averageHeartRate: [null as number | null, [Validators.required, Validators.min(0)]],
    totalSteps: [null as number | null, [Validators.required, Validators.min(0)]],
    totalDistance: [null as number | null, [Validators.required, Validators.min(0)]],
    totalCaloriesBurned: [null as number | null, [Validators.required, Validators.min(0)]],
  });

  // Form for adding a new workout type
  workoutForm = this.fb.group({
    workoutType: ['', Validators.required],
  });

  // Form for adding attributes to the current workout
  attributeForm = this.fb.group({
    key: ['', Validators.required],
    value: ['', Validators.required],
  });

  // State to hold the list of workouts being added
  workoutList = signal<WorkoutData[]>([]);

  // State to hold attributes for the current workout being built
  currentAttributes = signal<{ [key: string]: string }>({});

  addAttribute() {
    if (this.attributeForm.valid) {
      const key = this.attributeForm.value.key!;
      const value = this.attributeForm.value.value!;

      this.currentAttributes.update(attrs => ({
        ...attrs,
        [key]: value
      }));

      this.attributeForm.reset();
    }
  }

  removeAttribute(key: string) {
    this.currentAttributes.update(attrs => {
      const newAttrs = { ...attrs };
      delete newAttrs[key];
      return newAttrs;
    });
  }

  addWorkout() {
    if (this.workoutForm.valid) {
      const workoutType = this.workoutForm.value.workoutType!;
      const attributes = this.currentAttributes();

      this.workoutList.update(list => [
        ...list,
        { workoutType, attributes }
      ]);

      // Reset workout form and attributes
      this.workoutForm.reset();
      this.currentAttributes.set({});
    }
  }

  removeWorkout(index: number) {
    this.workoutList.update(list => list.filter((_, i) => i !== index));
  }

  save() {
    if (this.form.valid) {
      const currentUser = this.authService.currentUser();
      if (!currentUser) {
        alert('User not logged in!');
        return;
      }

      const formValue = this.form.value;

      const fitnessData: FitnessData = {
        userId: currentUser.userId,
        timestamp: new Date(formValue.timestamp!).toISOString(),
        averageHeartRate: formValue.averageHeartRate!,
        totalSteps: formValue.totalSteps!,
        totalDistance: formValue.totalDistance!,
        totalCaloriesBurned: formValue.totalCaloriesBurned!,
        workoutDataList: this.workoutList()
      };

      this.fitnessService.saveFitnessData(fitnessData).subscribe({
        next: () => {
          alert('Fitness data saved successfully!');
          this.form.reset({
            timestamp: new Date().toISOString().substring(0, 10)
          });
          this.workoutList.set([]);
          this.currentAttributes.set({});
        },
        error: (err) => {
          console.error('Error saving fitness data', err);
          alert('Failed to save fitness data.');
        }
      });
    }
  }
}
