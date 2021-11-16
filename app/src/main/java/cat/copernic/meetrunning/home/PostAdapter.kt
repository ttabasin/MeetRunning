package cat.copernic.meetrunning.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.R

class PostAdapter(private val postList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_row,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentPost = postList[position]

        holder.title.text = currentPost.title
        holder.description.text = currentPost.description

        holder.itemView.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_home_to_route)
        }
        //holder.image.imageAlpha = currentPost.image

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
            Log.i("PostAdapter", "clickFav")
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.txt_title)
        val description: TextView = itemView.findViewById(R.id.txt_ubication)
        val shareButton: ImageButton = itemView.findViewById(R.id.shareButton)
        val favButton: ImageButton = itemView.findViewById(R.id.favButton)

        //val image : ImageView = itemView.findViewById(R.id.image_post)

    }


}