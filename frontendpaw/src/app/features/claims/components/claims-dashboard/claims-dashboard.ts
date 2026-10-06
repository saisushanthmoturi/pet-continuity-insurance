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

  activeTab = 'queue';
  claims$: Observable<ClaimDTO[]> = this.store.select(claimsSelector);
  selectedClaim$: Observable<ClaimDTO | null> = this.store.select(selectedClaimSelector);
  documents$: Observable<ClaimDocumentDTO[]> = this.store.select(claimDocumentsSelector);

  investigationNotes = '';
  rejectionReason = 'Documentation insufficient to verify incapacitation event';

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['tab']) {
        this.activeTab = params['tab'];
      }
    });

    this.store.dispatch(loadClaims());
  }

  setTab(tab: string): void {
    this.activeTab = tab;
  }

  onSelectClaim(claim: ClaimDTO): void {
    this.store.dispatch(selectClaim({ claim }));
    if (claim.claimId) {
      this.store.dispatch(loadDocuments({ claimId: claim.claimId }));
    }
    this.activeTab = 'adjudication';
  }

  onVerifyEvent(claimId?: number): void {
    if (claimId) {
      this.store.dispatch(verifyEvent({ claimId }));
    }
  }

  onInvestigate(claimId?: number): void {
    if (claimId) {
      this.store.dispatch(investigateClaim({ claimId }));
    }
  }

  onApproveClaim(claimId?: number): void {
    if (claimId) {
      this.store.dispatch(approveClaim({ claimId }));
    }
  }

  onRejectClaim(claimId?: number): void {
    if (claimId) {
      this.store.dispatch(rejectClaim({ claimId, reason: this.rejectionReason }));
    }
  }
}
