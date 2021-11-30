package cat.copernic.meetrunning.UI.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import cat.copernic.meetrunning.databinding.FragmentStatsBinding
import cat.copernic.meetrunning.viewModel.StatsViewModel
import com.google.firebase.firestore.FirebaseFirestore

class StatsFragment : Fragment() {

    private lateinit var viewModel: StatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding = FragmentStatsBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this).get(StatsViewModel::class.java)

        binding.username.text = viewModel.user
        viewModel.db.collection("users").document(viewModel.currentUserEmail).get().addOnSuccessListener {
            binding.description.text = viewModel.description
        }

        viewModel.db.collection("users").document(viewModel.currentUserEmail).get().addOnSuccessListener {
            binding.distanceStat.text = viewModel.distance
            binding.timeStat.text = viewModel.time
        }

        //Funció de navegació
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

        return binding.root
    }


}