package cat.copernic.meetrunning

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import cat.copernic.meetrunning.databinding.FragmentAddRouteBinding
import cat.copernic.meetrunning.databinding.FragmentHomeBinding

class AddRouteFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAddRouteBinding.inflate(layoutInflater)

        binding.signUpContinue.setOnClickListener {
            it.findNavController().navigate(R.id.action_addRoute_to_addRouteMap)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

}