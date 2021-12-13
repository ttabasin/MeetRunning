package cat.copernic.meetrunning.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.R
import com.bumptech.glide.Glide

class PhotoAdapter(private val photos: ArrayList<Uri>) :
    RecyclerView.Adapter<PhotoAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_photo,
            parent, false
        )
        return PhotoAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoAdapter.MyViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(photos[position]) //3
            .centerCrop() //4
            .placeholder(R.drawable.ic_baseline_photo_24) //5
            .error(R.drawable.ic_baseline_photo_24) //6
            .into(holder.photo) //8
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val photo: ImageView = itemView.findViewById(R.id.ivPhoto)

    }

}

