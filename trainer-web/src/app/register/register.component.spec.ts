import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';
import { of, throwError } from 'rxjs';
import RegisterComponent from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const aSpy = jasmine.createSpyObj('AuthService', ['register', 'login']);
    const rSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [RegisterComponent, ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: aSpy },
        { provide: Router, useValue: rSpy }
      ]
    }).compileComponents();

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form initially', () => {
    expect(component.form.valid).toBeFalse();
  });

  it('should validate required fields', () => {
    const username = component.form.get('username');
    expect(username?.valid).toBeFalse();
    username?.setValue('test');
    expect(username?.valid).toBeTrue();
  });

  it('should submit form when valid and redirect on success', () => {
    component.form.patchValue({
      username: 'test',
      password: 'password',
      name: 'Test',
      surname: 'User',
      age: 25,
      height: 180,
      weight: 75,
      gender: 'MALE',
      lifestyle: 'SEDENTARY'
    });

    authServiceSpy.register.and.returnValue(of({} as any));
    authServiceSpy.login.and.returnValue(of({} as any));

    component.onSubmit();

    expect(authServiceSpy.register).toHaveBeenCalled();
    expect(authServiceSpy.login).toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should handle registration error', () => {
    component.form.patchValue({
      username: 'test',
      password: 'password',
      name: 'Test',
      surname: 'User',
      age: 25,
      height: 180,
      weight: 75,
      gender: 'MALE',
      lifestyle: 'SEDENTARY'
    });

    authServiceSpy.register.and.returnValue(throwError(() => new Error('Failed')));

    component.onSubmit();

    expect(authServiceSpy.register).toHaveBeenCalled();
    expect(component.error()).toBe('Registration failed. Please try again.');
  });
});
