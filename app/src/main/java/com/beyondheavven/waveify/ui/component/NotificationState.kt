package com.beyondheavven.waveify.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

class NotificationController(
    private val scope: CoroutineScope
) {
    var currentNotification by mutableStateOf<NotificationData?>(null)
        private set

    fun show(message: String, type: NotificationState) {
        scope.launch {
            currentNotification = NotificationData(message, type)
            delay(3000.milliseconds)
            currentNotification = null
        }
    }

    fun dismiss(){
        currentNotification = null
    }

}

@Composable
fun rememberNotificationController(): NotificationController {
    val scope = rememberCoroutineScope()
    return remember { NotificationController(scope) }
}