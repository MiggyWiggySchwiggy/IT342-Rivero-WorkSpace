package edu.cit.rivero.workspace.models

data class PaymentMethodRequest(
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String
)

data class ReservationCheckoutRequest(
    val spaceId: String,
    val startTime: String,
    val endTime: String,
    val paymentMethod: PaymentMethodRequest
)

data class ReservationResponseData(
    val reservationId: Long,
    val spaceId: String,
    val spaceName: String?,
    val status: String,
    val paymentStatus: String,
    val startTime: String,
    val endTime: String,
    val totalAmount: String
)

data class BookedSlotData(
    val startTime: String,
    val endTime: String
)

// Admin-facing reservation model (returned by GET /api/v1/reservations/all)
data class AdminReservationItem(
    val reservationId: Long,
    val userEmail: String,
    val userFirstName: String,
    val userLastName: String,
    val spaceId: String,
    val spaceName: String,
    val spaceLocation: String,
    val status: String,
    val paymentStatus: String,
    val startTime: String,
    val endTime: String,
    val totalAmount: String,
    val createdAt: String?
)
