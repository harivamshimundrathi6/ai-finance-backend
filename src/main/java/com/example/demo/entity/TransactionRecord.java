package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionRef;
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String failureReason;
    private String riskLevel;
    private String riskReason;
    private String merchantEmail;
    private LocalDateTime createdAt;

    public TransactionRecord() {
        this.createdAt = LocalDateTime.now();
    }

    public TransactionRecord(String transactionRef, String orderId, BigDecimal amount, String currency, String status, String failureReason, String riskLevel, String riskReason, String merchantEmail) {
        this.transactionRef = transactionRef;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.failureReason = failureReason;
        this.riskLevel = riskLevel;
        this.riskReason = riskReason;
        this.merchantEmail = merchantEmail;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public String getRiskReason() { return riskReason; }
    public void setRiskReason(String riskReason) { this.riskReason = riskReason; }
    public String getMerchantEmail() { return merchantEmail; }
    public void setMerchantEmail(String merchantEmail) { this.merchantEmail = merchantEmail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}