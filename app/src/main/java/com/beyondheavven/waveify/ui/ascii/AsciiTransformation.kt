package com.beyondheavven.waveify.ui.ascii

import android.graphics.Bitmap
import coil.size.Size
import coil.transform.Transformation

class AsciiTransformation(
    val columns: Int = 60,
    val colorMode: AsciiColorMode = AsciiColorMode.COLORED
) : Transformation {

    override val cacheKey: String = "ascii_${columns}_${colorMode.name}"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return input
    }
}
