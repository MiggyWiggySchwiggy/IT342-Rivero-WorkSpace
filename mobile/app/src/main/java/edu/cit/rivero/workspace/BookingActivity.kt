package edu.cit.rivero.workspace

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import edu.cit.rivero.workspace.api.ApiClient
import edu.cit.rivero.workspace.models.ApiResponse
import edu.cit.rivero.workspace.models.PaymentMethodRequest
import edu.cit.rivero.workspace.models.ReservationCheckoutRequest
import edu.cit.rivero.workspace.models.ReservationResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class BookingActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SPACE_ID = "BOOKING_SPACE_ID"
        const val EXTRA_SPACE_NAME = "BOOKING_SPACE_NAME"
        const val EXTRA_HOURLY_RATE = "BOOKING_HOURLY_RATE"
        const val EXTRA_PRESELECT_START = "BOOKING_PRESELECT_START" // "2026-05-14T08:00:00"
        const val EXTRA_PRESELECT_END = "BOOKING_PRESELECT_END"
    }

    private lateinit var tvSpaceName: TextView
    private lateinit var tvSelectedSlot: TextView
    private lateinit var btnSelectStartTime: Button
    private lateinit var btnSelectEndTime: Button
    private lateinit var tvPriceSummary: TextView
    private lateinit var etCardNumber: EditText
    private lateinit var etExpiry: EditText
    private lateinit var etCvv: EditText
    private lateinit var btnConfirmBooking: Button
    private lateinit var progressBar: ProgressBar

    private var selectedStartTime: OffsetDateTime? = null
    private var selectedEndTime: OffsetDateTime? = null
    private var selectedDateEpochMs: Long = System.currentTimeMillis()

    private lateinit var spaceId: String
    private var hourlyRate: Double = 0.0

    private val displayFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        spaceId = intent.getStringExtra(EXTRA_SPACE_ID) ?: run { finish(); return }
        val spaceName = intent.getStringExtra(EXTRA_SPACE_NAME) ?: "Workspace"
        hourlyRate = intent.getDoubleExtra(EXTRA_HOURLY_RATE, 0.0)

        // Views
        tvSpaceName = findViewById(R.id.tvBookingSpaceName)
        tvSelectedSlot = findViewById(R.id.tvSelectedSlot)
        btnSelectStartTime = findViewById(R.id.btnSelectStartTime)
        btnSelectEndTime = findViewById(R.id.btnSelectEndTime)
        tvPriceSummary = findViewById(R.id.tvPriceSummary)
        etCardNumber = findViewById(R.id.etCardNumber)
        etExpiry = findViewById(R.id.etExpiry)
        etCvv = findViewById(R.id.etCvv)
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking)
        progressBar = findViewById(R.id.progressBarBooking)

        // Back button
        findViewById<ImageButton>(R.id.btnBackBooking)?.setOnClickListener { finish() }

        tvSpaceName.text = spaceName

        // Pre-selected slot from availability grid
        val preStart = intent.getStringExtra(EXTRA_PRESELECT_START)
        val preEnd = intent.getStringExtra(EXTRA_PRESELECT_END)
        if (preStart != null && preEnd != null) {
            try {
                val startDt = LocalDateTime.parse(preStart).atOffset(ZoneOffset.UTC)
                val endDt = LocalDateTime.parse(preEnd).atOffset(ZoneOffset.UTC)
                selectedStartTime = startDt
                selectedEndTime = endDt
                btnSelectStartTime.text = startDt.format(displayFormatter)
                btnSelectEndTime.text = endDt.format(displayFormatter)
                tvSelectedSlot.text = "✓ Slot pre-filled from availability. You can adjust below."
                tvSelectedSlot.visibility = View.VISIBLE
                updatePriceSummary()
            } catch (e: Exception) { /* ignore parse errors, let user pick manually */ }
        }

        btnSelectStartTime.setOnClickListener { showDateThenTimePicker(isStart = true) }
        btnSelectEndTime.setOnClickListener {
            if (selectedStartTime == null) {
                Toast.makeText(this, "Please select a start time first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showTimePicker(isStart = false)
        }

        btnConfirmBooking.setOnClickListener { confirmBooking() }
    }

    // ── Date + Time picking ──

    private fun showDateThenTimePicker(isStart: Boolean) {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setCalendarConstraints(constraints)
            .build()
            .also { picker ->
                picker.addOnPositiveButtonClickListener { epochMs ->
                    selectedDateEpochMs = epochMs
                    showTimePicker(isStart = isStart)
                }
            }
            .show(supportFragmentManager, "DATE_PICKER")
    }

    private fun showTimePicker(isStart: Boolean) {
        MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(if (isStart) 9 else 11)
            .setMinute(0)
            .setTitleText(if (isStart) "Select start time" else "Select end time")
            .build()
            .also { picker ->
                picker.addOnPositiveButtonClickListener {
                    val dateInstant = Instant.ofEpochMilli(selectedDateEpochMs)
                    val localDate = dateInstant.atOffset(ZoneOffset.UTC).toLocalDate()
                    val pickedDt = LocalDateTime.of(
                        localDate.year, localDate.monthValue, localDate.dayOfMonth,
                        picker.hour, picker.minute
                    ).atOffset(ZoneOffset.UTC)

                    if (isStart) {
                        selectedStartTime = pickedDt
                        btnSelectStartTime.text = pickedDt.format(displayFormatter)
                        tvSelectedSlot.visibility = View.GONE
                        if (selectedEndTime?.isBefore(pickedDt) == true) {
                            selectedEndTime = null
                            btnSelectEndTime.text = "📅  Pick End Time"
                        }
                    } else {
                        if (pickedDt.isBefore(selectedStartTime) || pickedDt.isEqual(selectedStartTime)) {
                            Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show()
                            return@addOnPositiveButtonClickListener
                        }
                        selectedEndTime = pickedDt
                        btnSelectEndTime.text = pickedDt.format(displayFormatter)
                    }
                    updatePriceSummary()
                }
            }
            .show(supportFragmentManager, "TIME_PICKER")
    }

    private fun updatePriceSummary() {
        val start = selectedStartTime ?: return
        val end = selectedEndTime ?: return
        val durationMinutes = java.time.Duration.between(start, end).toMinutes()
        if (durationMinutes <= 0) return
        val billableHours = Math.ceil(durationMinutes / 60.0).toLong().coerceAtLeast(1)
        val subtotal = hourlyRate * billableHours
        val serviceFee = 49.0
        val total = subtotal + serviceFee
        tvPriceSummary.text = "%.0f hr(s) × ₱%.2f/hr = ₱%.2f\n+ Service Fee ₱%.2f\nTotal: ₱%.2f".format(
            billableHours.toDouble(), hourlyRate, subtotal, serviceFee, total
        )
        tvPriceSummary.visibility = View.VISIBLE
    }

    // ── Submit booking ──

    private fun confirmBooking() {
        val start = selectedStartTime
        val end = selectedEndTime

        if (start == null || end == null) {
            Toast.makeText(this, "Please select start and end times", Toast.LENGTH_SHORT).show()
            return
        }
        val cardNumber = etCardNumber.text.toString().trim()
        val expiry = etExpiry.text.toString().trim()
        val cvv = etCvv.text.toString().trim()
        if (cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
            Toast.makeText(this, "Please fill all payment details", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        val request = ReservationCheckoutRequest(
            spaceId = spaceId,
            startTime = start.toString(),
            endTime = end.toString(),
            paymentMethod = PaymentMethodRequest(
                cardNumber = cardNumber,
                expiryDate = expiry,
                cvv = cvv
            )
        )

        ApiClient.instance.createReservation(request)
            .enqueue(object : Callback<ApiResponse<ReservationResponseData>> {
                override fun onResponse(
                    call: Call<ApiResponse<ReservationResponseData>>,
                    response: Response<ApiResponse<ReservationResponseData>>
                ) {
                    setLoading(false)
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@BookingActivity,
                            "Booking confirmed! ✓",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@BookingActivity,
                            response.body()?.error?.message ?: "Booking failed. Please try again.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<ReservationResponseData>>,
                    t: Throwable
                ) {
                    setLoading(false)
                    Toast.makeText(this@BookingActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnConfirmBooking.isEnabled = !loading
        btnSelectStartTime.isEnabled = !loading
        btnSelectEndTime.isEnabled = !loading
    }
}
