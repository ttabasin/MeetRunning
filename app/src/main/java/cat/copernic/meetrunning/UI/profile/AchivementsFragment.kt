package cat.copernic.meetrunning.UI.profile

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentAchivementsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

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
        var countDist = 0
        var countTime = 0

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
                binding.achTime1.isVisible = false
                binding.achTime2.isVisible = false
                binding.achTime3.isVisible = false
            }

        }

        binding.achTime.setOnClickListener{
            if(binding.achTime1.isVisible){
                binding.achTime1.isVisible = false
                binding.achTime2.isVisible = false
                binding.achTime3.isVisible = false
            }else{

                binding.achTime1.isVisible = true
                binding.achTime2.isVisible = true
                binding.achTime3.isVisible = true

                binding.achDist1.isVisible = false
                binding.achDist2.isVisible = false
                binding.achDist3.isVisible = false
            }
        }

        binding.username.text = FirebaseAuth.getInstance().currentUser?.displayName.toString()

        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            binding.description.text = it.getString("description")

            //Distance
            if (it.getDouble("distance")!! >= 25) {
                binding.achDist1.setBackgroundColor(ContextCompat.getColor(context!!, R.color.appBar))
                countDist++
            }else{
                binding.achDist1.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.grey))
            }

            if (it.getDouble("distance")!! >= 50) {
                binding.achDist2.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.appBar))
                countDist++
            }else{
                binding.achDist2.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.grey))
            }

            if (it.getDouble("distance")!! >= 100) {
                countDist++
                binding.achDist3.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.appBar))
            }else{
                binding.achDist3.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.grey))
            }

            binding.achCompleteNumD.text = countDist.toString()

            //Time
            if (SimpleDateFormat("HH:mm:ss").format(it.get("time").toString().toInt().minus(TimeZone.getDefault().rawOffset))!! >= "01:00:00") {
                binding.achTime1.setBackgroundColor(ContextCompat.getColor(context!!, R.color.appBar))
                countTime++
            }else{
                binding.achTime1.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.grey))
            }

            if (SimpleDateFormat("HH:mm:ss").format(it.get("time").toString().toInt().minus(TimeZone.getDefault().rawOffset))!! >= "05:00:00") {
                binding.achTime2.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.appBar))
                countTime++
            }else{
                binding.achTime2.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.grey))
            }

            if (SimpleDateFormat("HH:mm:ss").format(it.get("time").toString().toInt().minus(TimeZone.getDefault().rawOffset))!! >= "10:00:00") {
                binding.achTime3.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.appBar))
                countTime++
            }else{
                binding.achTime3.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.grey))
            }
            binding.achCompleteNumT.text = countTime.toString()

        }



        return binding.root
    }

}

