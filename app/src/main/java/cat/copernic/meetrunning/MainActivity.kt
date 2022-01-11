package cat.copernic.meetrunning

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import cat.copernic.meetrunning.UI.authentication.SignInActivity
import cat.copernic.meetrunning.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
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
            runOnUiThread {
                binding.navView.getHeaderView(0).findViewById<TextView>(R.id.usernameMenu).text =
                    FirebaseAuth.getInstance().currentUser?.displayName.toString()
            }
        }

        //logOut
        binding.navView.menu.getItem(9).setOnMenuItemClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            FirebaseAuth.getInstance().signOut()
            startActivity(intent)
            this.finish()
            true
        }

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
            .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
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
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val REQUEST_CHECK_SETTINGS = 0
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            Log.e("c", "tenemos permiso gente")
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null){
                    val location = LatLng(it.latitude, it.longitude)
                    saveLocation(location)
                }else{
                    saveLocation(LatLng(0.0, 0.0))
                }
            }
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
