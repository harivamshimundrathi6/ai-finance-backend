package com.example.demo.controller;

import com.example.demo.entity.Merchant;
import com.example.demo.entity.ReconciliationRecord;
import com.example.demo.entity.TransactionRecord;
import com.example.demo.service.AuthService;
import com.example.demo.service.FinanceService;
import com.example.demo.service.GeminiCopilotService;
import com.example.demo.service.RazorpayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    private final FinanceService financeService;
    private final GeminiCopilotService copilotService;
    private final AuthService authService;
    private final RazorpayService razorpayService;

    public ApiController(FinanceService financeService,
                         GeminiCopilotService copilotService,
                         AuthService authService,
                         RazorpayService razorpayService) {
        this.financeService = financeService;
        this.copilotService = copilotService;
        this.authService = authService;
        this.razorpayService = razorpayService;
    }

    // -------------------------------------------------------------------------
    // 1. Merchant Authentication Routes
    // -------------------------------------------------------------------------

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");
            String businessName = body.get("businessName");

            Merchant merchant = authService.register(email, password, businessName);
            return ResponseEntity.ok(Map.of(
                "message", "Registered successfully",
                "email", merchant.getEmail(),
                "businessName", merchant.getBusinessName()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");

            Merchant merchant = authService.login(email, password);
            return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "email", merchant.getEmail(),
                "businessName", merchant.getBusinessName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // 2. Razorpay Integration Routes
    // -------------------------------------------------------------------------

    @GetMapping("/razorpay/key")
    public Map<String, String> getRazorpayKey() {
        return Map.of("keyId", razorpayService.getKeyId());
    }

    @PostMapping("/razorpay/order")
    public ResponseEntity<?> createRazorpayOrder(@RequestBody Map<String, Object> body) {
        try {
            BigDecimal amount = new BigDecimal(body.get("amount").toString());
            String orderId = razorpayService.createOrder(amount);
            return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "amount", amount
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> body) {
        try {
            String razorpayOrderId = body.get("razorpay_order_id").toString();
            String paymentId = body.get("razorpay_payment_id").toString();
            BigDecimal amount = new BigDecimal(body.get("amount").toString());

            razorpayService.processPaymentSuccess(razorpayOrderId, paymentId, amount);
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // 3. Financial Metrics & Transactions
    // -------------------------------------------------------------------------

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return financeService.getFinancialSummary();
    }

    @GetMapping("/transactions")
    public List<TransactionRecord> getTransactions() {
        return financeService.getAllTransactions();
    }

    @PostMapping("/transactions")
    public TransactionRecord addTransaction(@RequestBody TransactionRecord tx) {
        return financeService.createTransaction(tx);
    }

    // -------------------------------------------------------------------------
    // 4. Reconciliation Operations
    // -------------------------------------------------------------------------

    @GetMapping("/reconciliations")
    public List<ReconciliationRecord> getReconciliations() {
        return financeService.getAllReconciliations();
    }

    @PostMapping("/reconciliations/{id}/resolve")
    public Map<String, Boolean> resolveReconciliation(@PathVariable Long id) {
        boolean success = financeService.resolveReconciliation(id);
        return Map.of("success", success);
    }

    // -------------------------------------------------------------------------
    // 5. Revenue Recovery
    // -------------------------------------------------------------------------

    @PostMapping("/recovery/retry/{id}")
    public Map<String, Boolean> retryRecovery(@PathVariable Long id) {
        boolean success = financeService.triggerRevenueRecovery(id);
        return Map.of("success", success);
    }

    // -------------------------------------------------------------------------
    // 6. Gemini AI Finance Copilot
    // -------------------------------------------------------------------------

    @PostMapping("/copilot/chat")
    public Map<String, String> chatWithCopilot(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String reply = copilotService.askCopilot(message);
        return Map.of("reply", reply);
    }
}