package cat.copernic.meetrunning.ranking

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentRankingBinding
import cat.copernic.meetrunning.home.Post
import cat.copernic.meetrunning.home.PostAdapter
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*

class RankingFragment : Fragment(R.layout.fragment_ranking) {
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerRanking: RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var userAdapter: UserAdapter
    private lateinit var db: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRankingBinding.bind(view)

        binding.apply {

            //Posar el mes actual com a text
            val c = Calendar.getInstance()
            val currentMonth = c.get(Calendar.MONTH) + 1
            monthText.text = changeMonth(currentMonth.toString())

            calendar.setOnClickListener {
                //Crear una instància de DatePickerFragment
                val datePickerFragment = DatePickerFragment()
                val supportFragmentManager = requireActivity().supportFragmentManager

                supportFragmentManager.setFragmentResultListener(
                    "REQUEST_KEY",
                    viewLifecycleOwner
                ) { resultKey, bundle ->
                    //Canviar el text del mes
                    if (resultKey == "REQUEST_KEY") {
                        val date = bundle.getString("SELECTED_DATE")
                        monthText.text = changeMonth(date.toString())
                    }
                }

                //Mostrar el calendari
                datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
            }
        }




        recyclerRanking = binding.recyclerRanking
        recyclerRanking.layoutManager = LinearLayoutManager(context)
        recyclerRanking.setHasFixedSize(true)

        userArrayList = arrayListOf()

        userAdapter = UserAdapter(userArrayList)

        recyclerRanking.adapter = userAdapter

        EventChangeListener()

    }

    private fun EventChangeListener() {

        db = FirebaseFirestore.getInstance()
        db.collection("users").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        userArrayList.add(dc.document.toObject(User::class.java))

                    }
                }
                userAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeMonth(date: String): String {
        lateinit var month: String
        when {
            date.toString() == "01" -> {
                month = getString(R.string.january)
            }
            date.toString() == "02" -> {
                month = getString(R.string.february)
            }
            date.toString() == "03" -> {
                month = getString(R.string.march)
            }
            date.toString() == "04" -> {
                month = getString(R.string.april)
            }
            date.toString() == "05" -> {
                month = getString(R.string.may)
            }
            date.toString() == "06" -> {
                month = getString(R.string.june)
            }
            date.toString() == "07" -> {
                month = getString(R.string.july)
            }
            date.toString() == "08" -> {
                month = getString(R.string.august)
            }
            date.toString() == "09" -> {
                month = getString(R.string.september)
            }
            date.toString() == "10" -> {
                month = getString(R.string.october)
            }
            date.toString() == "11" -> {
                month = getString(R.string.november)
            }
            date.toString() == "12" -> {
                month = getString(R.string.december)
            }
        }
        return month
    }
}