package edu.cit.rivero.workspace.features.reservation.strategy;

import edu.cit.rivero.workspace.features.reservation.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class SandboxStripeStrategy implements PaymentStrategy {
    private String declineReason = "";

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public boolean processPayment(PaymentMethodRequest paymentDetails, double amount) {
        try {
            String cardNumber = paymentDetails.getCardNumber() != null ? paymentDetails.getCardNumber().replaceAll("\\s+", "") : "";

            if (cardNumber.startsWith("pm_")) {
                // New Flow: Stripe Elements integration (Web App)
                PaymentIntentCreateParams intentParams = PaymentIntentCreateParams.builder()
                        .setAmount((long) (amount * 100)) // Amount in cents
                        .setCurrency("usd") // Change to php if needed
                        .setPaymentMethod(cardNumber)
                        .setConfirm(true)
                        .setReturnUrl("http://localhost:5173/dashboard")
                        .build();

                PaymentIntent intent = PaymentIntent.create(intentParams);

                if ("succeeded".equals(intent.getStatus()) || "requires_action".equals(intent.getStatus())) {
                    return true;
                } else {
                    declineReason = "Payment failed with status: " + intent.getStatus();
                    return false;
                }

            } else {
                // Legacy Flow: Raw card details (Android App / Fallback)
                String expiry = paymentDetails.getExpiryDate();
                if (expiry == null || !expiry.contains("/")) {
                    declineReason = "Invalid expiry date format. Expected MM/YY";
                    return false;
                }
                String[] parts = expiry.split("/");
                long expMonth = Long.parseLong(parts[0]);
                long expYear = Long.parseLong("20" + parts[1]);

                java.util.Map<String, Object> card = new java.util.HashMap<>();
                card.put("number", cardNumber);
                card.put("exp_month", expMonth);
                card.put("exp_year", expYear);
                card.put("cvc", paymentDetails.getCvv());

                java.util.Map<String, Object> tokenParams = new java.util.HashMap<>();
                tokenParams.put("card", card);

                com.stripe.model.Token token = com.stripe.model.Token.create(tokenParams);

                java.util.Map<String, Object> chargeParams = new java.util.HashMap<>();
                chargeParams.put("amount", (long) (amount * 100)); // Amount in cents
                chargeParams.put("currency", "usd");
                chargeParams.put("source", token.getId());
                chargeParams.put("description", "WorkSpace Reservation");

                com.stripe.model.Charge charge = com.stripe.model.Charge.create(chargeParams);

                if (charge.getPaid() != null && charge.getPaid()) {
                    return true;
                } else {
                    declineReason = "Payment failed. Stripe charge not paid.";
                    return false;
                }
            }

        } catch (StripeException e) {
            // Catch Stripe errors (e.g. Card Declined, Insufficient Funds, Incorrect CVC)
            declineReason = e.getUserMessage() != null ? e.getUserMessage() : e.getMessage();
            return false;
        } catch (Exception e) {
            declineReason = "An unexpected error occurred during payment processing.";
            return false;
        }
    }

    @Override
    public String getDeclineReason() {
        return declineReason;
    }
}