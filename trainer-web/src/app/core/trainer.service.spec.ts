import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TrainerService, TrainingPlanRequest, TrainingPlanResponse, TrainingPlanContent } from './trainer.service';
import { environment } from '../../environments/environment';

describe('TrainerService', () => {
  let service: TrainerService;
  let httpMock: HttpTestingController;
  const backendUrl = environment.backendUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TrainerService]
    });
    service = TestBed.inject(TrainerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should generate plan', () => {
    const request: TrainingPlanRequest = {
      userId: 'user1',
      prompt: 'Build muscle',
      daysHistory: 7
    };

    const mockResponse: TrainingPlanResponse = {
      userId: 'user1',
      originalPrompt: 'Build muscle',
      trainingPlan: {
        recommendation: 'Lift weights',
        analysis: 'Good goal',
        confidence: 'High',
        sessions: []
      }
    };

    service.generatePlan(request).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${backendUrl}/trainer/plan`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(mockResponse);
  });
});
