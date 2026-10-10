import { ComponentFixture, TestBed } from "@angular/core/testing";
import { provideRouter, Router, ActivatedRoute } from "@angular/router";
import { provideStore, Store } from "@ngrx/store";
import { of } from "rxjs";
import { CustomerDashboardComponent } from "./customer-dashboard";
import { loginReducer } from "../../../auth/state/login.reducer";
import { customerReducer } from "../../state/customer.reducer";
import { CustomerService } from "../../services/customer-service";
import { UnderwriterService } from "../../../underwriter/services/underwriter-service";
import { ClaimsService } from "../../../claims/services/claims-service";
import { PetDTO } from "../../models/customerDTO";

describe("CustomerDashboardComponent", () => {
  let component: CustomerDashboardComponent;
  let fixture: ComponentFixture<CustomerDashboardComponent>;
  let customerServiceMock: any;
  let underwriterServiceMock: any;
  let claimsServiceMock: any;
  let router: Router;
  let store: Store;

  beforeEach(async () => {
    localStorage.clear();
    customerServiceMock = {
      getCustomerByUserId: vi.fn().mockReturnValue(of({ customerId: 1, userId: 1, firstName: "John", lastName: "Doe", email: "john@example.com" })),
      getPetsByCustomerId: vi.fn().mockReturnValue(of([])),
      getPoliciesByCustomerId: vi.fn().mockReturnValue(of([])),
      getAddresses: vi.fn().mockReturnValue(of([])),
      createPet: vi.fn().mockReturnValue(of({ petId: 10, name: "Rex", speciesCode: "DOG" })),
      addMedicalRecord: vi.fn().mockReturnValue(of({ medicalRecordId: 1 })),
      addCaretaker: vi.fn().mockReturnValue(of({ caretakerId: 1 })),
      createCarePlan: vi.fn().mockReturnValue(of({ carePlanId: 1 })),
      updateCustomer: vi.fn().mockReturnValue(of({ customerId: 1 })),
      addAddress: vi.fn().mockReturnValue(of({ addressId: 1 })),
      getCarePlanByPetId: vi.fn().mockReturnValue(of({})),
      getMedicalRecords: vi.fn().mockReturnValue(of([])),
      getCaretakersByPetId: vi.fn().mockReturnValue(of([]))
    };

    underwriterServiceMock = {
      calculateQuote: vi.fn().mockReturnValue(of({ quoteId: 5, calculatedPremium: 500 })),
      requestQuote: vi.fn().mockReturnValue(of({ quoteId: 5, calculatedPremium: 500 }))
    };

    claimsServiceMock = {
      getAllClaims: vi.fn().mockReturnValue(of([])),
      fileClaim: vi.fn().mockReturnValue(of({ claimId: 1, status: "PENDING" })),
      getAllLocalClaims: vi.fn().mockReturnValue([])
    };

    await TestBed.configureTestingModule({
      imports: [CustomerDashboardComponent],
      providers: [
        provideRouter([]),
        provideStore({
          login: loginReducer,
          customer: customerReducer
        }),
        { provide: CustomerService, useValue: customerServiceMock },
        { provide: UnderwriterService, useValue: underwriterServiceMock },
        { provide: ClaimsService, useValue: claimsServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({ tab: "overview" }),
            snapshot: {
              queryParams: { tab: "overview" }
            }
          }
        }
      ]
    }).compileComponents();

    store = TestBed.inject(Store);
    vi.spyOn(store, "dispatch");

    router = TestBed.inject(Router);
    vi.spyOn(router, "navigate");

    fixture = TestBed.createComponent(CustomerDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it("should create customer dashboard component", () => {
    expect(component).toBeTruthy();
  });

  it("should switch tabs via setTab", () => {
    component.setTab("pets");
    expect(component.activeTab).toBe("pets");

    component.setTab("claims");
    expect(component.activeTab).toBe("claims");
  });

  it("should toggle profile modal", () => {
    expect(component.isProfileModalOpen).toBe(false);
    component.openProfileModal();
    expect(component.isProfileModalOpen).toBe(true);
    component.closeProfileModal();
    expect(component.isProfileModalOpen).toBe(false);
  });

  it("should parse names correctly", () => {
    const res1 = component.parseNames("John Doe", "john@example.com");
    expect(res1.firstName).toBe("John");
    expect(res1.lastName).toBe("Doe");

    const res2 = component.parseNames("", "alice.smith@example.com");
    expect(res2.firstName).toBe("Alice");
  });

  it("should select a pet and set selectedPetId", () => {
    const mockPet: PetDTO = {
      petId: 42,
      customerId: 1,
      name: "Bella",
      speciesCode: "CAT",
      breedCode: "SIAMESE",
      gender: "FEMALE",
      dateOfBirth: "2022-05-01",
      weightValue: 4.5,
      weightUnit: "KG",
      annualCareCost: 800,
      expectedRemainingYears: 12
    };

    component.onSelectPet(mockPet);
    expect(component.selectedPetId).toBe(42);
    expect(store.dispatch).toHaveBeenCalled();
  });

  it("should dispatch createPet action when pet form is submitted", () => {
    component.newPet = {
      customerId: 1,
      name: "Max",
      speciesCode: "DOG",
      breedCode: "GERMAN_SHEPHERD",
      gender: "MALE",
      dateOfBirth: "2021-06-15",
      weightValue: 30,
      weightUnit: "KG",
      annualCareCost: 1500,
      expectedRemainingYears: 9
    };

    component.onCreatePet();
    expect(store.dispatch).toHaveBeenCalled();
  });
});
