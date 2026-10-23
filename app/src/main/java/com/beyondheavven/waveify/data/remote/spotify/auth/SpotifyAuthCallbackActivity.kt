package com.beyondheavven.waveify.data.remote.spotify.auth

import android.app.Activity
import android.os.Bundle

class SpotifyAuthCallbackActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val code = intent?.data?.getQueryParameter("code")
        if (code != null) {
            SpotifyAuthManager.exchangeCodeForToken(code) {
                finish()
            }
        } else {
            finish()
        }
    }

}