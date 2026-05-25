package edu.cit.rivero.workspace.features.space;

/**
 * Simple DTO for exposing booked time ranges on a space (no user info leaked).
 */
public class BookedSlotData {

    private String startTime;
    private String endTime;

    public BookedSlotData() {
    }

    public BookedSlotData(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
