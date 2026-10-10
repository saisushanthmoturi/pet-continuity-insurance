import { TestBed } from "@angular/core/testing";
import { provideHttpClient } from "@angular/common/http";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { AuthService } from "./auth-service";
import { JwtReqDTO, RegisterReqDTO } from "../models/JwtReqDTO";
import { JwtResDTO } from "../models/JwtResDTO";

describe("AuthService", () => {
  let service: AuthService;
  let httpTesting: HttpTestingController;
  const baseUrl = "http://localhost:8080/api/auth";

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AuthService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should authenticate user via login", () => {
    const creds: JwtReqDTO = { email: "user@example.com", password: "password123" };
    const mockRes: JwtResDTO = {
      token: "jwt-xyz-123",
      role: "CUSTOMER",
      userId: 1,
      email: "user@example.com",
      fullName: "John Doe"
    };

    service.login(creds).subscribe(res => {
      expect(res).toEqual(mockRes);
      expect(res.token).toBe("jwt-xyz-123");
    });

    const req = httpTesting.expectOne(`${baseUrl}/login`);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual(creds);
    req.flush(mockRes);
  });

  it("should register a new user", () => {
    const regReq: RegisterReqDTO = {
      fullName: "Alice Smith",
      email: "alice@example.com",
      password: "password123",
      role: "CUSTOMER"
    };

    service.register(regReq).subscribe(res => {
      expect(res).toBeTruthy();
      expect(res.success).toBe(true);
    });

    const req = httpTesting.expectOne(`${baseUrl}/register`);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual(regReq);
    req.flush({ success: true, message: "User registered" });
  });

  it("should validate current token session", () => {
    service.validate().subscribe(res => {
      expect(res.valid).toBe(true);
    });

    const req = httpTesting.expectOne(`${baseUrl}/validate`);
    expect(req.request.method).toBe("GET");
    req.flush({ valid: true });
  });

  it("should log out user", () => {
    service.logout().subscribe(res => {
      expect(res.status).toBe("logged out");
    });

    const req = httpTesting.expectOne(`${baseUrl}/logout`);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual({});
    req.flush({ status: "logged out" });
  });
});
