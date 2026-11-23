package com.beyondheavven.waveify.server.model

import com.google.gson.annotations.SerializedName

data class TokenRequest(
    @SerializedName("code") val code: String? = null,
    @SerializedName("codeVerifier") val codeVerifier: String? = null,
    @SerializedName("redirectUri") val redirectUri: String? = null
)

data class RefreshRequest(
    @SerializedName("refreshToken") val refreshToken: String? = null
)

data class DisconnectRequest(
    @SerializedName("accessToken") val accessToken: String? = null
)

data class ErrorResponse(
    @SerializedName("error") val error: String
)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("expires_in") val expiresIn: Long? = null,
    @SerializedName("scope") val scope: String? = null,
    @SerializedName("token_type") val tokenType: String? = null
)

data class MeResponse(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("urn") val urn: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("permalink_url") val permalinkUrl: String? = null
)

data class ProfileResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("urn") val urn: String?,
    @SerializedName("username") val username: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("permalink_url") val permalinkUrl: String?
)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("expires_in") val expiresIn: Long?,
    @SerializedName("scope") val scope: String?,
    @SerializedName("token_type") val tokenType: String?,
    @SerializedName("user") val user: ProfileResponse?
)
