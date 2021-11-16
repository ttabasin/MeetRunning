package cat.copernic.meetrunning.ranking

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment :
    DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val c = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)

        val selectedDate = SimpleDateFormat("MM", Locale.ENGLISH).format(c.time)
        val selectedDateBundle = Bundle()

        selectedDateBundle.putString("SELECTED_DATE", selectedDate)
        setFragmentResult("REQUEST_KEY", selectedDateBundle)

    }

}