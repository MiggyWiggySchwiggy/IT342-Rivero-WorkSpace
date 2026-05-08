package edu.cit.rivero.workspace.features.reservation;
import edu.cit.rivero.workspace.features.auth.*;
import edu.cit.rivero.workspace.features.space.*;
import edu.cit.rivero.workspace.features.reservation.*;
import edu.cit.rivero.workspace.features.reservation.strategy.*;
import edu.cit.rivero.workspace.common.*;
import edu.cit.rivero.workspace.security.*;


public class ReservationCheckoutRequest {

    private String spaceId;
    private String startTime;
    private String endTime;
    private PaymentMethodRequest paymentMethod;

    public ReservationCheckoutRequest() {
    }

    public String getSpaceId() { return spaceId; }
    public void setSpaceId(String spaceId) { this.spaceId = spaceId; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public PaymentMethodRequest getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethodRequest paymentMethod) { this.paymentMethod = paymentMethod; }
}
