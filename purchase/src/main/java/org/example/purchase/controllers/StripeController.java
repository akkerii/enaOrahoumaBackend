package org.example.purchase.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
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
        return ResponseEntity.ok(Collections.singletonMap("clientSecret", stripeService.createPaymentIntent(amount)));
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


    //updated
    @PostMapping("/stripe/webhook")
    public String handleWebhook(@RequestBody String payload) {
        // Here, you would parse and handle different event types
        // Log or process according to the event type
        return "Received";
    }

    @PostMapping("/create-subscription")
    public ResponseEntity<Map<String, Object>> createSubscription(@RequestBody Map<String, String> data) {
        Stripe.apiKey = "sk_test_51OvQL1JeISkzjGkftJ4YeJZTGwgr5KxjespCPaAL1BwNNXvtzXZFCRJUEGsZfuqfRO43gXiV4fPDqbnN2YTkmPTA00eGjIguha";

        String customerId = data.get("customerId");  // Assume you have a Stripe Customer ID already created
        String priceId = data.get("priceId");

        SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                .addItem(
                        SubscriptionCreateParams.Item.builder()
                                .setPrice(priceId)
                                .build()
                )
                .setCustomer(customerId)
                .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                .build();

        try {
            Subscription subscription = Subscription.create(params);
            return ResponseEntity.ok(Collections.singletonMap("subscriptionId", subscription.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }


    @PostMapping("/attach-payment-method")
    public ResponseEntity<Map<String, String>>attachPaymentMethod(@RequestBody Map<String, String> payload) {
        // String paymentMethodId = payload.get("paymentMethodId");
        //String customerId = payload.get("customerId");

        try {
            Stripe.apiKey = "sk_test_51OvQL1JeISkzjGkftJ4YeJZTGwgr5KxjespCPaAL1BwNNXvtzXZFCRJUEGsZfuqfRO43gXiV4fPDqbnN2YTkmPTA00eGjIguha";

            String paymentMethodId = payload.get("paymentMethodId");
            String customerId = payload.get("customerId");

            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                    .setCustomer(customerId)
                    .build();
            paymentMethod.attach(params);

            // Optionally set this payment method as default for the customer
            CustomerUpdateParams customerParams = CustomerUpdateParams.builder()
                    .setInvoiceSettings(
                            CustomerUpdateParams.InvoiceSettings.builder()
                                    .setDefaultPaymentMethod(paymentMethodId)
                                    .build())
                    .build();
            Customer customer = Customer.retrieve(customerId);
            customer.update(customerParams);

            return ResponseEntity.ok(Map.of("message", "Payment method attached successfully"));
        } catch (StripeException e) {
            e.printStackTrace();
            // Return a map with the error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
