package cat.copernic.meetrunning.UI.profile

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import cat.copernic.meetrunning.MainActivity
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentEditProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var db: FirebaseFirestore
    private var dataF: Uri? = null
    private var description: String? = ""
    private var continueBT = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        val currentUser = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        db = FirebaseFirestore.getInstance()

        //Posar el nom d'usuari al camp de text
        binding.changeUsername.setText(currentUser)
        //Posar l'email al camp de text
        binding.changeEmail.setText(currentUserEmail)
        //Posar la descripció al camp de text
        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            binding.changeDescription.setText(it.getString("description"))
            description = it.getString("description")
        }

        //Canviar la foto de perfil de l'usuari
        binding.changePhoto.setOnClickListener {
            openGallery()
        }



        //Botó per confirmar els canvis de l'usuari
        binding.confirmBtn.setOnClickListener {



            //Mirar si s'ha actualitzat la contrasenya per fer el canvi a la base de dades
            val user = FirebaseAuth.getInstance().currentUser
            val newPassword = binding.changePassword.text.toString().trim()

            if(checkInput()){
                user!!.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User password updated.")
                        }
                    }
            }


            //Mirar si s'ha canviat el nom d'usuari per fer el canvi a la base de dades
            if (currentUser != binding.changeUsername.toString().trim()) {
                val profileUpdates = userProfileChangeRequest {
                    displayName = binding.changeUsername.text.toString().trim()
                }

                FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
            }

            //Mirar si s'ha canviat la foto de perfil
            if (dataF != null) {
                val profileUpdates = userProfileChangeRequest {
                    photoUri = Uri.parse(dataF.toString())
                }
                FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
                println(dataF.toString())
            }

            //Canviar la descripció a la base de dades
            db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
                db.collection("users").document(currentUserEmail)
                    .update(
                        mapOf(
                            "description" to binding.changeDescription.text.toString()
                        )
                    )
            }

            val storage = FirebaseStorage.getInstance().reference
            val path = storage.child("users/$currentUserEmail/profile.jpg")
            val bitmap = binding.changePhoto.drawable.toBitmap()
            val baos = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val uploadTask = path.putBytes(data)
            uploadTask.addOnSuccessListener {  }

            //Reiniciar l'activity per fer efectius els canvis

            if(continueBT){
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }

        }
        setProfileImage()
        return binding.root
    }

    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            dataF = result.data?.data
            Glide.with(this)
                .load(dataF)
                .centerInside()
                .circleCrop()
                .into(binding.changePhoto)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startForActivityGallery.launch(intent)
    }

    private fun checkInput(): Boolean {

        if (binding.changePassword.text.toString().trim().isNotBlank()) {
            if (binding.changePassword.text.toString().trim() == binding.changeConfirmPassword.text.toString().trim()) {
                return true
            } else {
                Toast.makeText(context, R.string.error_passwd, Toast.LENGTH_LONG).show()
            }
        }
        return false

    }

    private fun setProfileImage() {
        FirebaseStorage.getInstance().reference.child("users/${FirebaseAuth.getInstance().currentUser?.email}/profile.jpg").downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it)
                .centerInside()
                .circleCrop()
                .into(binding.changePhoto)
        }
    }

}
