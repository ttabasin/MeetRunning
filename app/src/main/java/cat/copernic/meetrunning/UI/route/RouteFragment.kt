package cat.copernic.meetrunning.UI.route

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentRouteBinding
import cat.copernic.meetrunning.viewModel.RouteViewModel
import cat.copernic.meetrunning.viewModel.RouteViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class RouteFragment : Fragment() {

    private lateinit var binding: FragmentRouteBinding
    private lateinit var viewModel: RouteViewModel
    private lateinit var viewModelFactory: RouteViewModelFactory
    private var pos = 0
    val photos = arrayListOf<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRouteBinding.inflate(layoutInflater)
        viewModelFactory =
            RouteViewModelFactory(RouteFragmentArgs.fromBundle(requireArguments()).post)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RouteViewModel::class.java)

        binding.descriptionRoute.text = viewModel.description
        binding.titleRoute.text = viewModel.title
        binding.distanceTxt.text = "${"%.3f".format(viewModel.distance)}Km"
        binding.timeTxt.text =
            SimpleDateFormat("HH:mm:ss").format(viewModel.time?.minus(TimeZone.getDefault().rawOffset))

        val storage = FirebaseStorage.getInstance()
            .reference.child("users/${viewModel.user}/${viewModel.title}").listAll()
            .addOnSuccessListener {
                for (i in it.items) {
                    i.downloadUrl.addOnSuccessListener { itUri ->
                        photos.add(itUri!!)
                        Glide.with(this).load(photos[0])
                            .error(R.drawable.ic_baseline_photo_library_24)
                            .into(binding.photoGallery)
                    }
                }
            }
        /*storage.downloadUrl.addOnSuccessListener { url ->
           photos.add(url)
        }*/
        binding.signUpContinue.setOnClickListener {
            it.findNavController()
                .navigate(RouteFragmentDirections.actionRouteToRouteMap(viewModel.pos.toTypedArray()))
        }
        binding.imageNext.setOnClickListener {
            if (photos.size != 0) {
                if (pos >= photos.size - 1) {
                    pos = 0
                    setImage()
                } else {
                    pos++
                    setImage()
                }
            }

        }
        binding.imagePrev.setOnClickListener {
            if (photos.size != 0) {
                if (pos == 0) {
                    pos = photos.size - 1
                    setImage()
                } else {
                    pos--
                    setImage()
                }
            }
        }
        return binding.root
    }

    private fun setImage() {
        Glide.with(this).load(photos[pos])
            .error(R.drawable.ic_baseline_photo_library_24)
            .centerInside()
            .fitCenter()
            .placeholder(R.drawable.ic_baseline_photo_24)
            .into(binding.photoGallery)
    }

}