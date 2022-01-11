package cat.copernic.meetrunning.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.dataClass.User
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class UserAdapter(private val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    private lateinit var db: FirebaseFirestore
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_user,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentPost = userList[position]

        holder.username.text = currentPost.username
        holder.distance.text = currentPost.distance.toString() + "km"


        db = FirebaseFirestore.getInstance()
        FirebaseStorage.getInstance()
            .reference.child("users/${currentPost.email}")
            .listAll().addOnSuccessListener { d ->
                if (d.items.size > 0) {
                    d.items[0].downloadUrl.addOnSuccessListener { u ->
                        holder.imageView.background = getDrawable(u)
                    }
                } else {
                    holder.imageView.background = context.getDrawable(R.drawable.ic_baseline_account_circle_24)
                }
            }


    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val username: TextView = itemView.findViewById(R.id.txt_username)
        val distance: TextView = itemView.findViewById(R.id.txt_distance)
        val imageView: ImageView = itemView.findViewById(R.id.imageView2)

    }

    private fun getDrawable(u: Uri): Drawable = runBlocking(Dispatchers.IO) {
        Glide.with(context)
            .load(u).optionalCenterCrop().circleCrop()
            .into(Target.SIZE_ORIGINAL, 100).get()
    }


}