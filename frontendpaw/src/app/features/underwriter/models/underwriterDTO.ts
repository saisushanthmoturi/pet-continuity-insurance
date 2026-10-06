export interface QuoteDTO {
  quoteId?: number;
  quoteNumber?: string;
  customerId: number;
  petId: number;
  requestedCoverage?: number;
  coverageAmount?: number;
  coveragePeriod?: string;
  monthlyCareCost?: number;
  termYears?: number;
  premiumAmount?: number;
  calculatedPremium?: number;
  riskScore?: number;
  riskMultiplier?: number;
  riskClass?: string;
  decision?: string;
  decisionReason?: string;
  status?: string;
  createdAt?: string;
}

export interface RiskAssessmentDTO {
  riskAssessmentId?: number;
  quoteId: number;
  ageFactor?: number;
  breedFactor?: number;
  medicalFactor?: number;
  careCostFactor?: number;
  continuityFactor?: number;
  totalScore?: number;
  riskClass?: string;
  explanation?: string;
}

export interface RatingRuleDTO {
  ruleId?: number;
  ruleCode?: string;
  ruleSetVersion?: string;
  ruleName?: string;
  speciesCode?: string;
  breedCode?: string;
  minAgeMonths?: number;
  maxAgeMonths?: number;
  factor?: string;
  minValue?: number;
  maxValue?: number;
  score?: number;
  baseRate?: number;
  multiplier?: number;
  premiumFactor?: number;
  description?: string;
  status?: string;
}
