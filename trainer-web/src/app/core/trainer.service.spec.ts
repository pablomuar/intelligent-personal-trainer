import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TrainerService } from './trainer.service';
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
});
