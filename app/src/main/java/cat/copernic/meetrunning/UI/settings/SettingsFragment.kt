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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SettingsFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding = FragmentSettingsBinding.inflate(layoutInflater)
        val db = FirebaseFirestore.getInstance()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()

        Log.i("settings", "${context!!.resources.configuration.locales}")

        //Mirar en quin idioma està l'app per posar el botó en qüestió checked
        if (context!!.resources.configuration.locales[0].toString() == "[en_US]" || context!!.resources.configuration.locales.toString() == "[en_EN]") {
            binding.rbEnglish.isChecked = true
        } else if (context!!.resources.configuration.locales[0].toString() == "[es_ES]") {
            binding.rbSpanish.isChecked = true
        } else if (context!!.resources.configuration.locales[0].toString() == "[ca_ES]") {
            binding.rbCat.isChecked = true
        }

        //Funció dels radio buttons
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->

            //Si el que està checked es l'anglès fer el canvi d'idioma
            if (checkedId == binding.rbEnglish.id) {
                Toast.makeText(context, binding.rbEnglish.text.toString(), Toast.LENGTH_SHORT)
                    .show()

                val locale = Locale("en", "EN")
                val config: Configuration = context!!.resources.configuration
                config.setLocale(locale)

                this.resources.updateConfiguration(
                    config,
                    this.resources.displayMetrics
                )

                //Actualitzar l'idioma de l'usuari a la base de dades
                db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
                    db.collection("users").document(currentUserEmail).update(
                        mapOf(
                            "language" to "[en_US]"
                        )
                    )
                }

            }

            //Si el que està checked es l'espanyol fer el canvi d'idioma
            if (checkedId == binding.rbSpanish.id) {
                Toast.makeText(context, binding.rbSpanish.text.toString(), Toast.LENGTH_SHORT).show()

                val locale = Locale("es", "ES")
                val config: Configuration = context!!.resources.configuration
                config.setLocale(locale)

                this.resources.updateConfiguration(
                    config,
                    this.resources.displayMetrics
                )

                //Actualitzar l'idioma de l'usuari a la base de dades
                db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
                    db.collection("users").document(currentUserEmail).update(
                        mapOf(
                            "language" to "[es_ES]"
                        )
                    )
                }
            }

            //Si el que està checked es el català fer el canvi d'idioma
            if (checkedId == binding.rbCat.id) {
                Toast.makeText(context, binding.rbCat.text.toString(), Toast.LENGTH_SHORT).show()

                val locale = Locale("ca", "ES")
                val config: Configuration = context!!.resources.configuration
                config.setLocale(locale)

                this.resources.updateConfiguration(
                    config,
                    this.resources.displayMetrics
                )

                //Actualitzar l'idioma de l'usuari a la base de dades
                db.collection("users").document(currentUserEmail).get().addOnSuccessListener {
                    db.collection("users").document(currentUserEmail).update(
                        mapOf(
                            "language" to "[ca_ES]"
                        )
                    )
                }
            }

            //Reiniciar l'activity per fer el canvi d'idioma efectiu
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
            true

        }

        return binding.root
    }
}