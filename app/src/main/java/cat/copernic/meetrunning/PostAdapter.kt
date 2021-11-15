package cat.copernic.meetrunning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val postList : ArrayList<Post>) : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_row,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentPost = postList[position]

        holder.title.text = currentPost.title
        holder.description.text = currentPost.description

        holder.itemView.setOnClickListener{ view ->
            view.findNavController().navigate(R.id.action_home_to_route)
        }
        //holder.image.imageAlpha = currentPost.image

    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val title : TextView = itemView.findViewById(R.id.txt_title)
        val description : TextView = itemView.findViewById(R.id.txt_ubi)


        //val image : ImageView = itemView.findViewById(R.id.image_post)

    }


}