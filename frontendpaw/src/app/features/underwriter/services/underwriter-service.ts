import { inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, map } from "rxjs";
import { QuoteDTO, RatingRuleDTO, RiskAssessmentDTO } from "../models/underwriterDTO";

@Injectable({
  providedIn: "root"
})
export class UnderwriterService {
  private http = inject(HttpClient);
  private apiUrl = "http://localhost:8080/api/underwriting";

  calculateQuote(payload: any): Observable<QuoteDTO> {
    return this.requestQuote(payload);
  }

  requestQuote(payload: any): Observable<QuoteDTO> {
    const req = {
      customerId: Number(payload.customerId) || 1,
      petId: Number(payload.petId) || 1,
      requestedCoverage: Number(payload.coverageAmount || payload.requestedCoverage || 25000)
    };
    return this.http.post<any>(`${this.apiUrl}/quotes`, req).pipe(
      map((res: any) => ({
        quoteId: res.id || res.quoteId,
        customerId: res.customerId,
        petId: res.petId,
        requestedCoverage: res.requestedCoverage || req.requestedCoverage,
        coverageAmount: res.requestedCoverage || req.requestedCoverage,
        monthlyCareCost: res.monthlyPremium || 600,
        calculatedPremium: res.monthlyPremium ? Math.round(res.monthlyPremium * 12) : 720,
        riskMultiplier: res.riskScore ? Number((res.riskScore / 50).toFixed(2)) : 1.15,
        status: res.decision || res.status || "AUTO_APPROVED"
      }))
    );
  }

  getAllQuotes(): Observable<QuoteDTO[]> {
    return this.http.get<any[]>(`${this.apiUrl}/quotes`).pipe(
      map((list: any[]) =>
        (list || []).map((res) => ({
          quoteId: res.id || res.quoteId,
          customerId: res.customerId,
          petId: res.petId,
          requestedCoverage: res.requestedCoverage,
          coverageAmount: res.requestedCoverage,
          monthlyCareCost: res.monthlyPremium || 600,
          calculatedPremium: res.monthlyPremium ? Math.round(res.monthlyPremium * 12) : 720,
          riskMultiplier: res.riskScore ? Number((res.riskScore / 50).toFixed(2)) : 1.15,
          status: res.decision || res.status || "PENDING"
        }))
      )
    );
  }

  getQuoteById(id: number): Observable<QuoteDTO> {
    return this.http.get<any>(`${this.apiUrl}/quotes/${id}`).pipe(
      map((res: any) => ({
        quoteId: res.id || res.quoteId,
        customerId: res.customerId,
        petId: res.petId,
        requestedCoverage: res.requestedCoverage,
        coverageAmount: res.requestedCoverage,
        monthlyCareCost: res.monthlyPremium || 600,
        calculatedPremium: res.monthlyPremium ? Math.round(res.monthlyPremium * 12) : 720,
        riskMultiplier: res.riskScore ? Number((res.riskScore / 50).toFixed(2)) : 1.15,
        status: res.decision || res.status || "PENDING"
      }))
    );
  }

  getQuotesByCustomerId(customerId: number): Observable<QuoteDTO[]> {
    return this.http.get<any[]>(`${this.apiUrl}/quotes/customer/${customerId}`).pipe(
      map((list: any[]) =>
        (list || []).map((res) => ({
          quoteId: res.id || res.quoteId,
          customerId: res.customerId,
          petId: res.petId,
          requestedCoverage: res.requestedCoverage,
          coverageAmount: res.requestedCoverage,
          monthlyCareCost: res.monthlyPremium || 600,
          calculatedPremium: res.monthlyPremium ? Math.round(res.monthlyPremium * 12) : 720,
          riskMultiplier: res.riskScore ? Number((res.riskScore / 50).toFixed(2)) : 1.15,
          status: res.decision || res.status || "PENDING"
        }))
      )
    );
  }

  updateQuote(id: number, quote: Partial<QuoteDTO>): Observable<QuoteDTO> {
    return this.http.put<QuoteDTO>(`${this.apiUrl}/quotes/${id}`, quote);
  }

  getAssessmentByQuoteId(quoteId: number): Observable<RiskAssessmentDTO> {
    return this.http.get<RiskAssessmentDTO>(`${this.apiUrl}/assessments/quote/${quoteId}`);
  }

  reassessPetRisk(petId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/assessments/reassess/${petId}`, {});
  }

  getAllRules(): Observable<RatingRuleDTO[]> {
    return this.http.get<any[]>(`${this.apiUrl}/rules`).pipe(
      map((list: any[]) =>
        (list || []).map((res) => ({
          ruleId: res.id || res.ruleId,
          ruleCode: res.ruleCode,
          speciesCode: res.speciesCode || "DOG",
          breedCode: res.breedCode || "ALL",
          minAgeMonths: res.minAgeMonths || 0,
          maxAgeMonths: res.maxAgeMonths || 240,
          baseRate: res.baseRate || 45.0,
          multiplier: res.multiplier || 1.25,
          description: res.description || "Actuarial rule"
        }))
      )
    );
  }

  createRule(rule: RatingRuleDTO): Observable<RatingRuleDTO> {
    const payload = {
      ruleCode: rule.ruleCode,
      speciesCode: rule.speciesCode || "DOG",
      breedCode: rule.breedCode || "ALL",
      minAgeMonths: Number(rule.minAgeMonths) || 0,
      maxAgeMonths: Number(rule.maxAgeMonths) || 240,
      baseRate: Number(rule.baseRate) || 45.0,
      multiplier: Number(rule.multiplier) || 1.25,
      description: rule.description || "Actuarial rule"
    };
    return this.http.post<any>(`${this.apiUrl}/rules`, payload).pipe(
      map((res: any) => ({
        ruleId: res.id || res.ruleId,
        ruleCode: res.ruleCode,
        speciesCode: res.speciesCode,
        breedCode: res.breedCode,
        minAgeMonths: res.minAgeMonths,
        maxAgeMonths: res.maxAgeMonths,
        baseRate: res.baseRate,
        multiplier: res.multiplier,
        description: res.description
      }))
    );
  }
}
