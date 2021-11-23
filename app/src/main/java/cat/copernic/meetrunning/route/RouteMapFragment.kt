package cat.copernic.meetrunning.route

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.addRoute.AddRouteMapFragmentDirections
import cat.copernic.meetrunning.databinding.FragmentRouteMapBinding
import cat.copernic.meetrunning.home.LatLng
import com.google.android.gms.maps.model.LatLng as GLatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class RouteMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentRouteMapBinding
    private lateinit var mMap: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var route: List<com.google.android.gms.maps.model.LatLng>
    private lateinit var job: Job
    private var distance: Double = 0.0
    private val positions: ArrayList<GLatLng> = arrayListOf()
    private var btnPressed = false
    private lateinit var time: Date

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRouteMapBinding.inflate(inflater)

        var mapViewBundle: Bundle? = null
        mapViewBundle = savedInstanceState?.getBundle("MapViewBundleKey")
        route = RouteMapFragmentArgs.fromBundle(requireArguments()).route.toList()
        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.button.setOnClickListener {
            btnPressed = true
            job.cancel()
            //Guarda el tiempo del usuario en la bd
            val t = Calendar.getInstance().time.time - time.time
            //val tFinal = SimpleDateFormat("HH:mm:ss").format(t)
            val db = FirebaseFirestore.getInstance()
            val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()
            db.collection("users").document(currentUser).get().addOnSuccessListener {
                db.collection("users").document(currentUser).update(
                    mapOf(
                        "time" to t + it.get("time").toString().toInt()
                    )
                )
            }
            Log.i("array", "${positions.size} $positions")
            it.findNavController().navigate(R.id.action_routeMap_to_home)
        }

        return binding.root
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        //mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        enableMyLocation()
        getCurrentLocation()
        for (i in 1 until route.size){
            mMap.addPolyline(
                PolylineOptions().add(route[i], route[i-1])
                    .color(Color.GREEN).width(25F))
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if(isPermissionGranted()){
            time = Calendar.getInstance().time
            job = GlobalScope.launch(Dispatchers.IO) {
                var location: GLatLng
                delay(10000)
                distance = 0.0
                var c = 0
                while (!btnPressed) {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        location = GLatLng(it.latitude, it.longitude)
                        Log.d("location", "$location $c")
                        positions.add(location)
                        if (c > 0){
                            distance += calculateDistance(location, positions[c-1])
                            binding.textDistance.text = "${"%.3f".format(distance)}Km"
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                        c++
                    }
                    delay(5000)
                }
            }
        }else{
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            //getCurrentLocation()
        }
    }

    private fun calculateDistance(pos1: com.google.android.gms.maps.model.LatLng, pos2 : com.google.android.gms.maps.model.LatLng): Double{
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
        if (job.isActive){
            binding.mapView.onDestroy()
            btnPressed = true
            job.cancel()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

}