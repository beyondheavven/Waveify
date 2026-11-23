package com.beyondheavven.waveify.server.util

import com.google.gson.JsonParser

internal fun upstreamError(body: String): String {
    val detail = runCatching {
        val json = JsonParser.parseString(body).asJsonObject
        listOf("error_description", "error", "message", "error_message")
            .mapNotNull { key -> json.get(key)?.asString?.takeIf { it.isNotBlank() } }
            .firstOrNull()
    }.getOrNull()
    return detail ?: body.take(300).takeIf { it.isNotBlank() } ?: "no details"
}
