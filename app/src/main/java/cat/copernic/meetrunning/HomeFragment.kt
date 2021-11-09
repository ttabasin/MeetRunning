package cat.copernic.meetrunning

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.R.layout.fragment_home
import cat.copernic.meetrunning.databinding.FragmentHomeBinding
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var dbref: DatabaseReference
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postArrayList: ArrayList<Post>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentHomeBinding.inflate(layoutInflater)

        binding.floatingActionButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_home_to_addRoute)
        }

        postRecyclerView = binding.recycler
        postRecyclerView.layoutManager = LinearLayoutManager(context)
        postRecyclerView.setHasFixedSize(true)

        postArrayList = arrayListOf<Post>()
        getPostData()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getPostData() {
        dbref = FirebaseDatabase.getInstance().getReference("Posts")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (postSnapshot in snapshot.children) {
                        val post = postSnapshot.getValue(Post::class.java)
                        postArrayList.add(post!!)
                    }
                    postRecyclerView.adapter = PostAdapter(postArrayList)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}