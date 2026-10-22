package com.beyondheavven.waveify.ui.cover

import android.graphics.Bitmap
import androidx.compose.ui.graphics.toArgb
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.Color
import coil.size.Size
import coil.transform.Transformation
import com.beyondheavven.waveify.ui.theme.DarkBackground
import kotlin.math.roundToInt


class AsciiTransformation(

    private val columns: Int = 60,

    private val colorMode: AsciiColorMode = AsciiColorMode.COLORED,

    private val backgroundColor: Int = DarkBackground.toArgb()
): Transformation {

    override val cacheKey: String = "ascii_art_${columns}_${colorMode.name}_$backgroundColor"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val charAspectRatio = 0.6f
        val rows = (columns * charAspectRatio).roundToInt().coerceAtLeast(1)

        val scaled = Bitmap.createScaledBitmap(input, columns, rows, true)
        val pixels = IntArray(columns * rows)
        scaled.getPixels(pixels, 0, columns, 0,0,columns, rows)
        if (scaled != input) {
            scaled.recycle()
        }

        val outWidth = input.width.coerceIn(400,800)
        val outHeight = input.height.coerceIn(400, 800)
        val output = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawColor(backgroundColor)

        val cellWidth = outWidth.toFloat() / columns
        val cellHeight = outHeight.toFloat() / rows

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textSize = cellHeight * 0.95f
            textAlign = Paint.Align.CENTER
        }

        val fontMetrics = paint.fontMetrics
        val textBaselineOffset = (cellHeight - (fontMetrics.descent + fontMetrics.ascent)) / 2f

        val ramp = " .:-=+*#%@"
        val hsv = FloatArray(3)
        val charBuffer = CharArray(1)

        for (r in 0 until rows) {
            for (c in 0 until columns) {
                val pixel = pixels[r * columns + c]
                val red = (pixel shr 16) and 0xFF
                val green = (pixel shr 8) and 0xFF
                val blue = pixel and 0xFF
                val alpha = (pixel shr 24) and 0xFF

                val luminance = (0.299f * red + 0.587f * green + 0.114f * blue) / 255f
                val charIndex = ((ramp.length - 1) * luminance).toInt().coerceIn(0, ramp.length - 1)
                val ch = ramp[charIndex]

                if (ch == ' ') continue

                paint.color = when (colorMode) {
                    AsciiColorMode.COLORED -> {
                        Color.RGBToHSV(red, green, blue, hsv)
                        hsv[1] = (hsv[1] * 1.15f).coerceIn(0f, 1f)
                        hsv[2] = (hsv[2] * 1.10f).coerceIn(0.2f, 1f)
                        Color.HSVToColor(alpha, hsv)
                    }
                    AsciiColorMode.MONOCHROME -> {
                        val v = (255 * (0.25f + 0.75f * luminance)).toInt().coerceIn(0,255)
                        Color.argb(alpha, v,v,v)
                    }
                    AsciiColorMode.WAVEIFY_RED -> {
                        val v = (255 * (0.3f + 0.7f * luminance)).toInt().coerceIn(0,255)
                        Color.argb(alpha, v,(v * 0.16f).toInt(),(v * 0.33f).toInt())
                    }
                    AsciiColorMode.GREEN_MATRIX -> {
                        val v = (255 * (0.25f + 0.75f * luminance)).toInt().coerceIn(0,255)
                        Color.argb(alpha, (v * 0.15f).toInt(), v,(v*0.25f).toInt())
                    }
                }

                val x = c * cellWidth + cellWidth / 2f
                val y = r * cellHeight + textBaselineOffset
                charBuffer[0] = ch
                canvas.drawText(charBuffer, 0,1, x, y, paint)
            }
        }
        return output
    }


}