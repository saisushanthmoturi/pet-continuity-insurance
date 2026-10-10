import { TestBed } from "@angular/core/testing";
import { Router } from "@angular/router";
import { authGuard } from "./authGuard";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";

describe("authGuard", () => {
  let router: Router;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        {
          provide: Router,
          useValue: {
            navigate: vi.fn()
          }
        }
      ]
    });
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it("should allow activation when user is logged in", () => {
    localStorage.setItem("currentUser", JSON.stringify({ email: "user@example.com" }));

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );

    expect(result).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it("should block activation and navigate to login when user is not logged in", () => {
    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );

    expect(result).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(["auth/login"]);
  });
});
