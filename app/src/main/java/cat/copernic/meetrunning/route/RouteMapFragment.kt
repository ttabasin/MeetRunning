package cat.copernic.meetrunning.route

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cat.copernic.meetrunning.R

class RouteMapFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val route = RouteMapFragmentArgs.fromBundle(requireArguments()).route.toList()
        Log.d("cosa", route.toString())
        return inflater.inflate(R.layout.fragment_route_map, container, false)
    }

}