package cat.copernic.meetrunning.favorites

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.adapters.PostAdapterFav
import cat.copernic.meetrunning.databinding.FragmentFavoritesBinding
import cat.copernic.meetrunning.dataClass.PostHome
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
        binding.search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                postAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        postRecyclerView = binding.recycler
        postRecyclerView.layoutManager = LinearLayoutManager(context)
        postRecyclerView.setHasFixedSize(true)

        postArrayList = arrayListOf()

        postAdapter = PostAdapterFav(postArrayList)

        postRecyclerView.adapter = postAdapter

        addRouteToList()
        val c: CharSequence = ""
        postAdapter.filter.filter(c)

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
