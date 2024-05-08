package org.example.purchase.dto;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    public String createPaymentIntent(Long amount) throws Exception {

        Stripe.apiKey ="sk_test_51OvQL1JeISkzjGkftJ4YeJZTGwgr5KxjespCPaAL1BwNNXvtzXZFCRJUEGsZfuqfRO43gXiV4fPDqbnN2YTkmPTA00eGjIguha";

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency("usd")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent.getClientSecret();
    }



    public String createSubscription(String priceId) throws Exception {
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPrice(priceId)
                                .build()
                )
                .setSuccessUrl("https://localhost/success")
                .setCancelUrl("https://example.com/cancel")
                .build();

        Session session = Session.create(params);
        return session.getUrl(); // Redirect user to this URL for payment
    }
}
