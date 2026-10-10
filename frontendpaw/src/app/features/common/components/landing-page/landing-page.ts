import { Component, inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Router, RouterModule } from "@angular/router";
import { Store } from "@ngrx/store";
import { Observable } from "rxjs";
import { isLoggedInSelector, roleSelector } from "../../../auth/state/login.selector";

interface PetShowcaseItem {
  id: number;
  name: string;
  species: "dog" | "cat" | "senior";
  breed: string;
  age: string;
  highlight: string;
  careFund: number;
  avatarColor: string;
  badge: string;
}

interface FaqItem {
  question: string;
  answer: string;
  open: boolean;
}

@Component({
  selector: "app-landing-page",
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: "./landing-page.html",
  styleUrls: ["./landing-page.css"]
})
export class LandingPageComponent implements OnInit {
  private store = inject(Store);
  private router = inject(Router);

  isLoggedIn$: Observable<boolean> = this.store.select(isLoggedInSelector);
  role$: Observable<string> = this.store.select(roleSelector);

  // Interactive Care Estimator State
  selectedPetType: "dog" | "cat" | "senior" = "dog";
  petAgeYears = 3;
  monthlyExpense = 120;
  estimatedFund = 21600;
  estimatedMonthlyRate = 64.80;
  estimatedEmergencyReserve = 5000;

  // Active pet showcase category filter
  selectedShowcase: "all" | "dogs" | "cats" | "seniors" = "all";

  // Showcase pets collection
  pets: PetShowcaseItem[] = [
    {
      id: 1,
      name: "Sheru",
      species: "dog",
      breed: "Golden Retriever",
      age: "4 Years",
      highlight: "Active companion with daily park walks and specialized joint nutrition.",
      careFund: 28000,
      avatarColor: "#FE3082",
      badge: "Full Trust Active"
    },
    {
      id: 2,
      name: "Milo",
      species: "cat",
      breed: "Scottish Fold",
      age: "2 Years",
      highlight: "Curious indoor explorer requiring premium renal care and gentle guardian.",
      careFund: 18500,
      avatarColor: "#75013F",
      badge: "Guardian Assigned"
    },
    {
      id: 3,
      name: "Bella",
      species: "dog",
      breed: "German Shepherd",
      age: "6 Years",
      highlight: "Loyal protector with ongoing mobility therapy and veterinary monitoring.",
      careFund: 32000,
      avatarColor: "#B8215F",
      badge: "Vet Escrow Protected"
    },
    {
      id: 4,
      name: "Luna",
      species: "cat",
      breed: "Ragdoll",
      age: "3 Years",
      highlight: "Affectionate family feline with complete grooming directives and backup care.",
      careFund: 16000,
      avatarColor: "#FE3082",
      badge: "Full Trust Active"
    },
    {
      id: 5,
      name: "Barnaby",
      species: "senior",
      breed: "Beagle Senior",
      age: "11 Years",
      highlight: "Gentle senior requiring daily medication schedules and quiet retirement home.",
      careFund: 35000,
      avatarColor: "#75013F",
      badge: "Senior Care Priority"
    },
    {
      id: 6,
      name: "Cleo",
      species: "senior",
      breed: "Persian Senior",
      age: "10 Years",
      highlight: "Calm long-haired senior with comprehensive ophthalmic reserve fund.",
      careFund: 22000,
      avatarColor: "#B8215F",
      badge: "Continuous Reserve"
    }
  ];

  // FAQs collection
  faqs: FaqItem[] = [
    {
      question: "What is Pet Continuity Insurance and how is it different from standard pet insurance?",
      answer: "Standard pet health insurance only covers veterinary accidents and sickness while you care for your pet. PawContinuity is a legal fiduciary continuity trust: if you pass away, become incapacitated, or are unable to care for your pet, our platform legally secures a funded care trust, dispatches monthly allowances to your chosen caretakers, and oversees their health for the rest of their natural life.",
      open: true
    },
    {
      question: "Can I choose my own caretaker or does PawContinuity provide one?",
      answer: "Both! You designate a primary caretaker and an optional backup caretaker (friends, family, or partners). If your designated guardians become unavailable, our verified network of licensed caretakers and partner sanctuaries steps in to ensure zero interruption in love and care.",
      open: false
    },
    {
      question: "How does the Care Fund monthly allowance work?",
      answer: "Upon claim verification (e.g. owner loss or incapacity), the designated caretaker receives an automated monthly allowance for pet food, grooming, and standard upkeep, while veterinary funds remain held in an escrow reserve exclusively payable to licensed animal hospitals.",
      open: false
    },
    {
      question: "Are senior pets or pets with pre-existing conditions eligible?",
      answer: "Yes! Our actuarial underwriting engine customizes care funds based on age and health history rather than blanket denial. Senior companions receive specialized continuity terms to guarantee their golden years.",
      open: false
    }
  ];

  ngOnInit(): void {
    this.recalculateEstimate();
  }

  setPetType(type: "dog" | "cat" | "senior"): void {
    this.selectedPetType = type;
    if (type === "cat") {
      this.monthlyExpense = 85;
      this.petAgeYears = 2;
    } else if (type === "senior") {
      this.monthlyExpense = 190;
      this.petAgeYears = 10;
    } else {
      this.monthlyExpense = 130;
      this.petAgeYears = 3;
    }
    this.recalculateEstimate();
  }

  onAgeChange(val?: number): void {
    if (val !== undefined && val !== null) { this.petAgeYears = Number(val); }
    this.recalculateEstimate();
  }

  onExpenseChange(val?: number): void {
    if (val !== undefined && val !== null) { this.monthlyExpense = Number(val); }
    this.recalculateEstimate();
  }

  recalculateEstimate(): void {
    const remainingYears = Math.max(2, (this.selectedPetType === "cat" ? 16 : 14) - this.petAgeYears);
    const annualCost = this.monthlyExpense * 12;
    this.estimatedFund = annualCost * remainingYears;
    this.estimatedEmergencyReserve = Math.round(this.estimatedFund * 0.22);
    // Dynamic monthly premium: roughly 0.3% of the target care trust
    this.estimatedMonthlyRate = Math.round((this.estimatedFund * 0.003) * 100) / 100;
  }

  setShowcaseFilter(filter: "all" | "dogs" | "cats" | "seniors"): void {
    this.selectedShowcase = filter;
  }

  get filteredPets(): PetShowcaseItem[] {
    if (this.selectedShowcase === "all") return this.pets;
    if (this.selectedShowcase === "dogs") return this.pets.filter(p => p.species === "dog");
    if (this.selectedShowcase === "cats") return this.pets.filter(p => p.species === "cat");
    if (this.selectedShowcase === "seniors") return this.pets.filter(p => p.species === "senior");
    return this.pets;
  }

  toggleFaq(index: number): void {
    this.faqs[index].open = !this.faqs[index].open;
  }

  getDashboardLink(role: string): string {
    if (role === "ROLE_UNDERWRITER" || role === "UNDERWRITER") return "/underwriter";
    if (role === "ROLE_CLAIMS_ADJUSTER" || role === "CLAIMS_ADJUSTER") return "/claims";
    if (role === "ROLE_CARETAKER" || role === "CARETAKER") return "/caretaker";
    if (role === "ROLE_ADMIN" || role === "ADMIN") return "/admin";
    return "/customer";
  }

  scrollToEstimator(): void {
    const el = document.getElementById("care-estimator-section");
    if (el && typeof el.scrollIntoView === "function") {
      el.scrollIntoView({ behavior: "smooth" });
    }
  }
}
