package cat.copernic.meetrunning.UI.ranking

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.adapters.UserAdapter
import cat.copernic.meetrunning.dataClass.User
import cat.copernic.meetrunning.databinding.FragmentRankingBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
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
                        Log.e("Hola", "$date")
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

        addRouteToList()

    }

    //Mostrar els usuaris ordenats per km fets
    @SuppressLint("NotifyDataSetChanged")
    private fun addRouteToList() {
        db = FirebaseFirestore.getInstance()
        db.collection("users").addSnapshotListener { value, _ ->
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    userArrayList.add(dc.document.toObject(User::class.java))
                    userArrayList.sortByDescending { it.distance }
                    if (userArrayList.size > 5) {
                        userArrayList.removeAt(5)
                    }
                }
            }
            userAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Convertir el mes de numero a text
    private fun changeMonth(date: String): String {
        lateinit var month: String
        when (date) {
            "1" -> {
                month = getString(R.string.january)
            }
            "2" -> {
                month = getString(R.string.february)
            }
            "3" -> {
                month = getString(R.string.march)
            }
            "4" -> {
                month = getString(R.string.april)
            }
            "5" -> {
                month = getString(R.string.may)
            }
            "6" -> {
                month = getString(R.string.june)
            }
            "7" -> {
                month = getString(R.string.july)
            }
            "8" -> {
                month = getString(R.string.august)
            }
            "9" -> {
                month = getString(R.string.september)
            }
            "10" -> {
                month = getString(R.string.october)
            }
            "11" -> {
                month = getString(R.string.november)
            }
            "12" -> {
                month = getString(R.string.december)
            }
            "01" -> {
                month = getString(R.string.january)
            }
            "02" -> {
                month = getString(R.string.february)
            }
            "03" -> {
                month = getString(R.string.march)
            }
            "04" -> {
                month = getString(R.string.april)
            }
            "05" -> {
                month = getString(R.string.may)
            }
            "06" -> {
                month = getString(R.string.june)
            }
            "07" -> {
                month = getString(R.string.july)
            }
            "08" -> {
                month = getString(R.string.august)
            }
            "09" -> {
                month = getString(R.string.september)
            }
        }
        return month
    }
}