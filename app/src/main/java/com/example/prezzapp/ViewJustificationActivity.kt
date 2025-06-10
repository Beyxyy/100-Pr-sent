// src/main/java/com/example/prezzapp/ViewJustificationActivity.kt
package com.example.prezzapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ActivityViewJustificationBinding

class ViewJustificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewJustificationBinding
    private var absence: Absence? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewJustificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Use the correct way to get SerializableExtra for targetSdkVersion 33+
        absence = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("selected_absence", Absence::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("selected_absence") as? Absence
        }

        if (absence == null) {
            Toast.makeText(this, "Absence non trouvée.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar() // Assurez-vous d'avoir une toolbar et d'appeler cette méthode
        displayJustification()
    }

    private fun setupToolbar() {
        // Supposons que vous avez une toolbar définie dans votre layout activity_view_justification.xml
        // et que son ID est 'toolbar'
        setSupportActionBar(binding.toolbar) // Assurez-vous que 'binding.toolbar' existe
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Affiche la flèche de retour
        supportActionBar?.setDisplayShowTitleEnabled(false) // Cache le titre par défaut de la toolbar
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Gère le clic sur la flèche de retour
        }
        binding.toolbarTitle.text = "Justificatif" // Optionnel: définir le titre de la toolbar
    }


    private fun displayJustification() {
        binding.tvCourseDate.text = "${absence?.courseName} - ${absence?.date}"
        binding.tvProfessor.text = absence?.professorName ?: ""

        val justificationLink = absence?.justificationLink

        if (!justificationLink.isNullOrBlank()) {
            binding.tvJustificationLink.text = "Justificatif disponible" // Ou le lien lui-même si vous voulez l'afficher
            binding.btnOpenJustification.isEnabled = true
            binding.btnOpenJustification.setOnClickListener {
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(justificationLink))
                    startActivity(browserIntent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Impossible d'ouvrir le lien du justificatif.", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        } else {
            binding.tvJustificationLink.text = "Le justificatif n'est pas disponible."
            binding.btnOpenJustification.isEnabled = false
        }
    }
}