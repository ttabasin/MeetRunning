package cat.copernic.meetrunning.UI.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity: AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        //Amagar la app bar
        supportActionBar?.hide()

        binding =
            DataBindingUtil.setContentView<ActivityForgotPasswordBinding>(this, R.layout.activity_forgot_password)

        var emailAddress = ""

        //Funcionalitat del botó enviar email
        binding.buttonSE.setOnClickListener{
            //Agafar el email posat al edit text
            emailAddress = binding.emailToSend.text.toString().trim()
            //Posar el edit text en buit
            binding.emailToSend.setText("")
            //Enviar el mail de recuperar contrasenya
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
            //Iniciar el sign in activity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)

        }

        //Botó per anar al sign in activity
        binding.signInBtnP.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            this.finish()
            true
        }

    }

}