package cat.copernic.meetrunning.addRoute

import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.provider.MediaStore
import android.content.Intent
import android.graphics.Bitmap
import android.location.Geocoder
import android.util.Log
import androidx.navigation.fragment.findNavController
import cat.copernic.meetrunning.databinding.FragmentAddRouteBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import java.util.Locale

class AddRouteFragment : Fragment() {

    private lateinit var binding: FragmentAddRouteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddRouteBinding.inflate(layoutInflater)
        val args = AddRouteFragmentArgs.fromBundle(requireArguments())
        binding.distanceTxt.text = "${"%.3f".format(args.distance)}Km"

        /*if(binding.editTextDescription.text.isEmpty()){
            binding.signUpContinue.setEnabled(false)
        }else{*/
        binding.signUpContinue.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val gcd = Geocoder(context, Locale.getDefault())
            val route = Route(
                binding.editTextTitle.text.toString(),
                binding.editTextDescription.text.toString(),
                args.route.toMutableList(),
                FirebaseAuth.getInstance().currentUser?.email.toString(),
                gcd.getFromLocation(args.route[0].latitude, args.route[0].longitude, 1)[0].locality,
                args.distance.toDouble()
            )

            val email = FirebaseAuth.getInstance().currentUser?.email
            val a = db.collection("users").document(email.toString()).get().addOnSuccessListener {
                db.collection("users").document(email.toString())
                    .update(mapOf("distance" to args.distance.toDouble() + it.getDouble("distance")!!))
            }
            Log.d(
                "city",
                gcd.getFromLocation(args.route[0].latitude, args.route[0].longitude, 1)[0].locality
            )
            db.collection("posts").document(binding.editTextTitle.text.toString())
                .set(route).addOnCompleteListener {
                    findNavController().navigate(AddRouteFragmentDirections.actionAddRouteToHome())
                }
            db.collection("useres").document(email.toString()).collection("routes")
                .document(binding.editTextTitle.text.toString()).set(route)
        }
        //}


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

data class Route(
    var title: String = "",
    var description: String = "",
    var route: MutableList<LatLng> = ArrayList(),
    var user: String = "",
    var city: String = "",
    var distance: Double = 0.0
)

