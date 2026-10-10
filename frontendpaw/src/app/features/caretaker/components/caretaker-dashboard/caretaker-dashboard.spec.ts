import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ActivatedRoute } from "@angular/router";
import { provideStore, Store } from "@ngrx/store";
import { of } from "rxjs";
import { CaretakerDashboardComponent } from "./caretaker-dashboard";
import { caretakerReducer } from "../../state/caretaker.reducer";
import { CaretakerService } from "../../services/caretaker-service";
import { verifyCustody, disburseAllowance, submitExpense, transferBackup } from "../../state/caretaker.actions";

describe("CaretakerDashboardComponent", () => {
  let component: CaretakerDashboardComponent;
  let fixture: ComponentFixture<CaretakerDashboardComponent>;
  let caretakerServiceMock: any;
  let store: Store;

  beforeEach(async () => {
    localStorage.clear();
    caretakerServiceMock = {
      submitPetVerification: vi.fn().mockReturnValue(of({ status: "VERIFIED" })),
      disburseMonthlyAllowance: vi.fn().mockReturnValue(of({ amount: 800 })),
      submitExpense: vi.fn().mockReturnValue(of({ amount: 150 })),
      transferToBackup: vi.fn().mockReturnValue(of({ transferred: true }))
    };

    await TestBed.configureTestingModule({
      imports: [CaretakerDashboardComponent],
      providers: [
        provideStore({
          caretaker: caretakerReducer
        }),
        { provide: CaretakerService, useValue: caretakerServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({ tab: "custody" })
          }
        }
      ]
    }).compileComponents();

    store = TestBed.inject(Store);
    vi.spyOn(store, "dispatch");

    fixture = TestBed.createComponent(CaretakerDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it("should create caretaker dashboard component", () => {
    expect(component).toBeTruthy();
    expect(component.activeTab).toBe("custody");
  });

  it("should switch tabs via setTab", () => {
    component.setTab("expenses");
    expect(component.activeTab).toBe("expenses");
  });

  it("should dispatch verifyCustody when onVerifyCustody is called", () => {
    component.onVerifyCustody();
    expect(store.dispatch).toHaveBeenCalledWith(verifyCustody({
      verification: component.verificationModel
    }));
    expect(component.custodyVerified).toBe(true);
  });

  it("should dispatch disburseAllowance when onDisburseAllowance is called", () => {
    component.onDisburseAllowance();
    expect(store.dispatch).toHaveBeenCalledWith(disburseAllowance({
      fundId: 1,
      caretakerId: 1
    }));
  });

  it("should dispatch submitExpense and reset form when onSubmitExpense is called", () => {
    component.newExpense = {
      fundId: 1,
      category: "FOOD",
      amount: 75,
      receiptUrl: "https://example.com/receipt.pdf",
      description: "Monthly pet food"
    };

    component.onSubmitExpense();
    expect(store.dispatch).toHaveBeenCalledWith(submitExpense({
      fundId: 1,
      expense: {
        fundId: 1,
        category: "FOOD",
        amount: 75,
        receiptUrl: "https://example.com/receipt.pdf",
        description: "Monthly pet food"
      }
    }));
    expect(component.newExpense.amount).toBe(0);
  });

  it("should dispatch transferBackup when onTransferBackup is called", () => {
    component.onTransferBackup();
    expect(store.dispatch).toHaveBeenCalledWith(transferBackup({
      transfer: component.backupTransfer
    }));
    expect(component.transferSuccess).toBe(true);
  });
});
