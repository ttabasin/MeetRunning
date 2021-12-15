package cat.copernic.meetrunning.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class StatsViewModel(): ViewModel() {

    private val _currentUsername = MutableLiveData<String?>()
    val currentUsername: LiveData<String?>
        get() = _currentUsername

    private val _currentUserEmail = MutableLiveData<String?>()
    val currentUserEmail: LiveData<String?>
        get() = _currentUserEmail

    private var db = FirebaseFirestore.getInstance()

    private val _distance = MutableLiveData<String?>()
    val distance: LiveData<String?>
        get() = _distance

    private val _time = MutableLiveData<String?>()
    val time: LiveData<String?>
        get() = _time

    private val _description = MutableLiveData<String?>()
    val description: LiveData<String?>
        get() = _description

    init{

        _currentUsername.value = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        _currentUserEmail.value = FirebaseAuth.getInstance().currentUser?.email.toString()
        var email = ""

        db.collection("profile").document(currentUserEmail.value!!).get().addOnSuccessListener {
            email = it.getString("email")!!

            db.collection("users").document(email).get().addOnSuccessListener {
                _distance.value = "${"%.2f".format(it.get("distance"))}km"
                _time.value = SimpleDateFormat("HH:mm:ss").format(it.get("time").toString().toInt().minus(TimeZone.getDefault().rawOffset))
                _description.value = it.getString("description")
            }


        }


    }

}