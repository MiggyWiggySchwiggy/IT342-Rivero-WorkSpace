package edu.cit.rivero.workspace.features.reservation.strategy;

import edu.cit.rivero.workspace.features.reservation.*;
public interface PaymentStrategy {
    boolean processPayment(PaymentMethodRequest paymentDetails, double amount);
    String getDeclineReason();
}