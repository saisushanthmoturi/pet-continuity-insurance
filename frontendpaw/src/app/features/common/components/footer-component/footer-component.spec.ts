import { ComponentFixture, TestBed } from "@angular/core/testing";
import { provideRouter } from "@angular/router";
import { FooterComponent } from "./footer-component";

describe("FooterComponent", () => {
  let component: FooterComponent;
  let fixture: ComponentFixture<FooterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FooterComponent],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(FooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create footer component", () => {
    expect(component).toBeTruthy();
    expect(component.currentYear).toBeGreaterThanOrEqual(2025);
  });

  it("should render footer element", () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector("footer")).toBeTruthy();
  });
});
