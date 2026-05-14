package edu.cit.rivero.workspace

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import edu.cit.rivero.workspace.api.ApiClient
import edu.cit.rivero.workspace.models.ApiResponse
import edu.cit.rivero.workspace.models.AvailabilitySlot
import edu.cit.rivero.workspace.models.BookedSlot
import edu.cit.rivero.workspace.models.Space
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class SpaceDetailActivity : AppCompatActivity() {

    private lateinit var vpImageGallery: ViewPager2
    private lateinit var tvDetailName: TextView
    private lateinit var tvDetailType: TextView
    private lateinit var tvDetailPrice: TextView
    private lateinit var tvDetailDescription: TextView
    private lateinit var tvDetailAmenities: TextView
    private lateinit var tvDetailPolicies: TextView
    private lateinit var btnBookSpace: Button
    private lateinit var llAvailabilityGrid: LinearLayout
    private var currentSpace: Space? = null

    // Pre-selected slot times (set when user taps a slot chip)
    private var selectedStartISO: String? = null
    private var selectedEndISO: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space_detail)

        val spaceId = intent.getStringExtra("SPACE_ID") ?: return finish()

        // Back button
        findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener { finish() }

        vpImageGallery = findViewById(R.id.vpImageGallery)
        tvDetailName = findViewById(R.id.tvDetailName)
        tvDetailType = findViewById(R.id.tvDetailType)
        tvDetailPrice = findViewById(R.id.tvDetailPrice)
        tvDetailDescription = findViewById(R.id.tvDetailDescription)
        tvDetailAmenities = findViewById(R.id.tvDetailAmenities)
        tvDetailPolicies = findViewById(R.id.tvDetailPolicies)
        btnBookSpace = findViewById(R.id.btnBookSpace)
        llAvailabilityGrid = findViewById(R.id.llAvailabilityGrid)

        btnBookSpace.setOnClickListener {
            val space = currentSpace ?: return@setOnClickListener
            val intent = Intent(this, BookingActivity::class.java).apply {
                putExtra(BookingActivity.EXTRA_SPACE_ID, space.id)
                putExtra(BookingActivity.EXTRA_SPACE_NAME, space.name)
                putExtra(BookingActivity.EXTRA_HOURLY_RATE, space.hourlyRate)
                // Pass pre-selected slot times if available
                selectedStartISO?.let { putExtra(BookingActivity.EXTRA_PRESELECT_START, it) }
                selectedEndISO?.let { putExtra(BookingActivity.EXTRA_PRESELECT_END, it) }
            }
            startActivity(intent)
        }

        fetchSpaceDetails(spaceId)
    }

    private fun fetchSpaceDetails(spaceId: String) {
        ApiClient.instance.getSpaceById(spaceId).enqueue(object : Callback<ApiResponse<Space>> {
            override fun onResponse(call: Call<ApiResponse<Space>>, response: Response<ApiResponse<Space>>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val space = response.body()?.data
                    if (space != null) {
                        currentSpace = space
                        populateUI(space)
                        loadAvailability(spaceId)
                    }
                } else {
                    Toast.makeText(this@SpaceDetailActivity, "Failed to load space details", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse<Space>>, t: Throwable) {
                Toast.makeText(this@SpaceDetailActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateUI(space: Space) {
        tvDetailName.text = space.name
        tvDetailType.text = "${space.type} · ${space.location}"
        tvDetailPrice.text = "₱${space.hourlyRate} / hr"
        tvDetailDescription.text = space.description ?: "No description available."

        val amenitiesStr = mutableListOf<String>()
        if (!space.amenities.isNullOrBlank()) amenitiesStr.add("Amenities: ${space.amenities}")
        if (!space.utilities.isNullOrBlank()) amenitiesStr.add("Utilities: ${space.utilities}")
        tvDetailAmenities.text = if (amenitiesStr.isNotEmpty()) amenitiesStr.joinToString("\n") else "None"

        val policiesStr = mutableListOf<String>()
        if (!space.checkInWindow.isNullOrBlank()) policiesStr.add("Check-in: ${space.checkInWindow}")
        if (!space.cancellationPolicy.isNullOrBlank()) policiesStr.add("Cancellation: ${space.cancellationPolicy}")
        tvDetailPolicies.text = if (policiesStr.isNotEmpty()) policiesStr.joinToString("\n") else "Standard policies apply."

        val imageUrls = space.imageUrl?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
        vpImageGallery.adapter = ImageGalleryAdapter(imageUrls)
    }

    // ── Availability grid ──

    private fun loadAvailability(spaceId: String) {
        var apiSlots: List<AvailabilitySlot>? = null
        var bookedSlots: List<BookedSlot>? = null
        var pending = 2

        fun maybeRender() {
            pending--
            if (pending == 0) renderAvailabilityGrid(apiSlots ?: emptyList(), bookedSlots ?: emptyList())
        }

        ApiClient.instance.getSpaceAvailability(spaceId).enqueue(object : Callback<ApiResponse<List<AvailabilitySlot>>> {
            override fun onResponse(call: Call<ApiResponse<List<AvailabilitySlot>>>, response: Response<ApiResponse<List<AvailabilitySlot>>>) {
                if (response.isSuccessful && response.body()?.success == true)
                    apiSlots = response.body()?.data
                maybeRender()
            }
            override fun onFailure(call: Call<ApiResponse<List<AvailabilitySlot>>>, t: Throwable) { maybeRender() }
        })

        ApiClient.instance.getSpaceBookings(spaceId).enqueue(object : Callback<ApiResponse<List<BookedSlot>>> {
            override fun onResponse(call: Call<ApiResponse<List<BookedSlot>>>, response: Response<ApiResponse<List<BookedSlot>>>) {
                if (response.isSuccessful && response.body()?.success == true)
                    bookedSlots = response.body()?.data
                maybeRender()
            }
            override fun onFailure(call: Call<ApiResponse<List<BookedSlot>>>, t: Throwable) { maybeRender() }
        })
    }

    private fun renderAvailabilityGrid(slots: List<AvailabilitySlot>, bookings: List<BookedSlot>) {
        llAvailabilityGrid.removeAllViews()

        // Build a map of day→slots for the next 7 days
        val today = LocalDate.now()
        val isoFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        for (dayOffset in 0..6) {
            val date = today.plusDays(dayOffset.toLong())
            val dow = date.dayOfWeek.value % 7  // Convert to 0=Sun…6=Sat (Java's DayOfWeek is 1=Mon)

            val daySlots = slots.filter { it.dayOfWeek == dow && !it.blocked }
            if (daySlots.isEmpty()) continue

            // Day column
            val dayCol = LayoutInflater.from(this).inflate(R.layout.item_availability_day, llAvailabilityGrid, false)
            val tvDayLabel = dayCol.findViewById<TextView>(R.id.tvDayLabel)
            val tvDateLabel = dayCol.findViewById<TextView>(R.id.tvDateLabel)
            val slotContainer = dayCol.findViewById<LinearLayout>(R.id.llSlotContainer)

            tvDayLabel.text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            tvDateLabel.text = date.format(DateTimeFormatter.ofPattern("MMM d"))

            // Parse bookings for this date into minute ranges
            val dateISO = date.format(isoFmt)
            val bookedRanges = bookings.mapNotNull { b ->
                try {
                    val start = LocalDateTime.parse(b.startTime.substringBefore("Z").substringBefore("+"))
                    val end = LocalDateTime.parse(b.endTime.substringBefore("Z").substringBefore("+"))
                    if (start.toLocalDate().format(isoFmt) == dateISO)
                        Pair(start.hour * 60 + start.minute, end.hour * 60 + end.minute)
                    else null
                } catch (e: Exception) { null }
            }

            for (slot in daySlots) {
                val slotStartMin = timeToMin(slot.startTime)
                val slotEndMin = timeToMin(slot.endTime)
                val isBooked = bookedRanges.any { (bs, be) -> bs < slotEndMin && be > slotStartMin }

                val chip = TextView(this).apply {
                    text = "${slot.startTime}–${slot.endTime}"
                    textSize = 11f
                    setPadding(10, 6, 10, 6)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also { it.setMargins(0, 4, 0, 0) }

                    if (isBooked) {
                        setTextColor(ContextCompat.getColor(this@SpaceDetailActivity, android.R.color.white))
                        setBackgroundColor(0xFFDC2626.toInt())
                        isClickable = false
                    } else {
                        setTextColor(ContextCompat.getColor(this@SpaceDetailActivity, android.R.color.white))
                        setBackgroundColor(0xFF059669.toInt())
                        isClickable = true
                        setOnClickListener {
                            // Pre-fill booking times
                            selectedStartISO = "${date.format(isoFmt)}T${slot.startTime}:00"
                            selectedEndISO = "${date.format(isoFmt)}T${slot.endTime}:00"
                            Toast.makeText(
                                this@SpaceDetailActivity,
                                "Slot selected: ${slot.startTime}–${slot.endTime} on ${tvDateLabel.text}\nTap Book to continue.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                slotContainer.addView(chip)
            }

            llAvailabilityGrid.addView(dayCol)
        }

        if (llAvailabilityGrid.childCount == 0) {
            val empty = TextView(this).apply {
                text = "No availability slots set for this space."
                textSize = 13f
                setTextColor(0xFF9CA3AF.toInt())
                setPadding(0, 8, 0, 8)
            }
            llAvailabilityGrid.addView(empty)
        }
    }

    private fun timeToMin(t: String): Int {
        val parts = t.split(":")
        return (parts.getOrNull(0)?.toIntOrNull() ?: 0) * 60 + (parts.getOrNull(1)?.toIntOrNull() ?: 0)
    }
}
