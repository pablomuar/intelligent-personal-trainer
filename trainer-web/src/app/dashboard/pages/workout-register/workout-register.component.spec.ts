import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import WorkoutRegisterComponent from './workout-register.component';
import { FitnessService } from '../../../core/fitness.service';
import { AuthService } from '../../../core/auth/auth.service';
import { of } from 'rxjs';

describe('WorkoutRegisterComponent', () => {
  let component: WorkoutRegisterComponent;
  let fixture: ComponentFixture<WorkoutRegisterComponent>;
  let fitnessServiceSpy: jasmine.SpyObj<FitnessService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const fSpy = jasmine.createSpyObj('FitnessService', ['saveFitnessData']);
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

  it('should save fitness data', () => {
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
  });
});
