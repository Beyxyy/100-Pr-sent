package com.example.prezzapp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ActivityViewJustificationBinding
import com.example.prezzapp.database.SftpConnection
import com.example.prezzapp.model.AppDatabase
import java.io.File

class ViewJustificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewJustificationBinding
    private var absence: Absence? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewJustificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Récupération de l'absence depuis l'intent
        absence = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

        // Exemple : bouton d'upload fictif (à adapter selon ta logique d’ajout fichier)
        binding.btnUploadJustificatif.setOnClickListener {
            // Exemple : fichier local et chemin distant à adapter selon ton cas
            val localFile = File(filesDir, "justif_${absence!!.id}.pdf")
            val remotePath = "/home/groupe1/justificatifs/justif_${absence!!.id}.pdf"

            if (!localFile.exists()) {
                Toast.makeText(this, "Fichier justificatif introuvable localement.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadJustificatifAndUpdateDatabase(localFile, remotePath, absence!!.id.toInt())
        }
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

        val remotePath = absence?.justificationLink

        if (!remotePath.isNullOrBlank()) {
            val fileName = "justif_${absence?.id}.pdf"
            binding.tvJustificationLink.text = "Justificatif : $fileName"
            binding.btnOpenJustification.isEnabled = true

            binding.btnOpenJustification.setOnClickListener {
                downloadAndOpenJustificatif(remotePath, fileName)
            }
        } else {
            binding.tvJustificationLink.text = "Le justificatif n'est pas disponible."
            binding.btnOpenJustification.isEnabled = false
        }
    }

    private fun downloadAndOpenJustificatif(remotePath: String, localFileName: String) {
        SftpConnection.downloadFileViaSFTP(
            context = this,
            localFileName = localFileName,
            remoteFilePath = remotePath
        ) { success, message ->
            runOnUiThread {
                if (success) {
                    val localFile = File(filesDir, localFileName)
                    try {
                        val uri = FileProvider.getUriForFile(
                            this,
                            "${packageName}.provider",
                            localFile
                        )
                        val openIntent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        startActivity(openIntent)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Erreur à l'ouverture du fichier.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Téléchargement échoué : $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadJustificatifAndUpdateDatabase(localFile: File, remotePath: String, absenceId: Int) {
        SftpConnection.uploadFileViaSFTP(localFile.absolutePath, remotePath) { success, message ->
            runOnUiThread {
                if (success) {
                    Thread {
                        val db = AppDatabase.getDatabase(this)
                        val presence = db.presenceDao().getAbsenceById(absenceId)
                        if (presence != null) {
                            val updatedPresence = presence.copy(
                                estJustifie = true,
                                lien = remotePath
                            )
                            db.presenceDao().update(updatedPresence)

                            runOnUiThread {
                                Toast.makeText(
                                    this,
                                    "Justificatif envoyé et absence justifiée !",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this,
                                    "Absence introuvable en base.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }.start()
                } else {
                    Toast.makeText(this, "Erreur lors de l'envoi : $message", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
