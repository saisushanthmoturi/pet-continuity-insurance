import { TestBed } from "@angular/core/testing";
import { provideHttpClient } from "@angular/common/http";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { ClaimsService } from "./claims-service";
import { ClaimDTO, ClaimDocumentDTO } from "../models/claimDTO";

describe("ClaimsService", () => {
  let service: ClaimsService;
  let httpTesting: HttpTestingController;
  const apiUrl = "http://localhost:8080/api/claims";

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        ClaimsService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ClaimsService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
    localStorage.clear();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should save and retrieve claim settlement", () => {
    service.saveClaimSettlement(1, 30000, "APPROVED");
    const settlement = service.getClaimSettlement(1);
    expect(settlement.price).toBe(30000);
    expect(settlement.status).toBe("APPROVED");
  });

  it("should file claim and persist locally", () => {
    const newClaim: any = {
      petId: 5,
      policyId: 50,
      claimantName: "Bob",
      claimAmount: 20000,
      description: "Loss of primary caregiver"
    };

    service.fileClaim(newClaim).subscribe(claim => {
      expect(claim.petId).toBe(5);
      expect(claim.status).toBe("PENDING");
      expect(claim.claimId).toBeGreaterThan(0);
    });

    const req = httpTesting.expectOne(apiUrl);
    expect(req.request.method).toBe("POST");
    req.flush({ id: 101, ...newClaim, status: "PENDING" });
  });

  it("should retrieve all claims merging remote and local", () => {
    localStorage.setItem("paw_continuity_global_claims", JSON.stringify([{
      claimId: 999,
      petId: 7,
      policyId: 70,
      status: "PENDING",
      claimAmount: 15000
    }]));

    service.getAllClaims().subscribe(claims => {
      expect(claims.length).toBeGreaterThanOrEqual(1);
      expect(claims.some(c => c.claimId === 999)).toBe(true);
    });

    const req = httpTesting.expectOne(apiUrl);
    expect(req.request.method).toBe("GET");
    req.flush([{ id: 1, petId: 2, policyId: 20, claimAmount: 25000, status: "APPROVED" }]);
  });

  it("should get claim by id", () => {
    service.getClaimById(42).subscribe(claim => {
      expect(claim.claimId).toBe(42);
      expect(claim.claimAmount).toBe(25000);
    });

    const req = httpTesting.expectOne(`${apiUrl}/42`);
    expect(req.request.method).toBe("GET");
    req.flush({ id: 42, policyId: 1, claimAmount: 25000, status: "PENDING" });
  });

  it("should get claims by policy id", () => {
    service.getClaimsByPolicyId(101).subscribe(claims => {
      expect(claims.length).toBe(1);
      expect(claims[0].policyId).toBe(101);
    });

    const req = httpTesting.expectOne(`${apiUrl}/policy/101`);
    expect(req.request.method).toBe("GET");
    req.flush([{ id: 3, policyId: 101, claimAmount: 12000 }]);
  });

  it("should upload claim document", () => {
    const doc: ClaimDocumentDTO = {
      claimId: 5,
      documentType: "DEATH_CERTIFICATE",
      documentName: "death_cert.pdf",
      documentUrl: "https://example.com/cert.pdf"
    };

    service.uploadDocument(5, doc).subscribe(res => {
      expect(res.documentName).toBe("death_cert.pdf");
    });

    const req = httpTesting.expectOne(`${apiUrl}/5/documents`);
    expect(req.request.method).toBe("POST");
    req.flush(doc);
  });

  it("should get documents for claim", () => {
    service.getDocuments(5).subscribe(docs => {
      expect(docs.length).toBe(1);
    });

    const req = httpTesting.expectOne(`${apiUrl}/5/documents`);
    expect(req.request.method).toBe("GET");
    req.flush([{ claimId: 5, documentType: "DEATH_CERTIFICATE", documentName: "cert.pdf" }]);
  });

  it("should verify event with registry", () => {
    service.verifyEvent(5).subscribe(res => {
      expect(res.status).toBe("VERIFIED");
    });

    const req = httpTesting.expectOne(`${apiUrl}/5/verify-death`);
    expect(req.request.method).toBe("POST");
    req.flush({ status: "VERIFIED", claimId: 5 });
  });

  it("should trigger investigation / manual review", () => {
    service.investigateClaim(5).subscribe(res => {
      expect(res.status).toBe("MANUAL_REVIEW");
    });

    const req = httpTesting.expectOne(`${apiUrl}/5/investigate`);
    expect(req.request.method).toBe("POST");
    req.flush({ status: "MANUAL_REVIEW", claimId: 5 });
  });

  it("should approve claim", () => {
    service.approveClaim(5, 27000).subscribe(res => {
      expect(res.status).toBe("APPROVED");
    });

    const req = httpTesting.expectOne(`${apiUrl}/5/approve`);
    expect(req.request.method).toBe("POST");
    req.flush({ status: "APPROVED", claimId: 5, approvedAmount: 27000 });
  });

  it("should reject claim with reason", () => {
    service.rejectClaim(5, "Invalid documentation").subscribe(res => {
      expect(res.status).toBe("REJECTED");
    });

    const req = httpTesting.expectOne(`${apiUrl}/5/reject?reason=Invalid%20documentation`);
    expect(req.request.method).toBe("POST");
    req.flush({ status: "REJECTED", claimId: 5, rejectionReason: "Invalid documentation" });
  });
});
