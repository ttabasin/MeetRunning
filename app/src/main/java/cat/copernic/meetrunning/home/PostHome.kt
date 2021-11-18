package cat.copernic.meetrunning.home

import android.widget.ImageView
import com.google.android.gms.maps.model.LatLng

data class PostHome(
    val title: String = "",
    val description: String = "",
    val route: MutableList<LatLng>,
    val user: String = "",
    val city: String = ""
)
//var image: ImageView ?= null*/)
