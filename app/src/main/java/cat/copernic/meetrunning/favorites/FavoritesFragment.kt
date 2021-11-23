package cat.copernic.meetrunning.favorites

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentFavoritesBinding
import cat.copernic.meetrunning.databinding.FragmentHomeBinding
import cat.copernic.meetrunning.home.PostHome
import cat.copernic.meetrunning.home.PostAdapterHome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class FavoritesFragment : Fragment() {

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postArrayList: ArrayList<PostHome>
    private lateinit var postAdapter: PostAdapterFav
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFavoritesBinding.inflate(layoutInflater)

        postRecyclerView = binding.recycler
        postRecyclerView.layoutManager = LinearLayoutManager(context)
        postRecyclerView.setHasFixedSize(true)

        postArrayList = arrayListOf()

        postAdapter = PostAdapterFav(postArrayList)

        postRecyclerView.adapter = postAdapter

        addRouteToList()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun addRouteToList() {

        db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()

        db.collection("users").document(currentUser).collection("favorites")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        postArrayList.add(dc.document.toObject(PostHome::class.java))
                    }
                }
                postAdapter.notifyDataSetChanged()
            }
        })
    }
}
