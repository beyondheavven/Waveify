package com.beyondheavven.waveify.player.visualizer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object AudioPermissionManager {

    private var launcher: ActivityResultLauncher<String>? = null
    private var pendingCallback: ((Boolean) -> Unit)? = null

    fun register(launcher: ActivityResultLauncher<String>) {
        this.launcher = launcher
    }

    fun isGranted(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

    fun request(context: Context, onResult: (Boolean) -> Unit) {
        if (isGranted(context)) {
            onResult(true)
            return
        }

        pendingCallback = onResult
        launcher?.launch(Manifest.permission.RECORD_AUDIO) ?: onResult(false)
    }

    fun onPermissionResult(granted: Boolean) {
        pendingCallback?.invoke(granted)
        pendingCallback = null
    }
}
