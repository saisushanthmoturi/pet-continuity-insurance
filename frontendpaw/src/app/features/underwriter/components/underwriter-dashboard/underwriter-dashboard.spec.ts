import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ActivatedRoute } from "@angular/router";
import { provideStore, Store } from "@ngrx/store";
import { of } from "rxjs";
import { UnderwriterDashboardComponent } from "./underwriter-dashboard";
import { underwriterReducer } from "../../state/underwriter.reducer";
import { UnderwriterService } from "../../services/underwriter-service";
import { approveQuote, rejectQuote, createRatingRule } from "../../state/underwriter.actions";

describe("UnderwriterDashboardComponent", () => {
  let component: UnderwriterDashboardComponent;
  let fixture: ComponentFixture<UnderwriterDashboardComponent>;
  let underwriterServiceMock: any;
  let store: Store;

  beforeEach(async () => {
    localStorage.clear();
    underwriterServiceMock = {
      reassessPetRisk: vi.fn().mockReturnValue(of({
        petId: 1,
        updatedRiskScore: 1.15,
        recommendation: "STANDARD_APPROVAL"
      }))
    };

    await TestBed.configureTestingModule({
      imports: [UnderwriterDashboardComponent],
      providers: [
        provideStore({
          underwriter: underwriterReducer
        }),
        { provide: UnderwriterService, useValue: underwriterServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({ tab: "queue" })
          }
        }
      ]
    }).compileComponents();

    store = TestBed.inject(Store);
    vi.spyOn(store, "dispatch");

    fixture = TestBed.createComponent(UnderwriterDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it("should create underwriter dashboard component", () => {
    expect(component).toBeTruthy();
    expect(component.activeTab).toBe("queue");
  });

  it("should switch tabs via setTab", () => {
    component.setTab("rules");
    expect(component.activeTab).toBe("rules");
  });

  it("should dispatch approveQuote when onApproveQuote is called", () => {
    component.onApproveQuote(15);
    expect(store.dispatch).toHaveBeenCalledWith(approveQuote({ quoteId: 15 }));
  });

  it("should dispatch rejectQuote when onRejectQuote is called", () => {
    component.onRejectQuote(15);
    expect(store.dispatch).toHaveBeenCalledWith(rejectQuote({
      quoteId: 15,
      reason: "High actuarial mortality risk ratio"
    }));
  });

  it("should dispatch createRatingRule when onCreateRule is called", () => {
    component.newRule = {
      ruleCode: "FELINE_SENIOR",
      speciesCode: "CAT",
      breedCode: "ALL",
      minAgeMonths: 96,
      maxAgeMonths: 200,
      baseRate: 40.0,
      multiplier: 1.25,
      description: "Senior feline loading"
    };

    component.onCreateRule();
    expect(store.dispatch).toHaveBeenCalled();
  });

  it("should call reassessPetRisk and store reassessmentResult on onReassessPet", () => {
    component.reassessPetId = 5;
    component.onReassessPet();

    expect(underwriterServiceMock.reassessPetRisk).toHaveBeenCalledWith(5);
    expect(component.reassessmentResult).toBeTruthy();
    expect(component.reassessing).toBe(false);
  });
});
