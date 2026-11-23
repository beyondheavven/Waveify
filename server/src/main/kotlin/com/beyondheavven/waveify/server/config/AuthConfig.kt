package com.beyondheavven.waveify.server.config

private const val DEFAULT_REDIRECT_URI = "waveify://soundcloud/callback"
private const val DEFAULT_PORT = 8080

data class AuthConfig(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val port: Int
) {
    companion object {
        fun fromEnv(env: (String) -> String? = { System.getenv(it) }): AuthConfig? {
            val clientId = env("SOUNDCLOUD_CLIENT_ID").orEmpty()
            val clientSecret = env("SOUNDCLOUD_CLIENT_SECRET").orEmpty()
            if (clientId.isBlank() || clientSecret.isBlank()) return null

            return AuthConfig(
                clientId = clientId,
                clientSecret = clientSecret,
                redirectUri = env("SOUNDCLOUD_REDIRECT_URI")?.takeIf { it.isNotBlank() }
                    ?: DEFAULT_REDIRECT_URI,
                port = env("PORT")?.toIntOrNull() ?: DEFAULT_PORT
            )
        }
    }
}
