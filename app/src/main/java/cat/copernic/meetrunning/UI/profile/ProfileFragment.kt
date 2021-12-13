package cat.copernic.meetrunning.UI.profile

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.adapters.PhotoAdapter
import cat.copernic.meetrunning.adapters.PostAdapterMyRoutes
import cat.copernic.meetrunning.dataClass.DataRoute
import cat.copernic.meetrunning.databinding.FragmentProfileBinding
import cat.copernic.meetrunning.viewModel.StatsViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var viewModel: StatsViewModel
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postArrayList: ArrayList<DataRoute>
    private lateinit var postAdapter: PostAdapterMyRoutes
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentProfileBinding
    private lateinit var photoAdapter: PhotoAdapter
    private val photos = arrayListOf<Uri>()
    private val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()

        binding.cViewDist.isVisible = false
        binding.cViewTime.isVisible = false
        binding.achDistance.isVisible = false
        binding.achTime.isVisible = false
        binding.textView10.isVisible = false
        binding.achCompleteNumD.isVisible = false
        binding.achCompleteNumT.isVisible = false
        binding.textView11.isVisible = false
        binding.rvPhotos.isVisible = false


        //Navegar entre botons

        myRoutesBT()
        statsBT()
        achievementsBT()
        photosBT()

        binding.settingBT.setOnClickListener {
            it.findNavController()
                .navigate(ProfileFragmentDirections.actionMyRoutesToEditProfile())
        }

        //Mostrar el nom d'usuari al perfil
        binding.username.text = FirebaseAuth.getInstance().currentUser?.displayName.toString()

        //Mostrar la descripció de l'usuari al perfil
        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            binding.description.text = it.getString("description")
        }

        //MY ROUTES FRAGMENTS
        myRoutesF()

        //STATS FRAGMENT
        statsF()

        //ACHIEVEMENTS FRAGMENT
        achievementsF()

        //FRAGMENT PHOTOS
        photosF()

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

    private fun setProfileImage() {
        FirebaseStorage.getInstance().reference.child("users/${FirebaseAuth.getInstance().currentUser?.email}/profile.jpg").downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it)
                .centerInside()
                .circleCrop()
                .into(binding.profilePhoto)
        }
    }

    private fun getImages() {
        val user = FirebaseAuth.getInstance().currentUser?.email.toString()
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(user)
            .collection("routes").get().addOnSuccessListener {
                for (i in it.documents) {
                    FirebaseStorage.getInstance()
                        .reference.child("users/$user/${i.get("title").toString()}")
                        .listAll().addOnSuccessListener { uri ->
                            for (u in uri.items) {
                                u.downloadUrl.addOnSuccessListener { r ->
                                    photos.add(r)
                                    photoAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                }
            }
    }

    //NAVIGATION
    private fun myRoutesBT() {
        binding.myRoutesBT.setOnClickListener {
            binding.search.isVisible = true
            binding.recyclerMyRoutes.isVisible = true
            binding.cViewDist.isVisible = false
            binding.cViewTime.isVisible = false

            binding.achDistance.isVisible = false
            binding.achTime.isVisible = false
            binding.textView10.isVisible = false
            binding.achCompleteNumD.isVisible = false
            binding.achCompleteNumT.isVisible = false
            binding.textView11.isVisible = false

            binding.achDist1.isVisible = false
            binding.achDist2.isVisible = false
            binding.achDist3.isVisible = false
            binding.achTime1.isVisible = false
            binding.achTime2.isVisible = false
            binding.achTime3.isVisible = false

            binding.rvPhotos.isVisible = false

            binding.achTime.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
            binding.achDistance.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))

            binding.myRoutesBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.appBar))
            binding.statsBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
            binding.achivementsBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
            binding.photosBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
        }
    }

    private fun statsBT() {
        binding.statsBT.setOnClickListener {

            binding.cViewDist.isVisible = true
            binding.cViewTime.isVisible = true

            binding.search.isVisible = false
            binding.recyclerMyRoutes.isVisible = false
            binding.achDistance.isVisible = false
            binding.achTime.isVisible = false
            binding.textView10.isVisible = false
            binding.achCompleteNumD.isVisible = false
            binding.achCompleteNumT.isVisible = false
            binding.textView11.isVisible = false

            binding.rvPhotos.isVisible = false

            binding.myRoutesBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
            binding.statsBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.appBar))
            binding.achivementsBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
            binding.photosBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))

        }
    }

    private fun achievementsBT() {
        binding.achivementsBT.setOnClickListener {
            binding.achDistance.isVisible = true
            binding.achTime.isVisible = true
            binding.textView10.isVisible = true
            binding.achCompleteNumD.isVisible = true
            binding.achCompleteNumT.isVisible = true
            binding.textView11.isVisible = true

            binding.search.isVisible = false
            binding.recyclerMyRoutes.isVisible = false
            binding.cViewDist.isVisible = false
            binding.cViewTime.isVisible = false

            binding.achDist1.isVisible = false
            binding.achDist2.isVisible = false
            binding.achDist3.isVisible = false
            binding.achTime1.isVisible = false
            binding.achTime2.isVisible = false
            binding.achTime3.isVisible = false

            binding.rvPhotos.isVisible = false

            binding.achTime.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
            binding.achDistance.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))

            binding.myRoutesBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
            binding.statsBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
            binding.achivementsBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.appBar))
            binding.photosBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))

        }
    }

    private fun photosBT() {
        binding.photosBT.setOnClickListener {

            binding.achDistance.isVisible = false
            binding.achTime.isVisible = false
            binding.textView10.isVisible = false
            binding.achCompleteNumD.isVisible = false
            binding.achCompleteNumT.isVisible = false
            binding.textView11.isVisible = false

            binding.search.isVisible = false
            binding.recyclerMyRoutes.isVisible = false
            binding.cViewDist.isVisible = false
            binding.cViewTime.isVisible = false

            binding.achDist1.isVisible = false
            binding.achDist2.isVisible = false
            binding.achDist3.isVisible = false
            binding.achTime1.isVisible = false
            binding.achTime2.isVisible = false
            binding.achTime3.isVisible = false

            binding.rvPhotos.isVisible = true

            binding.myRoutesBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
            binding.statsBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
            binding.achivementsBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
            binding.photosBT.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.appBar))
        }
    }

    private fun myRoutesF(){

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

        setProfileImage()
    }

    private fun statsF(){

        viewModel = ViewModelProvider(this).get(StatsViewModel::class.java)

        binding.username.text = viewModel.user
        viewModel.db.collection("users").document(viewModel.currentUserEmail).get()
            .addOnSuccessListener {
                binding.description.text = viewModel.description
            }

        viewModel.db.collection("users").document(viewModel.currentUserEmail).get()
            .addOnSuccessListener {
                binding.distanceStat.text = viewModel.distance
                binding.timeStat.text = viewModel.time
            }
    }

    private fun achievementsF(){
        var countDist = 0
        var countTime = 0

        binding.achDistance.setOnClickListener {
            if (binding.achDist1.isVisible) {
                binding.achDistance.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.grey))
                binding.achDist1.isVisible = false
                binding.achDist2.isVisible = false
                binding.achDist3.isVisible = false
            } else {
                binding.achDistance.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.appBar))
                binding.achTime.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.grey))
                binding.achDist1.isVisible = true
                binding.achDist2.isVisible = true
                binding.achDist3.isVisible = true
                binding.achTime1.isVisible = false
                binding.achTime2.isVisible = false
                binding.achTime3.isVisible = false
            }

        }

        binding.achTime.setOnClickListener {
            if (binding.achTime1.isVisible) {
                binding.achTime.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
                binding.achTime1.isVisible = false
                binding.achTime2.isVisible = false
                binding.achTime3.isVisible = false
            } else {
                binding.achTime.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.appBar))
                binding.achDistance.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
                binding.achTime1.isVisible = true
                binding.achTime2.isVisible = true
                binding.achTime3.isVisible = true

                binding.achDist1.isVisible = false
                binding.achDist2.isVisible = false
                binding.achDist3.isVisible = false
            }
        }

        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            binding.description.text = it.getString("description")

            //Distance
            if (it.getDouble("distance")!! >= 25) {
                binding.achDist1.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.appBar
                    )
                )
                countDist++
            } else {
                binding.achDist1.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.grey
                    )
                )
            }

            if (it.getDouble("distance")!! >= 50) {
                binding.achDist2.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.appBar
                    )
                )
                countDist++
            } else {
                binding.achDist2.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.grey
                    )
                )
            }

            if (it.getDouble("distance")!! >= 100) {
                countDist++
                binding.achDist3.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.appBar
                    )
                )
            } else {
                binding.achDist3.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.grey
                    )
                )
            }

            binding.achCompleteNumD.text = countDist.toString()

            //Time
            if (SimpleDateFormat("HH:mm:ss").format(
                    it.get("time").toString().toInt().minus(TimeZone.getDefault().rawOffset)
                )!! >= "01:00:00"
            ) {
                binding.achTime1.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.appBar
                    )
                )
                countTime++
            } else {
                binding.achTime1.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.grey
                    )
                )
            }

            if (SimpleDateFormat("HH:mm:ss").format(
                    it.get("time").toString().toInt().minus(TimeZone.getDefault().rawOffset)
                )!! >= "05:00:00"
            ) {
                binding.achTime2.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.appBar
                    )
                )
                countTime++
            } else {
                binding.achTime2.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.grey
                    )
                )
            }

            if (SimpleDateFormat("HH:mm:ss").format(
                    it.get("time").toString().toInt().minus(TimeZone.getDefault().rawOffset)
                )!! >= "10:00:00"
            ) {
                binding.achTime3.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.appBar
                    )
                )
                countTime++
            } else {
                binding.achTime3.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.grey
                    )
                )
            }
            binding.achCompleteNumT.text = countTime.toString()

        }
    }

    private fun photosF(){
        getImages()
        photoAdapter = PhotoAdapter(photos)
        binding.rvPhotos.adapter = photoAdapter
        binding.rvPhotos.layoutManager = GridLayoutManager(requireContext(), 3)
    }
}