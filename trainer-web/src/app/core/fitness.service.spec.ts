import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FitnessService, FitnessData } from './fitness.service';
import { environment } from '../../environments/environment';

describe('FitnessService', () => {
  let service: FitnessService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.backendUrl}/data-persistence/fitness-data`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [FitnessService]
    });
    service = TestBed.inject(FitnessService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get fitness history', () => {
    const mockData: FitnessData[] = [
      { userId: 'user1', timestamp: '2023-10-27', totalSteps: 1000, averageHeartRate: 80, totalCaloriesBurned: 200 }
    ];
    const userId = 'user1';
    const from = '2023-10-01';
    const to = '2023-10-30';

    service.getFitnessHistory(userId, from, to).subscribe(data => {
      expect(data).toEqual(mockData);
    });

    const req = httpMock.expectOne(req =>
      req.url === `${apiUrl}/${userId}` &&
      req.params.get('from') === from &&
      req.params.get('to') === to
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockData);
  });

  it('should save fitness data', () => {
    const data: FitnessData = {
      userId: 'user1',
      timestamp: '2023-10-27',
      totalSteps: 1000,
      averageHeartRate: 80,
      totalCaloriesBurned: 200
    };

    service.saveFitnessData(data).subscribe(response => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(data);
    req.flush(null);
  });
});
