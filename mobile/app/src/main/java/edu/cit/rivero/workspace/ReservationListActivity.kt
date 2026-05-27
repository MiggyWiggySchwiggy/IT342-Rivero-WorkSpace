package edu.cit.rivero.workspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.rivero.workspace.api.ApiClient
import edu.cit.rivero.workspace.models.ApiResponse
import edu.cit.rivero.workspace.models.ReservationResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ReservationListActivity : AppCompatActivity() {

    private lateinit var rvReservations: RecyclerView
    private lateinit var progressBar: View
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_list)

        rvReservations = findViewById(R.id.rvReservations)
        progressBar = findViewById(R.id.progressBarReservations)
        tvEmpty = findViewById(R.id.tvEmptyReservations)

        // Back navigation
        findViewById<ImageButton>(R.id.btnBackReservations)?.setOnClickListener { finish() }

        rvReservations.layoutManager = LinearLayoutManager(this)
        fetchReservations()
    }

    private fun fetchReservations() {
        progressBar.visibility = View.VISIBLE

        ApiClient.instance.getMyReservations()
            .enqueue(object : Callback<ApiResponse<List<ReservationResponseData>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<ReservationResponseData>>>,
                    response: Response<ApiResponse<List<ReservationResponseData>>>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body()?.success == true) {
                        val reservations = response.body()?.data ?: emptyList()
                        if (reservations.isEmpty()) {
                            tvEmpty.visibility = View.VISIBLE
                        } else {
                            rvReservations.adapter = ReservationAdapter(reservations) { item ->
                                confirmCancelReservation(item)
                            }
                        }
                    } else {
                        tvEmpty.visibility = View.VISIBLE
                        Toast.makeText(
                            this@ReservationListActivity,
                            response.body()?.error?.message ?: "Could not load reservations",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<List<ReservationResponseData>>>,
                    t: Throwable
                ) {
                    progressBar.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                    Toast.makeText(this@ReservationListActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun confirmCancelReservation(item: ReservationResponseData) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Cancel Reservation")
            .setMessage("Are you sure you want to cancel your reservation for ${item.spaceName ?: "Space #" + item.spaceId}?")
            .setPositiveButton("Yes, Cancel") { _, _ ->
                cancelReservation(item.reservationId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelReservation(reservationId: Long) {
        progressBar.visibility = View.VISIBLE
        ApiClient.instance.cancelReservation(reservationId)
            .enqueue(object : Callback<ApiResponse<Void>> {
                override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@ReservationListActivity, "Reservation cancelled successfully", Toast.LENGTH_SHORT).show()
                        fetchReservations()
                    } else {
                        Toast.makeText(this@ReservationListActivity, "Failed to cancel reservation", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ReservationListActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // ── Adapter ──

    class ReservationAdapter(
        private val items: List<ReservationResponseData>,
        private val onCancelClick: (ReservationResponseData) -> Unit
    ) : RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvStatus: TextView = view.findViewById(R.id.tvReservationStatus)
            val tvSpaceId: TextView = view.findViewById(R.id.tvReservationSpaceId)
            val tvTime: TextView = view.findViewById(R.id.tvReservationTime)
            val tvAmount: TextView = view.findViewById(R.id.tvReservationAmount)
            val btnCancel: android.widget.Button = view.findViewById(R.id.btnCancelReservation)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reservation, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvStatus.text = item.status
            holder.tvSpaceId.text = item.spaceName ?: "Space #${item.spaceId}"
            holder.tvTime.text = formatTimeRange(item.startTime, item.endTime)
            holder.tvAmount.text = "₱${item.totalAmount}"

            // Color-code the status badge
            val statusColor = when (item.status.uppercase()) {
                "CONFIRMED", "UPCOMING" -> 0xFF2563EB.toInt()
                "ACTIVE" -> 0xFF059669.toInt()
                "CANCELLED" -> 0xFFDC2626.toInt()
                else -> 0xFF6B7280.toInt()
            }
            holder.tvStatus.setTextColor(statusColor)

            // Cancel button visibility
            if (item.status.uppercase() == "CONFIRMED" || item.status.uppercase() == "UPCOMING") {
                holder.btnCancel.visibility = View.VISIBLE
                holder.btnCancel.setOnClickListener {
                    onCancelClick(item)
                }
            } else {
                holder.btnCancel.visibility = View.GONE
            }
        }

        override fun getItemCount() = items.size

        private fun formatTimeRange(start: String, end: String): String {
            return try {
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val displayFormatter = DateTimeFormatter.ofPattern("MMM d · h:mm a")
                val endTime = DateTimeFormatter.ofPattern("h:mm a")
                val startDt = LocalDateTime.parse(start, formatter)
                val endDt = LocalDateTime.parse(end, formatter)
                "${startDt.format(displayFormatter)} – ${endDt.format(endTime)}"
            } catch (e: DateTimeParseException) {
                "$start – $end"
            }
        }
    }
}
