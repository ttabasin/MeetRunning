package cat.copernic.meetrunning.UI.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentAchivementsBinding
import cat.copernic.meetrunning.databinding.FragmentPhotosBinding
import com.google.firebase.auth.FirebaseAuth

class PhotosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding = FragmentPhotosBinding.inflate(layoutInflater)

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


        return binding.root
    }

}