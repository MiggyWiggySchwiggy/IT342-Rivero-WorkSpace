package edu.cit.rivero.workspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import edu.cit.rivero.workspace.api.ApiClient
import edu.cit.rivero.workspace.models.ApiResponse
import edu.cit.rivero.workspace.models.AvailabilitySlot
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminAvailabilityActivity : AppCompatActivity() {

    private lateinit var tvSpaceName: TextView
    private lateinit var rvSlots: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var btnAdd: Button
    private lateinit var btnDefaults: Button
    private lateinit var btnSave: Button

    private lateinit var spaceId: String
    private lateinit var spaceName: String

    private val slotsList = mutableListOf<AvailabilitySlot>()
    private lateinit var slotAdapter: SlotEditAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_availability)

        spaceId = intent.getStringExtra("SPACE_ID") ?: run { finish(); return }
        spaceName = intent.getStringExtra("SPACE_NAME") ?: "Workspace"

        tvSpaceName = findViewById(R.id.tvAvailabilitySpaceName)
        rvSlots = findViewById(R.id.rvAvailabilitySlots)
        progressBar = findViewById(R.id.progressBarAvailability)
        tvEmpty = findViewById(R.id.tvEmptyAvailability)
        btnAdd = findViewById(R.id.btnAddSlot)
        btnDefaults = findViewById(R.id.btnGenerateDefaults)
        btnSave = findViewById(R.id.btnSaveAvailability)

        tvSpaceName.text = spaceName

        findViewById<ImageButton>(R.id.btnBackAvailability)?.setOnClickListener { finish() }

        rvSlots.layoutManager = LinearLayoutManager(this)
        slotAdapter = SlotEditAdapter(slotsList,
            onTimeClick = { position, isStart -> showTimePickerDialog(position, isStart) },
            onDeleteClick = { position -> deleteSlot(position) }
        )
        rvSlots.adapter = slotAdapter

        btnAdd.setOnClickListener { addEmptySlot() }
        btnDefaults.setOnClickListener { generateDefaultSlots() }
        btnSave.setOnClickListener { saveAvailability() }

        loadAvailability()
    }

    private fun loadAvailability() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE

        ApiClient.instance.getSpaceAvailability(spaceId).enqueue(object : Callback<ApiResponse<List<AvailabilitySlot>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<AvailabilitySlot>>>,
                response: Response<ApiResponse<List<AvailabilitySlot>>>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    slotsList.clear()
                    slotsList.addAll(response.body()?.data ?: emptyList())
                    slotAdapter.notifyDataSetChanged()
                    updateEmptyView()
                } else {
                    Toast.makeText(this@AdminAvailabilityActivity, "Failed to load operating hours", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<AvailabilitySlot>>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@AdminAvailabilityActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addEmptySlot() {
        val newSlot = AvailabilitySlot(
            id = null,
            spaceId = spaceId,
            dayOfWeek = 1, // default Monday
            startTime = "09:00",
            endTime = "17:00",
            blocked = false
        )
        slotsList.add(newSlot)
        slotAdapter.notifyItemInserted(slotsList.size - 1)
        updateEmptyView()
    }

    private fun generateDefaultSlots() {
        slotsList.clear()
        val defaultTimes = listOf(
            Pair("08:00", "11:00"),
            Pair("12:00", "15:00"),
            Pair("16:00", "19:00")
        )
        // Monday (1) to Friday (5)
        for (day in 1..5) {
            for (time in defaultTimes) {
                slotsList.add(
                    AvailabilitySlot(
                        id = null,
                        spaceId = spaceId,
                        dayOfWeek = day,
                        startTime = time.first,
                        endTime = time.second,
                        blocked = false
                    )
                )
            }
        }
        slotAdapter.notifyDataSetChanged()
        updateEmptyView()
    }

    private fun deleteSlot(position: Int) {
        if (position >= 0 && position < slotsList.size) {
            slotsList.removeAt(position)
            slotAdapter.notifyItemRemoved(position)
            slotAdapter.notifyItemRangeChanged(position, slotsList.size)
            updateEmptyView()
        }
    }

    private fun updateEmptyView() {
        tvEmpty.visibility = if (slotsList.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showTimePickerDialog(position: Int, isStart: Boolean) {
        val slot = slotsList[position]
        val timeStr = if (isStart) slot.startTime else slot.endTime
        var currentHour = 9
        var currentMinute = 0
        try {
            val parts = timeStr.split(":")
            if (parts.size == 2) {
                currentHour = parts[0].toInt()
                currentMinute = parts[1].toInt()
            }
        } catch (e: Exception) {}

        MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(currentHour)
            .setMinute(currentMinute)
            .setTitleText(if (isStart) "Select Start Time" else "Select End Time")
            .build()
            .also { picker ->
                picker.addOnPositiveButtonClickListener {
                    val pickedTime = String.format("%02d:%02d", picker.hour, picker.minute)
                    val updatedSlot = if (isStart) {
                        slot.copy(startTime = pickedTime)
                    } else {
                        slot.copy(endTime = pickedTime)
                    }
                    slotsList[position] = updatedSlot
                    slotAdapter.notifyItemChanged(position)
                }
            }
            .show(supportFragmentManager, "TIME_PICKER")
    }

    private fun saveAvailability() {
        progressBar.visibility = View.VISIBLE

        ApiClient.instance.replaceSpaceAvailability(spaceId, slotsList).enqueue(object : Callback<ApiResponse<List<AvailabilitySlot>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<AvailabilitySlot>>>,
                response: Response<ApiResponse<List<AvailabilitySlot>>>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@AdminAvailabilityActivity, "Availability saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AdminAvailabilityActivity, "Failed to save settings", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<AvailabilitySlot>>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@AdminAvailabilityActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ── Adapter ──

    class SlotEditAdapter(
        private val items: MutableList<AvailabilitySlot>,
        private val onTimeClick: (Int, Boolean) -> Unit,
        private val onDeleteClick: (Int) -> Unit
    ) : RecyclerView.Adapter<SlotEditAdapter.VH>() {

        private val dayNames = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val spinnerDay: Spinner = view.findViewById(R.id.spinDayOfWeek)
            val cbBlocked: CheckBox = view.findViewById(R.id.cbBlocked)
            val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteSlot)
            val tvStart: TextView = view.findViewById(R.id.tvSlotStartTime)
            val tvEnd: TextView = view.findViewById(R.id.tvSlotEndTime)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_availability_slot_edit, parent, false))

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]

            // Setup Day Spinner
            val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, dayNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder.spinnerDay.adapter = adapter
            holder.spinnerDay.setSelection(item.dayOfWeek)

            holder.spinnerDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    if (items[position].dayOfWeek != pos) {
                        items[position] = items[position].copy(dayOfWeek = pos)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            holder.tvStart.text = item.startTime
            holder.tvEnd.text = item.endTime
            holder.cbBlocked.isChecked = item.blocked

            holder.tvStart.setOnClickListener { onTimeClick(position, true) }
            holder.tvEnd.setOnClickListener { onTimeClick(position, false) }

            holder.cbBlocked.setOnCheckedChangeListener { _, isChecked ->
                items[position] = items[position].copy(blocked = isChecked)
            }

            holder.btnDelete.setOnClickListener { onDeleteClick(position) }
        }

        override fun getItemCount() = items.size
    }
}
