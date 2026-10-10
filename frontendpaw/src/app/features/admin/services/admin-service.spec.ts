import { TestBed } from "@angular/core/testing";
import { provideHttpClient } from "@angular/common/http";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { AdminService } from "./admin-service";
import { UserDTO } from "../models/adminDTO";

describe("AdminService", () => {
  let service: AdminService;
  let httpTesting: HttpTestingController;
  const apiUrl = "http://localhost:8080/api";

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AdminService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AdminService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should retrieve users list", () => {
    const mockUsers: UserDTO[] = [
      { userId: 1, email: "admin@pawcontinuity.com", fullName: "Admin User", role: "ADMIN", status: "ACTIVE" }
    ];

    service.getUsers().subscribe(users => {
      expect(users).toEqual(mockUsers);
      expect(users.length).toBe(1);
    });

    const req = httpTesting.expectOne(`${apiUrl}/auth/users`);
    expect(req.request.method).toBe("GET");
    req.flush(mockUsers);
  });

  it("should retrieve all policies", () => {
    const mockPolicies = [{ policyId: 101, status: "ACTIVE", premium: 120 }];

    service.getAllPolicies().subscribe(policies => {
      expect(policies).toEqual(mockPolicies);
    });

    const req = httpTesting.expectOne(`${apiUrl}/policies`);
    expect(req.request.method).toBe("GET");
    req.flush(mockPolicies);
  });

  it("should retrieve all pet funds", () => {
    const mockFunds = [{ fundId: 5, balance: 15000, status: "ACTIVE" }];

    service.getAllFunds().subscribe(funds => {
      expect(funds).toEqual(mockFunds);
    });

    const req = httpTesting.expectOne(`${apiUrl}/payments/funds`);
    expect(req.request.method).toBe("GET");
    req.flush(mockFunds);
  });

  it("should check system health", () => {
    const mockHealth = { status: "UP", components: { db: { status: "UP" } } };

    service.checkHealth().subscribe(health => {
      expect(health.status).toBe("UP");
    });

    const req = httpTesting.expectOne("http://localhost:8080/actuator/health");
    expect(req.request.method).toBe("GET");
    req.flush(mockHealth);
  });
});
