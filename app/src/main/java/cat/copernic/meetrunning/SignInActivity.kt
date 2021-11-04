package cat.copernic.meetrunning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val signUpBt =findViewById<Button>(R.id.signUpBt)
        val signInBt = findViewById<Button>(R.id.signInBt)

        signUpBt.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        signInBt.setOnClickListener {
            if (checkInput()){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

    }
    //Comprueba que los campos no esten vacios
    private fun checkInput() : Boolean{
        if (findViewById<EditText>(R.id.username).text.isNotBlank() &&
            findViewById<EditText>(R.id.password).text.isNotBlank()){
            return true
        }
        return false
    }   
}