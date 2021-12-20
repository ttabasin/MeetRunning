package cat.copernic.meetrunning.UI.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import cat.copernic.meetrunning.MainActivity
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding =
            DataBindingUtil.setContentView<ActivitySignInBinding>(this, R.layout.activity_sign_in)

        //Iniciar la pantalla de registre
        binding.signUpBt.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.forgotPass.setOnClickListener{
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        //Funció per inciar sessió
        setup()
    }

    private fun setup() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        //Mirar si l'usuari existeix per iniciar directament al home
        if(auth.currentUser != null){
            val homeIntent = Intent(this, MainActivity::class.java)
            startActivity(homeIntent)
        }else{

            //Botó iniciar la sessió
            binding.signInBt.setOnClickListener {

                //Comprovar els camps
                if (checkInput()) {

                    //Crear la sessió amb els camps escrits
                    auth.signInWithEmailAndPassword(
                        binding.email.text.toString(),
                        binding.password.text.toString()
                    )
                        .addOnCompleteListener() {
                            if (it.isSuccessful) {
                                showHome()
                            } else {
                                showAlert()
                            }
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

    private fun showHome() {
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