package edu.cit.rivero.workspace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.cit.rivero.workspace.api.ApiClient

class ImageGalleryAdapter(private val imageUrls: List<String>) : RecyclerView.Adapter<ImageGalleryAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.ivGalleryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val url = imageUrls[position]
        val fullUrl = if (url.startsWith("http")) url else "${ApiClient.BASE_URL}$url"
        
        holder.imageView.load(fullUrl) {
            crossfade(true)
        }
    }

    override fun getItemCount(): Int = imageUrls.size
}
