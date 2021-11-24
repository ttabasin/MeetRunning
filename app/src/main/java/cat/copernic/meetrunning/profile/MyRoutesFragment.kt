package cat.copernic.meetrunning.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.databinding.FragmentMyRutesBinding
import cat.copernic.meetrunning.home.PostHome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.*

class MyRoutesFragment : Fragment() {

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postArrayList: ArrayList<PostHome>
    private lateinit var postAdapter: PostAdapterMyRoutes
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMyRutesBinding.inflate(layoutInflater)

        binding.search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                postAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        postRecyclerView = binding.recyclerMyRoutes
        postRecyclerView.layoutManager = LinearLayoutManager(context)
        postRecyclerView.setHasFixedSize(true)

        postArrayList = arrayListOf()

        postAdapter = PostAdapterMyRoutes(postArrayList)

        postRecyclerView.adapter = postAdapter

        addRouteToList()

        val c: CharSequence = ""
        postAdapter.filter.filter(c)

        return binding.root
    }

    private fun addRouteToList() {

        db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()

        db.collection("users").document(currentUser).collection("routes")
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