package cat.copernic.meetrunning.UI.profile

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentAchivementsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

open class AchivementsFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding = FragmentAchivementsBinding.inflate(layoutInflater)
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        db = FirebaseFirestore.getInstance()

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

        binding.achDistance.setOnClickListener{
            if(binding.achDist1.isVisible){
                binding.achDist1.isVisible = false
                binding.achDist2.isVisible = false
                binding.achDist3.isVisible = false
            }else{
                binding.achDist1.isVisible = true
                binding.achDist2.isVisible = true
                binding.achDist3.isVisible = true
                binding.achDistT1.isVisible = false
                binding.achDistT2.isVisible = false
                binding.achDistT3.isVisible = false
            }

        }

        binding.achTime.setOnClickListener{
            if(binding.achDistT1.isVisible){
                binding.achDistT1.isVisible = false
                binding.achDistT2.isVisible = false
                binding.achDistT3.isVisible = false
            }else{

                binding.achDistT1.isVisible = true
                binding.achDistT2.isVisible = true
                binding.achDistT3.isVisible = true

                binding.achDist1.isVisible = false
                binding.achDist2.isVisible = false
                binding.achDist3.isVisible = false
            }
        }

        binding.username.text = FirebaseAuth.getInstance().currentUser?.displayName.toString()

        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            binding.description.text = it.getString("description")
        }




        return binding.root
    }

}