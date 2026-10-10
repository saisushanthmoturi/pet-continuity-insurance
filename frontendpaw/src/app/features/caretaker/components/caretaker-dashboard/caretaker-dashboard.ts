import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  activeFundSelector,
  expensesSelector,
  caretakerStatusSelector
} from '../../state/caretaker.selectors';
import {
  loadFundByPolicy,
  loadExpenses,
  disburseAllowance,
  submitExpense,
  verifyCustody,
  transferBackup
} from '../../state/caretaker.actions';
import {
  ExpenseDTO,
  PetCareFundDTO,
  PetVerificationDTO,
  BackupTransferDTO
} from '../../models/caretakerDTO';
import { CaretakerService } from '../../services/caretaker-service';

@Component({
  selector: 'app-caretaker-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './caretaker-dashboard.html',
  styleUrls: ['./caretaker-dashboard.css']
})
export class CaretakerDashboardComponent implements OnInit {
  private store = inject(Store);
  private route = inject(ActivatedRoute);
  private caretakerService = inject(CaretakerService);

  activeTab = 'custody';
  activePolicyId = 1;
  activeFundId = 1;
  caretakerId = 1;

  activeFund$: Observable<PetCareFundDTO | null> = this.store.select(activeFundSelector);
  expenses$: Observable<ExpenseDTO[]> = this.store.select(expensesSelector);

  // Custody verification model
  verificationModel: PetVerificationDTO = {
    petId: 1,
    caretakerId: 1,
    verificationType: 'PHYSICAL_INSPECTION',
    status: 'CONFIRMED',
    notes: 'Pet safely received into custody. Microchip confirmed. Health stable.'
  };
  custodyVerified = false;

  // New expense model
  newExpense: ExpenseDTO = {
    fundId: 1,
    category: 'VET_CARE',
    amount: 150.00,
    receiptUrl: 'https://receipts.pawcontinuity.com/vet-visit-101.pdf',
    description: 'Wellness check and flea prevention medicine.'
  };

  // Backup transfer model
  backupTransfer: BackupTransferDTO = {
    petId: 1,
    currentCaretakerId: 1,
    backupCaretakerId: 2,
    reason: 'Temporary medical leave, transferring primary custody to backup caretaker.'
  };
  transferSuccess = false;

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['tab']) {
        this.activeTab = params['tab'];
      }
    });

    this.store.dispatch(loadFundByPolicy({ policyId: this.activePolicyId }));
    this.store.dispatch(loadExpenses({ fundId: this.activeFundId }));
  }

  setTab(tab: string): void {
    this.activeTab = tab;
  }

  onVerifyCustody(): void {
    const verPayload: PetVerificationDTO = {
      ...this.verificationModel
    };
    this.store.dispatch(verifyCustody({ verification: verPayload }));
    this.custodyVerified = true;
  }

  onDisburseAllowance(): void {
    this.store.dispatch(disburseAllowance({
      fundId: this.activeFundId,
      caretakerId: this.caretakerId
    }));
  }

  onSubmitExpense(): void {
    const expensePayload: ExpenseDTO = {
      ...this.newExpense,
      fundId: this.activeFundId,
      amount: Number(this.newExpense.amount) || 0
    };
    this.store.dispatch(submitExpense({
      fundId: this.activeFundId,
      expense: expensePayload
    }));
    this.newExpense = {
      fundId: this.activeFundId,
      category: "VET_CARE",
      amount: 0,
      receiptUrl: "",
      description: ""
    };
  }

  onTransferBackup(): void {
    const transferPayload: BackupTransferDTO = {
      ...this.backupTransfer
    };
    this.store.dispatch(transferBackup({ transfer: transferPayload }));
    this.transferSuccess = true;
  }
}
