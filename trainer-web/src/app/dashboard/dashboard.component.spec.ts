import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';
import DashboardComponent from './dashboard.component';
import { signal, Directive, Input, Component } from '@angular/core';

@Directive({
  selector: '[routerLink]',
  standalone: true
})
class RouterLinkStubDirective {
  @Input('routerLink') linkParams: any;
  @Input() routerLinkActive: any;
}

@Component({
  selector: 'router-outlet',
  standalone: true,
  template: ''
})
class RouterOutletStubComponent {}

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const aSpy = jasmine.createSpyObj('AuthService', ['logout', 'currentUser']);
    const rSpy = jasmine.createSpyObj('Router', ['navigate']);

    aSpy.currentUser = signal({ username: 'test', name: 'Test', surname: 'User' } as any);

    await TestBed.configureTestingModule({
      imports: [DashboardComponent, RouterLinkStubDirective, RouterOutletStubComponent],
      providers: [
        { provide: AuthService, useValue: aSpy },
        { provide: Router, useValue: rSpy },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => null } } } }
      ]
    })
    .overrideComponent(DashboardComponent, {
      remove: { imports: [RouterModule] },
      add: { imports: [RouterLinkStubDirective, RouterOutletStubComponent] }
    })
    .compileComponents();

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display user name', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('test');
  });

  it('should logout', () => {
    component.logout();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  });
});
