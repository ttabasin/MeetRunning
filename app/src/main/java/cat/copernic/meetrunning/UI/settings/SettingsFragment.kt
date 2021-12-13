package cat.copernic.meetrunning.UI.settings

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import cat.copernic.meetrunning.MainActivity
import cat.copernic.meetrunning.databinding.FragmentSettingsBinding
import java.util.*

class SettingsFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding = FragmentSettingsBinding.inflate(layoutInflater)

        Log.i("settings", "${context!!.resources.configuration.locales}")

        if (context!!.resources.configuration.locales.toString() == "[en_US]") {
            binding.rbEnglish.isChecked = true
        } else if (context!!.resources.configuration.locales.toString() == "[es_ES]") {
            binding.rbSpanish.isChecked = true
        } else if (context!!.resources.configuration.locales.toString() == "[ca_ES]") {
            binding.rbCat.isChecked = true
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->

            if (checkedId == binding.rbEnglish.id) {
                Toast.makeText(context, binding.rbEnglish.text.toString(), Toast.LENGTH_SHORT)
                    .show()

                val locale = Locale("en", "EN")
                val config: Configuration = context!!.resources.configuration
                config.setLocale(locale)

                context!!.resources.updateConfiguration(
                    config,
                    context!!.resources.displayMetrics
                )

            }
            if (checkedId == binding.rbSpanish.id) {
                Toast.makeText(context, binding.rbSpanish.text.toString(), Toast.LENGTH_SHORT).show()
                val locale = Locale("es", "ES")
                val config: Configuration = context!!.resources.configuration
                config.setLocale(locale)

                context!!.resources.updateConfiguration(
                    config,
                    context!!.resources.displayMetrics
                )

            }

            if (checkedId == binding.rbCat.id) {
                Toast.makeText(context, binding.rbCat.text.toString(), Toast.LENGTH_SHORT).show()
                val locale = Locale("ca", "ES")
                val config: Configuration = context!!.resources.configuration
                config.setLocale(locale)

                context!!.resources.updateConfiguration(
                    config,
                    context!!.resources.displayMetrics
                )

            }

            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()

        }

        return binding.root
    }
}