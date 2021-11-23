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
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentAddRouteBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
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
        val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()
        binding.distanceTxt.text = "${"%.3f".format(args.distance)}Km"
        binding.timeTxt.text = SimpleDateFormat("HH:mm:ss").format(args.time)

        /*if(binding.editTextDescription.text.isEmpty()){
            binding.signUpContinue.setEnabled(false)
        }else{*/
        binding.signUpContinue.setOnClickListener {
            if (checkInput()) {
                val db = FirebaseFirestore.getInstance()
                val gcd = Geocoder(context, Locale.getDefault())
                val route = Route(
                    binding.editTextTitle.text.toString(),
                    binding.editTextDescription.text.toString(),
                    args.route.toMutableList(),
                    currentUser,
                    gcd.getFromLocation(
                        args.route[0].latitude,
                        args.route[0].longitude,
                        1
                    )[0].locality,
                    args.distance.toDouble(),
                    args.time
                )

                //Actualitzar la distància feta de l'usuari
                db.collection("users").document(currentUser).get().addOnSuccessListener {
                    db.collection("users").document(currentUser)
                        .update(
                            mapOf(
                                "distance" to args.distance.toDouble() + it.getDouble("distance")!!
                            )
                        )
                }

                //Afegir la ruta i anar al home
                db.collection("posts").document(binding.editTextTitle.text.toString())
                    .set(route).addOnCompleteListener {
                        findNavController().navigate(AddRouteFragmentDirections.actionAddRouteToHome())
                    }

                //Afegir la ruta a l'usuari
                db.collection("useres").document(currentUser).collection("routes")
                    .document(binding.editTextTitle.text.toString()).set(route)
            }
        }
        //}


        binding.imageView3.setOnClickListener {
            dispatchTakePictureIntent()
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun checkInput(): Boolean {
        if (binding.editTextDescription.text.isNotBlank() && binding.editTextDescription.text.isNotBlank()) {
            return true
        }
        Toast.makeText(requireContext(), R.string.error_empt, Toast.LENGTH_LONG).show()
        return false
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
    var distance: Double = 0.0,
    var time: Int = 0
)

