package com.beyondheavven.waveify.server.util

internal fun userIdFromUrn(urn: String?): Long? =
    urn?.takeIf { it.isNotBlank() }?.substringAfterLast(':', "")?.toLongOrNull()
