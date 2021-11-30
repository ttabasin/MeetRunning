package cat.copernic.meetrunning.UI.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.ArrayList

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val mArrayUri: ArrayList<Uri?> = arrayListOf()
    private var pos = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        val currentUser = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()

        binding.changeUsername.setText(currentUser)
        binding.changeEmail.setText(currentUserEmail)

        binding.changePhoto.setOnClickListener {
            mArrayUri.clear()
            openGallery()

        }

        return binding.root
    }

    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            binding.changePhoto.setImageURI(data)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startForActivityGallery.launch(intent)
    }

}