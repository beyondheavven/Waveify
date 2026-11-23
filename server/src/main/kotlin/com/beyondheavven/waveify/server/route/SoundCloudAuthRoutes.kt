package com.beyondheavven.waveify.server.route

import com.beyondheavven.waveify.server.config.AuthConfig
import com.beyondheavven.waveify.server.model.AuthResponse
import com.beyondheavven.waveify.server.model.DisconnectRequest
import com.beyondheavven.waveify.server.model.ErrorResponse
import com.beyondheavven.waveify.server.model.ProfileResponse
import com.beyondheavven.waveify.server.model.RefreshRequest
import com.beyondheavven.waveify.server.model.TokenRequest
import com.beyondheavven.waveify.server.model.TokenResponse
import com.beyondheavven.waveify.server.service.ServiceResult
import com.beyondheavven.waveify.server.service.SoundCloudAuthService
import com.beyondheavven.waveify.server.util.readBody
import com.beyondheavven.waveify.server.util.respondJson
import com.beyondheavven.waveify.server.util.respondNoContent
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer

class SoundCloudAuthRoutes(
    private val config: AuthConfig,
    private val authService: SoundCloudAuthService
) {
    fun install(server: HttpServer) {
        server.createContext("/health") { exchange ->
            respondJson(exchange, 200, mapOf("status" to "ok"))
        }
        server.createContext("/api/auth/soundcloud/token") { exchange ->
            handleToken(exchange)
        }
        server.createContext("/api/auth/soundcloud/refresh") { exchange ->
            handleRefresh(exchange)
        }
        server.createContext("/api/auth/soundcloud/disconnect") { exchange ->
            handleDisconnect(exchange)
        }
    }

    private fun handleToken(exchange: HttpExchange) {
        if (!requirePost(exchange)) return

        val request = readBody<TokenRequest>(exchange)
        if (request?.code.isNullOrBlank() || request.codeVerifier.isNullOrBlank()) {
            respondJson(exchange, 400, ErrorResponse("code and codeVerifier are required"))
            return
        }
        if (request.redirectUri.isNullOrBlank() || request.redirectUri != config.redirectUri) {
            respondJson(exchange, 400, ErrorResponse("Invalid redirect_uri"))
            return
        }

        when (val result = authService.exchangeCode(
            code = request.code.orEmpty(),
            codeVerifier = request.codeVerifier.orEmpty(),
            redirectUri = request.redirectUri.orEmpty()
        )) {
            is ServiceResult.Failure -> respondJson(
                exchange,
                502,
                ErrorResponse("Token exchange failed (${result.status}): ${result.detail}")
            )
            is ServiceResult.Success -> respondWithToken(exchange, result.value)
        }
    }

    private fun handleRefresh(exchange: HttpExchange) {
        if (!requirePost(exchange)) return

        val request = readBody<RefreshRequest>(exchange)
        if (request?.refreshToken.isNullOrBlank()) {
            respondJson(exchange, 400, ErrorResponse("refreshToken is required"))
            return
        }

        when (val result = authService.refresh(request.refreshToken.orEmpty())) {
            is ServiceResult.Failure -> respondJson(
                exchange,
                502,
                ErrorResponse("Token refresh failed (${result.status}): ${result.detail}")
            )
            is ServiceResult.Success -> respondWithToken(exchange, result.value)
        }
    }

    private fun handleDisconnect(exchange: HttpExchange) {
        if (!requirePost(exchange)) return

        val request = readBody<DisconnectRequest>(exchange)
        if (request?.accessToken.isNullOrBlank()) {
            respondJson(exchange, 400, ErrorResponse("accessToken is required"))
            return
        }

        when (val result = authService.disconnect(request.accessToken.orEmpty())) {
            is ServiceResult.Failure -> {
                if (result.status == 404) {
                    respondNoContent(exchange)
                } else {
                    respondJson(
                        exchange,
                        502,
                        ErrorResponse("Disconnect failed (${result.status}): ${result.detail}")
                    )
                }
            }
            is ServiceResult.Success -> respondNoContent(exchange)
        }
    }

    private fun respondWithToken(exchange: HttpExchange, token: TokenResponse) {
        val profile = authService.fetchProfile(token.accessToken.orEmpty())
        respondJson(exchange, 200, buildAuthResponse(token, profile))
    }

    private fun buildAuthResponse(token: TokenResponse, user: ProfileResponse?) = AuthResponse(
        accessToken = token.accessToken.orEmpty(),
        refreshToken = token.refreshToken,
        expiresIn = token.expiresIn,
        scope = token.scope,
        tokenType = token.tokenType,
        user = user
    )

    private fun requirePost(exchange: HttpExchange): Boolean {
        if (exchange.requestMethod == "POST") return true
        respondJson(exchange, 405, ErrorResponse("Method not allowed"))
        return false
    }
}
