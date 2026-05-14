package edu.cit.rivero.workspace.models

data class Space(
    val id: String,
    val name: String,
    val location: String,
    val type: String,
    val capacity: Int,
    val hourlyRate: Double,
    val rating: Double,
    val available: Boolean,
    val description: String?,
    val imageUrl: String?,
    val amenities: String?,
    val utilities: String?,
    val checkInWindow: String?,
    val cancellationPolicy: String?
)

data class SpaceRequest(
    val name: String,
    val location: String,
    val type: String,
    val capacity: Int,
    val hourlyRate: Double,
    val available: Boolean,
    val description: String?,
    val amenities: String?,
    val utilities: String?,
    val checkInWindow: String?,
    val cancellationPolicy: String?
)

/** Returned by GET /api/v1/spaces/{spaceId}/availability */
data class AvailabilitySlot(
    val id: Long?,
    val spaceId: String?,
    val dayOfWeek: Int,    // 0=Sun, 1=Mon … 6=Sat
    val startTime: String, // "08:00"
    val endTime: String,   // "11:00"
    val blocked: Boolean
)

/** Returned by GET /api/v1/spaces/{spaceId}/bookings */
data class BookedSlot(
    val startTime: String, // ISO LocalDateTime e.g. "2026-05-11T08:00"
    val endTime: String
)
