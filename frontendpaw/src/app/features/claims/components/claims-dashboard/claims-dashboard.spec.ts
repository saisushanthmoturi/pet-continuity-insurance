import { ComponentFixture, TestBed } from "@angular/core/testing";
import { provideRouter, ActivatedRoute } from "@angular/router";
import { provideStore, Store } from "@ngrx/store";
import { of } from "rxjs";
import { ClaimsDashboardComponent } from "./claims-dashboard";
import { claimsReducer } from "../../state/claims.reducer";
import { ClaimsService } from "../../services/claims-service";
import { ClaimDTO } from "../../models/claimDTO";
import { selectClaim, loadDocuments } from "../../state/claims.actions";

describe("ClaimsDashboardComponent", () => {
  let component: ClaimsDashboardComponent;
  let fixture: ComponentFixture<ClaimsDashboardComponent>;
  let claimsServiceMock: any;
  let store: Store;

  const mockClaim: ClaimDTO = {
    claimId: 10,
    claimNumber: "CLM-010",
    policyId: 100,
    petId: 2,
    claimantName: "Alice",
    claimAmount: 25000,
    status: "PENDING"
  };

  beforeEach(async () => {
    localStorage.clear();
    claimsServiceMock = {
      getAllClaims: vi.fn().mockReturnValue(of([mockClaim])),
      getDocuments: vi.fn().mockReturnValue(of([])),
      verifyEvent: vi.fn().mockReturnValue(of({ status: "VERIFIED" })),
      investigateClaim: vi.fn().mockReturnValue(of({ status: "MANUAL_REVIEW" })),
      approveClaim: vi.fn().mockReturnValue(of({ status: "APPROVED" })),
      rejectClaim: vi.fn().mockReturnValue(of({ status: "REJECTED" })),
      getClaimSettlement: vi.fn().mockReturnValue({ price: 25000, status: "PENDING" }),
      getAllLocalClaims: vi.fn().mockReturnValue([])
    };

    await TestBed.configureTestingModule({
      imports: [ClaimsDashboardComponent],
      providers: [
        provideRouter([]),
        provideStore({
          claims: claimsReducer
        }),
        { provide: ClaimsService, useValue: claimsServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({ tab: "claims" })
          }
        }
      ]
    }).compileComponents();

    store = TestBed.inject(Store);
    vi.spyOn(store, "dispatch");

    fixture = TestBed.createComponent(ClaimsDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it("should create claims dashboard component", () => {
    expect(component).toBeTruthy();
  });

  it("should switch tabs via setTab", () => {
    component.setTab("under-review");
    expect(component.activeTab).toBe("under-review");
  });

  it("should select a claim and update state and dispatch actions", () => {
    component.onSelectClaim(mockClaim);
    expect(component.selectedClaim?.claimId).toBe(10);
    expect(component.selectedClaimPrice).toBe(25000);
    expect(component.activeTab).toBe("adjudication");
    expect(store.dispatch).toHaveBeenCalledWith(selectClaim({ claim: { ...mockClaim } }));
    expect(store.dispatch).toHaveBeenCalledWith(loadDocuments({ claimId: 10 }));
  });

  it("should update settlement preset price", () => {
    component.onSetPricePreset(30000);
    expect(component.selectedClaimPrice).toBe(30000);
  });

  it("should approve selected claim", () => {
    component.selectedClaim = { ...mockClaim };
    component.selectedClaimPrice = 25000;

    component.onApproveClaim(10, 25000);
    expect(claimsServiceMock.approveClaim).toHaveBeenCalledWith(10, 25000);
    expect(component.selectedClaim.status).toBe("APPROVED");
  });

  it("should reject selected claim with rejection reason", () => {
    component.selectedClaim = { ...mockClaim };
    component.rejectionReason = "Insufficient documentation";

    component.onRejectClaim(10);
    expect(claimsServiceMock.rejectClaim).toHaveBeenCalledWith(10, "Insufficient documentation");
    expect(component.selectedClaim.status).toBe("REJECTED");
  });

  it("should send claim to under review / investigation", () => {
    component.selectedClaim = { ...mockClaim };

    component.onSendToUnderReview(10);
    expect(claimsServiceMock.investigateClaim).toHaveBeenCalledWith(10);
    expect(component.selectedClaim.status).toBe("MANUAL_REVIEW");
  });
});
