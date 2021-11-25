package cat.copernic.meetrunning.UI.meetMap

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cat.copernic.meetrunning.databinding.FragmentMeetMapBinding

class MeetMapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var binding = FragmentMeetMapBinding.inflate(layoutInflater)

        return binding.root
    }

}