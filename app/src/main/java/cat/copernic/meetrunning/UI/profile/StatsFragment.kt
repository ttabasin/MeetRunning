package cat.copernic.meetrunning.UI.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import cat.copernic.meetrunning.databinding.FragmentStatsBinding
import com.google.firebase.auth.FirebaseAuth

class StatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding = FragmentStatsBinding.inflate(layoutInflater)

        binding.myRoutesBT.setOnClickListener{
            it.findNavController().navigate(StatsFragmentDirections.actionStatsToMyRoutes())
        }
        binding.achivementsBT.setOnClickListener{
            it.findNavController().navigate(StatsFragmentDirections.actionStatsToAchivements())
        }

        binding.photosBT.setOnClickListener{
            it.findNavController().navigate(StatsFragmentDirections.actionStatsToPhotos())
        }
        binding.settingBT.setOnClickListener{
            it.findNavController().navigate(StatsFragmentDirections.actionStatsToEditProfile())
        }

        binding.username.text = FirebaseAuth.getInstance().currentUser?.displayName.toString()

        return binding.root
    }

}