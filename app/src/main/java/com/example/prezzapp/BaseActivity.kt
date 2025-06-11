package com.example.prezzapp

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prezzapp.database.SftpConnection

open class BaseActivity : AppCompatActivity() {

    fun logout() {
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        sharedPref.edit().clear().apply()
        Toast.makeText(this, "Déconnecté", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        SftpConnection.getInstance().uploadFileviaSftp(
            this.getDatabasePath("prezzapp_database.db").absolutePath,
            "/data/prezzapp_database.db" // Nom du fichier sur le serveur SFTP
        ) { success, message ->
            if (success) {
                //blabla
            } else {
                //blabla
            }
        }
    }
}

