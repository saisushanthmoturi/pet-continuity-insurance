import { TestBed } from "@angular/core/testing";
import { provideHttpClient } from "@angular/common/http";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { UnderwriterService } from "./underwriter-service";
import { RatingRuleDTO, RiskAssessmentDTO } from "../models/underwriterDTO";

describe("UnderwriterService", () => {
  let service: UnderwriterService;
  let httpTesting: HttpTestingController;
  const apiUrl = "http://localhost:8080/api/underwriting";

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UnderwriterService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(UnderwriterService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should calculate / request quote", () => {
    const payload = { customerId: 1, petId: 2, coverageAmount: 30000 };
    const mockBackendRes = {
      id: 10,
      customerId: 1,
      petId: 2,
      requestedCoverage: 30000,
      monthlyPremium: 50,
      riskScore: 60,
      decision: "APPROVED"
    };

    service.requestQuote(payload).subscribe(res => {
      expect(res.quoteId).toBe(10);
      expect(res.requestedCoverage).toBe(30000);
      expect(res.monthlyCareCost).toBe(50);
      expect(res.calculatedPremium).toBe(600);
      expect(res.status).toBe("APPROVED");
    });

    const req = httpTesting.expectOne(`${apiUrl}/quotes`);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual({ customerId: 1, petId: 2, requestedCoverage: 30000 });
    req.flush(mockBackendRes);
  });

  it("should calculateQuote delegation", () => {
    const payload = { customerId: 3, petId: 4 };
    service.calculateQuote(payload).subscribe(res => {
      expect(res.quoteId).toBe(20);
    });

    const req = httpTesting.expectOne(`${apiUrl}/quotes`);
    req.flush({ id: 20, customerId: 3, petId: 4, monthlyPremium: 40 });
  });

  it("should retrieve all quotes", () => {
    const mockList = [
      { id: 1, customerId: 1, petId: 1, requestedCoverage: 20000, monthlyPremium: 45, decision: "AUTO_APPROVED" }
    ];

    service.getAllQuotes().subscribe(quotes => {
      expect(quotes.length).toBe(1);
      expect(quotes[0].quoteId).toBe(1);
      expect(quotes[0].status).toBe("AUTO_APPROVED");
    });

    const req = httpTesting.expectOne(`${apiUrl}/quotes`);
    expect(req.request.method).toBe("GET");
    req.flush(mockList);
  });

  it("should get quote by id", () => {
    service.getQuoteById(5).subscribe(quote => {
      expect(quote.quoteId).toBe(5);
    });

    const req = httpTesting.expectOne(`${apiUrl}/quotes/5`);
    expect(req.request.method).toBe("GET");
    req.flush({ id: 5, customerId: 2, petId: 3, requestedCoverage: 15000 });
  });

  it("should get quotes by customer id", () => {
    service.getQuotesByCustomerId(12).subscribe(quotes => {
      expect(quotes.length).toBe(1);
      expect(quotes[0].customerId).toBe(12);
    });

    const req = httpTesting.expectOne(`${apiUrl}/quotes/customer/12`);
    expect(req.request.method).toBe("GET");
    req.flush([{ id: 8, customerId: 12, petId: 2 }]);
  });

  it("should update quote", () => {
    const updatePayload = { status: "APPROVED" as const };
    service.updateQuote(7, updatePayload).subscribe(res => {
      expect(res.status).toBe("APPROVED");
    });

    const req = httpTesting.expectOne(`${apiUrl}/quotes/7`);
    expect(req.request.method).toBe("PUT");
    expect(req.request.body).toEqual(updatePayload);
    req.flush({ quoteId: 7, ...updatePayload });
  });

  it("should get assessment by quote id", () => {
    const mockAssessment: RiskAssessmentDTO = {
      riskAssessmentId: 99,
      quoteId: 7,
      totalScore: 45,
      riskClass: "LOW",
      explanation: "Healthy young pet"
    };

    service.getAssessmentByQuoteId(7).subscribe(res => {
      expect(res.riskClass).toBe("LOW");
    });

    const req = httpTesting.expectOne(`${apiUrl}/assessments/quote/7`);
    expect(req.request.method).toBe("GET");
    req.flush(mockAssessment);
  });

  it("should reassess pet risk", () => {
    service.reassessPetRisk(3).subscribe(res => {
      expect(res.status).toBe("REASSESSED");
    });

    const req = httpTesting.expectOne(`${apiUrl}/assessments/reassess/3`);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual({});
    req.flush({ status: "REASSESSED" });
  });

  it("should get all rating rules", () => {
    const mockRules = [
      { id: 1, ruleCode: "RULE_DOG_1", speciesCode: "DOG", breedCode: "GOLDEN", baseRate: 50.0 }
    ];

    service.getAllRules().subscribe(rules => {
      expect(rules.length).toBe(1);
      expect(rules[0].ruleCode).toBe("RULE_DOG_1");
    });

    const req = httpTesting.expectOne(`${apiUrl}/rules`);
    expect(req.request.method).toBe("GET");
    req.flush(mockRules);
  });

  it("should create rating rule", () => {
    const newRule: RatingRuleDTO = {
      ruleCode: "RULE_CAT_2",
      speciesCode: "CAT",
      breedCode: "PERSIAN",
      minAgeMonths: 6,
      maxAgeMonths: 120,
      baseRate: 35.0,
      multiplier: 1.1,
      description: "Persian cat rule"
    };

    service.createRule(newRule).subscribe(res => {
      expect(res.ruleCode).toBe("RULE_CAT_2");
    });

    const req = httpTesting.expectOne(`${apiUrl}/rules`);
    expect(req.request.method).toBe("POST");
    req.flush({ id: 10, ...newRule });
  });
});
