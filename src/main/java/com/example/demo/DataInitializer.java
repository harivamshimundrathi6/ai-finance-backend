package com.example.demo;

import com.example.demo.service.FinanceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(FinanceService financeService) {
        return args -> {
            // Real data only - all transactions and merchants are populated via live user activity
        };
    }
}