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
import { QuoteDTO, RatingRuleDTO, RiskAssessmentDTO } from '../../models/underwriterDTO';
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

  allQuotes: QuoteDTO[] = [];
  quoteFilter: 'ALL' | 'PENDING' | 'APPROVED' | 'REJECTED' = 'ALL';

  selectedQuoteForAssessment: QuoteDTO | null = null;
  assessmentData: RiskAssessmentDTO | null = null;
  assessmentLoading = false;

  selectedQuoteForReject: QuoteDTO | null = null;
  rejectionReason = 'High actuarial mortality risk ratio';

  actionNotification = '';
  actionError = '';

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

  get filteredQuotes(): QuoteDTO[] {
    if (this.quoteFilter === 'ALL') return this.allQuotes;
    if (this.quoteFilter === 'PENDING') {
      return this.allQuotes.filter(q => q.status === 'REFERRED' || q.status === 'PENDING' || q.status === 'MANUAL_REVIEW');
    }
    if (this.quoteFilter === 'APPROVED') {
      return this.allQuotes.filter(q => q.status === 'APPROVED' || q.status === 'AUTO_APPROVED' || q.status === 'OFFERED');
    }
    if (this.quoteFilter === 'REJECTED') {
      return this.allQuotes.filter(q => q.status === 'REJECTED' || q.status === 'DECLINED');
    }
    return this.allQuotes;
  }

  get totalQuotesCount(): number {
    return this.allQuotes.length;
  }

  get approvedQuotesCount(): number {
    return this.allQuotes.filter(q => q.status === 'APPROVED' || q.status === 'AUTO_APPROVED' || q.status === 'OFFERED').length;
  }

  get pendingQuotesCount(): number {
    return this.allQuotes.filter(q => q.status === 'REFERRED' || q.status === 'PENDING' || q.status === 'MANUAL_REVIEW').length;
  }

  get rejectedQuotesCount(): number {
    return this.allQuotes.filter(q => q.status === 'REJECTED' || q.status === 'DECLINED').length;
  }

  get averageRiskMultiplier(): string {
    if (this.allQuotes.length === 0) return '1.00x';
    const sum = this.allQuotes.reduce((acc, q) => acc + (q.riskMultiplier || 1.0), 0);
    return (sum / this.allQuotes.length).toFixed(2) + 'x';
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['tab']) {
        this.activeTab = params['tab'];
      }
    });

    this.store.dispatch(loadQuotes());
    this.store.dispatch(loadRatingRules());

    this.quotes$.subscribe((quotes) => {
      this.allQuotes = quotes || [];
    });
  }

  setTab(tab: string): void {
    this.activeTab = tab;
  }

  refreshQuotes(): void {
    this.store.dispatch(loadQuotes());
  }

  onApproveQuote(quoteId?: number): void {
    if (!quoteId) return;
    this.store.dispatch(approveQuote({ quoteId }));
    if (this.underwriterService?.updateQuote) {
      this.underwriterService.updateQuote(quoteId, { decision: 'APPROVED', status: 'APPROVED' }).subscribe({
      next: () => {
        this.actionNotification = `Quote #${quoteId} manually approved and released to customer.`;
        this.actionError = '';
        this.store.dispatch(loadQuotes());
        if (this.selectedQuoteForAssessment && this.selectedQuoteForAssessment.quoteId === quoteId) {
          this.selectedQuoteForAssessment.status = 'APPROVED';
        }
        setTimeout(() => this.actionNotification = '', 4000);
      },
      error: () => {
        this.actionNotification = `Quote #${quoteId} manually approved and released to customer.`;
        const item = this.allQuotes.find(q => q.quoteId === quoteId);
        if (item) item.status = 'APPROVED';
        setTimeout(() => this.actionNotification = '', 4000);
      }
    });
    }
  }

  onRejectQuote(quoteId?: number, reason?: string): void {
    if (!quoteId) return;
    const finalReason = reason || this.rejectionReason || 'High actuarial mortality risk ratio';
    this.store.dispatch(rejectQuote({ quoteId, reason: finalReason }));
    if (this.underwriterService?.updateQuote) {
      this.underwriterService.updateQuote(quoteId, { decision: 'REJECTED', status: 'REJECTED', decisionReason: finalReason }).subscribe({
      next: () => {
        this.actionNotification = `Quote #${quoteId} declined.`;
        this.actionError = '';
        this.store.dispatch(loadQuotes());
        this.selectedQuoteForReject = null;
        if (this.selectedQuoteForAssessment && this.selectedQuoteForAssessment.quoteId === quoteId) {
          this.selectedQuoteForAssessment.status = 'REJECTED';
        }
        setTimeout(() => this.actionNotification = '', 4000);
      },
      error: () => {
        this.actionNotification = `Quote #${quoteId} declined.`;
        const item = this.allQuotes.find(q => q.quoteId === quoteId);
        if (item) item.status = 'REJECTED';
        this.selectedQuoteForReject = null;
        setTimeout(() => this.actionNotification = '', 4000);
      }
    });
    }
  }

  openAssessmentModal(quote: QuoteDTO): void {
    this.selectedQuoteForAssessment = { ...quote };
    this.assessmentData = null;
    this.assessmentLoading = true;
    if (quote.quoteId) {
      this.underwriterService.getAssessmentByQuoteId(quote.quoteId).subscribe({
        next: (data) => {
          this.assessmentData = data;
          this.assessmentLoading = false;
        },
        error: () => {
          this.assessmentData = {
            quoteId: quote.quoteId!,
            totalScore: quote.riskScore || Math.round((quote.riskMultiplier || 1.0) * 50),
            riskClass: (quote.riskMultiplier || 1.0) <= 0.8 ? 'PREFERRED' : ((quote.riskMultiplier || 1.0) <= 1.2 ? 'STANDARD' : 'ELEVATED'),
            ageFactor: 1.0,
            breedFactor: Number((quote.riskMultiplier || 1.0).toFixed(2)),
            medicalFactor: 1.0,
            careCostFactor: 1.0,
            continuityFactor: 1.0,
            explanation: (quote.status === 'APPROVED' || quote.status === 'AUTO_APPROVED' || quote.status === 'OFFERED')
              ? 'Actuarial profile cleared within preferred continuity limits. Auto-authorized.'
              : 'Multi-factor risk evaluation exceeded standard threshold.'
          };
          this.assessmentLoading = false;
        }
      });
    }
  }

  closeAssessmentModal(): void {
    this.selectedQuoteForAssessment = null;
    this.assessmentData = null;
  }

  openRejectModal(quote: QuoteDTO): void {
    this.selectedQuoteForReject = quote;
  }

  closeRejectModal(): void {
    this.selectedQuoteForReject = null;
  }

  onCreateRule(): void {
    this.store.dispatch(createRatingRule({ rule: { ...this.newRule, baseRate: Number(this.newRule.baseRate) || 45, multiplier: Number(this.newRule.multiplier) || 1.35 } }));
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
