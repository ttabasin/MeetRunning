package cat.copernic.meetrunning.ranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.R


class UserAdapter(private val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_user,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentPost = userList[position]

        holder.username.text = currentPost.username
        holder.distance.text = currentPost.distance

        //holder.image.imageAlpha = currentPost.image
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val username: TextView = itemView.findViewById(R.id.txt_username)
        val distance: TextView = itemView.findViewById(R.id.txt_distance)

        //val image : ImageView = itemView.findViewById(R.id.image_post)
    }

}