package com.example.prezzapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ActivityJustifyAbsenceBinding
import com.example.prezzapp.model.AppDatabase

class JustifyAbsenceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJustifyAbsenceBinding
    private var selectedAbsence: Absence? = null
    private var selectedFileUri: Uri? = null
    private var userRole: String = "student"

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedFileUri = uri
                Toast.makeText(this, "Fichier sélectionné: ${uri.lastPathSegment}", Toast.LENGTH_LONG).show()
                uploadJustification()
            }
        } else {
            Toast.makeText(this, "Sélection de fichier annulée", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJustifyAbsenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRole = intent.getStringExtra("user_role") ?: "student"

        selectedAbsence = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("selected_absence", Absence::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("selected_absence") as? Absence
        }

        displayAbsenceDetails()

        if (userRole == "student") {
            setupFileUpload()
        } else {
            binding.cardUploadJustification.visibility = View.GONE
            binding.btnSelectFile.visibility = View.GONE
        }
    }

    private fun displayAbsenceDetails() {
        selectedAbsence?.let { absence ->
            binding.tvDetailCourseDate.text = "${absence.courseName} - ${absence.date}"
            binding.tvDetailProfessor.text = absence.professorName

            binding.tvMessageAbsence.text = if (userRole == "teacher") {
                "Cette absence est justifiée.\nVoici les informations fournies par l'élève."
            } else {
                "Votre absence en ${absence.courseName} le ${absence.date} n'est pas justifiée."
            }
        } ?: run {
            Toast.makeText(this, "Erreur : Absence non trouvée.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupFileUpload() {
        binding.cardUploadJustification.setOnClickListener {
            openFilePicker()
        }
        binding.btnSelectFile.setOnClickListener {
            openFilePicker()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        try {
            pickFileLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Impossible d'ouvrir le sélecteur de fichiers.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun uploadJustification() {
        val uri = selectedFileUri
        val absence = selectedAbsence

        if (uri == null) {
            Toast.makeText(this, "Veuillez sélectionner un fichier d'abord.", Toast.LENGTH_SHORT).show()
            return
        }

        if (absence == null) {
            Toast.makeText(this, "Absence non disponible.", Toast.LENGTH_SHORT).show()
            return
        }

        val db = AppDatabase.getDatabase(this)
        val presenceDao = db.presenceDao()

        Thread {
            val presence = presenceDao.getAbsenceById(absence.id.toInt())
            if (presence != null) {
                val updatedPresence = presence.copy(
                    estJustifie = true,
                    lien = uri.toString()
                )
                presenceDao.update(updatedPresence)  // <- ici on utilise update au lieu d'insert

                runOnUiThread {
                    Toast.makeText(this, "Justificatif envoyé et absence justifiée !", Toast.LENGTH_LONG).show()
                    finish()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Absence introuvable en base.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

}
