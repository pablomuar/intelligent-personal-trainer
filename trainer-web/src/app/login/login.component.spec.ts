import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';
import { of, throwError } from 'rxjs';
import LoginComponent from './login.component';
import { Directive, Input } from '@angular/core';

@Directive({
  selector: '[routerLink]',
  standalone: true
})
class RouterLinkStubDirective {
  @Input('routerLink') linkParams: any;
  @Input() queryParams: any;
}

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const aSpy = jasmine.createSpyObj('AuthService', ['login']);
    const rSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule, RouterLinkStubDirective],
      providers: [
        { provide: AuthService, useValue: aSpy },
        { provide: Router, useValue: rSpy },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => null } } } }
      ]
    })
    .overrideComponent(LoginComponent, {
      remove: { imports: [RouterModule] },
      add: { imports: [RouterLinkStubDirective] }
    })
    .compileComponents();

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form initially', () => {
    expect(component.form.valid).toBeFalse();
  });

  it('should validate inputs', () => {
    const username = component.form.get('username');
    const password = component.form.get('password');

    username?.setValue('');
    password?.setValue('');
    expect(username?.valid).toBeFalse();
    expect(password?.valid).toBeFalse();

    username?.setValue('user');
    password?.setValue('pass');
    expect(username?.valid).toBeTrue();
    expect(password?.valid).toBeTrue();
  });

  it('should login and redirect on success', () => {
    component.form.setValue({ username: 'user', password: 'pass' });
    authServiceSpy.login.and.returnValue(of({} as any));

    component.login();

    expect(authServiceSpy.login).toHaveBeenCalledWith({ username: 'user', password: 'pass' });
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/dashboard']);
    expect(component.error()).toBeNull();
  });

  it('should handle login error', () => {
    component.form.setValue({ username: 'user', password: 'pass' });
    authServiceSpy.login.and.returnValue(throwError(() => new Error('Failed')));

    component.login();

    expect(authServiceSpy.login).toHaveBeenCalled();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
    expect(component.error()).toContain('Login failed');
  });
});
