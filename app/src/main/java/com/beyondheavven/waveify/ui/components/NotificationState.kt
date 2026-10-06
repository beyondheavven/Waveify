package com.beyondheavven.waveify.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import java.lang.reflect.Array.set
import kotlin.time.Duration.Companion.milliseconds


enum class NotificationState {
    SUCCESS,
    ERROR,
    INFO
}

data class NotificationData(
    val message: String,
    val type: NotificationState = NotificationState.INFO
)

class NotificationController() {
    var currentNotification by mutableStateOf<NotificationData?>(null)
        private set

    suspend fun show(message: String, type: NotificationState) {
        currentNotification = NotificationData(message, type)
        delay(3000.milliseconds)
        currentNotification = null
    }

    fun dismiss(){
        currentNotification = null
    }

}

@Composable
fun rememberNotificationController(): NotificationController{
    return remember { NotificationController() }
}