package cat.copernic.meetrunning.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import cat.copernic.meetrunning.MainActivity
import cat.copernic.meetrunning.ProviderType
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        binding =
            DataBindingUtil.setContentView<ActivitySignInBinding>(this, R.layout.activity_sign_in)

        binding.signUpBt.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        setup()
    }

    private fun setup() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        if(auth.currentUser != null){
            val homeIntent = Intent(this, MainActivity::class.java)
            startActivity(homeIntent)
        }

        binding.signInBt.setOnClickListener {

            if (checkInput()) {
                auth.signInWithEmailAndPassword(
                    binding.email.text.toString(),
                    binding.password.text.toString()
                )
                    .addOnCompleteListener() {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
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
        builder.setPositiveButton(R.string.accept, null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, MainActivity::class.java)
        startActivity(homeIntent)
    }

    private fun checkInput(): Boolean {
        if (binding.email.text.isNotBlank() &&
            binding.password.text.isNotBlank()
        ) {
            return true
        }
        Toast.makeText(this, R.string.error_empt, Toast.LENGTH_LONG).show()
        return false
    }

}