package com.example.prezzapp

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
import androidx.room.Room
import com.example.prezzapp.model.AppDatabase
import com.example.prezzapp.model.Cours
import com.example.prezzapp.model.CoursDao
import com.example.prezzapp.model.PresenceDao
import com.example.prezzapp.model.Status
import com.example.prezzapp.model.User
import com.example.prezzapp.ui.theme.PrezzAppTheme
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import java.io.File
import java.io.FileOutputStream
import java.util.Properties

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
        testSSHConnection("10.74.252.198", 22, "groupe1", "Euler314")
        downloadFileViaSFTP(
            host = "10.74.252.198",
            port = 22,
            username = "groupe1",
            password = "Euler314",
            remoteFilePath = "data/user/users.json",
            localFileName = "rapport.json"
        ) { success, message ->
            if (success) {
                Log.d("DL", message)
            } else {
                Log.e("DL", message)
            }
        }

        listRemoteFilesViaSFTP(
            host = "10.74.252.198",
            port = 22,
            username = "groupe1",
            password = "Euler314",
            remoteDirectory = "/"
        ) { fichiers ->
            if (fichiers.isNotEmpty()) {
                for (nom in fichiers) {
                    Log.d("FICHIER", nom)
                }
            } else {
                Log.d("FICHIER", "Aucun fichier trouvé.")
            }
        }
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "prezzapp.db"
        ).build()
        val userDao = db.userDao()
        val presenceDao = db.presenceDao()
        val coursDao = db.coursDao()

        Thread {
            val cours = Cours(
                id = 0,
                prof = "1",
                jour = "15/06",
                heure = "10h",
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

            val users = userDao.getAll()
            for (u in users) {
                Log.d("DB", "Nom: ${u.name}, Statut: ${u.status}")
            }
        }.start()


    }
    fun testSSHConnection(host: String, port: Int, username: String, password: String) {
        Thread {
            try {
                val jsch = JSch()
                val session = jsch.getSession(username, host, port)
                session.setPassword(password)

                val config = Properties()
                config["StrictHostKeyChecking"] = "no" // Permet de ne pas vérifier la clé du host
                session.setConfig(config)

                session.connect(5000) // délai en ms



                Log.d("SSH", "Connexion réussie !")
                session.disconnect()
            } catch (e: Exception) {
                Log.e("SSH", "Erreur de connexion : ${e.message}")
            }
        }.start()
    }

    fun downloadFileViaSFTP(
        host: String,
        port: Int,
        username: String,
        password: String,
        remoteFilePath: String,
        localFileName: String,
        onResult: (Boolean, String) -> Unit
    ) {
        Thread {
            try {
                val jsch = JSch()
                val session = jsch.getSession(username, host, port)
                session.setPassword(password)

                val config = Properties()
                config["StrictHostKeyChecking"] = "no"
                session.setConfig(config)
                session.connect(5000)

                val channel = session.openChannel("sftp") as ChannelSftp
                channel.connect()

                val localFile = File(filesDir, localFileName)
                channel.get(remoteFilePath, FileOutputStream(localFile))

                channel.disconnect()
                session.disconnect()

                runOnUiThread {
                    onResult(true, "Fichier téléchargé dans : ${localFile.absolutePath}")
                }

            } catch (e: Exception) {
                runOnUiThread {
                    onResult(false, "Erreur téléchargement : ${e.message}")
                }
            }
        }.start()
    }

    fun listRemoteFilesViaSFTP(
        host: String,
        port: Int,
        username: String,
        password: String,
        remoteDirectory: String,
        onResult: (List<String>) -> Unit
    ) {
        Thread {
            try {
                val jsch = JSch()
                val session = jsch.getSession(username, host, port)
                session.setPassword(password)

                val config = Properties()
                config["StrictHostKeyChecking"] = "no"
                session.setConfig(config)
                session.connect(5000)

                val channel = session.openChannel("sftp") as ChannelSftp
                channel.connect()

                val fileList = mutableListOf<String>()

                val vector = channel.ls(remoteDirectory)
                for (entry in vector) {
                    if (entry is ChannelSftp.LsEntry) {
                        val name = entry.filename
                        // Ignore les dossiers "." et ".."
                        if (name != "." && name != "..") {
                            fileList.add(name)
                        }
                    }
                }

                channel.disconnect()
                session.disconnect()

                // Envoie la liste dans le thread principal
                runOnUiThread {
                    onResult(fileList)
                }

            } catch (e: Exception) {
                Log.e("SFTP", "Erreur lors du listing : ${e.message}")
                runOnUiThread {
                    onResult(emptyList())
                }
            }
        }.start()
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

