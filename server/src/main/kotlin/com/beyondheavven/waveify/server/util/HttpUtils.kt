package com.beyondheavven.waveify.server.util

import com.sun.net.httpserver.HttpExchange
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal inline fun <reified T> readBody(exchange: HttpExchange): T? = try {
    val body = exchange.requestBody.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
    if (body.isBlank()) null else gson.fromJson(body, T::class.java)
} catch (e: Exception) {
    null
}

internal fun respondJson(exchange: HttpExchange, status: Int, payload: Any) {
    respond(exchange, status, gson.toJson(payload))
}

internal fun respond(exchange: HttpExchange, status: Int, json: String) {
    val bytes = json.toByteArray(StandardCharsets.UTF_8)
    exchange.responseHeaders.set("Content-Type", "application/json; charset=utf-8")
    exchange.sendResponseHeaders(status, bytes.size.toLong())
    exchange.responseBody.use { it.write(bytes) }
}

internal fun respondNoContent(exchange: HttpExchange) {
    exchange.sendResponseHeaders(204, -1)
    exchange.close()
}

internal fun List<Pair<String, String>>.formEncode(): String = joinToString("&") { (name, value) ->
    "${name.urlEncode()}=${value.urlEncode()}"
}

private fun String.urlEncode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)
