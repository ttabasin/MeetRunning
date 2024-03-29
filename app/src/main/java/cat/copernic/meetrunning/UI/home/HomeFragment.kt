package cat.copernic.meetrunning.UI.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.MainActivity
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.adapters.PostAdapterHome
import cat.copernic.meetrunning.dataClass.DataRoute
import cat.copernic.meetrunning.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var dataRouteArrayList: ArrayList<DataRoute>
    private lateinit var postAdapterHome: PostAdapterHome
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(layoutInflater)

        //Posar text per defecte al cercador
        binding.search.setText("")
        //Donar-li funcionalitat al filtre
        binding.search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                postAdapterHome.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        //Botó per anar a crear una ruta
        binding.floatingActionButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_home_to_addRouteMap)
        }

        postRecyclerView = binding.recycler
        postRecyclerView.layoutManager = LinearLayoutManager(context)
        postRecyclerView.setHasFixedSize(true)

        dataRouteArrayList = arrayListOf()

        postAdapterHome = PostAdapterHome(dataRouteArrayList)


        postRecyclerView.adapter = postAdapterHome

        addRouteToList()
        val c: CharSequence = ""
        postAdapterHome.filter.filter(c)

        return binding.root
    }

    //Afegir els documents de la base de dades a un array per mostrar-los al recyclerView
    @SuppressLint("NotifyDataSetChanged")
    private fun addRouteToList() {

        db = FirebaseFirestore.getInstance()
        db.collection("posts").addSnapshotListener { value, _ ->
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    dataRouteArrayList.add(dc.document.toObject(DataRoute::class.java))
                }
            }
            postAdapterHome.notifyDataSetChanged()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.search.setText("")

    }

}

