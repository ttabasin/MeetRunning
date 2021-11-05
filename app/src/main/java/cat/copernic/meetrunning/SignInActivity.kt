package cat.copernic.meetrunning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import cat.copernic.meetrunning.databinding.ActivitySignInBinding

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
        binding.signInBt.setOnClickListener {
            if (checkInput()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun checkInput(): Boolean {
        if (binding.username.text.isNotBlank() &&
            binding.password.text.isNotBlank()
        ) {
            return true
        }
        Toast.makeText(this, "Empty Fields!", Toast.LENGTH_LONG).show()
        return false
    }
}