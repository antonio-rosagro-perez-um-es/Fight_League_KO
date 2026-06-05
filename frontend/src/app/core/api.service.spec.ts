import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { ApiService } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('gets fighter banners from public endpoint', () => {
    service.getFighterBanners().subscribe((fighters) => expect(fighters).toEqual([]));

    const request = http.expectOne('/api/fighters/all-banners');
    expect(request.request.method).toBe('GET');
    request.flush([]);
  });

  it('searches community combos with pagination params', () => {
    service.searchCommunityCombos({ fuse: 'SIDEKICK' }, 2, 20).subscribe();

    const request = http.expectOne((req) => req.url === '/api/combos/search');
    expect(request.request.method).toBe('POST');
    expect(request.request.params.get('page')).toBe('2');
    expect(request.request.params.get('size')).toBe('20');
    expect(request.request.body).toEqual({ fuse: 'SIDEKICK' });
    request.flush({ content: [], totalPages: 0, totalElements: 0, number: 2, size: 20, first: true, last: true });
  });

  it('sets game winner using game and user ids', () => {
    service.setGameWinner('game-1', 'user-1').subscribe();

    const request = http.expectOne('/api/games/game-1/winner/user-1');
    expect(request.request.method).toBe('PATCH');
    request.flush(null);
  });
});
