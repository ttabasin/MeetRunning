package cat.copernic.meetrunning.UI.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import cat.copernic.meetrunning.databinding.FragmentAchivementsBinding
import com.google.firebase.auth.FirebaseAuth

class AchivementsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding = FragmentAchivementsBinding.inflate(layoutInflater)

        binding.myRoutesBT.setOnClickListener{
            it.findNavController().navigate(AchivementsFragmentDirections.actionAchivementsToMyRoutes())
        }
        binding.statsBT.setOnClickListener{
            it.findNavController().navigate(AchivementsFragmentDirections.actionAchivementsToStats())
        }

        binding.photosBT.setOnClickListener{
            it.findNavController().navigate(AchivementsFragmentDirections.actionAchivementsToPhotos())
        }
        binding.settingBT.setOnClickListener{
            it.findNavController().navigate(AchivementsFragmentDirections.actionAchivementsToEditProfile())
        }

        binding.username.text = FirebaseAuth.getInstance().currentUser?.displayName.toString()

        return binding.root
    }

}