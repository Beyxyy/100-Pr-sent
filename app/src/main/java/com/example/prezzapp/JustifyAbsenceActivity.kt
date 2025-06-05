// src/main/java/com/example/prezzapp/JustifyAbsenceActivity.kt
package com.example.prezzapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ActivityJustifyAbsenceBinding

class JustifyAbsenceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJustifyAbsenceBinding
    private var selectedAbsence: Absence? = null
    private var selectedFileUri: Uri? = null // Pour stocker l'URI du fichier sélectionné

    // Lanceur d'activité pour sélectionner un fichier
    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedFileUri = uri
                Toast.makeText(this, "Fichier sélectionné: ${uri.lastPathSegment}", Toast.LENGTH_LONG).show()
                // Ici, vous pourriez activer un bouton "Envoyer" ou changer l'UI pour montrer que le fichier est prêt.
                uploadJustification() // Appel immédiat pour l'exemple
            }
        } else {
            Toast.makeText(this, "Sélection de fichier annulée", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJustifyAbsenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Récupérer l'objet Absence passé par l'Intent
        selectedAbsence = intent.getSerializableExtra("selected_absence") as? Absence

        setupToolbar()
        displayAbsenceDetails()
        setupFileUpload()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun displayAbsenceDetails() {
        selectedAbsence?.let { absence ->
            binding.tvDetailCourseDate.text = "${absence.courseName} - ${absence.date}"
            binding.tvDetailProfessor.text = "Professeur: ${absence.professorName}"
            // Le statut reste "Non justifié" car on est sur cette page pour justifier
            binding.tvMessageAbsence.text = "Votre absence en ${absence.courseName} le ${absence.date} n'est pas justifiée."
        } ?: run {
            // Gérer le cas où l'absence n'est pas passée (erreur)
            Toast.makeText(this, "Erreur: Absence non trouvée.", Toast.LENGTH_SHORT).show()
            finish() // Ferme l'activité
        }
    }

    private fun setupFileUpload() {
        // Gérer le clic sur le LinearLayout "Sélectionner un fichier"
        binding.cardUploadJustification.setOnClickListener { // Cliquer sur la CardView entière
            openFilePicker()
        }
        binding.btnSelectFile.setOnClickListener { // Cliquer sur le LinearLayout "Sélectionner un fichier"
            openFilePicker()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*" // Permet de sélectionner n'importe quel type de fichier
            // Pour des types spécifiques: "image/*" pour les images, "application/pdf" pour les PDF
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
        selectedFileUri?.let { uri ->
            selectedAbsence?.let { absence ->
                Toast.makeText(this, "Début de l'upload du justificatif pour ${absence.courseName}...", Toast.LENGTH_LONG).show()

                // ICI : Implémentez la LOGIQUE RÉELLE D'UPLOAD DU FICHIER
                // Cela implique généralement :
                // 1. Lire le contenu du fichier depuis l'URI (getContentResolver().openInputStream(uri))
                // 2. Envoyer le fichier à votre API backend (en utilisant Retrofit, OkHttp, etc.)
                // 3. Gérer la réponse du serveur (succès/échec)
                // 4. Mettre à jour le statut de l'absence dans votre base de données après un upload réussi

                // Pour l'exemple, simule un délai et marque l'absence comme justifiée
                binding.cardUploadJustification.postDelayed({
                    absence.isJustified = true // Met à jour l'objet Absence localement
                    Toast.makeText(this, "Justificatif envoyé et absence justifiée !", Toast.LENGTH_LONG).show()
                    // Si l'upload est un succès, vous pourriez vouloir terminer cette activité
                    // et rafraîchir la liste dans StudentDashboardActivity
                    finish() // Retourne à l'activité précédente
                }, 2000) // Simule 2 secondes d'upload

            }
        } ?: run {
            Toast.makeText(this, "Veuillez sélectionner un fichier d'abord.", Toast.LENGTH_SHORT).show()
        }
    }
}