package edu.cit.rivero.workspace.dto;

public class ReservationHistoryItemData {

    private Long reservationId;
    private String spaceId;
    private String spaceName;
    private String spaceLocation;
    private String status;
    private String paymentStatus;
    private String startTime;
    private String endTime;
    private String totalAmount;
    private String createdAt;

    public ReservationHistoryItemData() {
    }

    public ReservationHistoryItemData(Long reservationId, String spaceId, String spaceName, String spaceLocation,
                                      String status, String paymentStatus, String startTime, String endTime,
                                      String totalAmount, String createdAt) {
        this.reservationId = reservationId;
        this.spaceId = spaceId;
        this.spaceName = spaceName;
        this.spaceLocation = spaceLocation;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getSpaceId() { return spaceId; }
    public void setSpaceId(String spaceId) { this.spaceId = spaceId; }

    public String getSpaceName() { return spaceName; }
    public void setSpaceName(String spaceName) { this.spaceName = spaceName; }

    public String getSpaceLocation() { return spaceLocation; }
    public void setSpaceLocation(String spaceLocation) { this.spaceLocation = spaceLocation; }

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

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
