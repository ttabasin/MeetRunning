package cat.copernic.meetrunning.UI.route

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentRouteMapBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import com.google.android.gms.maps.model.LatLng as GLatLng

class RouteMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentRouteMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var route: List<com.google.android.gms.maps.model.LatLng>
    private var job: Job = Job()
    private var distance: Double = 0.0
    private val positions: ArrayList<GLatLng> = arrayListOf()
    private var btnPressed = false
    private lateinit var time: Date

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRouteMapBinding.inflate(inflater)

        val mapViewBundle: Bundle? = savedInstanceState?.getBundle("MapViewBundleKey")
        route = RouteMapFragmentArgs.fromBundle(requireArguments()).route.toList()
        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.button.setOnClickListener {
            btnPressed = true
            job.cancel()
            //Saves user time in the db
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

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            gps = true
            checkPermission()
        }
    }
    private var gps = false
    private fun checkPermission() {
        if (isPermissionGranted() && gps) {
            enableMyLocation()
            getCurrentLocation()
        } else if (isPermissionGranted()) {
            createLocationRequest()
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

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        //mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        checkPermission()
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(route[0]))
        for (i in 1 until route.size) {
            mMap.addPolyline(
                PolylineOptions().add(route[i], route[i - 1])
                    .color(Color.GREEN).width(25F)
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
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
    }

    private fun calculateDistance(
        pos1: com.google.android.gms.maps.model.LatLng,
        pos2: com.google.android.gms.maps.model.LatLng
    ): Double {
        val earthRadius = 6371
        val dLat = degreesToRadian(pos2.latitude - pos1.latitude)
        val dLon = degreesToRadian(pos2.longitude - pos1.longitude)

        val lat1 = degreesToRadian(pos1.latitude)
        val lat2 = degreesToRadian(pos2.latitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * cos(lat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    private fun degreesToRadian(i: Double): Double {
        return i * Math.PI / 180
    }


    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
            mMap.isMyLocationEnabled = true
    }

    private fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val REQUEST_CHECK_SETTINGS = 0
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {

            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            gps = true
            checkPermission()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    startIntentSenderForResult(
                        exception.resolution.intentSender, REQUEST_CHECK_SETTINGS,
                        null, 0, 0, 0, null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == -1){
            gps = true
            checkPermission()
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
        if  (job.isActive) {
            //binding.mapView.onDestroy()
            btnPressed = true
            job.cancel()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

}