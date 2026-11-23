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
import com.beyondheavven.waveify.data.soundcloud.auth.SoundCloudAuthManager
import com.beyondheavven.waveify.feature.player.spotify.SpotifyPlayerManager
import com.beyondheavven.waveify.feature.player.visualizer.AudioPermissionManager
import com.beyondheavven.waveify.core.storage.AsciiSettings
import com.beyondheavven.waveify.navigation.NavigationBar
import com.beyondheavven.waveify.navigation.NavigationItem
import com.beyondheavven.waveify.feature.player.MiniPlayer
import com.beyondheavven.waveify.feature.player.PlayerModal
import com.beyondheavven.waveify.feature.home.HomeScreen
import com.beyondheavven.waveify.feature.library.LibraryScreen
import com.beyondheavven.waveify.feature.profile.ProfileScreen
import com.beyondheavven.waveify.feature.search.SearchScreen
import com.beyondheavven.waveify.feature.settings.SettingsScreen
import com.beyondheavven.waveify.theme.AppearanceSettings
import com.beyondheavven.waveify.theme.WaveifyTheme

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
