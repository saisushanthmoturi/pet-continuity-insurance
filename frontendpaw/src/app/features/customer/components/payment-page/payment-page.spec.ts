import { ComponentFixture, TestBed } from "@angular/core/testing";
import { provideRouter, Router, ActivatedRoute } from "@angular/router";
import { of, throwError } from "rxjs";
import { PaymentPageComponent } from "./payment-page";
import { CustomerService } from "../../services/customer-service";

describe("PaymentPageComponent", () => {
  let component: PaymentPageComponent;
  let fixture: ComponentFixture<PaymentPageComponent>;
  let customerServiceMock: any;
  let router: Router;

  beforeEach(async () => {
    localStorage.clear();
    customerServiceMock = {
      payAndActivatePolicy: vi.fn().mockReturnValue(of({ status: "ACTIVE" })),
      activatePolicy: vi.fn().mockReturnValue(of({ status: "ACTIVE" }))
    };

    await TestBed.configureTestingModule({
      imports: [PaymentPageComponent],
      providers: [
        provideRouter([]),
        { provide: CustomerService, useValue: customerServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({
              policyId: "5",
              policyNumber: "POL-005",
              coverage: "30000",
              amount: "90"
            })
          }
        }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    vi.spyOn(router, "navigate");

    fixture = TestBed.createComponent(PaymentPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it("should create payment page component", () => {
    expect(component).toBeTruthy();
    expect(component.policyId).toBe(5);
    expect(component.policyNumber).toBe("POL-005");
    expect(component.amount).toBe(90);
  });

  it("should process payment successfully and set paymentSuccess flag", () => {
    component.onPayNow();

    expect(customerServiceMock.payAndActivatePolicy).toHaveBeenCalledWith(5, 1, 90);
    expect(component.paymentSuccess).toBe(true);
    expect(component.isProcessing).toBe(false);
    expect(component.transactionRef).toContain("TXN-");
  });

  it("should handle error by fallback activation", () => {
    customerServiceMock.payAndActivatePolicy.mockReturnValue(throwError(() => new Error("Network error")));

    component.onPayNow();

    expect(customerServiceMock.activatePolicy).toHaveBeenCalledWith(5);
    expect(component.paymentSuccess).toBe(true);
  });

  it("should navigate back to policies tab on returnToPolicies", () => {
    component.returnToPolicies();
    expect(router.navigate).toHaveBeenCalledWith(["/customer"], { queryParams: { tab: "policies" } });
  });
});
