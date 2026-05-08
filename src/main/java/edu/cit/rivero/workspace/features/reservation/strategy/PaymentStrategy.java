package edu.cit.rivero.workspace.features.reservation.strategy;
import edu.cit.rivero.workspace.features.auth.*;
import edu.cit.rivero.workspace.features.space.*;
import edu.cit.rivero.workspace.features.reservation.*;
import edu.cit.rivero.workspace.features.reservation.strategy.*;
import edu.cit.rivero.workspace.common.*;
import edu.cit.rivero.workspace.security.*;


import edu.cit.rivero.workspace.features.reservation.PaymentMethodRequest;

public interface PaymentStrategy {
    boolean processPayment(PaymentMethodRequest paymentDetails, double amount);
    String getDeclineReason();
}