package com.example.demo;

import com.example.demo.service.FinanceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                // Ensure legacy orphaned transactions without a merchant_email are assigned to cobaltaxis@gmail.com
                jdbcTemplate.update("UPDATE transactions SET merchant_email = 'cobaltaxis@gmail.com' WHERE merchant_email IS NULL OR merchant_email = ''");
                jdbcTemplate.update("UPDATE reconciliation_records SET merchant_email = 'cobaltaxis@gmail.com' WHERE merchant_email IS NULL OR merchant_email = ''");
            } catch (Exception e) {
                // Table might not exist or columns might be initializing
            }
        };
    }
}