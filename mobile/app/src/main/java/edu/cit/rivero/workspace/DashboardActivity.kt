package edu.cit.rivero.workspace // Make sure this matches your package name!

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// 1. A simple data class for the UI
data class SpaceUiModel(val name: String, val type: String, val price: String)

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // 2. Setup the RecyclerView
        val rvSpaces = findViewById<RecyclerView>(R.id.rvSpaces)
        rvSpaces.layoutManager = LinearLayoutManager(this)

        // 3. Create some dummy UI data just for the Phase 2 visual screenshot.
        // In Phase 3, you will replace this list by consuming the actual /api/v1/spaces endpoint!
        val dummySpaces = listOf(
            SpaceUiModel("The Glasshouse Room", "Meeting Room • 4-6 People", "$25 / hr"),
            SpaceUiModel("Focus Pod A", "Quiet Zone • 1 Person", "$10 / hr"),
            SpaceUiModel("Open Collab Space", "Hot Desk • Open Area", "$5 / hr"),
            SpaceUiModel("Creative Studio", "Workshop • 10 People", "$40 / hr")
        )

        // 4. Attach the adapter
        rvSpaces.adapter = SpaceAdapter(dummySpaces)
    }

    // --- Simple RecyclerView Adapter ---
    class SpaceAdapter(private val spaces: List<SpaceUiModel>) : RecyclerView.Adapter<SpaceAdapter.SpaceViewHolder>() {

        class SpaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
            holder.tvType.text = space.type
            holder.tvPrice.text = space.price
        }

        override fun getItemCount() = spaces.size
    }
}