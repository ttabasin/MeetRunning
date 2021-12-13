package cat.copernic.meetrunning.UI.authentication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cat.copernic.meetrunning.MainActivity
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        binding =
            DataBindingUtil.setContentView<ActivitySignUpBinding>(this, R.layout.activity_sign_up)

        binding.signInBtn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        setup(this)
    }

    private fun checkInput(): Boolean {
        if (binding.editPassword.text.isNotBlank() && binding.editConfirmPassword.text.isNotBlank()
            && binding.SignUpEmail.text.isNotBlank() && binding.SignUpConfirmEmail.text.isNotBlank()
            && binding.checkBox.isChecked
        ) {
            if (binding.editPassword.text.toString() == binding.editConfirmPassword.text.toString()) {
                return true
            }
            Toast.makeText(this, R.string.error_passwd, Toast.LENGTH_LONG).show()
            return false
        }
        Toast.makeText(this, R.string.error_empt, Toast.LENGTH_LONG).show()
        return false
    }

    private fun setup(context: Context) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        binding.signUpContinue.setOnClickListener {

            if (checkInput()) {
                auth.createUserWithEmailAndPassword(
                    binding.SignUpConfirmEmail.text.toString(),
                    binding.editPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {

                        db = FirebaseFirestore.getInstance()
                        val user = hashMapOf(
                            "username" to binding.SignUpEmail.text.toString(),
                            "email" to binding.SignUpConfirmEmail.text.toString(),
                            "distance" to 0.0,
                            "time" to 0,
                            "description" to getString(R.string.auto_bio),
                            "language" to "EN_en"
                        )

                        db.collection("users").document(binding.SignUpConfirmEmail.text.toString())
                            .set(user)

                        //Añade el nombre de usuario
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(binding.SignUpEmail.text.toString()).build()

                        val storage = FirebaseStorage.getInstance().reference
                        val path = storage.child("users/${auth.currentUser?.email.toString()}/profile.jpg")
                        val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.profile)
                        println("$bitmap")
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        val uploadTask = path.putBytes(data)
                        uploadTask.addOnSuccessListener {  }

                        auth.currentUser?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("User", "User profile updated.")
                                }
                            }
                        showHome()
                    } else {
                        showAlert()
                    }
                }
            }
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(R.string.error_auth)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome() {
        val homeIntent = Intent(this, MainActivity::class.java)
        startActivity(homeIntent)
    }

}
