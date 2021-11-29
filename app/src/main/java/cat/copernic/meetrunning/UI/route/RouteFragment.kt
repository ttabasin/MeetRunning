package cat.copernic.meetrunning.UI.route

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import cat.copernic.meetrunning.databinding.FragmentRouteBinding
import cat.copernic.meetrunning.viewModel.RouteViewModel
import cat.copernic.meetrunning.viewModel.RouteViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class RouteFragment : Fragment() {

    private lateinit var binding: FragmentRouteBinding
    private lateinit var viewModel: RouteViewModel
    private lateinit var viewModelFactory: RouteViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRouteBinding.inflate(layoutInflater)
        viewModelFactory = RouteViewModelFactory(RouteFragmentArgs.fromBundle(requireArguments()).post)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RouteViewModel::class.java)

        binding.descriptionRoute.text = viewModel.description
        binding.titleRoute.text = viewModel.title
        binding.distanceTxt.text = "${"%.3f".format(viewModel.distance)}Km"
        binding.timeTxt.text =
            SimpleDateFormat("HH:mm:ss").format(viewModel.time?.minus(TimeZone.getDefault().rawOffset))


        binding.signUpContinue.setOnClickListener {
            it.findNavController()
                .navigate(RouteFragmentDirections.actionRouteToRouteMap(viewModel.pos.toTypedArray()))
        }
        return binding.root
    }

}