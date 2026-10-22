package com.beyondheavven.waveify.ui.screen.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.beyondheavven.waveify.ui.theme.DarkSurfaceElevated
import com.beyondheavven.waveify.ui.theme.DividerColor
import com.beyondheavven.waveify.ui.theme.StatusErrorBg
import com.beyondheavven.waveify.ui.theme.StatusSuccess
import com.beyondheavven.waveify.ui.theme.StatusSuccessBg
import com.beyondheavven.waveify.ui.theme.TextPrimary
import com.beyondheavven.waveify.ui.theme.WaveifyRed


private data class BannerStyle(
    val backgroundColor: Color,
    val icon: ImageVector,
    val iconColor: Color,
    val borderColor: Color
)
@Composable
fun NotificationBanner(
    controller: NotificationController,
    modifier: Modifier = Modifier
) {
    val notification = controller.currentNotification

    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        if (notification != null) {
            val style = when (notification.type) {
                NotificationState.SUCCESS -> BannerStyle(
                    backgroundColor = StatusSuccessBg,
                    icon = Icons.Default.CheckCircle,
                    iconColor = StatusSuccess,
                    borderColor = StatusSuccess.copy(alpha = 0.35f)
                )

                NotificationState.ERROR -> BannerStyle(
                    backgroundColor = StatusErrorBg,
                    icon = Icons.Default.Error,
                    iconColor = WaveifyRed,
                    borderColor = DividerColor
                )

                NotificationState.INFO -> BannerStyle(
                    backgroundColor = DarkSurfaceElevated,
                    icon = Icons.Default.Notifications,
                    iconColor = WaveifyRed,
                    borderColor = WaveifyRed.copy(0.35f)
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = style.backgroundColor,
                border = BorderStroke(1.dp, style.borderColor),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = style.icon,
                        contentDescription = null,
                        tint = style.iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}