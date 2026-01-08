import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { User } from './user.model';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let routerSpy: jasmine.SpyObj<Router>;
  const backendUrl = environment.backendUrl;

  beforeEach(() => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService,
        { provide: Router, useValue: routerSpy }
      ]
    });

    localStorage.removeItem('USER_INFO');
  });

  afterEach(() => {
    if (httpMock) httpMock.verify();
    localStorage.removeItem('USER_INFO');
  });

  it('should be created', () => {
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    expect(service).toBeTruthy();
  });

  it('should initialize currentUser from localStorage', () => {
    const mockUser: User = { username: 'test', password: 'pwd', name: 'Test', surname: 'User', age: 25, height: 180, weight: 75, gender: 'MALE', lifestyle: 'SEDENTARY', diseases: [] };
    localStorage.setItem('USER_INFO', JSON.stringify(mockUser));

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    expect(service.currentUser()).toEqual(mockUser);
  });

  it('should login successfully', () => {
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

    const mockUser: User = { username: 'test', password: 'pwd', name: 'Test', surname: 'User', age: 25, height: 180, weight: 75, gender: 'MALE', lifestyle: 'SEDENTARY', diseases: [] };
    const credentials = { username: 'test', password: 'pwd' };

    service.login(credentials).subscribe(user => {
      expect(user).toEqual(mockUser);
      expect(service.currentUser()).toEqual(mockUser);
      expect(localStorage.getItem('USER_INFO')).toEqual(JSON.stringify(mockUser));
    });

    const req = httpMock.expectOne(`${backendUrl}/users/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(credentials);
    req.flush(mockUser);
  });

  it('should register successfully', () => {
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

    const mockUser: User = { username: 'test', password: 'pwd', name: 'Test', surname: 'User', age: 25, height: 180, weight: 75, gender: 'MALE', lifestyle: 'SEDENTARY', diseases: [] };

    service.register(mockUser).subscribe(user => {
      expect(user).toEqual(mockUser);
    });

    const req = httpMock.expectOne(`${backendUrl}/users`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockUser);
    req.flush(mockUser);
  });

  it('should logout successfully', () => {
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

    const mockUser: User = { username: 'test', password: 'pwd', name: 'Test', surname: 'User', age: 25, height: 180, weight: 75, gender: 'MALE', lifestyle: 'SEDENTARY', diseases: [] };
    localStorage.setItem('USER_INFO', JSON.stringify(mockUser));
    service.currentUser.set(mockUser);

    service.logout();

    expect(service.currentUser()).toBeNull();
    expect(localStorage.getItem('USER_INFO')).toBeNull();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should check authentication status', () => {
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

    expect(service.isAuthenticated()).toBeFalse();

    const mockUser: User = { username: 'test', password: 'pwd', name: 'Test', surname: 'User', age: 25, height: 180, weight: 75, gender: 'MALE', lifestyle: 'SEDENTARY', diseases: [] };
    service.currentUser.set(mockUser);

    expect(service.isAuthenticated()).toBeTrue();
  });
});
