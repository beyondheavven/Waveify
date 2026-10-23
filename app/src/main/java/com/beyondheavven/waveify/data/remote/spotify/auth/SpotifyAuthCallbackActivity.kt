package com.beyondheavven.waveify.data.remote.spotify.auth

import android.app.Activity
import android.os.Bundle

class SpotifyAuthCallbackActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent?.data
        val code = intent?.data?.getQueryParameter("code")
        val error = uri?.getQueryParameter("error")

        when {
            code != null -> {
                SpotifyAuthManager.exchangeCodeForToken(code) {
                    finish()
                }
            }
            error != null -> {
                SpotifyAuthManager.notifyError(error)
                finish()
            }
            else -> finish()

        }

    }

}