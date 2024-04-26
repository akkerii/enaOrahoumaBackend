package org.example.purchase.controllers;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import org.example.purchase.dto.StripeService;
import org.example.purchase.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/stripe")
@CrossOrigin
public class StripeController {


    @Autowired
    PurchaseService purchaseService;

        private final StripeService stripeService;

        public StripeController(StripeService stripeService) {
            this.stripeService = stripeService;
        }

        @PostMapping("/create-payment-intent")
        public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody Map<String, Long> data) throws Exception {
            Stripe.apiKey = "sk_test_51OvQL1JeISkzjGkftJ4YeJZTGwgr5KxjespCPaAL1BwNNXvtzXZFCRJUEGsZfuqfRO43gXiV4fPDqbnN2YTkmPTA00eGjIguha";

            Long amount = data.get("amount");
            return  ResponseEntity.ok(Collections.singletonMap("clientSecret", stripeService.createPaymentIntent(amount)));
        }



    @GetMapping("/payment/total/{paymentId}")
    public ResponseEntity<Double> getTotalPayments(@PathVariable("paymentId") String paymentId) {
        Stripe.apiKey = "sk_test_51OvQL1JeISkzjGkftJ4YeJZTGwgr5KxjespCPaAL1BwNNXvtzXZFCRJUEGsZfuqfRO43gXiV4fPDqbnN2YTkmPTA00eGjIguha";

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentId);
            // Stripe amounts are in the smallest currency unit (e.g., cents for USD)
            double totalAmount = paymentIntent.getAmount() / 100.0;
            return ResponseEntity.ok(totalAmount);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/seller/total/{sellerId}")
    public Double getTotalSells(@PathVariable Integer sellerId) {
        return purchaseService.getTotalSells(sellerId);
    }
}
