package com.beyondheavven.waveify.server

import com.beyondheavven.waveify.server.config.AuthConfig
import com.beyondheavven.waveify.server.route.SoundCloudAuthRoutes
import com.beyondheavven.waveify.server.service.SoundCloudAuthService
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.util.concurrent.Executors

fun main() {
    val config = AuthConfig.fromEnv()
    if (config == null) {
        System.err.println("SOUNDCLOUD_CLIENT_ID and SOUNDCLOUD_CLIENT_SECRET are required")
        return
    }

    val server = HttpServer.create(InetSocketAddress(config.port), 0)
    SoundCloudAuthRoutes(config, SoundCloudAuthService(config)).install(server)
    server.executor = Executors.newFixedThreadPool(4)
    server.start()
    println("Waveify auth server listening on http://localhost:${config.port}")
}
