import { ComponentFixture, TestBed } from "@angular/core/testing";
import { provideRouter } from "@angular/router";
import { provideStore } from "@ngrx/store";
import { HeaderComponent } from "./header-component";
import { loginReducer } from "../../../auth/state/login.reducer";
import { customerReducer } from "../../../customer/state/customer.reducer";

describe("HeaderComponent", () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [HeaderComponent],
      providers: [
        provideRouter([{ path: "auth/login", component: class DummyComponent {} }]),
        provideStore({
          login: loginReducer,
          customer: customerReducer
        })
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it("should create header component", () => {
    expect(component).toBeTruthy();
  });

  it("should return correct dashboard link based on role", () => {
    expect(component.getDashboardLink("ROLE_UNDERWRITER")).toBe("/underwriter");
    expect(component.getDashboardLink("UNDERWRITER")).toBe("/underwriter");
    expect(component.getDashboardLink("ROLE_CLAIMS_ADJUSTER")).toBe("/claims");
    expect(component.getDashboardLink("CLAIMS_ADJUSTER")).toBe("/claims");
    expect(component.getDashboardLink("ROLE_CARETAKER")).toBe("/caretaker");
    expect(component.getDashboardLink("CARETAKER")).toBe("/caretaker");
    expect(component.getDashboardLink("ROLE_ADMIN")).toBe("/admin");
    expect(component.getDashboardLink("ADMIN")).toBe("/admin");
    expect(component.getDashboardLink("ROLE_CUSTOMER")).toBe("/customer");
    expect(component.getDashboardLink("CUSTOMER")).toBe("/customer");
  });

  it("should format role string nicely", () => {
    expect(component.formatRole("ROLE_CLAIMS_ADJUSTER")).toBe("CLAIMS ADJUSTER");
    expect(component.formatRole("ROLE_ADMIN")).toBe("ADMIN");
    expect(component.formatRole("")).toBe("");
  });

  it("should detect auth pages", () => {
    component.currentUrl = "/auth/login";
    expect(component.isAuthPage()).toBe(true);

    component.currentUrl = "/auth/register";
    expect(component.isAuthPage()).toBe(true);

    component.currentUrl = "/customer";
    expect(component.isAuthPage()).toBe(false);
  });

  it("should detect active tabs correctly", () => {
    component.currentUrl = "/customer?tab=pets";
    expect(component.isRoleTabActive("/customer", "pets", "overview")).toBe(true);
    expect(component.isRoleTabActive("/customer", "claims", "overview")).toBe(false);

    component.currentUrl = "/customer";
    expect(component.isRoleTabActive("/customer", "overview", "overview")).toBe(true);
  });

  it("should perform logout cleaning localStorage and dispatching state", () => {
    localStorage.setItem("token", "dummy");
    localStorage.setItem("role", "CUSTOMER");
    localStorage.setItem("currentUser", "{}");

    component.onLogout();

    expect(localStorage.getItem("token")).toBeNull();
    expect(localStorage.getItem("role")).toBeNull();
    expect(localStorage.getItem("currentUser")).toBeNull();
  });
});
