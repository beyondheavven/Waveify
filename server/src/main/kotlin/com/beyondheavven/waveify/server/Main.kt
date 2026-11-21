package com.beyondheavven.waveify.server

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

private const val TOKEN_URL = "https://secure.soundcloud.com/oauth/token"
private const val ME_URL = "https://api.soundcloud.com/me"
private const val DISCONNECT_URL = "https://api.soundcloud.com/disconnect"
private const val DEFAULT_REDIRECT_URI = "waveify://soundcloud/callback"

private val gson = Gson()
private val httpClient = HttpClient.newHttpClient()

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

internal fun userIdFromUrn(urn: String?): Long? =
    urn?.takeIf { it.isNotBlank() }?.substringAfterLast(':', "")?.toLongOrNull()

fun main() {
    val clientId = System.getenv("SOUNDCLOUD_CLIENT_ID").orEmpty()
    val clientSecret = System.getenv("SOUNDCLOUD_CLIENT_SECRET").orEmpty()
    val redirectUri = System.getenv("SOUNDCLOUD_REDIRECT_URI")?.takeIf { it.isNotBlank() }
        ?: DEFAULT_REDIRECT_URI
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080

    if (clientId.isBlank() || clientSecret.isBlank()) {
        System.err.println("SOUNDCLOUD_CLIENT_ID and SOUNDCLOUD_CLIENT_SECRET are required")
        return
    }

    val server = HttpServer.create(InetSocketAddress(port), 0)

    server.createContext("/health") { exchange ->
        respondJson(exchange, 200, mapOf("status" to "ok"))
    }

    server.createContext("/api/auth/soundcloud/token") { exchange ->
        if (exchange.requestMethod != "POST") {
            respondJson(exchange, 405, ErrorResponse("Method not allowed"))
            return@createContext
        }

        val request = readBody<TokenRequest>(exchange)
        if (request?.code.isNullOrBlank() || request.codeVerifier.isNullOrBlank()) {
            respondJson(exchange, 400, ErrorResponse("code and codeVerifier are required"))
            return@createContext
        }
        if (request.redirectUri.isNullOrBlank() || request.redirectUri != redirectUri) {
            respondJson(exchange, 400, ErrorResponse("Invalid redirect_uri"))
            return@createContext
        }

        val result = postForm(
            TOKEN_URL,
            listOf(
                "grant_type" to "authorization_code",
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "code" to request.code.orEmpty(),
                "code_verifier" to request.codeVerifier.orEmpty(),
                "redirect_uri" to request.redirectUri.orEmpty()
            )
        )

        if (result.status !in 200..299) {
            respondJson(exchange, 502, ErrorResponse("Token exchange failed (${result.status})"))
            return@createContext
        }

        val token = gson.fromJson(result.body, TokenResponse::class.java)
        if (token?.accessToken.isNullOrBlank()) {
            respondJson(exchange, 502, ErrorResponse("Token response is invalid"))
            return@createContext
        }

        respondJson(exchange, 200, buildAuthResponse(token, fetchProfile(token.accessToken.orEmpty())))
    }

    server.createContext("/api/auth/soundcloud/refresh") { exchange ->
        if (exchange.requestMethod != "POST") {
            respondJson(exchange, 405, ErrorResponse("Method not allowed"))
            return@createContext
        }

        val request = readBody<RefreshRequest>(exchange)
        if (request?.refreshToken.isNullOrBlank()) {
            respondJson(exchange, 400, ErrorResponse("refreshToken is required"))
            return@createContext
        }

        val result = postForm(
            TOKEN_URL,
            listOf(
                "grant_type" to "refresh_token",
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "refresh_token" to request.refreshToken.orEmpty()
            )
        )

        if (result.status !in 200..299) {
            respondJson(exchange, 502, ErrorResponse("Token refresh failed (${result.status})"))
            return@createContext
        }

        val token = gson.fromJson(result.body, TokenResponse::class.java)
        if (token?.accessToken.isNullOrBlank()) {
            respondJson(exchange, 502, ErrorResponse("Token response is invalid"))
            return@createContext
        }

        respondJson(exchange, 200, buildAuthResponse(token, fetchProfile(token.accessToken.orEmpty())))
    }

    server.createContext("/api/auth/soundcloud/disconnect") { exchange ->
        if (exchange.requestMethod != "POST") {
            respondJson(exchange, 405, ErrorResponse("Method not allowed"))
            return@createContext
        }

        val request = readBody<DisconnectRequest>(exchange)
        if (request?.accessToken.isNullOrBlank()) {
            respondJson(exchange, 400, ErrorResponse("accessToken is required"))
            return@createContext
        }

        val result = postEmpty(
            DISCONNECT_URL,
            mapOf("Authorization" to "OAuth ${request.accessToken.orEmpty()}")
        )

        if (result.status !in 200..299 && result.status != 404) {
            respondJson(exchange, 502, ErrorResponse("Disconnect failed (${result.status})"))
            return@createContext
        }

        respondNoContent(exchange)
    }

    server.executor = Executors.newFixedThreadPool(4)
    server.start()
    println("Waveify auth server listening on http://localhost:$port")
}

private fun buildAuthResponse(token: TokenResponse, user: ProfileResponse?) = AuthResponse(
    accessToken = token.accessToken.orEmpty(),
    refreshToken = token.refreshToken,
    expiresIn = token.expiresIn,
    scope = token.scope,
    tokenType = token.tokenType,
    user = user
)

private fun fetchProfile(accessToken: String): ProfileResponse? {
    val request = HttpRequest.newBuilder(URI.create(ME_URL))
        .header("Authorization", "OAuth $accessToken")
        .header("Accept", "application/json")
        .GET()
        .build()

    val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
    if (response.statusCode() !in 200..299) return null

    val me = gson.fromJson(response.body(), MeResponse::class.java) ?: return null
    val username = me.username?.takeIf { it.isNotBlank() } ?: return null

    return ProfileResponse(
        id = me.id ?: userIdFromUrn(me.urn),
        urn = me.urn,
        username = username,
        avatarUrl = me.avatarUrl,
        permalinkUrl = me.permalinkUrl
    )
}

private fun postForm(url: String, form: List<Pair<String, String>>): HttpResult {
    val request = HttpRequest.newBuilder(URI.create(url))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("Accept", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(form.formEncode(), StandardCharsets.UTF_8))
        .build()

    val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
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

private fun List<Pair<String, String>>.formEncode(): String = joinToString("&") { (name, value) ->
    "${name.urlEncode()}=${value.urlEncode()}"
}

private fun String.urlEncode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)

private data class HttpResult(val status: Int, val body: String)

private inline fun <reified T> readBody(exchange: HttpExchange): T? = try {
    val body = exchange.requestBody.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
    if (body.isBlank()) null else gson.fromJson(body, T::class.java)
} catch (e: Exception) {
    null
}

private fun respondJson(exchange: HttpExchange, status: Int, payload: Any) {
    respond(exchange, status, gson.toJson(payload))
}

private fun respond(exchange: HttpExchange, status: Int, json: String) {
    val bytes = json.toByteArray(StandardCharsets.UTF_8)
    exchange.responseHeaders.set("Content-Type", "application/json; charset=utf-8")
    exchange.sendResponseHeaders(status, bytes.size.toLong())
    exchange.responseBody.use { it.write(bytes) }
}

private fun respondNoContent(exchange: HttpExchange) {
    exchange.sendResponseHeaders(204, -1)
    exchange.close()
}
