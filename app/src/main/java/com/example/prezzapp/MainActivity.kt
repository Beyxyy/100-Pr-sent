package com.example.prezzapp

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.example.prezzapp.database.SftpConnection
import com.example.prezzapp.model.AppDatabase
import com.example.prezzapp.model.Cours
import com.example.prezzapp.model.Status
import com.example.prezzapp.model.User
import com.example.prezzapp.ui.theme.PrezzAppTheme
import java.io.File
import kotlin.compareTo


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrezzAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        Thread {
            val sftpConnection: SftpConnection = SftpConnection.getInstance()
            sftpConnection.testSSHConnection { success, message ->
                if (success) {
                    Log.d("SSH", "Connection successful: $message")
                    sftpConnection.importDatabaseFromServer(this)
                    AppDatabase.getDatabase(this)
                } else {
                    Log.e("SSH", "Connection failed: $message")
                }
            }

        }.start()

/*

        val userDao = db.userDao()x
        val presenceDao = db.presenceDao()
        val coursDao = db.coursDao()

        Thread {
            val cours = Cours(
                id = 0,
                prof = "1",
                jour = "15/06",
                heure = "10h-12h",
                groupe = "CM",
                annee = "1A",
                spe = "IR",
            )
            coursDao.deleteAll()
            coursDao.insert(cours)

            val COURS = coursDao.getAll()
            for (c in COURS) {
                Log.d("DB", "ID: ${c.id}, spe: ${c.spe}")
            }
            userDao.deleteAll()
            val user = User(
                id = 0, name = "Alice", password = "password123",
                td = "td1",
                tp = "tp1",
                annee = "1A",
                spe = "IR",
                status = Status.STUDENT,
                login = "alice.bertrand@uha.fr"
            )
            userDao.insert(user)
            // Demander la permission d’écriture si Android 10 ou moins
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
            db.close()
            // Export automatique de la base de données au démarrage de l'app
            val exportSuccess = db.exportDatabase(this)

            if (exportSuccess) {
                Log.d("ExportDB", "Base de données exportée avec succès.")
            } else {
                Log.e("ExportDB", "Échec de l'export de la base de données.")
            }

        }.start()
*/
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrezzAppTheme {
        Greeting("Android")
    }
}

