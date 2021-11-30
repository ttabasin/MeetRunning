package cat.copernic.meetrunning.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*


class StatsViewModel: ViewModel() {
    private val currentUsername = FirebaseAuth.getInstance().currentUser?.displayName.toString()
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
    var db = FirebaseFirestore.getInstance()
    var distance: String? = ""
    var time: String? = ""

    init{
        db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
            distance = "${"%.2f".format(it.get("distance"))}km"
            time = SimpleDateFormat("HH:mm:ss").format(it.get("time").toString().toInt().minus(TimeZone.getDefault().rawOffset))

        }
    }

    val user = currentUsername

}