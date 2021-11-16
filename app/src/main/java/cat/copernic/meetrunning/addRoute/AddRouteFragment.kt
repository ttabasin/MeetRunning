package cat.copernic.meetrunning.addRoute

import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import cat.copernic.meetrunning.databinding.FragmentAddRouteBinding

import android.provider.MediaStore

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import cat.copernic.meetrunning.R


class AddRouteFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var binding: FragmentAddRouteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddRouteBinding.inflate(layoutInflater)

        binding.signUpContinue.setOnClickListener {
            it.findNavController().navigate(R.id.action_addRoute_to_addRouteMap)
        }

        binding.imageView3.setOnClickListener {
            dispatchTakePictureIntent()
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    val REQUEST_IMAGE_CAPTURE = 1

    //Hace la foto
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    //Pone la foto en el imageView
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("foto", "foto")
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageView3.setImageBitmap(imageBitmap)
        }
    }

}

