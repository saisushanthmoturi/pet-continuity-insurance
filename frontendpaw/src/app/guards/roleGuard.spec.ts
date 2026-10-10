import { TestBed } from "@angular/core/testing";
import { Router } from "@angular/router";
import { roleGuard } from "./roleGuard";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";

describe("roleGuard", () => {
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

  it("should block and navigate to login if currentUser is missing", () => {
    const route = { data: { roles: ["ROLE_ADMIN"] } } as unknown as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      roleGuard(route, {} as RouterStateSnapshot)
    );

    expect(result).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(["auth/login"]);
  });

  it("should block and navigate to login if user role does not match expected roles", () => {
    localStorage.setItem("currentUser", JSON.stringify({ role: "ROLE_CUSTOMER" }));
    const route = { data: { roles: ["ROLE_ADMIN", "ADMIN"] } } as unknown as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      roleGuard(route, {} as RouterStateSnapshot)
    );

    expect(result).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(["auth/login"]);
  });

  it("should allow activation if user role matches expected roles", () => {
    localStorage.setItem("currentUser", JSON.stringify({ role: "ROLE_ADMIN" }));
    const route = { data: { roles: ["ROLE_ADMIN", "ADMIN"] } } as unknown as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      roleGuard(route, {} as RouterStateSnapshot)
    );

    expect(result).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it("should allow activation if no specific roles are required in route data", () => {
    localStorage.setItem("currentUser", JSON.stringify({ role: "ROLE_CUSTOMER" }));
    const route = { data: {} } as unknown as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      roleGuard(route, {} as RouterStateSnapshot)
    );

    expect(result).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
