package cat.copernic.meetrunning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import cat.copernic.meetrunning.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        binding =
            DataBindingUtil.setContentView<ActivitySignUpBinding>(this, R.layout.activity_sign_up)
        //Pantalla principal
        binding.signUpContinue.setOnClickListener {
            if (checkInput()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        //Pantalla Login
        binding.signInBtn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }


    //Comprueba que los campos no esten vacios
    private fun checkInput(): Boolean {
        if (binding.editPassword.text.isNotBlank() && binding.editPassword.text.isNotBlank()
            && binding.SignUpEmail.text.isNotBlank() && binding.SignUpConfirmEmail.text.isNotBlank()
            && binding.checkBox.isChecked
        ) {
            return true
        }
        Toast.makeText(this, "Campos vacios", Toast.LENGTH_LONG).show()
        return false
    }
}