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
import com.beyondheavven.waveify.ui.screen.home.HomeScreen
import com.beyondheavven.waveify.ui.screen.library.LibraryScreen
import com.beyondheavven.waveify.ui.component.NavigationBar
import com.beyondheavven.waveify.ui.component.NavigationItem
import com.beyondheavven.waveify.ui.screen.profile.ProfileScreen
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
                    when (currentTab) {
                        NavigationItem.HOME -> HomeScreen(innerPadding)
                        NavigationItem.PROFILE -> ProfileScreen(innerPadding)
                        NavigationItem.LIBRARY -> LibraryScreen(innerPadding)
                        else -> HomeScreen(innerPadding)
                    }
                }
            }
        }
    }
}