package com.beyondheavven.waveify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.beyondheavven.waveify.player.spotify.SpotifyPlayerManager
import com.beyondheavven.waveify.ui.cover.AsciiSettings
import com.beyondheavven.waveify.ui.player.MiniPlayer
import com.beyondheavven.waveify.ui.screen.home.HomeScreen
import com.beyondheavven.waveify.ui.screen.library.LibraryScreen
import com.beyondheavven.waveify.ui.navigation.NavigationBar
import com.beyondheavven.waveify.ui.navigation.NavigationItem
import com.beyondheavven.waveify.ui.player.PlayerModal
import com.beyondheavven.waveify.ui.screen.profile.ProfileScreen
import com.beyondheavven.waveify.ui.theme.WaveifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AsciiSettings.init(this)
        setContent {
            WaveifyTheme {
                var currentTab by remember { mutableStateOf(NavigationItem.HOME) }
                var isFullPlayerVisible by remember { mutableStateOf(false) }
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        Column{
                            MiniPlayer(
                                onClick = { isFullPlayerVisible = true}
                            )
                            NavigationBar(
                                selectedItem = currentTab,
                                onItemSelected = { currentTab = it}
                            )
                        }
                    }
                ) { innerPadding ->
                    when (currentTab) {
                        NavigationItem.HOME -> HomeScreen(
                            innerPadding,
                            onNavigateToLibrary = { currentTab = NavigationItem.LIBRARY }
                            )
                        NavigationItem.PROFILE -> ProfileScreen(innerPadding)
                        NavigationItem.LIBRARY -> LibraryScreen(innerPadding)
                        else -> HomeScreen(innerPadding)
                    }
                }

                if (isFullPlayerVisible) {
                    PlayerModal (
                        onDismiss = {isFullPlayerVisible = false}
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        SpotifyPlayerManager.disconnect()
    }
}