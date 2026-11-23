package com.beyondheavven.waveify.data.spotify.auth

import android.app.Activity
import android.os.Bundle

class SpotifyAuthCallbackActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.data
        SpotifyAuthManager.handleCallback(
            context = this,
            code = uri?.getQueryParameter("code"),
            state = uri?.getQueryParameter("state"),
            error = uri?.getQueryParameter("error"),
            errorDescription = uri?.getQueryParameter("error_description"),
            onComplete = { finish() }
        )
    }
}
