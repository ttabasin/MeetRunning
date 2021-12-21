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

        supportActionBar?.hide()

        binding =
            DataBindingUtil.setContentView<ActivityForgotPasswordBinding>(this, R.layout.activity_forgot_password)

        var emailAddress = ""

        binding.buttonSE.setOnClickListener{
            emailAddress = binding.emailToSend.text.toString().trim()
            Log.i("Forgot", "$emailAddress")
            binding.emailToSend.setText("")
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)

        }

        binding.signInBtnP.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            this.finish()
            true
        }

    }

}