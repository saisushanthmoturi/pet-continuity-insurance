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
    component.email = "alice@example.com";
    component.password = "Secr3t!Pass";
    component.role = "CUSTOMER";

    component.onSubmit();

    expect(localStorage.getItem("user_registered_name_alice@example.com")).toBe("Alice Wonderland");
    expect(store.dispatch).toHaveBeenCalledWith(signup({
      userReq: {
        fullName: "Alice Wonderland",
        email: "alice@example.com",
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
});
