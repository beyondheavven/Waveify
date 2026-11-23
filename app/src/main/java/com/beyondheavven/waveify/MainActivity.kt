package com.beyondheavven.waveify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.beyondheavven.waveify.data.remote.soundcloud.auth.SoundCloudAuthManager
import com.beyondheavven.waveify.player.spotify.SpotifyPlayerManager
import com.beyondheavven.waveify.player.visualizer.AudioPermissionManager
import com.beyondheavven.waveify.ui.cover.AsciiSettings
import com.beyondheavven.waveify.ui.navigation.NavigationBar
import com.beyondheavven.waveify.ui.navigation.NavigationItem
import com.beyondheavven.waveify.ui.player.MiniPlayer
import com.beyondheavven.waveify.ui.player.PlayerModal
import com.beyondheavven.waveify.ui.screen.home.HomeScreen
import com.beyondheavven.waveify.ui.screen.library.LibraryScreen
import com.beyondheavven.waveify.ui.screen.profile.ProfileScreen
import com.beyondheavven.waveify.ui.screen.search.SearchScreen
import com.beyondheavven.waveify.ui.screen.settings.SettingsScreen
import com.beyondheavven.waveify.ui.theme.AppearanceSettings
import com.beyondheavven.waveify.ui.theme.WaveifyTheme

class MainActivity : ComponentActivity() {

    private val requestRecordAudioPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        AudioPermissionManager.onPermissionResult(granted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AudioPermissionManager.register(requestRecordAudioPermission)
        enableEdgeToEdge()
        AsciiSettings.init(this)
        AppearanceSettings.init(this)
        SoundCloudAuthManager.init(this)
        setContent {
            val themeMode = AppearanceSettings.themeMode
            WaveifyTheme(themeMode = themeMode) {
                var currentTab by rememberSaveable { mutableStateOf(NavigationItem.HOME) }
                var isFullPlayerVisible by rememberSaveable { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        Column {
                            MiniPlayer(onClick = { isFullPlayerVisible = true })
                            NavigationBar(
                                selectedItem = currentTab,
                                onItemSelected = { currentTab = it }
                            )
                        }
                    }
                ) { innerPadding ->
                    when (currentTab) {
                        NavigationItem.HOME -> HomeScreen(
                            padding = innerPadding,
                            onNavigateToLibrary = { currentTab = NavigationItem.LIBRARY },
                            onNavigateToProfile = { currentTab = NavigationItem.PROFILE }
                        )

                        NavigationItem.SEARCH -> SearchScreen(
                            padding = innerPadding,
                            onNavigateToProfile = { currentTab = NavigationItem.PROFILE }
                        )

                        NavigationItem.LIBRARY -> LibraryScreen(
                            padding = innerPadding,
                            onNavigationToProfile = { currentTab = NavigationItem.PROFILE }
                        )

                        NavigationItem.PROFILE -> ProfileScreen(
                            padding = innerPadding,
                            onNavigateToSettings = { currentTab = NavigationItem.SETTINGS }
                        )

                        NavigationItem.SETTINGS -> SettingsScreen(
                            padding = innerPadding,
                            onNavigateToProfile = { currentTab = NavigationItem.PROFILE }
                        )
                    }
                }

                if (isFullPlayerVisible) {
                    PlayerModal(onDismiss = { isFullPlayerVisible = false })
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        SpotifyPlayerManager.disconnect()
    }
}
