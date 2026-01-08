import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FitnessService, FitnessData } from '../../../core/fitness.service';
import { AuthService } from '../../../core/auth/auth.service';
import WorkoutHistoryComponent from './workout-history.component';
import { of, throwError } from 'rxjs';
import { signal } from '@angular/core';

describe('WorkoutHistoryComponent', () => {
  let component: WorkoutHistoryComponent;
  let fixture: ComponentFixture<WorkoutHistoryComponent>;
  let fitnessServiceSpy: jasmine.SpyObj<FitnessService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const fSpy = jasmine.createSpyObj('FitnessService', ['getFitnessHistory']);
    const aSpy = jasmine.createSpyObj('AuthService', ['currentUser']);

    aSpy.currentUser = signal({ userId: 'u1' } as any);

    await TestBed.configureTestingModule({
      imports: [WorkoutHistoryComponent],
      providers: [
        { provide: FitnessService, useValue: fSpy },
        { provide: AuthService, useValue: aSpy }
      ]
    }).compileComponents();

    fitnessServiceSpy = TestBed.inject(FitnessService) as jasmine.SpyObj<FitnessService>;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;

    fitnessServiceSpy.getFitnessHistory.and.returnValue(of([]));

    fixture = TestBed.createComponent(WorkoutHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load data on init', () => {
    expect(fitnessServiceSpy.getFitnessHistory).toHaveBeenCalled();
  });

  it('should update range and reload data', () => {
    component.setRange('30d');
    expect(component.activeRange()).toBe('30d');
    expect(fitnessServiceSpy.getFitnessHistory).toHaveBeenCalledTimes(2);
  });

  it('should handle data loading error', () => {
    fitnessServiceSpy.getFitnessHistory.and.returnValue(throwError(() => new Error('Err')));
    component.loadData('7d');
    expect(component.loading()).toBeFalse();
  });

  it('should format attributes list', () => {
    const attrs = { duration: '30m', heartRate: '120' };
    const list = component.getAttributesList(attrs);
    expect(list.length).toBe(2);
    expect(list[0].key).toBe('Duration');
    expect(list[1].key).toBe('Heart Rate');
  });

  it('should show tooltip', () => {
    const mockEvent = {
      currentTarget: {
        getBoundingClientRect: () => ({ right: 100, bottom: 200 })
      }
    } as any;

    component.showTooltip(mockEvent, { key: 'val' });

    const data = component.tooltipData();
    expect(data).toBeTruthy();
    expect(data?.x).toBe(100);
    expect(data?.y).toBe(205);
  });

  it('should hide tooltip', () => {
    component.hideTooltip();
    expect(component.tooltipData()).toBeNull();
  });
});
