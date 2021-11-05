package cat.copernic.meetrunning

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cat.copernic.meetrunning.R.layout.fragment_home
import cat.copernic.meetrunning.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = FragmentHomeBinding.inflate(layoutInflater)

        //binding.recycler.layoutManager = LinearLayoutManager(this)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentHomeBinding.inflate(layoutInflater)

        binding.floatingActionButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_home_to_addRoute)
        }
        // Inflate the layout for this fragment
        return binding.root
    }


}