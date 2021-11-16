package cat.copernic.meetrunning

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cat.copernic.meetrunning.databinding.FragmentAddRouteMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.*
import kotlin.math.*


class AddRouteMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentAddRouteMapBinding
    private lateinit var mMap: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location: LatLng
    private var btnPressed = false
    private val positions: ArrayList<LatLng> = arrayListOf()
    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var mapViewBundle: Bundle? = null
        mapViewBundle = savedInstanceState?.getBundle("MapViewBundleKey")

        binding = FragmentAddRouteMapBinding.inflate(layoutInflater)
        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.button.setOnClickListener {
            btnPressed = true
            job.cancel()
            Log.i("array", "${positions.size} $positions")
        }
        return binding.root
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        enableMyLocation()
        getCurrentLocation()
        //val sydney = LatLng(-34.0, 151.0)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if(isPermissionGranted()){
            job = GlobalScope.launch(Dispatchers.Default) {
                delay(10000)
                var distance = 0.0
                var c = 0
                while (!btnPressed) {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        location = LatLng(it.latitude, it.longitude)
                        Log.d("location", "$location $c")
                        positions.add(location)
                        /*mMap.addMarker(
                            MarkerOptions()
                                .position(location)
                                .title(c.toString())
                        )*/
                        if (c > 0){
                            Log.d("pos", positions[c-1].toString())
                            //Log.d("d", calculateDistance(location, positions.get(c-1)).toString())
                            distance += calculateDistance(location, positions[c-1])
                            binding.textDistance.text = "${"%.3f".format(distance)}Km"
                            mMap.addPolyline(PolylineOptions().add(location, positions[c-1])
                                .color(Color.GREEN).width(25F))
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                        c++
                    }
                    delay(15000)
                }
            }
        }else{
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun calculateDistance(pos1: LatLng, pos2 : LatLng): Double{
        val earthRadius = 6371
        val dLat = degreesToRadian(pos2.latitude - pos1.latitude)
        val dLon = degreesToRadian(pos2.longitude - pos1.longitude)

        val lat1 = degreesToRadian(pos1.latitude)
        val lat2 = degreesToRadian(pos2.latitude)

        val a = sin(dLat/2) * sin(dLat/2) +
                sin(dLon/2) * sin(dLon/2) * cos(lat1) * cos(lat2)
        val c = 2 * atan2(sqrt(a), sqrt(1-a))
        return earthRadius * c
    }

    private fun degreesToRadian(i: Double): Double {
        return i * Math.PI / 180
    }


    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

}