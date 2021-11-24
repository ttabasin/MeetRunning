package cat.copernic.meetrunning.route

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import cat.copernic.meetrunning.databinding.FragmentRouteBinding
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RouteFragment : Fragment() {

    lateinit var binding: FragmentRouteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRouteBinding.inflate(layoutInflater)
        val args = RouteFragmentArgs.fromBundle(requireArguments())
        val pos: ArrayList<LatLng> = ArrayList()

        binding.descriptionRoute.text = args.post?.description
        binding.titleRoute.text = args.post?.title
        binding.distanceTxt.text = "${"%.3f".format(args.post?.distance)}Km"
        binding.timeTxt.text =
            SimpleDateFormat("HH:mm:ss").format(args.post?.time?.minus(TimeZone.getDefault().rawOffset))
        for (p in args.post?.route!!) {
            pos.add(LatLng(p.latitude, p.longitude))
        }

        binding.signUpContinue.setOnClickListener {
            it.findNavController()
                .navigate(RouteFragmentDirections.actionRouteToRouteMap(pos.toTypedArray()))
        }
        return binding.root
    }

}