import { TestBed } from "@angular/core/testing";
import { provideHttpClient } from "@angular/common/http";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { CaretakerService } from "./caretaker-service";
import {
  BackupTransferDTO,
  DisbursementDTO,
  ExpenseDTO,
  PetCareFundDTO,
  PetVerificationDTO
} from "../models/caretakerDTO";

describe("CaretakerService", () => {
  let service: CaretakerService;
  let httpTesting: HttpTestingController;
  const apiUrl = "http://localhost:8080/api";

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CaretakerService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CaretakerService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should submit pet verification check", () => {
    const verification: PetVerificationDTO = {
      petId: 10,
      caretakerId: 2,
      verificationType: "PERIODIC_CHECK",
      status: "VERIFIED",
      notes: "Pet is thriving"
    };

    service.submitPetVerification(verification).subscribe(res => {
      expect(res.status).toBe("VERIFIED");
    });

    const req = httpTesting.expectOne(`${apiUrl}/care/verify-pet`);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual(verification);
    req.flush({ status: "VERIFIED" });
  });

  it("should check caretaker eligibility", () => {
    service.checkEligibility().subscribe(res => {
      expect(res.eligible).toBe(true);
    });

    const req = httpTesting.expectOne(`${apiUrl}/care/eligibility/check`);
    expect(req.request.method).toBe("GET");
    req.flush({ eligible: true });
  });

  it("should get fund by policy id", () => {
    const mockFund: PetCareFundDTO = {
      fundId: 1,
      policyId: 101,
      totalAmount: 50000,
      availableAmount: 48000,
      monthlyAllowance: 800,
      status: "ACTIVE"
    };

    service.getFundByPolicyId(101).subscribe(fund => {
      expect(fund).toEqual(mockFund);
    });

    const req = httpTesting.expectOne(`${apiUrl}/payments/funds/by-policy/101`);
    expect(req.request.method).toBe("GET");
    req.flush(mockFund);
  });

  it("should get fund by fund id", () => {
    const mockFund: PetCareFundDTO = {
      fundId: 5,
      policyId: 202,
      totalAmount: 30000,
      availableAmount: 29000,
      monthlyAllowance: 600,
      status: "ACTIVE"
    };

    service.getFundById(5).subscribe(fund => {
      expect(fund.fundId).toBe(5);
    });

    const req = httpTesting.expectOne(`${apiUrl}/payments/funds/5`);
    expect(req.request.method).toBe("GET");
    req.flush(mockFund);
  });

  it("should disburse monthly allowance", () => {
    const mockDisbursement: DisbursementDTO = {
      disbursementId: 1,
      fundId: 5,
      caretakerId: 12,
      amount: 800,
      status: "COMPLETED"
    };

    service.disburseMonthlyAllowance(5, 12).subscribe(res => {
      expect(res.amount).toBe(800);
    });

    const req = httpTesting.expectOne(`${apiUrl}/payments/funds/5/disburse`);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual({ caretakerId: 12 });
    req.flush(mockDisbursement);
  });

  it("should submit caretaker expense", () => {
    const expense: ExpenseDTO = {
      fundId: 5,
      amount: 150,
      category: "VET_BILL",
      description: "Annual vaccines"
    };

    service.submitExpense(5, expense).subscribe(res => {
      expect(res.amount).toBe(150);
    });

    const req = httpTesting.expectOne(`${apiUrl}/payments/funds/5/expense`);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual(expense);
    req.flush({ ...expense, expenseId: 99 });
  });

  it("should transfer pet care to backup caretaker", () => {
    const transfer: BackupTransferDTO = {
      petId: 4,
      primaryCaretakerId: 2,
      backupCaretakerId: 8,
      reason: "Temporary relocation"
    };

    service.transferToBackup(transfer).subscribe(res => {
      expect(res.transferred).toBe(true);
    });

    const req = httpTesting.expectOne(`${apiUrl}/care/backup-transfer/4`);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual(transfer);
    req.flush({ transferred: true });
  });
});
