package com.beyondheavven.waveify.ui.cover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.beyondheavven.waveify.ui.theme.DarkSurfaceVariant
import com.beyondheavven.waveify.ui.theme.TextMuted

enum class CoverSize(val columns: Int) {
    SMALL(columns = 28),
    MEDIUM(columns = 36),
    LARGE(columns = 68)
}

@Composable
fun WaveifyCover(
    coverUrl: String?,
    modifier: Modifier = Modifier,
    size: CoverSize = CoverSize.MEDIUM,
    shape: Shape = RoundedCornerShape(8.dp),
    contentDescription: String? = null
){
    val context = LocalContext.current

    if (coverUrl.isNullOrEmpty()) {
        Box(
            modifier = modifier
                .clip(shape)
                .background(DarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = TextMuted
            )
        }

        return
    }

    val imageRequest = remember(coverUrl, AsciiSettings.isEnabled, AsciiSettings.currentMode, shape) {
        ImageRequest.Builder(context)
            .data(coverUrl)
            .apply {
                if (AsciiSettings.isEnabled) {
                    transformations(
                        AsciiTransformation(
                            columns = size.columns,
                            colorMode = AsciiSettings.currentMode
                        )
                    )
                }
            }
            .crossfade(true)
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier.clip(shape),
        contentScale = ContentScale.Crop
    )

}