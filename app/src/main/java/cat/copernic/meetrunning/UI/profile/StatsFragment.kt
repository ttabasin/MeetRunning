package cat.copernic.meetrunning.UI.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import cat.copernic.meetrunning.databinding.FragmentStatsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class StatsFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding = FragmentStatsBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        val currentUsername = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()

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

        binding.username.text = currentUsername

        //binding.distanceStat.text = userDistance()
        //binding.timeStat.text = db.collection("users").document(currentUserEmail)
        Log.i("StatsFragment", "$currentUserEmail")
        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            binding.distanceStat.text = "${"%.2f".format(it.get("distance"))}km"
            binding.timeStat.text = SimpleDateFormat("HH:mm:ss").format(it.get("time"))
        }

        return binding.root
    }


}