package edu.cit.rivero.workspace.features.space;

import jakarta.persistence.*;

@Entity
@Table(name = "availability_slots")
public class AvailabilitySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "space_id", nullable = false, length = 64)
    private String spaceId;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek; // 0 = Sunday, 1 = Monday, ... 6 = Saturday

    @Column(name = "start_time", nullable = false, length = 5)
    private String startTime; // "08:00"

    @Column(name = "end_time", nullable = false, length = 5)
    private String endTime; // "11:00"

    @Column(nullable = false)
    private Boolean blocked = false;

    public AvailabilitySlot() {
    }

    public AvailabilitySlot(String spaceId, Integer dayOfWeek, String startTime, String endTime, Boolean blocked) {
        this.spaceId = spaceId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.blocked = blocked;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSpaceId() { return spaceId; }
    public void setSpaceId(String spaceId) { this.spaceId = spaceId; }

    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public Boolean getBlocked() { return blocked; }
    public void setBlocked(Boolean blocked) { this.blocked = blocked; }
}
