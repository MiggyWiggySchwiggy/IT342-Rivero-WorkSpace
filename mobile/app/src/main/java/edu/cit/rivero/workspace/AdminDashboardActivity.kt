package edu.cit.rivero.workspace

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import edu.cit.rivero.workspace.api.ApiClient
import edu.cit.rivero.workspace.models.AdminReservationItem
import edu.cit.rivero.workspace.models.ApiResponse
import edu.cit.rivero.workspace.models.Space
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var tabSpaces: TextView
    private lateinit var tabReservations: TextView
    private lateinit var tabAnalytics: TextView
    private lateinit var contentFrame: FrameLayout

    // Views inflated into contentFrame
    private var spacesView: View? = null
    private var reservationsView: View? = null
    private var analyticsView: View? = null

    private var allSpaces: List<Space> = emptyList()
    private var allReservations: List<AdminReservationItem> = emptyList()

    // Launch SpaceFormActivity and refresh on success
    private val launchSpaceForm = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadSpaces()
        }
    }

    // Launch ImageUploadActivity and refresh on success
    private val launchImageUpload = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadSpaces()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        tabSpaces = findViewById(R.id.tabSpaces)
        tabReservations = findViewById(R.id.tabReservations)
        tabAnalytics = findViewById(R.id.tabAnalytics)
        contentFrame = findViewById(R.id.adminContentFrame)

        // Greeting
        val firstName = SessionManager.getFirstName(this)
        findViewById<TextView>(R.id.tvAdminGreeting).text =
            if (firstName.isNotEmpty()) "Welcome back, $firstName" else "Welcome back"

        // Profile button
        findViewById<TextView>(R.id.btnAdminProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        tabSpaces.setOnClickListener { showSpacesTab() }
        tabReservations.setOnClickListener { showReservationsTab() }
        tabAnalytics.setOnClickListener { showAnalyticsTab() }

        // Start with Spaces tab
        showSpacesTab()
    }

    // ══════════════════════════════════════════
    // Tab switching
    // ══════════════════════════════════════════

    private fun showSpacesTab() {
        setTabSelected(tabSpaces, tabReservations, tabAnalytics)
        if (spacesView == null) {
            spacesView = LayoutInflater.from(this).inflate(R.layout.fragment_admin_spaces, contentFrame, false)
            setupSpacesView(spacesView!!)
        }
        contentFrame.removeAllViews()
        contentFrame.addView(spacesView)
        if (allSpaces.isEmpty()) loadSpaces()
    }

    private fun showReservationsTab() {
        setTabSelected(tabReservations, tabSpaces, tabAnalytics)
        if (reservationsView == null) {
            reservationsView = LayoutInflater.from(this).inflate(R.layout.fragment_admin_reservations, contentFrame, false)
            setupReservationsView(reservationsView!!)
        }
        contentFrame.removeAllViews()
        contentFrame.addView(reservationsView)
        if (allReservations.isEmpty()) loadReservations()
    }

    private fun showAnalyticsTab() {
        setTabSelected(tabAnalytics, tabSpaces, tabReservations)
        if (analyticsView == null) {
            analyticsView = LayoutInflater.from(this).inflate(R.layout.fragment_admin_analytics, contentFrame, false)
        }
        contentFrame.removeAllViews()
        contentFrame.addView(analyticsView)
        loadAnalyticsData()
    }

    private fun setTabSelected(selected: TextView, unselected1: TextView, unselected2: TextView) {
        selected.setBackgroundResource(R.drawable.tab_selected_bg)
        selected.setTextColor(0xFF1D4ED8.toInt())  // blue-700
        unselected1.setBackgroundColor(0x00000000)  // transparent
        unselected1.setTextColor(0xFF6B7280.toInt()) // gray-500
        unselected2.setBackgroundColor(0x00000000)  // transparent
        unselected2.setTextColor(0xFF6B7280.toInt()) // gray-500
    }

    private fun loadAnalyticsData() {
        val view = analyticsView ?: return
        val progress = view.findViewById<ProgressBar>(R.id.progressBarAnalytics)
        val contentLayout = view.findViewById<View>(R.id.layoutAnalyticsContent)

        progress.visibility = View.VISIBLE
        contentLayout.visibility = View.GONE

        // Fetch spaces first
        ApiClient.instance.getSpaces().enqueue(object : Callback<ApiResponse<List<Space>>> {
            override fun onResponse(call: Call<ApiResponse<List<Space>>>, spacesResponse: Response<ApiResponse<List<Space>>>) {
                if (spacesResponse.isSuccessful && spacesResponse.body()?.success == true) {
                    val spaces = spacesResponse.body()?.data ?: emptyList()
                    
                    // Fetch reservations
                    ApiClient.instance.getAllReservations().enqueue(object : Callback<ApiResponse<List<AdminReservationItem>>> {
                        override fun onResponse(
                            call: Call<ApiResponse<List<AdminReservationItem>>>,
                            resResponse: Response<ApiResponse<List<AdminReservationItem>>>
                        ) {
                            progress.visibility = View.GONE
                            contentLayout.visibility = View.VISIBLE

                            if (resResponse.isSuccessful && resResponse.body()?.success == true) {
                                val reservations = resResponse.body()?.data ?: emptyList()
                                computeAndDisplayAnalytics(spaces, reservations)
                            } else {
                                Toast.makeText(this@AdminDashboardActivity, "Failed to load bookings for analytics", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ApiResponse<List<AdminReservationItem>>>, t: Throwable) {
                            progress.visibility = View.GONE
                            contentLayout.visibility = View.VISIBLE
                            Toast.makeText(this@AdminDashboardActivity, "Network error", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    progress.visibility = View.GONE
                    contentLayout.visibility = View.VISIBLE
                    Toast.makeText(this@AdminDashboardActivity, "Failed to load spaces for analytics", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Space>>>, t: Throwable) {
                progress.visibility = View.GONE
                contentLayout.visibility = View.VISIBLE
                Toast.makeText(this@AdminDashboardActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun computeAndDisplayAnalytics(spaces: List<Space>, reservations: List<AdminReservationItem>) {
        val view = analyticsView ?: return
        val tvRevenue = view.findViewById<TextView>(R.id.tvKpiRevenue)
        val tvBookings = view.findViewById<TextView>(R.id.tvKpiBookings)
        val tvActive = view.findViewById<TextView>(R.id.tvKpiActive)
        val tvSpaces = view.findViewById<TextView>(R.id.tvKpiSpaces)
        val layoutTopSpaces = view.findViewById<android.widget.LinearLayout>(R.id.layoutTopSpacesContainer)

        var totalRevenue = 0.0
        var activeBookings = 0
        for (res in reservations) {
            val isCancelled = res.status.equals("CANCELLED", ignoreCase = true)
            if (!isCancelled) {
                val amount = res.totalAmount.toDoubleOrNull() ?: 0.0
                totalRevenue += amount
            }
            if (res.status.equals("CONFIRMED", ignoreCase = true) || res.status.equals("ACTIVE", ignoreCase = true)) {
                activeBookings++
            }
        }

        tvRevenue.text = "₱%,.2f".format(totalRevenue)
        tvBookings.text = reservations.size.toString()
        tvActive.text = activeBookings.toString()
        tvSpaces.text = spaces.size.toString()

        val bookingCounts = reservations.groupBy { it.spaceId }
            .mapValues { it.value.size }
        val sortedSpaces = bookingCounts.toList().sortedByDescending { it.second }
        val spaceNameMap = spaces.associate { it.id to it.name }

        layoutTopSpaces.removeAllViews()
        if (sortedSpaces.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "No booking data available yet."
                setTextColor(0xFF64748B.toInt())
                textSize = 14f
            }
            layoutTopSpaces.addView(emptyTv)
        } else {
            val topN = sortedSpaces.take(5)
            for ((index, pair) in topN.withIndex()) {
                val spaceId = pair.first
                val count = pair.second
                val spaceName = spaceNameMap[spaceId] ?: "Space #${spaceId}"

                val rowView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_2, layoutTopSpaces, false)
                val text1 = rowView.findViewById<TextView>(android.R.id.text1)
                val text2 = rowView.findViewById<TextView>(android.R.id.text2)

                text1.text = "#${index + 1}  $spaceName"
                text1.textSize = 15f
                text1.setTypeface(null, android.graphics.Typeface.BOLD)
                text1.setTextColor(0xFF1E293B.toInt())

                text2.text = "$count bookings"
                text2.textSize = 13f
                text2.setTextColor(0xFF64748B.toInt())
                text2.setPadding(0, 2, 0, 8)

                layoutTopSpaces.addView(rowView)
            }
        }
    }

    // ══════════════════════════════════════════
    // Spaces Tab
    // ══════════════════════════════════════════

    private fun setupSpacesView(view: View) {
        val rvSpaces = view.findViewById<RecyclerView>(R.id.rvAdminSpaces)
        rvSpaces.layoutManager = LinearLayoutManager(this)

        view.findViewById<Button>(R.id.btnAddSpace).setOnClickListener {
            launchSpaceForm.launch(Intent(this, SpaceFormActivity::class.java))
        }
    }

    private fun loadSpaces() {
        val view = spacesView ?: return
        val progress = view.findViewById<ProgressBar>(R.id.progressBarSpaces)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmptySpaces)
        val rv = view.findViewById<RecyclerView>(R.id.rvAdminSpaces)

        progress.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE

        ApiClient.instance.getSpaces().enqueue(object : Callback<ApiResponse<List<Space>>> {
            override fun onResponse(call: Call<ApiResponse<List<Space>>>, response: Response<ApiResponse<List<Space>>>) {
                progress.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    allSpaces = response.body()?.data ?: emptyList()
                    if (allSpaces.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                    } else {
                        rv.adapter = SpaceAdminAdapter(allSpaces,
                            onEdit = { space -> openEditSpace(space) },
                            onImages = { space -> openImageUpload(space) },
                            onAvailability = { space -> openAvailabilityManager(space) },
                            onDelete = { space -> confirmDeleteSpace(space) }
                        )
                    }
                } else {
                    tvEmpty.visibility = View.VISIBLE
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<Space>>>, t: Throwable) {
                progress.visibility = View.GONE
                Toast.makeText(this@AdminDashboardActivity, "Failed to load spaces", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openEditSpace(space: Space) {
        val intent = Intent(this, SpaceFormActivity::class.java).apply {
            putExtra(SpaceFormActivity.EXTRA_SPACE_ID, space.id)
            putExtra(SpaceFormActivity.EXTRA_SPACE_NAME, space.name)
            putExtra(SpaceFormActivity.EXTRA_SPACE_LOCATION, space.location)
            putExtra(SpaceFormActivity.EXTRA_SPACE_TYPE, space.type)
            putExtra(SpaceFormActivity.EXTRA_SPACE_CAPACITY, space.capacity)
            putExtra(SpaceFormActivity.EXTRA_SPACE_RATE, space.hourlyRate)
            putExtra(SpaceFormActivity.EXTRA_SPACE_AVAILABLE, space.available)
            putExtra(SpaceFormActivity.EXTRA_SPACE_DESCRIPTION, space.description)
            putExtra(SpaceFormActivity.EXTRA_SPACE_AMENITIES, space.amenities)
            putExtra(SpaceFormActivity.EXTRA_SPACE_UTILITIES, space.utilities)
            putExtra(SpaceFormActivity.EXTRA_SPACE_CHECK_IN, space.checkInWindow)
            putExtra(SpaceFormActivity.EXTRA_SPACE_POLICY, space.cancellationPolicy)
        }
        launchSpaceForm.launch(intent)
    }

    private fun openAvailabilityManager(space: Space) {
        val intent = Intent(this, AdminAvailabilityActivity::class.java).apply {
            putExtra("SPACE_ID", space.id)
            putExtra("SPACE_NAME", space.name)
        }
        startActivity(intent)
    }

    private fun openImageUpload(space: Space) {
        val intent = Intent(this, ImageUploadActivity::class.java).apply {
            putExtra(ImageUploadActivity.EXTRA_SPACE_ID, space.id)
            putExtra(ImageUploadActivity.EXTRA_SPACE_NAME, space.name)
        }
        launchImageUpload.launch(intent)
    }

    private fun confirmDeleteSpace(space: Space) {
        AlertDialog.Builder(this)
            .setTitle("Delete Workspace")
            .setMessage("Delete \"${space.name}\"?\nThis action cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deleteSpace(space) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSpace(space: Space) {
        ApiClient.instance.deleteSpace(space.id).enqueue(object : Callback<ApiResponse<Void>> {
            override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AdminDashboardActivity, "\"${space.name}\" deleted.", Toast.LENGTH_SHORT).show()
                    loadSpaces()
                } else {
                    Toast.makeText(this@AdminDashboardActivity,
                        response.body()?.error?.message ?: "Delete failed", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                Toast.makeText(this@AdminDashboardActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ══════════════════════════════════════════
    // Reservations Tab
    // ══════════════════════════════════════════

    private var currentFilter = "ALL"

    private fun setupReservationsView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.rvAdminReservations)
        rv.layoutManager = LinearLayoutManager(this)

        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupFilter)
        view.findViewById<Chip>(R.id.chipAll).setOnClickListener { currentFilter = "ALL"; applyFilter() }
        view.findViewById<Chip>(R.id.chipConfirmed).setOnClickListener { currentFilter = "CONFIRMED"; applyFilter() }
        view.findViewById<Chip>(R.id.chipCancelled).setOnClickListener { currentFilter = "CANCELLED"; applyFilter() }
    }

    private fun loadReservations() {
        val view = reservationsView ?: return
        val progress = view.findViewById<ProgressBar>(R.id.progressBarReservationsAdmin)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmptyReservationsAdmin)
        val rv = view.findViewById<RecyclerView>(R.id.rvAdminReservations)

        progress.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE

        ApiClient.instance.getAllReservations().enqueue(object : Callback<ApiResponse<List<AdminReservationItem>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<AdminReservationItem>>>,
                response: Response<ApiResponse<List<AdminReservationItem>>>
            ) {
                progress.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    allReservations = response.body()?.data ?: emptyList()
                    applyFilter()
                } else {
                    tvEmpty.visibility = View.VISIBLE
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<AdminReservationItem>>>, t: Throwable) {
                progress.visibility = View.GONE
                Toast.makeText(this@AdminDashboardActivity, "Failed to load reservations", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun applyFilter() {
        val view = reservationsView ?: return
        val tv = view.findViewById<TextView>(R.id.tvEmptyReservationsAdmin)
        val rv = view.findViewById<RecyclerView>(R.id.rvAdminReservations)

        val filtered = if (currentFilter == "ALL") allReservations
        else allReservations.filter { it.status.uppercase() == currentFilter }

        if (filtered.isEmpty()) {
            tv.visibility = View.VISIBLE
            rv.adapter = null
        } else {
            tv.visibility = View.GONE
            rv.adapter = ReservationAdminAdapter(filtered, onCancel = { item -> confirmCancelReservation(item) })
        }
    }

    private fun confirmCancelReservation(item: AdminReservationItem) {
        AlertDialog.Builder(this)
            .setTitle("Cancel Reservation")
            .setMessage("Cancel booking #${item.reservationId} for ${item.spaceName}?\nUser: ${item.userEmail}")
            .setPositiveButton("Cancel Booking") { _, _ -> cancelReservation(item) }
            .setNegativeButton("Keep", null)
            .show()
    }

    private fun cancelReservation(item: AdminReservationItem) {
        ApiClient.instance.cancelReservation(item.reservationId).enqueue(object : Callback<ApiResponse<Void>> {
            override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AdminDashboardActivity, "Reservation #${item.reservationId} cancelled.", Toast.LENGTH_SHORT).show()
                    allReservations = emptyList() // Force reload
                    loadReservations()
                } else {
                    Toast.makeText(this@AdminDashboardActivity, "Failed to cancel", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                Toast.makeText(this@AdminDashboardActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ══════════════════════════════════════════
    // Adapters
    // ══════════════════════════════════════════

    class SpaceAdminAdapter(
        private val spaces: List<Space>,
        private val onEdit: (Space) -> Unit,
        private val onImages: (Space) -> Unit,
        private val onAvailability: (Space) -> Unit,
        private val onDelete: (Space) -> Unit
    ) : RecyclerView.Adapter<SpaceAdminAdapter.VH>() {

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvAdminSpaceName)
            val tvLocation: TextView = view.findViewById(R.id.tvAdminSpaceLocation)
            val tvAvailability: TextView = view.findViewById(R.id.tvAdminSpaceAvailability)
            val tvTypeCapacity: TextView = view.findViewById(R.id.tvAdminSpaceType)
            val tvRate: TextView = view.findViewById(R.id.tvAdminSpaceRate)
            val btnEdit: Button = view.findViewById(R.id.btnEditSpace)
            val btnImages: Button = view.findViewById(R.id.btnUploadImages)
            val btnDelete: Button = view.findViewById(R.id.btnDeleteSpace)
            val btnManageAvailability: Button = view.findViewById(R.id.btnManageAvailability)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_admin_space, parent, false))

        override fun onBindViewHolder(holder: VH, position: Int) {
            val space = spaces[position]
            holder.tvName.text = space.name
            holder.tvLocation.text = space.location
            holder.tvTypeCapacity.text = "${space.type} · ${space.capacity} people"
            holder.tvRate.text = "₱${space.hourlyRate}/hr"

            if (space.available) {
                holder.tvAvailability.text = "Open"
                holder.tvAvailability.setTextColor(0xFF166534.toInt())
            } else {
                holder.tvAvailability.text = "Closed"
                holder.tvAvailability.setTextColor(0xFF991B1B.toInt())
            }

            holder.btnEdit.setOnClickListener { onEdit(space) }
            holder.btnImages.setOnClickListener { onImages(space) }
            holder.btnManageAvailability.setOnClickListener { onAvailability(space) }
            holder.btnDelete.setOnClickListener { onDelete(space) }
        }

        override fun getItemCount() = spaces.size
    }

    class ReservationAdminAdapter(
        private val items: List<AdminReservationItem>,
        private val onCancel: (AdminReservationItem) -> Unit
    ) : RecyclerView.Adapter<ReservationAdminAdapter.VH>() {

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvId: TextView = view.findViewById(R.id.tvAdminResId)
            val tvStatus: TextView = view.findViewById(R.id.tvAdminResStatus)
            val tvSpace: TextView = view.findViewById(R.id.tvAdminResSpace)
            val tvUser: TextView = view.findViewById(R.id.tvAdminResUser)
            val tvTime: TextView = view.findViewById(R.id.tvAdminResTime)
            val tvAmount: TextView = view.findViewById(R.id.tvAdminResAmount)
            val btnCancel: Button = view.findViewById(R.id.btnCancelReservation)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_admin_reservation, parent, false))

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            holder.tvId.text = "Booking #${item.reservationId}"
            holder.tvStatus.text = item.status
            holder.tvSpace.text = "${item.spaceName} · ${item.spaceLocation}"
            holder.tvUser.text = "${item.userFirstName} ${item.userLastName} · ${item.userEmail}"
            holder.tvTime.text = formatTimeRange(item.startTime, item.endTime)
            holder.tvAmount.text = "Total: ₱${item.totalAmount}"

            // Color-code the status badge
            val color = when (item.status.uppercase()) {
                "CONFIRMED" -> 0xFF166534.toInt()
                "CANCELLED" -> 0xFF991B1B.toInt()
                "COMPLETED" -> 0xFF1E40AF.toInt()
                else -> 0xFF6B7280.toInt()
            }
            holder.tvStatus.setTextColor(color)

            // Only show Cancel button for CONFIRMED reservations
            if (item.status.uppercase() == "CONFIRMED") {
                holder.btnCancel.visibility = View.VISIBLE
                holder.btnCancel.setOnClickListener { onCancel(item) }
            } else {
                holder.btnCancel.visibility = View.GONE
            }
        }

        override fun getItemCount() = items.size

        private fun formatTimeRange(start: String, end: String): String {
            return try {
                val fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val display = DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a")
                val endFmt = DateTimeFormatter.ofPattern("h:mm a")
                val startDt = LocalDateTime.parse(start, fmt)
                val endDt = LocalDateTime.parse(end, fmt)
                "${startDt.format(display)} – ${endDt.format(endFmt)}"
            } catch (e: DateTimeParseException) {
                "$start – $end"
            }
        }
    }
}
