package com.beyondheavven.waveify.server.service

import com.beyondheavven.waveify.server.config.AuthConfig
import com.beyondheavven.waveify.server.model.MeResponse
import com.beyondheavven.waveify.server.model.ProfileResponse
import com.beyondheavven.waveify.server.model.TokenResponse
import com.beyondheavven.waveify.server.util.formEncode
import com.beyondheavven.waveify.server.util.gson
import com.beyondheavven.waveify.server.util.upstreamError
import com.beyondheavven.waveify.server.util.userIdFromUrn
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

private const val TOKEN_URL = "https://secure.soundcloud.com/oauth/token"
private const val ME_URL = "https://api.soundcloud.com/me"
private const val DISCONNECT_URL = "https://api.soundcloud.com/disconnect"

class SoundCloudAuthService(
    private val config: AuthConfig,
    private val httpClient: HttpClient = HttpClient.newHttpClient()
) {
    fun exchangeCode(code: String, codeVerifier: String, redirectUri: String): ServiceResult<TokenResponse> {
        val result = postForm(
            TOKEN_URL,
            listOf(
                "grant_type" to "authorization_code",
                "client_id" to config.clientId,
                "client_secret" to config.clientSecret,
                "code" to code,
                "code_verifier" to codeVerifier,
                "redirect_uri" to redirectUri
            )
        )
        return parseTokenResult(result)
    }

    fun refresh(refreshToken: String): ServiceResult<TokenResponse> {
        val result = postForm(
            TOKEN_URL,
            listOf(
                "grant_type" to "refresh_token",
                "client_id" to config.clientId,
                "client_secret" to config.clientSecret,
                "refresh_token" to refreshToken
            )
        )
        return parseTokenResult(result)
    }

    fun disconnect(accessToken: String): ServiceResult<Unit> {
        val result = postEmpty(
            DISCONNECT_URL,
            mapOf("Authorization" to "OAuth $accessToken")
        )
        if (result.status !in 200..299) {
            return ServiceResult.Failure(result.status, upstreamError(result.body))
        }
        return ServiceResult.Success(Unit)
    }

    fun fetchProfile(accessToken: String): ProfileResponse? {
        val request = HttpRequest.newBuilder(URI.create(ME_URL))
            .header("Authorization", "OAuth $accessToken")
            .header("Accept", "application/json")
            .GET()
            .build()

        val response = httpClient.send(
            request,
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        )
        if (response.statusCode() !in 200..299) return null

        val me = runCatching { gson.fromJson(response.body(), MeResponse::class.java) }.getOrNull()
            ?: return null
        val username = me.username?.takeIf { it.isNotBlank() } ?: return null

        return ProfileResponse(
            id = me.id ?: userIdFromUrn(me.urn),
            urn = me.urn,
            username = username,
            avatarUrl = me.avatarUrl,
            permalinkUrl = me.permalinkUrl
        )
    }

    private fun parseTokenResult(result: HttpResult): ServiceResult<TokenResponse> {
        if (result.status !in 200..299) {
            return ServiceResult.Failure(result.status, upstreamError(result.body))
        }
        val token = runCatching { gson.fromJson(result.body, TokenResponse::class.java) }.getOrNull()
        if (token?.accessToken.isNullOrBlank()) {
            return ServiceResult.Failure(result.status, "Token response is invalid")
        }
        return ServiceResult.Success(token)
    }

    private fun postForm(url: String, form: List<Pair<String, String>>): HttpResult {
        val request = HttpRequest.newBuilder(URI.create(url))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(form.formEncode(), StandardCharsets.UTF_8))
            .build()

        val response = httpClient.send(
            request,
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        )
        return HttpResult(response.statusCode(), response.body())
    }

    private fun postEmpty(url: String, headers: Map<String, String>): HttpResult {
        val builder = HttpRequest.newBuilder(URI.create(url))
            .header("Accept", "application/json")
        headers.forEach { (name, value) -> builder.header(name, value) }
        val response = httpClient.send(
            builder.POST(HttpRequest.BodyPublishers.noBody()).build(),
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        )
        return HttpResult(response.statusCode(), response.body())
    }

    private data class HttpResult(val status: Int, val body: String)
}
