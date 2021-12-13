package cat.copernic.meetrunning.UI.addRoute

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentAddRouteMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*


class AddRouteMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentAddRouteMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location: LatLng
    private var btnPressed = false
    private val positions: ArrayList<LatLng> = arrayListOf()
    private lateinit var job: Job
    private var distance = 0.0
    private lateinit var startTime: Date

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        var mapViewBundle: Bundle?
        mapViewBundle = savedInstanceState?.getBundle("MapViewBundleKey")

        binding = FragmentAddRouteMapBinding.inflate(layoutInflater)
        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.button.setOnClickListener {
            btnPressed = true
            job.cancel()
            val t = Calendar.getInstance().time.time - startTime.time
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
            it.findNavController().navigate(AddRouteMapFragmentDirections
                .actionAddRouteMapToAddRoute(positions.toTypedArray(), distance.toFloat(), t.toInt()))
        }
        return binding.root
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            enableMyLocation()
            getCurrentLocation()
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        enableMyLocation()
        getCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if(isPermissionGranted()){
            startTime = Calendar.getInstance().time
            job = GlobalScope.launch(Dispatchers.IO) {
                delay(10000)
                distance = 0.0
                var c = 0
                while (!btnPressed) {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        location = LatLng(it.latitude, it.longitude)
                        Log.d("location", "$location $c")
                        positions.add(location)
                        if (c > 0){
                            Log.d("pos", positions[c-1].toString())
                            distance += calculateDistance(location, positions[c-1])
                            binding.textDistance.text = "${"%.3f".format(distance)}Km"
                            mMap.addPolyline(PolylineOptions().add(location, positions[c-1])
                                .color(Color.GREEN).width(25F))
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                        c++
                    }
                    delay(5000)
                }
            }
        }else{
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
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