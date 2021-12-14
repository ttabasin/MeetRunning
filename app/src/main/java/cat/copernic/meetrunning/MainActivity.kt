package cat.copernic.meetrunning

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import cat.copernic.meetrunning.UI.authentication.SignInActivity
import cat.copernic.meetrunning.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var language: String = "[en_EN]"
    private lateinit var db: FirebaseFirestore
    private val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.home,
                R.id.meetMap,
                R.id.ranking,
                R.id.favorites,
                R.id.settings,
                R.id.signInActivity
            ), drawerLayout
        )

        val navController = this.findNavController(R.id.myNavHostFragment)

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)

        //Añade el email al nav header
        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.emailMenu).text =
            FirebaseAuth.getInstance().currentUser?.email.toString()

        //Pantalla perfil
        binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.photoProfile)
            .setOnClickListener {
                navController.navigate(R.id.myRoutes)
                drawerLayout.closeDrawers()
            }

        GlobalScope.launch {
            delay(1000)
            setProfileImage()
        }
        //Usuario al header
        GlobalScope.launch {
            delay(1000)
            binding.navView.getHeaderView(0).findViewById<TextView>(R.id.usernameMenu).text =
                FirebaseAuth.getInstance().currentUser?.displayName.toString()
        }

        //logOut
        binding.navView.menu.getItem(9).setOnMenuItemClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            FirebaseAuth.getInstance().signOut()
            startActivity(intent)
            true
        }

        db = FirebaseFirestore.getInstance()

        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            language = it.getString("language").toString()
            Log.i("Main", "IDIOMAAAAA: $language")

            if (language.trim() == "[ca_ES]") {
                val locale = Locale("ca", "ES")
                val config: Configuration = this.resources.configuration
                config.setLocale(locale)

                this.resources.updateConfiguration(
                    config,
                    this.resources.displayMetrics
                )
            } else if(language.trim() == "[en_EN]" || language.trim() == "[en_US]"){
                val locale = Locale("en", "EN")
                val config: Configuration = this.resources.configuration
                config.setLocale(locale)

                this.resources.updateConfiguration(
                    config,
                    this.resources.displayMetrics
                )
            } else if(language.trim() == "[es_ES]"){
                val locale = Locale("es", "ES")
                val config: Configuration = this.resources.configuration
                config.setLocale(locale)

                this.resources.updateConfiguration(
                    config,
                    this.resources.displayMetrics
                )
            }


        }


    }

    private fun setProfileImage() {
        FirebaseStorage.getInstance().reference.child("users/${FirebaseAuth.getInstance().currentUser?.email}/profile.jpg").downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it)
                .centerInside()
                .circleCrop()
                .into(binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.photoProfile))
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(
            navController,
            appBarConfiguration
        ) || super.onSupportNavigateUp()
    }

    /*override fun onSaveInstanceState(guardarEstado: Bundle) {
        super.onSaveInstanceState(guardarEstado)

        db = FirebaseFirestore.getInstance()

        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            language = it.getString("language").toString()
            Log.i("Main", "SAVE INSTANCE: $language")
            guardarEstado.putString("language", language)
        }

    }

    override fun onRestoreInstanceState(recEstado: Bundle) {
        super.onRestoreInstanceState(recEstado)
        language = recEstado.getString("language")!!
        val config: Configuration = this.resources.configuration
        Log.i("Main", "RESTORE INSTANCE: $language")

        if(language == "[en_EN]"){
            val locale = Locale("en", "EN")
            config.setLocale(locale)
        }else if(language == "[es_ES]"){
            val locale = Locale("es", "ES")
            config.setLocale(locale)
        }else if(language == "[ca_ES]"){
            val locale = Locale("ca", "ES")
            config.setLocale(locale)
        }

        this.resources.updateConfiguration(
            config,
            this.resources.displayMetrics
        )

    }*/

    override fun onDestroy() {
        super.onDestroy()
        
    }

}