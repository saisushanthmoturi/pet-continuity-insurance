package com.example.paymentfundservice.service;

import com.example.paymentfundservice.dto.CreateFundRequest;
import com.example.paymentfundservice.dto.EligibilityResponse;
import com.example.paymentfundservice.dto.ExpenseRequest;
import com.example.paymentfundservice.dto.PaymentRequest;
import com.example.paymentfundservice.model.FundTransaction;
import com.example.paymentfundservice.model.PetContinuityFund;
import com.example.paymentfundservice.model.PremiumPayment;
import com.example.paymentfundservice.repository.FundRepository;
import com.example.paymentfundservice.repository.PaymentRepository;
import com.example.paymentfundservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PaymentFundService {

    private static final Logger log = LoggerFactory.getLogger(PaymentFundService.class);

    private final PaymentRepository paymentRepository;
    private final FundRepository fundRepository;
    private final TransactionRepository transactionRepository;
    private final WebClient webClient;
    private final ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public PaymentFundService(PaymentRepository paymentRepository,
                              FundRepository fundRepository,
                              TransactionRepository transactionRepository,
                              WebClient.Builder webClientBuilder,
                              ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.paymentRepository = paymentRepository;
        this.fundRepository = fundRepository;
        this.transactionRepository = transactionRepository;
        this.webClient = webClientBuilder.build();
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public Mono<PremiumPayment> processPremiumPayment(PaymentRequest req) {
        if (req.policyId() == null || req.amount() == null || req.amount() <= 0) {
            return Mono.error(new IllegalArgumentException("policyId and positive amount are required"));
        }

        boolean failure = Boolean.TRUE.equals(req.simulateFailure());
        String status = failure ? "FAILED" : "SUCCESS";
        String method = req.paymentMethod() != null ? req.paymentMethod() : "SIMULATED_CARD";

        PremiumPayment payment = PremiumPayment.create(req.policyId(), req.amount(), method, status);
        ReactiveCircuitBreaker cb = circuitBreakerFactory.create("paymentCB");

        return paymentRepository.save(payment)
                .flatMap(saved -> {
                    if ("SUCCESS".equals(status)) {
                        log.info("Payment SUCCESS for policyId={}, calling PolicyService to activate", req.policyId());
                        return cb.run(
                                webClient.post()
                                        .uri("http://PolicyService/api/policies/{id}/activate", req.policyId())
                                        .header("X-User-Role", "INTERNAL_SERVICE")
                                        .retrieve()
                                        .toBodilessEntity()
                                        .thenReturn(saved),
                                e -> {
                                    log.warn("CircuitBreaker fallback: Could not activate policy via REST: {}", e.getMessage());
                                    return Mono.just(saved);
                                }
                        );
                    } else {
                        log.warn("Simulated payment FAILED for policyId={}", req.policyId());
                        return Mono.just(saved);
                    }
                });
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_INTERNAL_SERVICE', 'ROLE_CLAIMS_OFFICER')")
    public Mono<PetContinuityFund> createFund(CreateFundRequest req) {
        if (req.policyId() == null || req.petId() == null || req.totalCoverage() == null || req.totalCoverage() <= 0) {
            return Mono.error(new IllegalArgumentException("policyId, petId, and valid totalCoverage are required"));
        }

        return fundRepository.findByPolicyId(req.policyId())
                .switchIfEmpty(Mono.defer(() -> {
                    PetContinuityFund fund = PetContinuityFund.create(
                            req.policyId(),
                            req.petId(),
                            req.totalCoverage(),
                            req.monthlyAllowance(),
                            req.vetReserve(),
                            req.emergencyReserve()
                    );
                    return fundRepository.save(fund)
                            .flatMap(savedFund -> {
                                log.info("Created Pet Continuity Fund id={} for policyId={}, total=${}",
                                        savedFund.getId(), savedFund.getPolicyId(), savedFund.getTotalFund());
                                FundTransaction initialTxn = FundTransaction.create(
                                        savedFund.getId(),
                                        "DEPOSIT",
                                        savedFund.getTotalFund(),
                                        savedFund.getCurrentBalance(),
                                        "Initial Continuity Fund allocation upon approved death claim",
                                        "SUCCESS"
                                );
                                return transactionRepository.save(initialTxn).thenReturn(savedFund);
                            });
                }));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CARETAKER', 'ROLE_ADMIN')")
    public Mono<FundTransaction> disburseMonthly(Long fundId, Long petId, Long caretakerId) {
        return fundRepository.findById(fundId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fund not found with id: " + fundId)))
                .flatMap(fund -> {
                    if (!"ACTIVE".equals(fund.getStatus())) {
                        return Mono.error(new IllegalStateException("Fund is not ACTIVE. Current status: " + fund.getStatus()));
                    }

                    // Call CareVerificationService to check eligibility via Circuit Breaker
                    ReactiveCircuitBreaker cb = circuitBreakerFactory.create("paymentCB");
                    return cb.run(
                            webClient.get()
                                    .uri(uriBuilder -> uriBuilder
                                            .scheme("http")
                                            .host("CareVerificationService")
                                            .path("/api/care/eligibility/check")
                                            .queryParam("petId", petId)
                                            .queryParam("caretakerId", caretakerId)
                                            .build())
                                    .header("X-User-Role", "INTERNAL_SERVICE")
                                    .retrieve()
                                    .bodyToMono(EligibilityResponse.class),
                            e -> {
                                log.warn("CircuitBreaker fallback: CareVerificationService unavailable: {}", e.getMessage());
                                return Mono.just(new EligibilityResponse(false, "Care service unavailable", null));
                            }
                    )
                            .flatMap(eligibility -> {
                                if (eligibility.eligible()) {
                                    double allowance = fund.getMonthlyAllowance();
                                    if (fund.getCurrentBalance() < allowance) {
                                        FundTransaction failTxn = FundTransaction.create(
                                                fund.getId(), "DISBURSEMENT", 0.0, fund.getCurrentBalance(),
                                                "Insufficient fund balance for monthly allowance", "FAILED");
                                        return transactionRepository.save(failTxn);
                                    }
                                    fund.setCurrentBalance(fund.getCurrentBalance() - allowance);
                                    return fundRepository.save(fund)
                                            .flatMap(updatedFund -> {
                                                log.info("Disbursed monthly ${} from fundId={} to caretakerId={}", allowance, fund.getId(), caretakerId);
                                                FundTransaction txn = FundTransaction.create(
                                                        updatedFund.getId(),
                                                        "DISBURSEMENT",
                                                        allowance,
                                                        updatedFund.getCurrentBalance(),
                                                        "Monthly care benefit released to caretaker id " + caretakerId,
                                                        "SUCCESS"
                                                );
                                                return transactionRepository.save(txn);
                                            });
                                } else {
                                    log.warn("Monthly disbursement SUSPENDED for fundId={}: {}", fund.getId(), eligibility.reason());
                                    fund.setStatus("SUSPENDED");
                                    return fundRepository.save(fund)
                                            .flatMap(updatedFund -> {
                                                FundTransaction txn = FundTransaction.create(
                                                        updatedFund.getId(),
                                                        "DISBURSEMENT",
                                                        0.0,
                                                        updatedFund.getCurrentBalance(),
                                                        "Monthly disbursement suspended: " + eligibility.reason(),
                                                        "SUSPENDED"
                                                );
                                                return transactionRepository.save(txn);
                                            });
                                }
                            });
                });
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CARETAKER', 'ROLE_ADMIN')")
    public Mono<FundTransaction> recordExpense(Long fundId, ExpenseRequest req) {
        if (req.amount() == null || req.amount() <= 0) {
            return Mono.error(new IllegalArgumentException("Positive expense amount is required"));
        }
        return fundRepository.findById(fundId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fund not found with id: " + fundId)))
                .flatMap(fund -> {
                    if (fund.getCurrentBalance() < req.amount()) {
                        return Mono.error(new IllegalStateException("Insufficient fund balance for expense: " + req.amount()));
                    }
                    String type = (req.transactionType() == null || req.transactionType().isBlank()) ? "VET_EXPENSE" : req.transactionType().toUpperCase();
                    if ("VET_EXPENSE".equals(type)) {
                        fund.setVetReserve(Math.max(0.0, fund.getVetReserve() - req.amount()));
                    } else if ("EMERGENCY".equals(type)) {
                        fund.setEmergencyReserve(Math.max(0.0, fund.getEmergencyReserve() - req.amount()));
                    }
                    fund.setCurrentBalance(fund.getCurrentBalance() - req.amount());
                    return fundRepository.save(fund)
                            .flatMap(updatedFund -> {
                                log.info("Recorded {} of ${} on fundId={}", type, req.amount(), updatedFund.getId());
                                FundTransaction txn = FundTransaction.create(
                                        updatedFund.getId(),
                                        type,
                                        req.amount(),
                                        updatedFund.getCurrentBalance(),
                                        req.description() != null ? req.description() : (type + " payout"),
                                        "SUCCESS"
                                    );
                                return transactionRepository.save(txn);
                            });
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Flux<PetContinuityFund> getAllFunds() {
        return fundRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<PetContinuityFund> updateFund(Long id, CreateFundRequest req) {
        return fundRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fund not found with id: " + id)))
                .flatMap(fund -> {
                    if (req.monthlyAllowance() != null) fund.setMonthlyAllowance(req.monthlyAllowance());
                    if (req.vetReserve() != null) fund.setVetReserve(req.vetReserve());
                    if (req.emergencyReserve() != null) fund.setEmergencyReserve(req.emergencyReserve());
                    if (req.totalCoverage() != null) fund.setTotalFund(req.totalCoverage());
                    return fundRepository.save(fund);
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<PetContinuityFund> updateFundStatus(Long id, String status) {
        return fundRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fund not found with id: " + id)))
                .flatMap(fund -> {
                    fund.setStatus(status.toUpperCase());
                    return fundRepository.save(fund);
                });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<Void> deleteFund(Long id) {
        return fundRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fund not found with id: " + id)))
                .flatMap(fund -> fundRepository.delete(fund));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Flux<PremiumPayment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public Mono<PremiumPayment> getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Payment not found with id: " + id)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CARETAKER', 'ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public Mono<PetContinuityFund> getFundById(Long id) {
        return fundRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fund not found with id: " + id)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CARETAKER', 'ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public Mono<PetContinuityFund> getFundByPolicyId(Long policyId) {
        return fundRepository.findByPolicyId(policyId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fund not found for policyId: " + policyId)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CARETAKER', 'ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public Flux<FundTransaction> getTransactions(Long fundId) {
        return transactionRepository.findByFundIdOrderByCreatedAtDesc(fundId);
    }
}
