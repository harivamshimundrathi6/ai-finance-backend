package com.example.demo.service;

import com.example.demo.entity.ReconciliationRecord;
import com.example.demo.entity.TransactionRecord;
import com.example.demo.repository.ReconciliationRepository;
import com.example.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FinanceService {

    private final TransactionRepository transactionRepo;
    private final ReconciliationRepository reconciliationRepo;

    public FinanceService(TransactionRepository transactionRepo, ReconciliationRepository reconciliationRepo) {
        this.transactionRepo = transactionRepo;
        this.reconciliationRepo = reconciliationRepo;
    }

    public TransactionRecord createTransaction(TransactionRecord tx) {
        if (tx.getAmount() != null && tx.getAmount().compareTo(new BigDecimal("100000")) > 0) {
            tx.setRiskLevel("HIGH");
            tx.setRiskReason("High volume anomaly (> 100,000 INR)");
        } else if ("FAILED".equalsIgnoreCase(tx.getStatus())) {
            tx.setRiskLevel("MEDIUM");
            tx.setRiskReason("Failed payment: " + (tx.getFailureReason() != null ? tx.getFailureReason() : "Declined"));
        } else {
            tx.setRiskLevel("LOW");
            tx.setRiskReason("Standard transaction signature");
        }
        return transactionRepo.save(tx);
    }

    public List<TransactionRecord> getAllTransactions() {
        return transactionRepo.findAll();
    }

    public List<TransactionRecord> getTransactions(String merchantEmail) {
        if (merchantEmail != null && !merchantEmail.trim().isEmpty()) {
            return transactionRepo.findByMerchantEmailOrderByCreatedAtDesc(merchantEmail.trim());
        }
        return transactionRepo.findAll();
    }

    public ReconciliationRecord reconcile(String orderId, BigDecimal internalAmount, BigDecimal gatewayAmount, String merchantEmail) {
        BigDecimal diff = internalAmount.subtract(gatewayAmount).abs();
        String status = (diff.compareTo(BigDecimal.ZERO) == 0) ? "MATCHED" : "UNRESOLVED";
        return reconciliationRepo.save(new ReconciliationRecord(orderId, internalAmount, gatewayAmount, diff, status, merchantEmail));
    }

    public List<ReconciliationRecord> getAllReconciliations() {
        return reconciliationRepo.findAll();
    }

    public List<ReconciliationRecord> getReconciliations(String merchantEmail) {
        if (merchantEmail != null && !merchantEmail.trim().isEmpty()) {
            return reconciliationRepo.findByMerchantEmailOrderByCheckedAtDesc(merchantEmail.trim());
        }
        return reconciliationRepo.findAll();
    }

    public boolean resolveReconciliation(Long id) {
        Optional<ReconciliationRecord> recordOpt = reconciliationRepo.findById(id);
        if (recordOpt.isPresent()) {
            ReconciliationRecord rec = recordOpt.get();
            rec.setReconciliationStatus("RESOLVED_MANUAL");
            reconciliationRepo.save(rec);
            return true;
        }
        return false;
    }

    public boolean triggerRevenueRecovery(Long transactionId) {
        Optional<TransactionRecord> txOpt = transactionRepo.findById(transactionId);
        if (txOpt.isPresent()) {
            TransactionRecord tx = txOpt.get();
            tx.setStatus("SUCCESS");
            tx.setRiskLevel("LOW");
            tx.setRiskReason("Recovered via Automated Smart Retry Engine");
            tx.setFailureReason(null);
            transactionRepo.save(tx);
            return true;
        }
        return false;
    }

    public Map<String, Object> getFinancialSummary() {
        return getFinancialSummary(null);
    }

    public Map<String, Object> getFinancialSummary(String merchantEmail) {
        List<TransactionRecord> all = (merchantEmail != null && !merchantEmail.trim().isEmpty())
                ? transactionRepo.findByMerchantEmail(merchantEmail.trim())
                : transactionRepo.findAll();

        List<ReconciliationRecord> recs = (merchantEmail != null && !merchantEmail.trim().isEmpty())
                ? reconciliationRepo.findByMerchantEmail(merchantEmail.trim())
                : reconciliationRepo.findAll();

        BigDecimal totalVolume = all.stream()
                .filter(t -> "SUCCESS".equalsIgnoreCase(t.getStatus()))
                .map(TransactionRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long failedCount = all.stream().filter(t -> "FAILED".equalsIgnoreCase(t.getStatus())).count();
        long highRiskCount = all.stream().filter(t -> "HIGH".equalsIgnoreCase(t.getRiskLevel())).count();
        long matchedCount = recs.stream()
                .filter(r -> "MATCHED".equalsIgnoreCase(r.getReconciliationStatus()) || "RESOLVED_MANUAL".equalsIgnoreCase(r.getReconciliationStatus()))
                .count();
        double matchRate = recs.isEmpty() ? 100.0 : ((double) matchedCount / recs.size()) * 100.0;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalVolume", totalVolume);
        summary.put("totalTransactions", all.size());
        summary.put("failedPayments", failedCount);
        summary.put("highRiskCount", highRiskCount);
        summary.put("reconciliationMatchRate", Math.round(matchRate * 10.0) / 10.0);
        return summary;
    }
}