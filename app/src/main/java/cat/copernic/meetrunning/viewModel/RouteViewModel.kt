package cat.copernic.meetrunning.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import cat.copernic.meetrunning.dataClass.DataRoute
import cat.copernic.meetrunning.dataClass.LatLng
import com.google.android.gms.maps.model.LatLng as GLatLng

class RouteViewModel(route: DataRoute?): ViewModel() {
    val description = route?.description
    val title = route?.title
    val distance = route?.distance
    val time = route?.time
    val r = route?.route
    val pos: ArrayList<GLatLng> = arrayListOf()
    val user = route?.user
    init {
        convertPos()
    }
    private fun convertPos() {
        for (p in r!!) {
            pos.add(GLatLng(p.latitude, p.longitude))
        }
    }

}