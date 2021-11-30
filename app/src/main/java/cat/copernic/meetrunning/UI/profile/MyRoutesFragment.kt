package cat.copernic.meetrunning.UI.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.adapters.PostAdapterMyRoutes
import cat.copernic.meetrunning.dataClass.DataRoute
import cat.copernic.meetrunning.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class MyRoutesFragment : Fragment() {

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postArrayList: ArrayList<DataRoute>
    private lateinit var postAdapter: PostAdapterMyRoutes
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileBinding.inflate(layoutInflater)
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        db = FirebaseFirestore.getInstance()

        binding.statsBT.setOnClickListener{
            it.findNavController().navigate(MyRoutesFragmentDirections.actionMyRoutesToStats())
        }
        binding.achivementsBT.setOnClickListener{
            it.findNavController().navigate(MyRoutesFragmentDirections.actionMyRoutesToAchivements())
        }
        binding.photosBT.setOnClickListener{
            it.findNavController().navigate(MyRoutesFragmentDirections.actionMyRoutesToPhotos())
        }
        binding.settingBT.setOnClickListener{
            it.findNavController().navigate(MyRoutesFragmentDirections.actionMyRoutesToEditProfile())
        }

        binding.username.text = FirebaseAuth.getInstance().currentUser?.displayName.toString()

        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            binding.description.text = it.getString("description")
        }


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

    @SuppressLint("NotifyDataSetChanged")
    private fun addRouteToList() {

        db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()

        db.collection("users").document(currentUser).collection("routes")
            .addSnapshotListener { value, _ ->
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        postArrayList.add(dc.document.toObject(DataRoute::class.java))
                    }
                }
                postAdapter.notifyDataSetChanged()
            }
    }

}