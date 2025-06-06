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
import com.example.login.ui.theme.LoginTheme
import com.example.prezzapp.database.SftpConnection
import com.example.prezzapp.model.AppDatabase
import com.example.prezzapp.model.Cours
import com.example.prezzapp.model.Status
import com.example.prezzapp.model.User

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        val sftpConnection = SftpConnection.getInstance()
        sftpConnection.testSSHConnection { success, message ->
            if (success) {
                Log.d("SSH", "Connection successful: $message")
            } else {
                Log.e("SSH", "Connection failed: $message")
            }
        }

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()
        val coursDao = db.coursDao()

        Thread {
            coursDao.deleteAll()
            userDao.deleteAll()

            val cours = Cours(
                id = 0,
                prof = "Joel Dion",
                nomcours = "Mathématiques",
                jour = "15/06",
                heure = "10h",
                groupe = "CM",
                annee = "1A",
                spe = "IR"
            )
            coursDao.insert(cours)

            val user = User(
                id = 0,
                name = "Alice",
                login = "alice.bertrand@uha.fr",
                password = "password123",
                td = "td1",
                tp = "tp1",
                annee = "1A",
                spe = "IR",
                status = Status.STUDENT
            )
            userDao.insert(user)

            val allCours = coursDao.getAll()
            val allUsers = userDao.getAll()

            allCours.forEach {
                Log.d("DB", "Cours: ${it.nomcours}, Prof: ${it.prof}")
            }

            allUsers.forEach {
                Log.d("DB", "User: ${it.name}, Statut: ${it.status}")
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
    LoginTheme {
        Greeting("Android")
    }
}
