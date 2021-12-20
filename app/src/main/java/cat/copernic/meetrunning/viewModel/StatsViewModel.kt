package cat.copernic.meetrunning.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class StatsViewModel: ViewModel() {

    private var db = FirebaseFirestore.getInstance()

    private val _currentUserEmail = MutableLiveData<String?>()

    private val _distance = MutableLiveData<String?>()
    val distance: LiveData<String?>
        get() = _distance

    private val _time = MutableLiveData<String?>()
    val time: LiveData<String?>
        get() = _time

    init{

        _currentUserEmail.value = FirebaseAuth.getInstance().currentUser?.email.toString()

        db.collection("users").document(_currentUserEmail.value!!).get().addOnSuccessListener {
            _distance.value = "${"%.2f".format(it.get("distance"))}km"
            _time.value = SimpleDateFormat("HH:mm:ss").format(it.get("time").toString().toInt().minus(TimeZone.getDefault().rawOffset))
        }
    }
}