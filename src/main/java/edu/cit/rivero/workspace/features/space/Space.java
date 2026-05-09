package edu.cit.rivero.workspace.features.space;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "spaces")
public class Space {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "hourly_rate", nullable = false)
    private Double hourlyRate;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private Boolean available;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String amenities; // Comma-separated, e.g. "Ergonomic chair,Noise-controlled booth"

    @Column(columnDefinition = "TEXT")
    private String utilities; // Comma-separated, e.g. "High-speed Wi-Fi,Power outlet"

    @Column(name = "check_in_window")
    private String checkInWindow; // e.g. "Anytime between 8:00 AM - 9:00 PM"

    @Column(name = "cancellation_policy")
    private String cancellationPolicy; // e.g. "Free cancellation up to 2 hours before check-in."

    public Space() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public String getUtilities() { return utilities; }
    public void setUtilities(String utilities) { this.utilities = utilities; }

    public String getCheckInWindow() { return checkInWindow; }
    public void setCheckInWindow(String checkInWindow) { this.checkInWindow = checkInWindow; }

    public String getCancellationPolicy() { return cancellationPolicy; }
    public void setCancellationPolicy(String cancellationPolicy) { this.cancellationPolicy = cancellationPolicy; }
}

