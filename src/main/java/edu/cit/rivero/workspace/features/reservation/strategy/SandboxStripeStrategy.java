package edu.cit.rivero.workspace.features.reservation.strategy;

import edu.cit.rivero.workspace.features.reservation.*;
import org.springframework.stereotype.Component;

@Component
public class SandboxStripeStrategy implements PaymentStrategy {
    private String declineReason = "";

    @Override
    public boolean processPayment(PaymentMethodRequest paymentDetails, double amount) {
        // Mock Stripe Validation Logic
        if (paymentDetails.getCardNumber() == null || paymentDetails.getCardNumber().length() < 15) {
            declineReason = "Invalid card length";
            return false;
        }
        if (paymentDetails.getCvv() == null || !paymentDetails.getCvv().equals("123")) {
            declineReason = "CVV verification failed";
            return false;
        }
        return true; // Payment Success
    }

    @Override
    public String getDeclineReason() {
        return declineReason;
    }
}