package cat.copernic.meetrunning.UI.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import cat.copernic.meetrunning.MainActivity
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentEditProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var db: FirebaseFirestore
    private var dataF: Uri? = null
    private var description: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        val currentUser = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        db = FirebaseFirestore.getInstance()

        binding.changeUsername.setText(currentUser)
        binding.changeEmail.setText(currentUserEmail)
        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            binding.changeDescription.setText(it.getString("description"))
            description = it.getString("description")
        }

        binding.changePhoto.setOnClickListener {

            openGallery()
        }

        binding.confirmBtn.setOnClickListener {

            if (checkInput()) {
                FirebaseAuth.getInstance().currentUser?.updatePassword(
                    binding.changePassword.text.toString().trim()
                )
            }

            if (currentUser != binding.changeUsername.toString().trim()) {
                val profileUpdates = userProfileChangeRequest {
                    displayName = binding.changeUsername.text.toString().trim()
                }

                FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
            }

            if (dataF != null) {
                val profileUpdates = userProfileChangeRequest {
                    photoUri = Uri.parse(dataF.toString())
                }
                FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
                println("${dataF.toString()}")
            }

            db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
                db.collection("users").document(currentUserEmail)
                    .update(
                        mapOf(
                            "description" to binding.changeDescription.text.toString()
                        )
                    )
            }


            it.findNavController()
                .navigate(
                    EditProfileFragmentDirections
                        .actionEditProfileToMyRoutes()
                )


            val storage = FirebaseStorage.getInstance().reference
            val path = storage.child("users/$currentUserEmail/profile.jpg")
            val bitmap = binding.changePhoto.drawable.toBitmap()
            val baos = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val uploadTask = path.putBytes(data)
            uploadTask.addOnSuccessListener {  }
        }
        return binding.root
    }

    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            dataF = result.data?.data
            binding.changePhoto.setImageURI(dataF)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startForActivityGallery.launch(intent)
    }

    private fun checkInput(): Boolean {

        if (binding.changePassword.text.toString().isNotBlank()) {
            if (binding.changePassword.text.toString() == binding.changeConfirmPassword.text.toString()) {
                return true
            } else {
                Toast.makeText(context, R.string.error_passwd, Toast.LENGTH_LONG).show()
            }
        }
        return false

    }

}