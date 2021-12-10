package cat.copernic.meetrunning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.firebase.storage.FirebaseStorage

enum class ProviderType {
    BASIC
}

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.home, R.id.meetMap, R.id.ranking, R.id.favorites, R.id.settings, R.id.signInActivity
            ), drawerLayout
        )

        val navController = this.findNavController(R.id.myNavHostFragment)

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)

        //Añade el email al nav header
        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.emailMenu).text =
            FirebaseAuth.getInstance().currentUser?.email.toString()

        //Pantalla perfil
        binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.photoProfile).setOnClickListener {
            navController.navigate(R.id.myRoutes)
            drawerLayout.closeDrawers()
        }

        setProfileImage()
        //Usuario al header
        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.usernameMenu).text =
            FirebaseAuth.getInstance().currentUser?.displayName.toString()

        //logOut
        binding.navView.menu.getItem(9).setOnMenuItemClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            FirebaseAuth.getInstance().signOut()
            startActivity(intent)
            true
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
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

}