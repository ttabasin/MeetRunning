package cat.copernic.meetrunning.UI.settings

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil.setContentView
import cat.copernic.meetrunning.R
import cat.copernic.meetrunning.databinding.FragmentSettingsBinding
import java.util.*

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding = FragmentSettingsBinding.inflate(layoutInflater)

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->

            if(checkedId == binding.rbEnglish.id){
                Toast.makeText(context, binding.rbEnglish.text.toString(), Toast.LENGTH_SHORT).show()
                val locale = Locale("en", "EN")
                Locale.setDefault(locale)
                val config: Configuration = context!!.resources.configuration
                config.setLocale(locale)

                context!!.resources.updateConfiguration(
                    config,
                    context!!.resources.displayMetrics
                )

            }
            if(checkedId == binding.rbSpanish.id){
                Toast.makeText(context, binding.rbSpanish.text.toString(), Toast.LENGTH_SHORT).show()
                val locale = Locale("es", "ES")
                Locale.setDefault(locale)
                val config: Configuration = context!!.resources.configuration
                config.setLocale(locale)

                context!!.resources.updateConfiguration(
                    config,
                    context!!.resources.displayMetrics
                )
            }

            if(checkedId == binding.rbCat.id){
                Toast.makeText(context, binding.rbCat.text.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}