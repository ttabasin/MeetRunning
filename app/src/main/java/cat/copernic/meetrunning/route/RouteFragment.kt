package cat.copernic.meetrunning.route

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentAddRouteMapBinding
import cat.copernic.meetrunning.databinding.FragmentRouteBinding
import cat.copernic.meetrunning.home.PostHome

class RouteFragment : Fragment() {

 lateinit var binding: FragmentRouteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRouteBinding.inflate(layoutInflater)
        val args = RouteFragmentArgs.fromBundle(requireArguments())
        binding.descriptionRoute.text = args.post.description
        binding.titleRoute.text = args.post.title
        Log.d("ruta", args.post.route.toString())
        return binding.root
    }

}