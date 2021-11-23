package cat.copernic.meetrunning.favorites

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
import cat.copernic.meetrunning.home.PostHome
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class PostAdapterFav(private val postFavList: ArrayList<PostHome>) :
    RecyclerView.Adapter<PostAdapterFav.MyViewHolder>(), Filterable {

    private lateinit var db: FirebaseFirestore
    private var filteredPost = arrayListOf<PostHome>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_row_fav,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentPost = postFavList[position]

        holder.title.text = currentPost.title
        holder.location.text = currentPost.city

        holder.itemView.setOnClickListener { view ->
            view.findNavController().navigate(FavoritesFragmentDirections.actionFavoritesToRoute(currentPost))
            Log.i("PostAdapter", "$currentPost")
        }

        holder.shareButton.setOnClickListener {
            Log.i("PostAdapter", "clickShare")
            /*val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)*/
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

        //val image : ImageView = itemView.findViewById(R.id.image_post)

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val txt = charSequence.toString()
                if (txt.isBlank()){
                    filteredPost = postFavList
                }else{
                    val fpost = arrayListOf<PostHome>()
                    for (p in postFavList){
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
                filteredPost = filterResults.values as java.util.ArrayList<PostHome>
                notifyDataSetChanged()
            }
        }
    }


}