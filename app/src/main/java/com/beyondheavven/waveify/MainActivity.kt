package com.beyondheavven.waveify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.components.HomeScreen
import com.beyondheavven.waveify.components.NavigationBar
import com.beyondheavven.waveify.components.NavigationItem
import com.beyondheavven.waveify.ui.theme.WaveifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaveifyTheme {
                var currentTab by remember { mutableStateOf(NavigationItem.HOME) }
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            selectedItem = currentTab,
                            onItemSelected = { currentTab = it},
                            Modifier.padding(20.dp)
                        )
                    }
                ) { innerPadding ->
                    HomeScreen(innerPadding)
                }
            }
        }
    }
}