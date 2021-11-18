package cat.copernic.meetrunning.addRoute

import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import cat.copernic.meetrunning.databinding.FragmentAddRouteBinding

import android.provider.MediaStore

import android.content.Intent
import android.graphics.Bitmap
import android.location.Geocoder
import android.util.Log
import androidx.navigation.fragment.findNavController
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.home.PostHome
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.Route
import java.util.*
import java.util.Locale


class AddRouteFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var binding: FragmentAddRouteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddRouteBinding.inflate(layoutInflater)
        val args = AddRouteFragmentArgs.fromBundle(requireArguments())
        binding.distanceTxt.text = args.distance.toString()

        binding.signUpContinue.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val gcd = Geocoder(context, Locale.getDefault())
            val route = PostHome(
                binding.editTextTextPersonName2.text.toString(),
                binding.editTextTextMultiLine2.text.toString(),
                args.route.toMutableList(),
                FirebaseAuth.getInstance().currentUser?.email.toString(),
                gcd.getFromLocation(args.route[0].latitude, args.route[0].longitude, 1)[0].locality
            )
            Log.d("city", gcd.getFromLocation(args.route[0].latitude, args.route[0].longitude, 1)[0].locality)
            db.collection("posts").document(binding.editTextTextPersonName2.text.toString())
                .set(route).addOnCompleteListener {
                    findNavController().navigate(AddRouteFragmentDirections.actionAddRouteToHome())
                }
        }

        binding.imageView3.setOnClickListener {
            dispatchTakePictureIntent()
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    val REQUEST_IMAGE_CAPTURE = 1

    //Hace la foto
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    //Pone la foto en el imageView
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("foto", "foto")
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageView3.setImageBitmap(imageBitmap)
        }
    }

}

