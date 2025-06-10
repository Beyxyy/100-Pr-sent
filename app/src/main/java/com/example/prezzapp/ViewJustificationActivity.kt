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

        setupToolbar()
        displayJustification()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbarTitle.text = "Justificatif"
    }


    private fun displayJustification() {
        binding.tvCourseDate.text = "${absence?.courseName} - ${absence?.date}"
        binding.tvProfessor.text = absence?.professorName ?: ""

        val justificationLink = absence?.justificationLink

        if (!justificationLink.isNullOrBlank()) {
            binding.tvJustificationLink.text = "Justificatif disponible"
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