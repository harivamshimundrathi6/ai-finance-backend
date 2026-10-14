package com.example.demo.service;

import com.example.demo.entity.TransactionRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeminiCopilotService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final FinanceService financeService;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GeminiCopilotService(FinanceService financeService) {
        this.financeService = financeService;
        this.restClient = RestClient.create();
        this.objectMapper = new ObjectMapper();
    }

    public String askCopilot(String userPrompt) {
        return askCopilot(userPrompt, null);
    }

    public String askCopilot(String userPrompt, String merchantEmail) {
        Map<String, Object> summary = financeService.getFinancialSummary(merchantEmail);
        List<TransactionRecord> txs = financeService.getTransactions(merchantEmail);

        // Convert transactions into a plain textual ledger
        String transactionDetails = txs.stream()
                .map(t -> String.format("Payment ID: %s, Order ID: %s, Amount: %s %s, Status: %s, Risk Level: %s, Notes: %s",
                        t.getTransactionRef(), t.getOrderId(), t.getCurrency(), t.getAmount(),
                        t.getStatus(), t.getRiskLevel(), (t.getRiskReason() != null ? t.getRiskReason() : "None")))
                .collect(Collectors.joining("\n"));

        // Prompt specifically instructed to avoid markdown, symbols, and formatting characters
        String systemContext = String.format(
            "You are a helpful and polite financial assistant for a merchant business.\n\n" +
            "Current Business Metrics:\n" +
            "- Total Collected Volume: Rupees %s\n" +
            "- Total Transactions: %s\n" +
            "- Failed Transactions: %s\n" +
            "- Flagged High Risk: %s\n" +
            "- Reconciliation Rate: %s percent\n\n" +
            "Recent Transactions Ledger:\n%s\n\n" +
            "STRICT WRITING RULES:\n" +
            "1. Speak in plain, clear, everyday English so anyone without a finance background can understand.\n" +
            "2. Never use markdown symbols. Do not use asterisks (**), backticks (`), hashtags (#), bullet symbols, or brackets.\n" +
            "3. Write in clean, complete sentences with natural paragraph breaks.\n" +
            "4. If answering about a specific payment ID, explain simply whether the payment went through and why it was flagged in plain terms.",
            summary.get("totalVolume"),
            summary.get("totalTransactions"),
            summary.get("failedPayments"),
            summary.get("highRiskCount"),
            summary.get("reconciliationMatchRate"),
            transactionDetails
        );

        Map<String, Object> body = Map.of(
            "contents", new Object[]{
                Map.of("parts", new Object[]{
                    Map.of("text", systemContext + "\n\nMerchant Question: " + userPrompt)
                })
            }
        );

        String[] models = {"gemini-2.5-flash", "gemini-1.5-flash"};
        for (String model : models) {
            String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;
            try {
                String rawResponse = restClient.post()
                        .uri(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(String.class);

                JsonNode root = objectMapper.readTree(rawResponse);
                String reply = root.path("candidates").get(0)
                           .path("content").path("parts").get(0)
                           .path("text").asText();

                return sanitizeText(reply);
            } catch (Exception e) {
                if (model.equals(models[models.length - 1])) {
                    return "We are currently having trouble contacting the assistant. Please try again shortly.";
                }
            }
        }
        return "The service is temporarily unavailable. Please try again in a few moments.";
    }

    /**
     * Strips residual markdown characters like asterisks, backticks, and hash tags.
     */
    private String sanitizeText(String input) {
        if (input == null) return "";
        return input.replace("**", "")
                    .replace("*", "")
                    .replace("`", "")
                    .replace("#", "")
                    .replace("_", "")
                    .trim();
    }
}