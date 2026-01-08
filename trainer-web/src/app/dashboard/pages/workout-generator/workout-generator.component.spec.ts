import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/auth/auth.service';
import { TrainerService, TrainingPlanContent } from '../../../core/trainer.service';
import WorkoutGeneratorComponent from './workout-generator.component';
import { of, throwError } from 'rxjs';
import { signal } from '@angular/core';

describe('WorkoutGeneratorComponent', () => {
  let component: WorkoutGeneratorComponent;
  let fixture: ComponentFixture<WorkoutGeneratorComponent>;
  let trainerServiceSpy: jasmine.SpyObj<TrainerService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const tSpy = jasmine.createSpyObj('TrainerService', ['generatePlan']);
    const aSpy = jasmine.createSpyObj('AuthService', ['currentUser']);

    aSpy.currentUser = signal({ userId: 'u1' } as any);

    await TestBed.configureTestingModule({
      imports: [WorkoutGeneratorComponent, ReactiveFormsModule],
      providers: [
        { provide: TrainerService, useValue: tSpy },
        { provide: AuthService, useValue: aSpy }
      ]
    }).compileComponents();

    trainerServiceSpy = TestBed.inject(TrainerService) as jasmine.SpyObj<TrainerService>;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;

    fixture = TestBed.createComponent(WorkoutGeneratorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate prompt', () => {
    expect(component.form.valid).toBeFalse();
    component.form.setValue({ prompt: 'Test' });
    expect(component.form.valid).toBeTrue();
  });

  it('should generate plan successfully', () => {
    const mockPlan: TrainingPlanContent = {
      recommendation: 'Rec',
      analysis: 'Anal',
      confidence: 'High',
      sessions: []
    };

    trainerServiceSpy.generatePlan.and.returnValue(of({
      userId: 'u1',
      originalPrompt: 'Test',
      trainingPlan: mockPlan
    }));

    component.form.setValue({ prompt: 'Test' });
    component.generatePlan();

    expect(trainerServiceSpy.generatePlan).toHaveBeenCalled();
    expect(component.plan()).toEqual(mockPlan);
    expect(component.loading()).toBeFalse();
    expect(component.error()).toBeNull();
  });

  it('should handle generation error', () => {
    trainerServiceSpy.generatePlan.and.returnValue(throwError(() => new Error('Err')));

    component.form.setValue({ prompt: 'Test' });
    component.generatePlan();

    expect(component.plan()).toBeNull();
    expect(component.loading()).toBeFalse();
    expect(component.error()).toContain('Failed to generate plan');
  });

  it('should format intensity', () => {
    expect(component.formatIntensity('VERY_HIGH')).toBe('Very-High');
    expect(component.formatIntensity('')).toBe('');
  });
});
