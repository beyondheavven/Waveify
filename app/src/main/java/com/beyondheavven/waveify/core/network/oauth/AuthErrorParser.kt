package com.beyondheavven.waveify.core.network.oauth

import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException

internal object AuthErrorParser {

    fun message(throwable: Throwable): String {
        if (throwable is HttpException) {
            val body = runCatching { throwable.response()?.errorBody()?.string().orEmpty() }
                .getOrDefault("")
            val detail = parseDetail(body) ?: body.take(300).takeIf { it.isNotBlank() }
            return "HTTP ${throwable.code()}" + (detail?.let { ": $it" } ?: "")
        }
        if (throwable is IOException) {
            return throwable.message ?: "Network error"
        }
        return throwable.message ?: "Authentication error"
    }

    private fun parseDetail(body: String): String? = runCatching {
        val json = JsonParser.parseString(body).asJsonObject
        listOf("error_description", "error", "message", "error_message")
            .mapNotNull { key -> json.get(key)?.asString?.takeIf { it.isNotBlank() } }
            .firstOrNull()
    }.getOrNull()
}
