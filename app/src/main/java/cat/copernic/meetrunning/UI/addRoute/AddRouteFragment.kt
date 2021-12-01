package cat.copernic.meetrunning.UI.addRoute

import android.app.Activity
import android.content.ContentResolver
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentAddRouteBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import java.util.ArrayList

class AddRouteFragment : Fragment() {

    private lateinit var binding: FragmentAddRouteBinding
    private val mArrayUri: ArrayList<Uri?> = arrayListOf()
    private var pos = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddRouteBinding.inflate(layoutInflater)
        val args = AddRouteFragmentArgs.fromBundle(requireArguments())
        val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()
        binding.distanceTxt.text = "${"%.3f".format(args.distance)}Km"
        binding.timeTxt.text =
            SimpleDateFormat("HH:mm:ss").format(args.time - TimeZone.getDefault().rawOffset)
        binding.imageNext.setOnClickListener {
            if (mArrayUri.size != 0) {
                if (pos >= mArrayUri.size - 1) {
                    binding.photoGallery.setImageURI(mArrayUri[0])
                    pos = 0
                } else {
                    pos++
                    binding.photoGallery.setImageURI(mArrayUri[pos])
                }
            }

        }
        binding.imagePrev.setOnClickListener {
            if (mArrayUri.size != 0) {
                if (pos == 0) {
                    pos = mArrayUri.size - 1
                    binding.photoGallery.setImageURI(mArrayUri[pos])
                } else {
                    pos--
                    binding.photoGallery.setImageURI(mArrayUri[pos])
                }
            }
        }

        binding.continueRoute.setOnClickListener {
            //Mira si no te camps buits
            if (checkInput()) {
                val db = FirebaseFirestore.getInstance()
                val gcd = Geocoder(context, Locale.getDefault())
                //Crea l'objecte ruta
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

                //Mirar si el titol existeix
                db.collection("posts").document(binding.editTextTitle.text.toString()).get()
                    .addOnSuccessListener { document ->
                        //Si existeix no et deixa crear la ruta
                        if (document.exists()) {
                            Toast.makeText(context, R.string.error_title, Toast.LENGTH_LONG).show()
                        } else {
                            //Afegir la ruta i anar al home
                            db.collection("posts").document(binding.editTextTitle.text.toString())
                                .set(route).addOnCompleteListener {
                                    findNavController().navigate(AddRouteFragmentDirections.actionAddRouteToHome())
                                }
                            //Afegir la ruta a l'usuari
                            db.collection("users").document(currentUser).collection("routes")
                                .document(binding.editTextTitle.text.toString()).set(route)
                        }
                    }

            }
            val storage = FirebaseStorage.getInstance().reference
            for ((c, i) in mArrayUri.withIndex()){
                val path = storage.child("users/$currentUser/${binding.editTextTitle.text}/$c.jpg")
                //val bitmap = BitmapFactory.decodeStream(java.net.URL(i?.path.toString()).openStream())
                //val bitmap = i. as Bitmap
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, i)
                val baos = ByteArrayOutputStream()

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val uploadTask = path.putBytes(data)
                uploadTask.addOnSuccessListener {
                    Toast.makeText(requireContext(), "bien", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.photoGallery.setOnClickListener {
            mArrayUri.clear()
            openGallery()
        }

        return binding.root
    }

    private fun checkInput(): Boolean {
        if (binding.editTextDescription.text.isNotBlank() && binding.editTextDescription.text.isNotBlank()) {
            return true
        }
        Toast.makeText(requireContext(), R.string.error_empt, Toast.LENGTH_LONG).show()
        return false
    }

    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            pos = 0
            val data = result.data?.clipData
            if (data != null) {
                for (i in 0 until data.itemCount) {
                    mArrayUri.add(data.getItemAt(i).uri)
                }
                Log.d("img", "$data")
                binding.photoGallery.setImageURI(mArrayUri[0])
            } else if (result.data?.data != null) {
                mArrayUri.add(result.data?.data)
                binding.photoGallery.setImageURI(mArrayUri[0])
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startForActivityGallery.launch(intent)
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

