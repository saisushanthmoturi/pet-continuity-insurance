import { ComponentFixture, TestBed } from "@angular/core/testing";
import { provideRouter } from "@angular/router";
import { provideStore, Store } from "@ngrx/store";
import { LoginComponent } from "./login-component";
import { loginReducer } from "../../state/login.reducer";
import { customerReducer } from "../../../customer/state/customer.reducer";
import { login } from "../../state/login.action";

describe("LoginComponent", () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let store: Store;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
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

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it("should create login component", () => {
    expect(component).toBeTruthy();
  });

  it("should populate demo credentials on fillDemo", () => {
    component.fillDemo("customer");
    expect(component.email).toBe("john.doe@example.com");
    expect(component.password).toBe("Password123!");

    component.fillDemo("underwriter");
    expect(component.email).toBe("underwriter@pawcontinuity.com");

    component.fillDemo("claims");
    expect(component.email).toBe("adjuster@pawcontinuity.com");

    component.fillDemo("caretaker");
    expect(component.email).toBe("caretaker@pawcontinuity.com");

    component.fillDemo("admin");
    expect(component.email).toBe("admin@pawcontinuity.com");
  });

  it("should dispatch login action on onSubmit with credentials", () => {
    component.email = "test@example.com";
    component.password = "secretpass";

    component.onSubmit();

    expect(store.dispatch).toHaveBeenCalledWith(login({
      jwtReq: {
        email: "test@example.com",
        password: "secretpass"
      }
    }));
  });

  it("should not dispatch login action if credentials are missing", () => {
    vi.clearAllMocks();
    component.email = "";
    component.password = "";

    component.onSubmit();

    expect(store.dispatch).not.toHaveBeenCalled();
  });
});
