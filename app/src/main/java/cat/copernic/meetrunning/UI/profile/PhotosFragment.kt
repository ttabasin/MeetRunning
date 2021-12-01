package cat.copernic.meetrunning.UI.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.adapters.PhotoAdapter
import cat.copernic.meetrunning.databinding.FragmentAchivementsBinding
import cat.copernic.meetrunning.databinding.FragmentPhotosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PhotosFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding = FragmentPhotosBinding.inflate(layoutInflater)
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        db = FirebaseFirestore.getInstance()
        binding.myRoutesBT.setOnClickListener{
            it.findNavController().navigate(PhotosFragmentDirections.actionPhotosToMyRoutes())
        }
        binding.statsBT.setOnClickListener{
            it.findNavController().navigate(PhotosFragmentDirections.actionPhotosToStats())
        }

        binding.achivementsBT.setOnClickListener{
            it.findNavController().navigate(PhotosFragmentDirections.actionPhotosToAchivements())
        }
        binding.settingBT.setOnClickListener{
            it.findNavController().navigate(PhotosFragmentDirections.actionPhotosToEditProfile())
        }

        binding.username.text = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            binding.description.text = it.getString("description")
        }
        val test = arrayListOf<String>("", "", "", "")
        photoAdapter = PhotoAdapter(test)
        binding.rvPhotos.adapter = photoAdapter
        binding.rvPhotos.layoutManager = GridLayoutManager(requireContext(), 3)

        return binding.root
    }

}