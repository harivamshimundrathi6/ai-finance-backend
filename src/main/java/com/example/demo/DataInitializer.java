package com.example.demo;

import com.example.demo.entity.TransactionRecord;
import com.example.demo.service.FinanceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(FinanceService financeService) {
        return args -> {
            if (financeService.getAllTransactions().isEmpty()) {
                financeService.createTransaction(new TransactionRecord("TXN-101", "ORD-901", new BigDecimal("1500.00"), "INR", "SUCCESS", null, "LOW", "Clean"));
                financeService.createTransaction(new TransactionRecord("TXN-102", "ORD-902", new BigDecimal("125000.00"), "INR", "SUCCESS", null, "HIGH", "High volume ticket"));
                financeService.createTransaction(new TransactionRecord("TXN-103", "ORD-903", new BigDecimal("4999.00"), "INR", "FAILED", "GATEWAY_TIMEOUT", "MEDIUM", "Failed payment candidate"));
                financeService.createTransaction(new TransactionRecord("TXN-104", "ORD-904", new BigDecimal("850.00"), "INR", "FAILED", "INSUFFICIENT_FUNDS", "MEDIUM", "Failed payment candidate"));

                financeService.reconcile("ORD-901", new BigDecimal("1500.00"), new BigDecimal("1500.00"));
                financeService.reconcile("ORD-902", new BigDecimal("125000.00"), new BigDecimal("124500.00"));
            }
        };
    }
}