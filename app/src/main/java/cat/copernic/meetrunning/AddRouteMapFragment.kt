package cat.copernic.meetrunning

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cat.copernic.meetrunning.databinding.FragmentAddRouteMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class AddRouteMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentAddRouteMapBinding
    private lateinit var mMap: GoogleMap
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var mapViewBundle: Bundle? = null
        mapViewBundle = savedInstanceState?.getBundle("MapViewBundleKey")

        binding = FragmentAddRouteMapBinding.inflate(layoutInflater)
        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync(this)
        return binding.root
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        //mMap.isMyLocationEnabled = true
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(
            MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

}