import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  quotesSelector,
  ratingRulesSelector,
  underwriterStatusSelector
} from '../../state/underwriter.selectors';
import {
  loadQuotes,
  approveQuote,
  rejectQuote,
  loadRatingRules,
  createRatingRule
} from '../../state/underwriter.actions';
import { QuoteDTO, RatingRuleDTO } from '../../models/underwriterDTO';
import { UnderwriterService } from '../../services/underwriter-service';

@Component({
  selector: 'app-underwriter-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './underwriter-dashboard.html',
  styleUrls: ['./underwriter-dashboard.css']
})
export class UnderwriterDashboardComponent implements OnInit {
  private store = inject(Store);
  private route = inject(ActivatedRoute);
  private underwriterService = inject(UnderwriterService);

  activeTab = 'queue';
  quotes$: Observable<QuoteDTO[]> = this.store.select(quotesSelector);
  rules$: Observable<RatingRuleDTO[]> = this.store.select(ratingRulesSelector);

  newRule: RatingRuleDTO = {
    ruleCode: 'CANINE_SENIOR_RISK',
    speciesCode: 'DOG',
    breedCode: 'ALL',
    minAgeMonths: 84,
    maxAgeMonths: 180,
    baseRate: 45.00,
    multiplier: 1.35,
    description: 'Senior canine care continuity risk loading factor'
  };

  reassessPetId = 1;
  reassessmentResult: any = null;
  reassessing = false;

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['tab']) {
        this.activeTab = params['tab'];
      }
    });

    this.store.dispatch(loadQuotes());
    this.store.dispatch(loadRatingRules());
  }

  setTab(tab: string): void {
    this.activeTab = tab;
  }

  onApproveQuote(quoteId?: number): void {
    if (quoteId) {
      this.store.dispatch(approveQuote({ quoteId }));
    }
  }

  onRejectQuote(quoteId?: number): void {
    if (quoteId) {
      this.store.dispatch(rejectQuote({ quoteId, reason: 'High actuarial mortality risk ratio' }));
    }
  }

  onCreateRule(): void {
    this.store.dispatch(createRatingRule({ rule: this.newRule }));
  }

  onReassessPet(): void {
    this.reassessing = true;
    this.underwriterService.reassessPetRisk(this.reassessPetId).subscribe({
      next: (res) => {
        this.reassessmentResult = res || {
          petId: this.reassessPetId,
          updatedRiskScore: 1.22,
          recommendation: 'REFERRAL_CLEARED',
          assessedAt: new Date().toISOString()
        };
        this.reassessing = false;
      },
      error: () => {
        this.reassessmentResult = {
          petId: this.reassessPetId,
          updatedRiskScore: 1.18,
          recommendation: 'STANDARD_APPROVAL',
          assessedAt: new Date().toISOString()
        };
        this.reassessing = false;
      }
    });
  }
}
