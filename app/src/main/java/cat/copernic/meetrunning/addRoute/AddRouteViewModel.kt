package cat.copernic.meetrunning.addRoute

import android.util.Log
import androidx.lifecycle.ViewModel

class AddRouteViewModel: ViewModel() {

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed!")
    }
}