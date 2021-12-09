package cat.copernic.meetrunning.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.UI.route.RouteFragment
import cat.copernic.meetrunning.dataClass.DataRoute
import cat.copernic.meetrunning.dataClass.LatLng
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng as GLatLng

class RouteViewModel(route: DataRoute?) : ViewModel() {
    private val _description = MutableLiveData<String?>()
    val description: LiveData<String?>
        get() = _description
    private val _title = MutableLiveData<String?>()
    val title: LiveData<String?>
        get() = _title
    private val _distance = MutableLiveData<Double?>()
    val distance: LiveData<Double?>
        get() = _distance
    private val _time = MutableLiveData<Int?>()
    val time: LiveData<Int?>
        get() = _time
    private val _r = MutableLiveData<MutableList<LatLng>>()
    val r: LiveData<MutableList<LatLng>>
        get() = _r
    private val _pos = MutableLiveData<MutableList<GLatLng>>()
    val pos: LiveData<MutableList<GLatLng>>
        get() = _pos
    private val _user = MutableLiveData<String?>()
    val user: LiveData<String?>
        get() = _user
    private val _photos = MutableLiveData<MutableList<Uri>>()
    val photos: LiveData<MutableList<Uri>>
        get() = _photos
    private var _readyPhotos = MutableLiveData<Boolean>()
    val readyPhotos: LiveData<Boolean>
        get() = _readyPhotos

    init {
        _description.value = route?.description
        _title.value = route?.title
        _distance.value = route?.distance
        _r.value = route?.route
        _user.value = route?.user
        _time.value = route?.time
        _readyPhotos.value = false
        _photos.value = arrayListOf()
        _pos.value = arrayListOf()
        convertPos()
        getPhotos()
    }

    private fun convertPos() {
        for (p in _r.value!!) {
            _pos.value?.add(GLatLng(p.latitude, p.longitude))
        }
    }

    private fun getPhotos() {
        FirebaseStorage.getInstance()
            .reference.child("users/${_user.value}/${_title.value}").listAll()
            .addOnSuccessListener {
                for (i in it.items) {
                    i.downloadUrl.addOnSuccessListener { itUri ->
                        _photos.value?.add(itUri!!)
                        _readyPhotos.value = true
                    }
                }
            }
    }
}