package com.truckmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloTM1DApp()
        }
    }
}

@Composable
fun HelloTM1DApp() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Text(text = "Hello TM1D 🚛")
    }
}
