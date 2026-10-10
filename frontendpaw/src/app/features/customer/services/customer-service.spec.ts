import { TestBed } from "@angular/core/testing";
import { provideHttpClient } from "@angular/common/http";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { CustomerService } from "./customer-service";
import {
  AddressDTO,
  CarePlanDTO,
  CaretakerDTO,
  CustomerDTO,
  MedicalRecordDTO,
  PetDTO,
  PremiumPaymentDTO
} from "../models/customerDTO";

describe("CustomerService", () => {
  let service: CustomerService;
  let httpTesting: HttpTestingController;
  const apiUrl = "http://localhost:8080/api";

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        CustomerService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CustomerService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
    localStorage.clear();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should create customer", () => {
    const customer: CustomerDTO = {
      userId: 5,
      firstName: "Jane",
      lastName: "Doe",
      phone: "555-1234",
      kycStatus: "VERIFIED"
    };

    service.createCustomer(customer).subscribe(res => {
      expect(res.customerId).toBe(10);
      expect(res.firstName).toBe("Jane");
      expect(res.lastName).toBe("Doe");
    });

    const req = httpTesting.expectOne(`${apiUrl}/customers`);
    expect(req.request.method).toBe("POST");
    req.flush({ id: 10, ...customer, status: "ACTIVE" });
  });

  it("should update customer", () => {
    const customer: CustomerDTO = {
      customerId: 10,
      userId: 5,
      firstName: "Jane",
      lastName: "Smith",
      phone: "555-9876",
      kycStatus: "VERIFIED"
    };

    service.updateCustomer(10, customer).subscribe(res => {
      expect(res.lastName).toBe("Smith");
    });

    const req = httpTesting.expectOne(`${apiUrl}/customers/10`);
    expect(req.request.method).toBe("PUT");
    req.flush({ id: 10, ...customer });
  });

  it("should get customer by userId", () => {
    service.getCustomerByUserId(5).subscribe(res => {
      expect(res.userId).toBe(5);
      expect(res.customerId).toBe(10);
    });

    const req = httpTesting.expectOne(`${apiUrl}/customers/user/5`);
    expect(req.request.method).toBe("GET");
    req.flush({ id: 10, userId: 5, firstName: "Jane", lastName: "Smith" });
  });

  it("should add and get address", () => {
    const address: AddressDTO = {
      customerId: 10,
      streetAddress: "123 Main St",
      city: "Austin",
      state: "TX",
      postalCode: "78701",
      country: "USA"
    };

    service.addAddress(10, address).subscribe(res => {
      expect(res.city).toBe("Austin");
      expect(res.customerId).toBe(10);
    });

    const reqPost = httpTesting.expectOne(`${apiUrl}/customers/10/addresses`);
    expect(reqPost.request.method).toBe("POST");
    reqPost.flush({ id: 1, ...address });

    service.getAddresses(10).subscribe(addrs => {
      expect(addrs.length).toBe(1);
    });

    const reqGet = httpTesting.expectOne(`${apiUrl}/customers/10/addresses`);
    expect(reqGet.request.method).toBe("GET");
    reqGet.flush([{ id: 1, ...address }]);
  });

  it("should create and get pets", () => {
    const pet: PetDTO = {
      customerId: 10,
      name: "Buddy",
      speciesCode: "DOG",
      breedCode: "GOLDEN_RETRIEVER",
      gender: "MALE",
      dateOfBirth: "2023-01-01",
      weightValue: 25,
      weightUnit: "KG",
      annualCareCost: 1200,
      expectedRemainingYears: 10
    };

    service.createPet(pet).subscribe(res => {
      expect(res.name).toBe("Buddy");
      expect(res.petId).toBe(1);
    });

    const reqPost = httpTesting.expectOne(`${apiUrl}/pets`);
    expect(reqPost.request.method).toBe("POST");
    reqPost.flush({ id: 1, ...pet });

    service.getPetsByCustomerId(10).subscribe(pets => {
      expect(pets.length).toBeGreaterThanOrEqual(1);
    });

    const reqGet = httpTesting.expectOne(`${apiUrl}/pets/customer/10`);
    expect(reqGet.request.method).toBe("GET");
    reqGet.flush([{ id: 1, ...pet }]);
  });

  it("should add and get medical records", () => {
    const record: MedicalRecordDTO = {
      petId: 1,
      recordType: "VACCINATION",
      diagnosis: "Rabies booster",
      treatment: "Routine vaccine",
      vetName: "Dr. Smith",
      recordDate: "2026-10-10",
      riskLevel: "LOW",
      annualMedCost: 150,
      notes: "Routine vaccination"
    };

    service.addMedicalRecord(1, record).subscribe(res => {
      expect(res.recordType).toBe("VACCINATION");
    });

    const reqPost = httpTesting.expectOne(`${apiUrl}/pets/1/medical-records`);
    expect(reqPost.request.method).toBe("POST");
    reqPost.flush({ id: 50, ...record });

    service.getMedicalRecords(1).subscribe(records => {
      expect(records.length).toBe(1);
    });

    const reqGet = httpTesting.expectOne(`${apiUrl}/pets/1/medical-records`);
    expect(reqGet.request.method).toBe("GET");
    reqGet.flush([{ id: 50, ...record }]);
  });

  it("should add and get caretakers", () => {
    const caretaker: CaretakerDTO = {
      petId: 1,
      name: "Emily",
      phone: "555-4321",
      email: "emily@example.com",
      relationship: "FRIEND",
      address: "10 Oak Lane",
      priority: "PRIMARY"
    };

    service.addCaretaker(caretaker).subscribe(res => {
      expect(res.name).toBe("Emily");
    });

    const reqPost = httpTesting.expectOne(`${apiUrl}/care/caretakers`);
    expect(reqPost.request.method).toBe("POST");
    reqPost.flush({ id: 15, ...caretaker });

    service.getCaretakersByPetId(1).subscribe(list => {
      expect(list.length).toBe(1);
    });

    const reqGet = httpTesting.expectOne(`${apiUrl}/care/caretakers/pet/1`);
    expect(reqGet.request.method).toBe("GET");
    reqGet.flush([{ id: 15, ...caretaker }]);
  });

  it("should create and get care plan", () => {
    const plan: CarePlanDTO = {
      petId: 1,
      feedingInstructions: "Grain free kibble",
      medicationInstructions: "Glucosamine daily",
      vetDetails: "Downtown Vet Clinic",
      routineDetails: "Morning walk 30 mins",
      specialRequirements: "Fear of fireworks"
    };

    service.createCarePlan(plan).subscribe(res => {
      expect(res.feedingInstructions).toBe("Grain free kibble");
    });

    const reqPost = httpTesting.expectOne(`${apiUrl}/care/care-plans`);
    expect(reqPost.request.method).toBe("POST");
    reqPost.flush({ id: 25, ...plan, vetContact: plan.vetDetails, specialNeeds: plan.specialRequirements });

    service.getCarePlanByPetId(1).subscribe(res => {
      expect(res.petId).toBe(1);
    });

    const reqGet = httpTesting.expectOne(`${apiUrl}/care/care-plans/pet/1`);
    expect(reqGet.request.method).toBe("GET");
    reqGet.flush({ id: 25, ...plan, vetContact: plan.vetDetails, specialNeeds: plan.specialRequirements });
  });

  it("should issue policy and get policies by customer", () => {
    service.issuePolicyFromQuote(100).subscribe(policy => {
      expect(policy.policyNumber).toBe("POL-100");
    });

    const reqIssue = httpTesting.expectOne(`${apiUrl}/policies/from-quote/100`);
    expect(reqIssue.request.method).toBe("POST");
    reqIssue.flush({ policyId: 100, policyNumber: "POL-100", customerId: 10, petId: 1, coverageAmount: 25000, premiumAmount: 600, status: "ACTIVE" });

    service.getPoliciesByCustomerId(10).subscribe(policies => {
      expect(policies.length).toBeGreaterThanOrEqual(1);
    });

    const reqList = httpTesting.expectOne(`${apiUrl}/policies/customer/10`);
    expect(reqList.request.method).toBe("GET");
    reqList.flush([{ policyId: 100, policyNumber: "POL-100", customerId: 10, petId: 1, coverageAmount: 25000, premiumAmount: 600, status: "ACTIVE" }]);
  });

  it("should pay premium and activate policy", () => {
    const payment: PremiumPaymentDTO = {
      policyId: 100,
      customerId: 10,
      amount: 120,
      paymentMethod: "CREDIT_CARD"
    };

    service.payPremium(payment).subscribe(res => {
      expect(res.status).toBe("SUCCESS");
    });

    const reqPay = httpTesting.expectOne(`${apiUrl}/payments/premium`);
    expect(reqPay.request.method).toBe("POST");
    reqPay.flush({ status: "SUCCESS" });

    service.activatePolicy(100).subscribe(res => {
      expect(res.status).toBe("ACTIVE");
    });

    const reqAct = httpTesting.expectOne(`${apiUrl}/policies/100/activate`);
    expect(reqAct.request.method).toBe("POST");
    reqAct.flush({ status: "ACTIVE" });
  });

  it("should payAndActivatePolicy chaining payment and activation", () => {
    service.payAndActivatePolicy(100, 10, 150).subscribe(res => {
      expect(res.status).toBe("ACTIVE");
    });

    const reqPay = httpTesting.expectOne(`${apiUrl}/payments/premium`);
    expect(reqPay.request.method).toBe("POST");
    reqPay.flush({ status: "SUCCESS" });

    const reqAct = httpTesting.expectOne(`${apiUrl}/policies/100/activate`);
    expect(reqAct.request.method).toBe("POST");
    reqAct.flush({ status: "ACTIVE" });
  });
});
