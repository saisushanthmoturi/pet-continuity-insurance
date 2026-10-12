import { ComponentFixture, TestBed } from "@angular/core/testing";
import { provideRouter } from "@angular/router";
import { provideStore, Store } from "@ngrx/store";
import { RegisterComponent } from "./register-component";
import { loginReducer } from "../../state/login.reducer";
import { customerReducer } from "../../../customer/state/customer.reducer";
import { signup } from "../../state/login.action";

describe("RegisterComponent", () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let store: Store;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        provideRouter([]),
        provideStore({
          login: loginReducer,
          customer: customerReducer
        })
      ]
    }).compileComponents();

    store = TestBed.inject(Store);
    vi.spyOn(store, "dispatch");

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it("should create register component", () => {
    expect(component).toBeTruthy();
  });

  it("should dispatch signup action on valid onSubmit", () => {
    component.fullName = "Alice Wonderland";
    component.email = "alice.unique@example.com";
    component.password = "Secr3t!Pass";
    component.role = "CUSTOMER";

    component.onSubmit();

    expect(localStorage.getItem("user_registered_name_alice.unique@example.com")).toBe("Alice Wonderland");
    expect(store.dispatch).toHaveBeenCalledWith(signup({
      userReq: {
        fullName: "Alice Wonderland",
        email: "alice.unique@example.com",
        password: "Secr3t!Pass",
        role: "CUSTOMER"
      }
    }));
  });

  it("should not dispatch signup action if fields are missing", () => {
    vi.clearAllMocks();
    component.fullName = "";
    component.email = "alice@example.com";
    component.password = "";

    component.onSubmit();

    expect(store.dispatch).not.toHaveBeenCalled();
  });

  it("should reject username that is entirely numbers", () => {
    vi.clearAllMocks();
    component.fullName = "12345678";
    component.email = "newuser@example.com";
    component.password = "Secr3t!Pass";

    component.onSubmit();

    expect(store.dispatch).not.toHaveBeenCalled();
    expect(component.fullNameError).toContain("cannot consist entirely of numbers");
  });

  it("should reject email that is already registered", () => {
    vi.clearAllMocks();
    component.fullName = "Johnathan Doe";
    component.email = "john.doe@example.com";
    component.password = "Secr3t!Pass";

    component.onSubmit();

    expect(store.dispatch).not.toHaveBeenCalled();
    expect(component.emailError).toContain("already exists");
  });

  it("should reject submission if confirm password does not match password", () => {
    vi.clearAllMocks();
    component.fullName = "Valid User";
    component.email = "valid.user@example.com";
    component.password = "Password123!";
    component.confirmPassword = "DifferentPass!";

    component.onSubmit();

    expect(store.dispatch).not.toHaveBeenCalled();
    expect(component.confirmPasswordError).toBe("Passwords do not match.");
  });
});
