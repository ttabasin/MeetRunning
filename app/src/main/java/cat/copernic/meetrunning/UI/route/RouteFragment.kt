package cat.copernic.meetrunning.UI.route

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Observer
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
import java.text.SimpleDateFormat
import java.util.*
import com.bumptech.glide.Glide
import kotlinx.coroutines.*

class RouteFragment : Fragment() {

    private lateinit var binding: FragmentRouteBinding
    private lateinit var viewModel: RouteViewModel
    private lateinit var viewModelFactory: RouteViewModelFactory
    private var pos = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRouteBinding.inflate(layoutInflater)
        viewModelFactory =
            RouteViewModelFactory(RouteFragmentArgs.fromBundle(requireArguments()).post)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RouteViewModel::class.java)

        viewModel.description.observe(viewLifecycleOwner, {
            binding.descriptionRoute.text = it.toString()
        })
        viewModel.title.observe(viewLifecycleOwner, {
            binding.titleRoute.text = it
        })
        viewModel.distance.observe(
            viewLifecycleOwner,
            { binding.distanceTxt.text = "${"%.3f".format(it)}Km" })
        viewModel.time.observe(viewLifecycleOwner, {
            binding.timeTxt.text =
                SimpleDateFormat("HH:mm:ss").format(it?.minus(TimeZone.getDefault().rawOffset))
        })

        //setImage()
        binding.signUpContinue.setOnClickListener {
            it.findNavController()
                .navigate(RouteFragmentDirections.actionRouteToRouteMap(viewModel.pos.value?.toTypedArray()!!))
        }
        binding.imageNext.setOnClickListener {
            if (viewModel.photos.value?.size != 0) {
                if (pos >= viewModel.photos.value?.size!! - 1) {
                    pos = 0
                    setImage()
                } else {
                    pos++
                    setImage()
                }
            }

        }
        binding.imagePrev.setOnClickListener {
            if (viewModel.photos.value?.size != 0) {
                if (pos == 0) {
                    pos = viewModel.photos.value?.size!! - 1
                    setImage()
                } else {
                    pos--
                    setImage()
                }
            }
        }
        GlobalScope.launch {
            delay(1000)
            if (viewModel.readyPhotos.value!!) {
                Handler(Looper.getMainLooper()).post() {
                    setImage()
                }
            }
        }

        return binding.root
    }

    private fun setImage() {
        Glide.with(this).load(viewModel.photos.value!![pos])
            .error(R.drawable.ic_baseline_photo_library_24)
            .centerInside()
            .fitCenter()
            .placeholder(R.drawable.ic_baseline_photo_24)
            .into(binding.photoGallery)
    }

}