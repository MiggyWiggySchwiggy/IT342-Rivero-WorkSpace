package edu.cit.rivero.workspace

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.cit.rivero.workspace.api.ApiClient
import edu.cit.rivero.workspace.models.ApiResponse
import edu.cit.rivero.workspace.models.Space
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var rvSpaces: RecyclerView
    private lateinit var spaceAdapter: SpaceAdapter
    private lateinit var etSearch: EditText
    private var allSpaces: List<Space> = emptyList()
    private var searchQuery: String = ""
    private var selectedTypeFilter: String = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Personalize greeting with user's first name
        val firstName = SessionManager.getFirstName(this)
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        tvGreeting?.text = if (firstName.isNotEmpty()) "Hello, $firstName 👋" else "Good morning,"

        rvSpaces = findViewById(R.id.rvSpaces)
        etSearch = findViewById(R.id.etSearch)
        rvSpaces.layoutManager = LinearLayoutManager(this)

        spaceAdapter = SpaceAdapter(emptyList()) { space ->
            val intent = Intent(this, SpaceDetailActivity::class.java)
            intent.putExtra("SPACE_ID", space.id)
            startActivity(intent)
        }
        rvSpaces.adapter = spaceAdapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Wire ChipGroup Filters
        val chipGroup = findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipGroupFilters)
        chipGroup?.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedChipId = checkedIds.first()
                val chip = findViewById<com.google.android.material.chip.Chip>(selectedChipId)
                selectedTypeFilter = chip?.text?.toString() ?: "All"
            } else {
                selectedTypeFilter = "All"
            }
            applyFilters()
        }

        // Wire up bottom navigation tabs
        findViewById<TextView>(R.id.navBookings)?.setOnClickListener {
            startActivity(Intent(this, ReservationListActivity::class.java))
        }
        findViewById<TextView>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        fetchSpaces()
    }

    private fun applyFilters() {
        val filtered = allSpaces.filter { space ->
            val matchesSearch = if (searchQuery.isEmpty()) {
                true
            } else {
                val query = searchQuery.lowercase()
                space.name.lowercase().contains(query) ||
                space.type.lowercase().contains(query) ||
                space.location.lowercase().contains(query)
            }

            val matchesType = if (selectedTypeFilter.equals("All", ignoreCase = true)) {
                true
            } else {
                space.type.equals(selectedTypeFilter, ignoreCase = true)
            }

            matchesSearch && matchesType
        }
        spaceAdapter.updateSpaces(filtered)
    }

    private fun fetchSpaces() {
        ApiClient.instance.getSpaces().enqueue(object : Callback<ApiResponse<List<Space>>> {
            override fun onResponse(call: Call<ApiResponse<List<Space>>>, response: Response<ApiResponse<List<Space>>>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    allSpaces = response.body()?.data ?: emptyList()
                    spaceAdapter.updateSpaces(allSpaces)
                } else {
                    Toast.makeText(this@DashboardActivity, "Failed to load spaces", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Space>>>, t: Throwable) {
                Log.e("DashboardActivity", "Error fetching spaces", t)
                Toast.makeText(this@DashboardActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    class SpaceAdapter(
        private var spaces: List<Space>,
        private val onItemClick: (Space) -> Unit
    ) : RecyclerView.Adapter<SpaceAdapter.SpaceViewHolder>() {

        fun updateSpaces(newSpaces: List<Space>) {
            spaces = newSpaces
            notifyDataSetChanged()
        }

        class SpaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val ivSpaceImage: ImageView = view.findViewById(R.id.ivSpaceImage)
            val tvName: TextView = view.findViewById(R.id.tvSpaceName)
            val tvType: TextView = view.findViewById(R.id.tvSpaceType)
            val tvPrice: TextView = view.findViewById(R.id.tvSpacePrice)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpaceViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_space, parent, false)
            return SpaceViewHolder(view)
        }

        override fun onBindViewHolder(holder: SpaceViewHolder, position: Int) {
            val space = spaces[position]
            holder.tvName.text = space.name
            holder.tvType.text = "${space.type} • ${space.capacity} People"
            holder.tvPrice.text = "₱${space.hourlyRate} / hr"

            // Parse image URL (comma-separated list)
            val imageUrls = space.imageUrl?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
            if (!imageUrls.isNullOrEmpty()) {
                val firstImage = imageUrls.first()
                val fullUrl = if (firstImage.startsWith("http")) firstImage else "${ApiClient.BASE_URL}$firstImage"
                holder.ivSpaceImage.load(fullUrl) {
                    crossfade(true)
                }
            } else {
                holder.ivSpaceImage.setImageDrawable(null)
            }

            holder.itemView.setOnClickListener {
                onItemClick(space)
            }
        }

        override fun getItemCount() = spaces.size
    }
}