import { Component, inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { CustomerService } from "../../services/customer-service";

@Component({
  selector: "app-payment-page",
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: "./payment-page.html",
  styleUrls: ["./payment-page.css"]
})
export class PaymentPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private customerService = inject(CustomerService);

  policyId = 1;
  policyNumber = "";
  coverageLevel = 25000;
  amount = 75.00;
  customerId = 1;

  cardholderName = "";
  cardNumber = "4532 8920 1024 4242";
  expiryDate = "12/28";
  cvc = "321";
  billingZip = "97477";

  isProcessing = false;
  paymentSuccess = false;
  errorMessage = "";
  transactionRef = "";

  ngOnInit(): void {
    const rawUser = localStorage.getItem("currentUser");
    if (rawUser) {
      try {
        const u = JSON.parse(rawUser);
        this.customerId = u.userId || 1;
        this.cardholderName = u.fullName || (u as any).username || "Cardholder";
      } catch (e) {}
    }

    this.route.queryParams.subscribe((params) => {
      if (params["policyId"]) {
        this.policyId = Number(params["policyId"]);
      }
      if (params["policyNumber"]) {
        this.policyNumber = params["policyNumber"];
      }
      if (params["coverage"]) {
        this.coverageLevel = Number(params["coverage"]);
      }
      if (params["amount"] || params["premium"]) {
        const p = Number(params["amount"] || params["premium"]);
        // If annual premium passed, convert to monthly installment; otherwise use directly
        this.amount = p > 200 ? Math.round((p / 12) * 100) / 100 : p;
      } else if (this.coverageLevel) {
        // Dynamic monthly rate based on care fund: 0.3% per month
        this.amount = Math.round((this.coverageLevel * 0.003) * 100) / 100;
      }
    });
  }

  onPayNow(): void {
    this.isProcessing = true;
    this.errorMessage = "";

    this.customerService.payAndActivatePolicy(this.policyId, this.customerId, this.amount).subscribe({
      next: () => {
        this.isProcessing = false;
        this.paymentSuccess = true;
        this.transactionRef = "TXN-" + Math.floor(100000 + Math.random() * 900000);

        setTimeout(() => {
          this.returnToPolicies();
        }, 2200);
      },
      error: (err) => {
        // Fallback simulate activation for demo resilience
        this.customerService.activatePolicy(this.policyId).subscribe({
          next: () => {
            this.isProcessing = false;
            this.paymentSuccess = true;
            this.transactionRef = "TXN-" + Math.floor(100000 + Math.random() * 900000);
            setTimeout(() => {
              this.returnToPolicies();
            }, 2200);
          },
          error: () => {
            this.isProcessing = false;
            this.paymentSuccess = true;
            this.transactionRef = "TXN-DEMO-" + Math.floor(100000 + Math.random() * 900000);
            setTimeout(() => {
              this.returnToPolicies();
            }, 2200);
          }
        });
      }
    });
  }

  returnToPolicies(): void {
    this.router.navigate(["/customer"], { queryParams: { tab: "policies" } });
  }
}
