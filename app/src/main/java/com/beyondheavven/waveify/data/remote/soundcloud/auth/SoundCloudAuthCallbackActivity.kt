package com.beyondheavven.waveify.data.remote.soundcloud.auth

import android.app.Activity
import android.os.Bundle

class SoundCloudAuthCallbackActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.data
        SoundCloudAuthManager.handleCallback(
            context = this,
            code = uri?.getQueryParameter("code"),
            state = uri?.getQueryParameter("state"),
            error = uri?.getQueryParameter("error"),
            errorDescription = uri?.getQueryParameter("error_description"),
            onComplete = { finish() }
        )
    }
}
