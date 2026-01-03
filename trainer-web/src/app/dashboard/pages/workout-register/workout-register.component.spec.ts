import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import WorkoutRegisterComponent from './workout-register.component';
import { FitnessService, FitnessData } from '../../../core/fitness.service';
import { AuthService } from '../../../core/auth/auth.service';
import { of } from 'rxjs';

describe('WorkoutRegisterComponent', () => {
  let component: WorkoutRegisterComponent;
  let fixture: ComponentFixture<WorkoutRegisterComponent>;
  let fitnessServiceSpy: jasmine.SpyObj<FitnessService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const fSpy = jasmine.createSpyObj('FitnessService', ['saveFitnessData', 'getFitnessHistory']);
    const aSpy = jasmine.createSpyObj('AuthService', ['currentUser']);

    await TestBed.configureTestingModule({
      imports: [WorkoutRegisterComponent, HttpClientTestingModule, ReactiveFormsModule],
      providers: [
        { provide: FitnessService, useValue: fSpy },
        { provide: AuthService, useValue: aSpy }
      ]
    })
    .compileComponents();

    fitnessServiceSpy = TestBed.inject(FitnessService) as jasmine.SpyObj<FitnessService>;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;

    // Mock user
    authServiceSpy.currentUser.and.returnValue({
      userId: 'test-user',
      username: 'Test User',
      age: 30,
      height: 180,
      weight: 75,
      gender: 'MALE',
      diseases: []
    });

    // Default mock for getFitnessHistory to return empty list
    fitnessServiceSpy.getFitnessHistory.and.returnValue(of([]));

    fixture = TestBed.createComponent(WorkoutRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add a workout to the list', () => {
    component.workoutForm.setValue({ workoutType: 'Running' });
    component.attributeForm.setValue({ key: 'Duration', value: '30m' });
    component.addAttribute();
    component.addWorkout();

    expect(component.workoutList().length).toBe(1);
    expect(component.workoutList()[0].workoutType).toBe('Running');
    expect(component.workoutList()[0].attributes['Duration']).toBe('30m');
  });

  it('should save fitness data and show success message', fakeAsync(() => {
    component.form.patchValue({
      timestamp: '2023-10-27',
      averageHeartRate: 120,
      totalSteps: 5000,
      totalDistance: 5,
      totalCaloriesBurned: 300
    });

    fitnessServiceSpy.saveFitnessData.and.returnValue(of(void 0));

    component.save();

    expect(fitnessServiceSpy.saveFitnessData).toHaveBeenCalled();
    const args = fitnessServiceSpy.saveFitnessData.calls.mostRecent().args[0];
    expect(args.userId).toBe('test-user');
    expect(args.averageHeartRate).toBe(120);

    expect(component.successMessage()).toBe('Fitness data saved successfully!');

    tick(3000);
    expect(component.successMessage()).toBeNull();
  }));

  it('should load data when timestamp changes', () => {
    const mockData: FitnessData = {
      userId: 'test-user',
      timestamp: '2023-10-27T00:00:00.000Z',
      averageHeartRate: 150,
      totalSteps: 8000,
      totalDistance: 7.5,
      totalCaloriesBurned: 600,
      workoutDataList: []
    };

    fitnessServiceSpy.getFitnessHistory.and.returnValue(of([mockData]));

    // Trigger value change
    component.form.get('timestamp')?.setValue('2023-10-27');

    expect(fitnessServiceSpy.getFitnessHistory).toHaveBeenCalledWith('test-user', '2023-10-27', '2023-10-27');
    expect(component.form.get('averageHeartRate')?.value).toBe(150);
    expect(component.form.get('totalSteps')?.value).toBe(8000);
  });

  it('should clear form when no data exists for date', () => {
    // Setup initial state
    component.form.patchValue({
      averageHeartRate: 150
    });

    fitnessServiceSpy.getFitnessHistory.and.returnValue(of([]));

    // Trigger value change
    component.form.get('timestamp')?.setValue('2023-10-28');

    expect(fitnessServiceSpy.getFitnessHistory).toHaveBeenCalledWith('test-user', '2023-10-28', '2023-10-28');
    expect(component.form.get('averageHeartRate')?.value).toBeNull();
  });
});
