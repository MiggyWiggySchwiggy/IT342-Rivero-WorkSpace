package edu.cit.rivero.workspace.dto;

public class ReservationResponseData {

    private Long reservationId;
    private String spaceId;
    private String status;
    private String paymentStatus;
    private String startTime;
    private String endTime;
    private String totalAmount;

    public ReservationResponseData() {
    }

    public ReservationResponseData(Long reservationId, String spaceId, String status, String paymentStatus,
                                   String startTime, String endTime, String totalAmount) {
        this.reservationId = reservationId;
        this.spaceId = spaceId;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalAmount = totalAmount;
    }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getSpaceId() { return spaceId; }
    public void setSpaceId(String spaceId) { this.spaceId = spaceId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getTotalAmount() { return totalAmount; }
    public void setTotalAmount(String totalAmount) { this.totalAmount = totalAmount; }
}
