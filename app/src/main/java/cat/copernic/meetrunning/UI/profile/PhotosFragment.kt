package cat.copernic.meetrunning.UI.profile

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.adapters.PhotoAdapter
import cat.copernic.meetrunning.databinding.FragmentPhotosBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URI

class PhotosFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var photoAdapter: PhotoAdapter
    private val photos = arrayListOf<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        getImages()
        val binding = FragmentPhotosBinding.inflate(layoutInflater)
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        db = FirebaseFirestore.getInstance()
        binding.myRoutesBT.setOnClickListener {
            it.findNavController().navigate(PhotosFragmentDirections.actionPhotosToMyRoutes())
        }
        binding.statsBT.setOnClickListener {
            it.findNavController().navigate(PhotosFragmentDirections.actionPhotosToStats())
        }

        binding.achivementsBT.setOnClickListener {
            it.findNavController().navigate(PhotosFragmentDirections.actionPhotosToAchivements())
        }
        binding.settingBT.setOnClickListener {
            it.findNavController().navigate(PhotosFragmentDirections.actionPhotosToEditProfile())
        }

        binding.username.text = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            binding.description.text = it.getString("description")
        }

        photoAdapter = PhotoAdapter(photos)
        binding.rvPhotos.adapter = photoAdapter
        binding.rvPhotos.layoutManager = GridLayoutManager(requireContext(), 3)

        return binding.root
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
}