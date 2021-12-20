package cat.copernic.meetrunning

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import cat.copernic.meetrunning.UI.authentication.SignInActivity
import cat.copernic.meetrunning.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
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
    private var db = FirebaseFirestore.getInstance()
    private val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
                val bundle = Bundle()
                bundle.putString("email", currentUserEmail)
                navController.navigate(R.id.myRoutes, bundle)
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

        /*db = FirebaseFirestore.getInstance()

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
            } else if (language.trim() == "[en_EN]" || language.trim() == "[en_US]") {
                val locale = Locale("en", "EN")
                val config: Configuration = this.resources.configuration
                config.setLocale(locale)

                this.resources.updateConfiguration(
                    config,
                    this.resources.displayMetrics
                )
            } else if (language.trim() == "[es_ES]") {
                val locale = Locale("es", "ES")
                val config: Configuration = this.resources.configuration
                config.setLocale(locale)

                this.resources.updateConfiguration(
                    config,
                    this.resources.displayMetrics
                )
            }


        }*/

        createNotificationChannel()
        GlobalScope.launch {
            val user = FirebaseAuth.getInstance().currentUser?.email.toString()
            while (true) {

                db.collection("users").document(user).get().addOnSuccessListener {
                    val achievements = it.get("achievements") as MutableMap<String, Boolean>
                    if (achievements["25"] == false) {
                        println("la cosa ba")
                    }
                    if (it.get("distance").toString()
                            .toDouble() > 25 && achievements["25"] == false
                    ) {
                        sendNotification("${getString(R.string.achievement)} 25Km")
                        achievements["25"] = true
                        db.collection("users").document(user).update("achievements", achievements)
                    }
                    if (it.get("distance").toString()
                            .toDouble() > 50 && achievements["50"] == false
                    ) {
                        sendNotification("${getString(R.string.achievement)} 50Km")
                        achievements["50"] = true
                        db.collection("users").document(user).update("achievements", achievements)
                    }
                    if (it.get("distance").toString()
                            .toDouble() > 100 && achievements["100"] == false
                    ) {
                        sendNotification("${getString(R.string.achievement)} 100Km")
                        achievements["100"] = true
                        db.collection("users").document(user).update("achievements", achievements)
                    }
                }
                delay(15000)
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

    private val CHANNEL_ID = "meetrunning"
    private val notificacioId = 1

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //>=26 version Oreo i superiors
            val nom = "Titol de la notificació"
            val descripcio = "Descripció notificació."
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(CHANNEL_ID, nom, importancia)
            canal.description = descripcio
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    private fun sendNotification(m: String) {

        val resultIntent: Intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val resultPendingIntent = PendingIntent.getActivity(
            this, 0, resultIntent, 0
        )

        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("MeetRunning")
            .setContentText(m)
            //.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .setContentIntent(resultPendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Notification.DEFAULT_SOUND, Notification.DEFAULT_VIBRATE, Notification.DEFAULT_LIGHTS.
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificacioId, mBuilder.build())
    }

    override fun onPause() {
        super.onPause()
        val map = mapOf(
            "latitude" to 0.00000,
            "longitude" to 0.00000
        )
        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            db.collection("users").document(currentUserEmail).update(
                mapOf(
                    "location" to map
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        fusedLocationClient.lastLocation.addOnSuccessListener {
            var location = LatLng(it.latitude, it.longitude)
            saveLocation(location)
        }
    }

    private fun saveLocation(l: LatLng) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()
        db.collection("users").document(currentUser).get().addOnSuccessListener {
            db.collection("users").document(currentUser).update(
                "location", l
            )
        }
    }
}
