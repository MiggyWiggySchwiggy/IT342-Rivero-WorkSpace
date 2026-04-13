package edu.cit.rivero.workspace.strategy;

import edu.cit.rivero.workspace.dto.PaymentMethodRequest;

public interface PaymentStrategy {
    boolean processPayment(PaymentMethodRequest paymentDetails, double amount);
    String getDeclineReason();
}