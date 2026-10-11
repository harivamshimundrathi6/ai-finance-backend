# AI Finance Controller - Spring Boot Backend

An enterprise AI-driven finance controller and merchant transaction management backend built with Spring Boot, JPA, MySQL, Razorpay SDK, and Google Gemini AI.

## Features
- **Transaction Processing**: Secure order generation, payment capture, and status lifecycle management.
- **Razorpay Integration**: Native verification of payment signatures and gateway webhooks/callbacks.
- **Automated Ledger Reconciliation**: Compares internal ledger accounts with payment gateway reports and detects anomalies/discrepancies.
- **Smart Payment Recovery**: Automated retry mechanisms and payment link dispatch for abandoned/failed checkouts.
- **AI Financial Copilot**: In-depth financial reporting and anomaly detection powered by Google Gemini AI.

## Tech Stack
- **Framework**: Spring Boot 3.3.2 (Java 17)
- **Database**: MySQL / Spring Data JPA & Hibernate
- **Payment Gateway**: Razorpay Java SDK
- **AI Engine**: Google Gemini API

## Setup & Run
1. Configure database credentials in `src/main/resources/application.properties`.
2. Build and run:
   ```bash
   ./mvnw clean spring-boot:run
   ```
3. API Endpoints:
   - `GET /api/summary` - Aggregate metrics & financial performance
   - `GET /api/transactions` - Transaction ledger
   - `GET /api/reconciliations` - Reconciliation reports
   - `POST /api/copilot/chat` - Gemini AI Financial Copilot
