package com.example.prezzapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.prezzapp.ui.theme.PrezzAppTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.prezzapp.components.HomeContainer
import com.example.prezzapp.components.Header
import com.example.prezzapp.service.AdminService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val adminService = AdminService(this)
        lifecycleScope.launch {
            adminService.initData() // Initialise la base de données en arrière-plan
            setContent {
                PrezzAppTheme {
                    MyApp(modifier = Modifier.fillMaxSize(), this@AdminActivity)
                }
            }
        }
    }
}

@Composable
fun MyApp(modifier: Modifier = Modifier, activity: ComponentActivity) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Navigation(activity = activity)

    }
}
