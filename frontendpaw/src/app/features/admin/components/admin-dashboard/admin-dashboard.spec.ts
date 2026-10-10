import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ActivatedRoute } from "@angular/router";
import { provideStore } from "@ngrx/store";
import { of } from "rxjs";
import { AdminDashboardComponent } from "./admin-dashboard";
import { adminReducer } from "../../state/admin.reducer";
import { ClaimsService } from "../../../claims/services/claims-service";
import { ClaimDTO } from "../../../claims/models/claimDTO";

describe("AdminDashboardComponent", () => {
  let component: AdminDashboardComponent;
  let fixture: ComponentFixture<AdminDashboardComponent>;
  let claimsServiceMock: any;

  const mockClaims: ClaimDTO[] = [
    { claimId: 1, policyId: 10, claimAmount: 25000, status: "PENDING" },
    { claimId: 2, policyId: 10, claimAmount: 30000, status: "MANUAL_REVIEW" },
    { claimId: 3, policyId: 10, claimAmount: 20000, status: "APPROVED" }
  ];

  beforeEach(async () => {
    localStorage.clear();
    claimsServiceMock = {
      getAllClaims: vi.fn().mockReturnValue(of(mockClaims)),
      approveClaim: vi.fn().mockReturnValue(of({ status: "APPROVED" })),
      rejectClaim: vi.fn().mockReturnValue(of({ status: "REJECTED" })),
      investigateClaim: vi.fn().mockReturnValue(of({ status: "MANUAL_REVIEW" }))
    };

    await TestBed.configureTestingModule({
      imports: [AdminDashboardComponent],
      providers: [
        provideStore({
          admin: adminReducer
        }),
        { provide: ClaimsService, useValue: claimsServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({ tab: "overview" })
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AdminDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it("should create admin dashboard component", () => {
    expect(component).toBeTruthy();
    expect(component.activeTab).toBe("overview");
  });

  it("should switch tabs via setTab", () => {
    component.setTab("users");
    expect(component.activeTab).toBe("users");
  });

  it("should format user roles nicely", () => {
    expect(component.formatRole("ROLE_ADMIN")).toBe("ADMIN");
    expect(component.formatRole("ROLE_CLAIMS_ADJUSTER")).toBe("CLAIMS ADJUSTER");
    expect(component.formatRole("")).toBe("");
  });

  it("should compute filtered claims and counts", () => {
    expect(component.claims.length).toBe(3);
    expect(component.manualReviewCount).toBe(1);
    expect(component.approvedClaimsCount).toBe(1);

    component.claimFilter = "PENDING";
    expect(component.filteredClaims.length).toBe(1);
    expect(component.filteredClaims[0].claimId).toBe(1);
  });

  it("should approve claim", () => {
    component.onApproveClaim(1, 26000);
    expect(claimsServiceMock.approveClaim).toHaveBeenCalledWith(1, 26000);
    expect(component.adminActionSuccess).toContain("Claim #1 accepted & approved");
  });

  it("should send claim to under review", () => {
    component.onSendToUnderReview(1);
    expect(claimsServiceMock.investigateClaim).toHaveBeenCalledWith(1);
    expect(component.adminActionSuccess).toContain("sent to Under Review section");
  });

  it("should open, cancel, and confirm reject modal", () => {
    component.openRejectModal(mockClaims[0]);
    expect(component.selectedClaimForReject).toBe(mockClaims[0]);

    component.cancelReject();
    expect(component.selectedClaimForReject).toBeNull();

    component.openRejectModal(mockClaims[0]);
    component.onConfirmReject();
    expect(claimsServiceMock.rejectClaim).toHaveBeenCalledWith(1, "Documentation insufficient or discrepancies detected during fiduciary review");
    expect(component.selectedClaimForReject).toBeNull();
  });
});
