import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { QuoteDTO, RatingRuleDTO, RiskAssessmentDTO } from '../models/underwriterDTO';

@Injectable({
  providedIn: 'root'
})
export class UnderwriterService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/underwriting';

  requestQuote(payload: QuoteDTO): Observable<QuoteDTO> {
    return this.http.post<QuoteDTO>(`${this.apiUrl}/quotes`, payload);
  }

  getAllQuotes(): Observable<QuoteDTO[]> {
    return this.http.get<QuoteDTO[]>(`${this.apiUrl}/quotes`);
  }

  getQuoteById(id: number): Observable<QuoteDTO> {
    return this.http.get<QuoteDTO>(`${this.apiUrl}/quotes/${id}`);
  }

  getQuotesByCustomerId(customerId: number): Observable<QuoteDTO[]> {
    return this.http.get<QuoteDTO[]>(`${this.apiUrl}/quotes/customer/${customerId}`);
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
    return this.http.get<RatingRuleDTO[]>(`${this.apiUrl}/rules`);
  }

  createRule(rule: RatingRuleDTO): Observable<RatingRuleDTO> {
    return this.http.post<RatingRuleDTO>(`${this.apiUrl}/rules`, rule);
  }
}
