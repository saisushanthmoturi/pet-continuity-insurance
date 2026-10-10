import { ComponentFixture, TestBed } from "@angular/core/testing";
import { provideRouter } from "@angular/router";
import { provideStore } from "@ngrx/store";
import { LandingPageComponent } from "./landing-page";
import { loginReducer } from "../../../auth/state/login.reducer";

describe("LandingPageComponent", () => {
  let component: LandingPageComponent;
  let fixture: ComponentFixture<LandingPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandingPageComponent],
      providers: [
        provideRouter([]),
        provideStore({
          login: loginReducer
        })
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LandingPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create the landing page component", () => {
    expect(component).toBeTruthy();
    expect(component.selectedPetType).toBe("dog");
    expect(component.petAgeYears).toBe(3);
    expect(component.monthlyExpense).toBe(120);
    expect(component.pets.length).toBeGreaterThan(0);
    expect(component.faqs.length).toBeGreaterThan(0);
  });

  it("should recalculate care fund when pet type changes to cat", () => {
    component.setPetType("cat");
    expect(component.selectedPetType).toBe("cat");
    expect(component.monthlyExpense).toBe(85);
    expect(component.petAgeYears).toBe(2);
    expect(component.estimatedFund).toBeGreaterThan(0);
    expect(component.estimatedMonthlyRate).toBeGreaterThan(0);
    expect(component.estimatedEmergencyReserve).toBeGreaterThan(0);
  });

  it("should recalculate care fund when pet type changes to senior", () => {
    component.setPetType("senior");
    expect(component.selectedPetType).toBe("senior");
    expect(component.monthlyExpense).toBe(190);
    expect(component.petAgeYears).toBe(10);
    expect(component.estimatedFund).toBeGreaterThan(0);
  });

  it("should update calculations when age changes", () => {
    const originalFund = component.estimatedFund;
    component.onAgeChange(10);
    expect(component.petAgeYears).toBe(10);
    expect(component.estimatedFund).not.toBe(originalFund);
  });

  it("should update calculations when expense changes", () => {
    const originalFund = component.estimatedFund;
    component.onExpenseChange(250);
    expect(component.monthlyExpense).toBe(250);
    expect(component.estimatedFund).not.toBe(originalFund);
  });

  it("should filter pets showcase properly", () => {
    component.setShowcaseFilter("dogs");
    expect(component.selectedShowcase).toBe("dogs");
    expect(component.filteredPets.every(p => p.species === "dog")).toBe(true);

    component.setShowcaseFilter("cats");
    expect(component.filteredPets.every(p => p.species === "cat")).toBe(true);

    component.setShowcaseFilter("seniors");
    expect(component.filteredPets.every(p => p.species === "senior")).toBe(true);

    component.setShowcaseFilter("all");
    expect(component.filteredPets.length).toBe(component.pets.length);
  });

  it("should toggle FAQ item open state", () => {
    const initialStatus = component.faqs[0].open;
    component.toggleFaq(0);
    expect(component.faqs[0].open).toBe(!initialStatus);
    component.toggleFaq(0);
    expect(component.faqs[0].open).toBe(initialStatus);
  });

  it("should return appropriate dashboard path based on user role", () => {
    expect(component.getDashboardLink("ROLE_UNDERWRITER")).toBe("/underwriter");
    expect(component.getDashboardLink("ROLE_CLAIMS_ADJUSTER")).toBe("/claims");
    expect(component.getDashboardLink("ROLE_CARETAKER")).toBe("/caretaker");
    expect(component.getDashboardLink("ROLE_ADMIN")).toBe("/admin");
    expect(component.getDashboardLink("ROLE_CUSTOMER")).toBe("/customer");
    expect(component.getDashboardLink("")).toBe("/customer");
  });

  it("should handle smooth scroll to estimator safely", () => {
    const el = document.getElementById("care-estimator-section");
    if (el) {
      let called = false;
      el.scrollIntoView = () => { called = true; };
      component.scrollToEstimator();
      expect(called).toBe(true);
    } else {
      expect(() => component.scrollToEstimator()).not.toThrow();
    }
  });
});
