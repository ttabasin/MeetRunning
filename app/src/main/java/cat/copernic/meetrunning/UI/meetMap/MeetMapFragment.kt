package cat.copernic.meetrunning.UI.meetMap

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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentMeetMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.*
import java.util.*
import com.google.android.gms.maps.model.CameraPosition

import com.google.android.gms.maps.model.PolylineOptions





class MeetMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMeetMapBinding
    private lateinit var mMap: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var job: Job
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var btnPressed = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMeetMapBinding.inflate(layoutInflater)
        var mapViewBundle: Bundle? = null
        mapViewBundle = savedInstanceState?.getBundle("MapViewBundleKey")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync(this)
        return binding.root
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        getCurrentLocation()
        enableMyLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (isPermissionGranted()) {
            job = GlobalScope.launch(Dispatchers.IO) {
                while (!btnPressed) {
                    var location: LatLng
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        location = LatLng(it.latitude, it.longitude)
                        mMap.addCircle(
                            CircleOptions()
                                .center(location)
                                .radius(350.0)
                                .strokeColor(Color.BLACK)
                                .fillColor(Color.parseColor("#99ff5945"))
                        )
                    }
                    delay(1000)
                    activity?.runOnUiThread {
                        mMap.clear()
                    }
                }

            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0F))
            }
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
        if (job.isActive){
            btnPressed = true
            job.cancel()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }



}