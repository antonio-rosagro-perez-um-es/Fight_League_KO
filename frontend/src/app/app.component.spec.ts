import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app.component';
import { AuthService } from './core/auth.service';
import { NotificationService } from './core/notification.service';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            role: () => null,
            authenticated: () => false,
            user: () => null,
            logout: jasmine.createSpy('logout'),
          },
        },
        {
          provide: NotificationService,
          useValue: { message: () => null, clear: jasmine.createSpy('clear') },
        },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should close menus', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;

    app.toggleDropdown();
    app.toggleMobileMenu();
    app.closeMenus();

    expect(app.dropdownOpen()).toBeFalse();
    expect(app.mobileMenuOpen()).toBeFalse();
  });
});
