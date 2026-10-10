import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  claimsSelector,
  selectedClaimSelector,
  claimDocumentsSelector,
  claimsStatusSelector
} from '../../state/claims.selectors';
import {
  loadClaims,
  selectClaim,
  loadDocuments,
  verifyEvent,
  investigateClaim,
  approveClaim,
  rejectClaim
} from '../../state/claims.actions';
import { ClaimDTO, ClaimDocumentDTO } from '../../models/claimDTO';
import { ClaimsService } from '../../services/claims-service';

@Component({
  selector: 'app-claims-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './claims-dashboard.html',
  styleUrls: ['./claims-dashboard.css']
})
export class ClaimsDashboardComponent implements OnInit {
  private store = inject(Store);
  private route = inject(ActivatedRoute);
  private claimsService = inject(ClaimsService);

  activeTab = 'queue';
  claims$: Observable<ClaimDTO[]> = this.store.select(claimsSelector);
  selectedClaim$: Observable<ClaimDTO | null> = this.store.select(selectedClaimSelector);
  documents$: Observable<ClaimDocumentDTO[]> = this.store.select(claimDocumentsSelector);

  allClaimsList: ClaimDTO[] = [];
  selectedClaim: ClaimDTO | null = null;
  selectedClaimPrice: number = 5000;
  underReviewSettlementPrices: { [claimId: number]: number } = {};

  investigationNotes = '';
  rejectionReason = 'Documentation insufficient to verify incapacitation event';
  actionNotification = '';
  actionError = '';

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['tab']) {
        this.activeTab = params['tab'];
      }
    });

    this.store.dispatch(loadClaims());
    this.refreshClaimsList();

    this.selectedClaim$.subscribe((c) => {
      if (c) {
        this.selectedClaim = { ...c };
        this.selectedClaimPrice = c.approvedAmount || c.claimAmount || 5000;
      }
    });
  }

  setTab(tab: string): void {
    this.activeTab = tab;
  }

  refreshClaimsList(): void {
    this.claimsService.getAllClaims().subscribe({
      next: (list) => {
        this.allClaimsList = list || [];
        this.allClaimsList.forEach(c => {
          if (c.claimId && !this.underReviewSettlementPrices[c.claimId]) {
            this.underReviewSettlementPrices[c.claimId] = c.approvedAmount || c.claimAmount || 25000;
          }
        });
      },
      error: () => {}
    });
  }

  get underReviewClaims(): ClaimDTO[] {
    return this.allClaimsList.filter(c => c.status === 'MANUAL_REVIEW' || c.status === 'INVESTIGATING');
  }

  get underReviewCount(): number {
    return this.underReviewClaims.length;
  }

  onSelectClaim(claim: ClaimDTO): void {
    this.selectedClaim = { ...claim };
    this.selectedClaimPrice = claim.approvedAmount || claim.claimAmount || 25000;
    this.store.dispatch(selectClaim({ claim: { ...claim } }));
    if (claim.claimId) {
      this.store.dispatch(loadDocuments({ claimId: claim.claimId }));
    }
    this.activeTab = 'adjudication';
  }

  onSetPricePreset(amount: number): void {
    this.selectedClaimPrice = amount;
    if (this.selectedClaim) {
      this.selectedClaim.approvedAmount = amount;
      this.selectedClaim.claimAmount = amount;
    }
  }

  onVerifyEvent(claimId?: number): void {
    if (claimId) {
      this.store.dispatch(verifyEvent({ claimId }));
      this.claimsService.verifyEvent(claimId).subscribe({
        next: () => {
          this.actionNotification = `Vital statistics verified for Claim #${claimId}.`;
          this.refreshClaimsList();
          setTimeout(() => this.actionNotification = '', 4000);
        }
      });
    }
  }

  onSendToUnderReview(claimId?: number): void {
    if (!claimId) return;
    this.claimsService.investigateClaim(claimId).subscribe({
      next: () => {
        this.actionNotification = `Claim #${claimId} moved to Under Review section for manual assessment.`;
        this.actionError = '';
        this.store.dispatch(loadClaims());
        this.refreshClaimsList();
        if (this.selectedClaim && this.selectedClaim.claimId === claimId) {
          this.selectedClaim.status = 'MANUAL_REVIEW';
        }
        setTimeout(() => this.actionNotification = '', 4000);
      },
      error: (err) => {
        this.actionError = `Failed to send claim to under review: ${err?.message || 'Error'}`;
      }
    });
  }

  onInvestigate(claimId?: number): void {
    this.onSendToUnderReview(claimId);
  }

  onApproveClaim(claimId?: number, customAmount?: number): void {
    if (!claimId) return;
    const finalAmount = customAmount !== undefined ? customAmount : this.selectedClaimPrice;
    this.claimsService.approveClaim(claimId, finalAmount).subscribe({
      next: () => {
        this.actionNotification = `Claim #${claimId} accepted and approved! Settlement payout of $${finalAmount} authorized.`;
        this.actionError = '';
        this.store.dispatch(loadClaims());
        this.refreshClaimsList();
        if (this.selectedClaim && this.selectedClaim.claimId === claimId) {
          this.selectedClaim.status = 'APPROVED';
          this.selectedClaim.approvedAmount = finalAmount;
        }
        setTimeout(() => this.actionNotification = '', 5000);
      },
      error: (err) => {
        this.actionError = `Failed to approve claim: ${err?.message || 'Error'}`;
      }
    });
  }

  onRejectClaim(claimId?: number): void {
    if (!claimId) return;
    this.claimsService.rejectClaim(claimId, this.rejectionReason).subscribe({
      next: () => {
        this.actionNotification = `Claim #${claimId} rejected.`;
        this.actionError = '';
        this.store.dispatch(loadClaims());
        this.refreshClaimsList();
        if (this.selectedClaim && this.selectedClaim.claimId === claimId) {
          this.selectedClaim.status = 'REJECTED';
          this.selectedClaim.rejectionReason = this.rejectionReason;
        }
        setTimeout(() => this.actionNotification = '', 4000);
      },
      error: (err) => {
        this.actionError = `Failed to reject claim: ${err?.message || 'Error'}`;
      }
    });
  }
}
