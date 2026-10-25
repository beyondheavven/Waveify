package com.beyondheavven.waveify.player.visualizer

import android.media.audiofx.Visualizer
import android.util.Log

class AudioWaveformTracker {

    private var visualizer: Visualizer? = null

    var onWaveformUpdate: ((ByteArray) -> Unit)? = null

    fun attachTo(audioSessionId: Int) {
        if (audioSessionId <= 0) return
        release()

        try {
            visualizer = Visualizer(audioSessionId).apply {
                captureSize = 128
                setDataCaptureListener(
                    object : Visualizer.OnDataCaptureListener {
                        override fun onWaveFormDataCapture(
                            visualizer: Visualizer?,
                            data: ByteArray?,
                            rate: Int
                        ) {
                            if (data != null) {
                                onWaveformUpdate?.invoke(data)
                            }
                        }

                        override fun onFftDataCapture(
                            visualizer: Visualizer?,
                            fft: ByteArray?,
                            samplingRate: Int
                        ) {}
                    },
                    Visualizer.getMaxCaptureRate() / 2,
                    true,
                    false
                )
                enabled = true
            }
        } catch (e: Exception) {
            Log.e("AudioTracker", "Unable to start Visualizer: ${e.message}")
        }
    }

    fun release() {
        try {
            visualizer?.enabled = false
            visualizer?.release()
        } catch (e: Exception){
            Log.e("AudioTracker", "An error occurred ${e.message}")
        } finally {
            visualizer = null
        }
    }
}