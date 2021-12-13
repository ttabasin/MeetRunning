package cat.copernic.meetrunning.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.UI.profile.ProfileFragmentDirections
import cat.copernic.meetrunning.dataClass.DataRoute
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostAdapterMyRoutes (private val postMyListRoute: ArrayList<DataRoute>) :
    RecyclerView.Adapter<PostAdapterMyRoutes.MyViewHolder>(), Filterable {

    private lateinit var db: FirebaseFirestore
    private var filteredPost = arrayListOf<DataRoute>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_row,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentPost = filteredPost[position]
        val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()
        db = FirebaseFirestore.getInstance()

        holder.title.text = currentPost.title
        holder.location.text = currentPost.city

        holder.itemView.setOnClickListener { view ->
            view.findNavController().navigate(ProfileFragmentDirections.actionMyRoutesToRoute(currentPost))
        }

        holder.shareButton.setOnClickListener { view ->
            Log.i("PostAdapter", "clickShare")

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Title: ${holder.title.text} \nLocation: ${holder.location.text}")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            view.context.startActivity(shareIntent)

        }

        db.collection("users").document(currentUser).collection("favorites")
            .document(currentPost.title.toString()).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    holder.favButton.setImageResource(R.drawable.ic_baseline_star_24check)

                    holder.favButton.setOnClickListener {
                        db.collection("users").document(currentUser).collection("favorites")
                            .document(currentPost.title.toString()).delete()
                        holder.favButton.setImageResource(R.drawable.ic_baseline_star_border_24)
                    }
                } else {
                    holder.favButton.setOnClickListener {
                        db.collection("users").document(currentUser).collection("favorites")
                            .document(currentPost.title.toString()).set(currentPost)
                        holder.favButton.setImageResource(R.drawable.ic_baseline_star_24check)
                    }
                }
            }

    }

    override fun getItemCount(): Int {
        return filteredPost.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.txt_title)
        val location: TextView = itemView.findViewById(R.id.txt_location)
        val shareButton: ImageButton = itemView.findViewById(R.id.shareButton)
        val favButton: ImageButton = itemView.findViewById(R.id.favButton)

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val txt = charSequence.toString()
                if (txt.isBlank()){
                    filteredPost = postMyListRoute
                }else{
                    val fpost = arrayListOf<DataRoute>()
                    for (p in postMyListRoute){
                        if (p.title?.lowercase()?.contains(txt.lowercase()) == true){
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