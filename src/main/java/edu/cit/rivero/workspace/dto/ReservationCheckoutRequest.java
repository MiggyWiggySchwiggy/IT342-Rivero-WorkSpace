package edu.cit.rivero.workspace.dto;

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
