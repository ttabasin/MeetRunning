package cat.copernic.meetrunning.UI.meetMap

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
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentMeetMapBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class MeetMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMeetMapBinding
    private lateinit var mMap: GoogleMap
    private var job: Job = Job()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var btnPressed = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMeetMapBinding.inflate(layoutInflater)
        val mapViewBundle: Bundle? = savedInstanceState?.getBundle("MapViewBundleKey")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync(this)
        return binding.root
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
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

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        checkPermission()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        Log.e("current", "location")
        job = GlobalScope.launch(Dispatchers.IO) {
            delay(2000)
            while (!btnPressed) {
                var location: LatLng
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    location = LatLng(it.latitude, it.longitude)
                    saveLocation(location)
                    getNearbyLocation(location)
                    mMap.addCircle(
                        CircleOptions()
                            .center(location)
                            .radius(350.0)
                            .strokeColor(Color.BLACK)
                            .fillColor(Color.parseColor("#99ff5945"))
                    )
                }
                delay(10000)
                activity?.runOnUiThread {
                    mMap.clear()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        mMap.isMyLocationEnabled = true
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun saveLocation(l: LatLng) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()
        db.collection("users").document(currentUser).get().addOnSuccessListener {
            db.collection("users").document(currentUser).update(
                "location", l
            )
        }
    }

    private fun getNearbyLocation(l: LatLng) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()
        db.collection("users").get().addOnSuccessListener {
            for (i in it) {
                var str = i.get("location").toString()
                str = str.replace("[^-?.0-9]+".toRegex(), " ")
                val a = listOf(*str.trim { it <= ' ' }.split(" ").toTypedArray())
                if (i.get("location") != null && calculateDistance(
                        l,
                        LatLng(a[0].toDouble(), a[1].toDouble())
                    ) <= 0.300 && i.get("email").toString() != currentUser
                ) {
                    mMap.addMarker(
                        MarkerOptions()
                            .title(i.get("username").toString())
                            .position(LatLng(a[0].toDouble(), a[1].toDouble()))
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_run))
                    )
                }
            }
        }
    }

    private fun calculateDistance(pos1: LatLng, pos2: LatLng): Double {
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
        if (resultCode == -1) {
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
        binding.mapView.onDestroy()
        if (job.isActive) {
            btnPressed = true
            job.cancel()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


}