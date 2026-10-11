package com.example.demo.service;

import com.example.demo.entity.TransactionRecord;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final FinanceService financeService;

    public RazorpayService(FinanceService financeService) {
        this.financeService = financeService;
    }

    public String getKeyId() {
        return keyId;
    }

    public String createOrder(BigDecimal amount) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount.multiply(new BigDecimal(100)).intValue());
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "rec_" + System.currentTimeMillis());

        Order order = client.orders.create(orderRequest);
        return order.get("id").toString();
    }

    public void processPaymentSuccess(String razorpayOrderId, String paymentId, BigDecimal amount) {
        TransactionRecord tx = new TransactionRecord(
            paymentId, 
            razorpayOrderId, 
            amount, 
            "INR", 
            "SUCCESS", 
            null, 
            "LOW", 
            "Direct Payment captured via Razorpay Gateway"
        );
        financeService.createTransaction(tx);
        financeService.reconcile(razorpayOrderId, amount, amount);
    }
}