package cat.copernic.meetrunning.adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.UI.favorites.FavoritesFragmentDirections
import cat.copernic.meetrunning.dataClass.DataRoute
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class PostAdapterFav(private val postFavList: ArrayList<DataRoute>) :
    RecyclerView.Adapter<PostAdapterFav.MyViewHolder>(), Filterable {

    private lateinit var db: FirebaseFirestore
    private var filteredPost = arrayListOf<DataRoute>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_row_fav,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    private fun getDrawable(u: Uri): Drawable = runBlocking(Dispatchers.IO) {
        Glide.with(context)
            .load(u)
            .into(Target.SIZE_ORIGINAL, 100).get()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentPost = postFavList[position]

        FirebaseStorage.getInstance()
            .reference.child("users/${currentPost.user}/${currentPost.title}")
            .listAll().addOnSuccessListener { d ->
                if (d.items.size > 0) {
                    d.items[0].downloadUrl.addOnSuccessListener { u ->
                        holder.image.background = getDrawable(u)
                    }
                }
            }

        holder.title.text = currentPost.title
        holder.location.text = currentPost.city

        holder.itemView.setOnClickListener { view ->
            view.findNavController().navigate(FavoritesFragmentDirections.actionFavoritesToRoute(currentPost)
            )
        }

        holder.shareButton.setOnClickListener { view ->
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Title: ${holder.title.text} \nLocation: ${holder.location.text}"
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            view.context.startActivity(shareIntent)
        }

        holder.favButton.setOnClickListener {
            db = FirebaseFirestore.getInstance()
            val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()
            db.collection("users").document(currentUser).collection("favorites")
                .document(currentPost.title.toString()).delete()
            holder.favButton.setImageResource(R.drawable.ic_baseline_star_border_24)
        }
    }

    override fun getItemCount(): Int {
        return filteredPost.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.txt_title2)
        val location: TextView = itemView.findViewById(R.id.txt_location2)
        val shareButton: ImageButton = itemView.findViewById(R.id.shareButton2)
        val favButton: ImageButton = itemView.findViewById(R.id.favButton2)

        val image : ImageView = itemView.findViewById(R.id.image_post2)

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val txt = charSequence.toString()
                if (txt.isBlank()) {
                    filteredPost = postFavList
                } else {
                    val fpost = arrayListOf<DataRoute>()
                    for (p in postFavList) {
                        if (p.title?.lowercase()?.contains(txt.lowercase()) == true) {
                            fpost.add(p)
                        }
                    }
                    filteredPost = fpost
                }
                val filterResults = FilterResults()
                filterResults.values = filteredPost
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredPost = filterResults.values as java.util.ArrayList<DataRoute>
                notifyDataSetChanged()
            }
        }
    }


}