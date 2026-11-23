package com.beyondheavven.waveify.server.service

sealed interface ServiceResult<out T> {
    data class Success<out T>(val value: T) : ServiceResult<T>
    data class Failure(val status: Int, val detail: String) : ServiceResult<Nothing>
}
