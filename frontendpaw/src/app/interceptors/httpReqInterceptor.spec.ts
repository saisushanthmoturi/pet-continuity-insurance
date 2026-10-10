import { TestBed } from "@angular/core/testing";
import { HttpClient, provideHttpClient, withInterceptors } from "@angular/common/http";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { Router } from "@angular/router";
import { httpReqInterceptor } from "./httpReqInterceptor";

describe("httpReqInterceptor", () => {
  let http: HttpClient;
  let httpTesting: HttpTestingController;
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
        },
        provideHttpClient(withInterceptors([httpReqInterceptor])),
        provideHttpClientTesting()
      ]
    });
    http = TestBed.inject(HttpClient);
    httpTesting = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpTesting.verify();
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it("should attach Bearer token when token is present in localStorage", () => {
    localStorage.setItem("token", "secret-test-token-123");

    http.get("/api/test-secure").subscribe();

    const req = httpTesting.expectOne("/api/test-secure");
    expect(req.request.headers.get("Authorization")).toBe("Bearer secret-test-token-123");
    req.flush({});
  });

  it("should not attach Authorization header when token is not present", () => {
    http.get("/api/test-public").subscribe();

    const req = httpTesting.expectOne("/api/test-public");
    expect(req.request.headers.has("Authorization")).toBe(false);
    req.flush({});
  });

  it("should clear localStorage and navigate to login on 401 Unauthorized", () => {
    localStorage.setItem("currentUser", JSON.stringify({ name: "Bob" }));
    localStorage.setItem("token", "expired-token");
    localStorage.setItem("role", "CUSTOMER");

    http.get("/api/protected").subscribe({
      next: () => expect.unreachable("should have failed"),
      error: (err) => {
        expect(err.status).toBe(401);
      }
    });

    const req = httpTesting.expectOne("/api/protected");
    req.flush("Unauthorized", { status: 401, statusText: "Unauthorized" });

    expect(localStorage.getItem("currentUser")).toBeNull();
    expect(localStorage.getItem("token")).toBeNull();
    expect(localStorage.getItem("role")).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith(["auth/login"]);
  });
});
