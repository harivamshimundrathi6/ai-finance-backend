package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliation_records")
public class ReconciliationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private BigDecimal internalAmount;
    private BigDecimal gatewayAmount;
    private BigDecimal difference;
    private String reconciliationStatus;
    private LocalDateTime checkedAt;

    public ReconciliationRecord() {
        this.checkedAt = LocalDateTime.now();
    }

    public ReconciliationRecord(String orderId, BigDecimal internalAmount, BigDecimal gatewayAmount, BigDecimal difference, String reconciliationStatus) {
        this.orderId = orderId;
        this.internalAmount = internalAmount;
        this.gatewayAmount = gatewayAmount;
        this.difference = difference;
        this.reconciliationStatus = reconciliationStatus;
        this.checkedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public BigDecimal getInternalAmount() { return internalAmount; }
    public void setInternalAmount(BigDecimal internalAmount) { this.internalAmount = internalAmount; }
    public BigDecimal getGatewayAmount() { return gatewayAmount; }
    public void setGatewayAmount(BigDecimal gatewayAmount) { this.gatewayAmount = gatewayAmount; }
    public BigDecimal getDifference() { return difference; }
    public void setDifference(BigDecimal difference) { this.difference = difference; }
    public String getReconciliationStatus() { return reconciliationStatus; }
    public void setReconciliationStatus(String reconciliationStatus) { this.reconciliationStatus = reconciliationStatus; }
    public LocalDateTime getCheckedAt() { return checkedAt; }
}