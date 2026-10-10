import { Component, inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { Store } from "@ngrx/store";
import { Observable } from "rxjs";
import {
  usersSelector,
  globalPoliciesSelector,
  globalFundsSelector,
  systemHealthSelector,
  adminStatusSelector
} from "../../state/admin.selectors";
import {
  loadUsers,
  loadGlobalPolicies,
  loadGlobalFunds,
  checkSystemHealth
} from "../../state/admin.actions";
import { UserDTO } from "../../models/adminDTO";
import { ClaimsService } from "../../../claims/services/claims-service";
import { ClaimDTO } from "../../../claims/models/claimDTO";

@Component({
  selector: "app-admin-dashboard",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./admin-dashboard.html",
  styleUrls: ["./admin-dashboard.css"]
})
export class AdminDashboardComponent implements OnInit {
  private store = inject(Store);
  private route = inject(ActivatedRoute);
  private claimsService = inject(ClaimsService);

  activeTab = "overview";
  users$: Observable<UserDTO[]> = this.store.select(usersSelector);
  policies$: Observable<any[]> = this.store.select(globalPoliciesSelector);
  funds$: Observable<any[]> = this.store.select(globalFundsSelector);
  health$: Observable<any> = this.store.select(systemHealthSelector);

  userRoleFilter = "ALL";

  claims: ClaimDTO[] = [];
  claimSettlementPrices: { [claimId: number]: number } = {};
  claimFilter = "ALL";
  adminActionSuccess = "";
  adminActionError = "";
  rejectionReasonModal = "";
  selectedClaimForReject: ClaimDTO | null = null;

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params["tab"]) {
        this.activeTab = params["tab"];
      }
    });

    this.store.dispatch(loadUsers());
    this.store.dispatch(loadGlobalPolicies());
    this.store.dispatch(loadGlobalFunds());
    this.store.dispatch(checkSystemHealth());

    this.loadAdminClaims();
  }

  setTab(tab: string): void {
    this.activeTab = tab;
  }

  formatRole(role: string): string {
    if (!role) return "";
    return role.replace("ROLE_", "").replace("_", " ");
  }

  loadAdminClaims(): void {
    this.claimsService.getAllClaims().subscribe({
      next: (data) => {
        this.claims = data || [];
        this.claims.forEach(c => {
          if (c.claimId && !this.claimSettlementPrices[c.claimId]) {
            this.claimSettlementPrices[c.claimId] = c.approvedAmount || c.claimAmount || 25000;
          }
        });
      },
      error: () => {
        this.claims = [];
      }
    });
  }

  get filteredClaims(): ClaimDTO[] {
    if (this.claimFilter === "ALL") return this.claims;
    if (this.claimFilter === "MANUAL_REVIEW") {
      return this.claims.filter(c => c.status === "MANUAL_REVIEW" || c.status === "INVESTIGATING");
    }
    return this.claims.filter(c => c.status === this.claimFilter);
  }

  get manualReviewCount(): number {
    return this.claims.filter(c => c.status === "MANUAL_REVIEW" || c.status === "INVESTIGATING").length;
  }

  get approvedClaimsCount(): number {
    return this.claims.filter(c => c.status === "APPROVED").length;
  }

  setPricePreset(claimId: number, amount: number): void {
    this.claimSettlementPrices[claimId] = amount;
  }

  onApproveClaim(claimId?: number, customPrice?: number): void {
    if (!claimId) return;
    const finalAmount = customPrice !== undefined ? customPrice : (this.claimSettlementPrices[claimId] || 25000);
    this.claimsService.approveClaim(claimId, finalAmount).subscribe({
      next: () => {
        this.adminActionSuccess = `Claim #${claimId} accepted & approved with $${finalAmount} settlement payout. Care trust account and payout pipeline active.`;
        this.adminActionError = "";
        this.loadAdminClaims();
        setTimeout(() => this.adminActionSuccess = "", 5000);
      },
      error: (err) => {
        this.adminActionError = `Failed to manually approve claim #${claimId}: ${err?.error?.error || err?.message || "Error"}`;
        this.adminActionSuccess = "";
      }
    });
  }

  onSendToUnderReview(claimId?: number): void {
    if (!claimId) return;
    this.claimsService.investigateClaim(claimId).subscribe({
      next: () => {
        this.adminActionSuccess = `Claim #${claimId} sent to Under Review section for manual assessment.`;
        this.adminActionError = "";
        this.loadAdminClaims();
        setTimeout(() => this.adminActionSuccess = "", 5000);
      },
      error: (err) => {
        this.adminActionError = `Failed to send claim to under review: ${err?.error?.error || err?.message || "Error"}`;
      }
    });
  }

  openRejectModal(claim: ClaimDTO): void {
    this.selectedClaimForReject = claim;
    this.rejectionReasonModal = "Documentation insufficient or discrepancies detected during fiduciary review";
  }

  cancelReject(): void {
    this.selectedClaimForReject = null;
  }

  onConfirmReject(): void {
    if (!this.selectedClaimForReject?.claimId) return;
    const cid = this.selectedClaimForReject.claimId;
    this.claimsService.rejectClaim(cid, this.rejectionReasonModal).subscribe({
      next: () => {
        this.adminActionSuccess = `Claim #${cid} rejected.`;
        this.adminActionError = "";
        this.selectedClaimForReject = null;
        this.loadAdminClaims();
        setTimeout(() => this.adminActionSuccess = "", 5000);
      },
      error: (err) => {
        this.adminActionError = `Failed to reject claim: ${err?.error?.error || err?.message || "Error"}`;
        this.selectedClaimForReject = null;
      }
    });
  }

  onInvestigateClaim(claimId?: number): void {
    this.onSendToUnderReview(claimId);
  }
}
