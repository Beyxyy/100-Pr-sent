package com.example.prezzapp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.prezzapp.data.Absence
import com.example.prezzapp.databinding.ActivityViewJustificationBinding
import com.example.prezzapp.database.SftpConnection
import com.example.prezzapp.model.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class JustifyAbsenceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewJustificationBinding
    private var absence: Absence? = null

    private val pickPdfLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            handleSelectedFile(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewJustificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Désactivation permanente du bouton
        binding.btnUploadJustificatif.isEnabled = false
        binding.btnUploadJustificatif.alpha = 0.5f // Optionnel pour effet visuel

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

        binding.btnUploadJustificatif.setOnClickListener {
            // Ce code ne sera jamais atteint puisque le bouton est désactivé
            pickPdfLauncher.launch(arrayOf("application/pdf"))
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

        val isJustified = absence?.isJustified == true
        val remotePath = absence?.justificationLink
        val fileName = "justif_${absence?.id}.pdf"
        val localFile = File(filesDir, fileName)

        if (isJustified && !remotePath.isNullOrBlank() && localFile.exists()) {
            binding.tvJustificationLink.text = "Justificatif : $fileName"
            binding.btnOpenJustification.isEnabled = true
            binding.btnOpenJustification.setOnClickListener {
                downloadAndOpenJustificatif(remotePath, fileName)
            }
            binding.tvStatusLabel.text = "Justifié"
            binding.tvStatusLabel.setBackgroundResource(R.drawable.rounded_green_background)
        } else {
            binding.tvJustificationLink.text = "Le justificatif n'est pas disponible."
            binding.btnOpenJustification.isEnabled = false
            binding.tvStatusLabel.text = "Non justifié"
            binding.tvStatusLabel.setBackgroundResource(R.drawable.rounded_red_background)
        }
    }

    private fun handleSelectedFile(uri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val filename = "justif_${absence!!.id}.pdf"
                val destFile = File(filesDir, filename)

                contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(destFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                } ?: throw Exception("Impossible d'ouvrir le fichier choisi.")

                if (!destFile.exists()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@JustifyAbsenceActivity, "Erreur : fichier local non créé", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@JustifyAbsenceActivity, "Fichier local créé avec succès : ${destFile.absolutePath}", Toast.LENGTH_LONG).show()
                }

                val remotePath = "/data/ir/justificatifs/$filename"
                uploadJustificatifAndUpdateDatabase(destFile, remotePath, absence!!.id.toInt())

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@JustifyAbsenceActivity, "Erreur lors de la copie du fichier : ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun uploadJustificatifAndUpdateDatabase(localFile: File, remotePath: String, absenceId: Int) {
        Log.d("Upload", "Upload fichier local : ${localFile.absolutePath}")
        Log.d("Upload", "Upload chemin distant : $remotePath")

        if (!localFile.exists()) {
            runOnUiThread {
                Toast.makeText(this, "Le fichier local à uploader n'existe pas !", Toast.LENGTH_LONG).show()
            }
            return
        }

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
                                Toast.makeText(this, "Justificatif envoyé et absence justifiée !", Toast.LENGTH_LONG).show()
                                finish()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this, "Absence introuvable en base.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }.start()
                } else {
                    Toast.makeText(this, "Erreur lors de l'envoi : $message", Toast.LENGTH_LONG).show()
                }
            }
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
                        val uri = androidx.core.content.FileProvider.getUriForFile(
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
}
