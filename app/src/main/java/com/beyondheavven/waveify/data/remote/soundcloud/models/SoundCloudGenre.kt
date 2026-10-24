package com.beyondheavven.waveify.data.remote.soundcloud.models

enum class SoundCloudGenre(

    val queryValue: String,

    val displayName: String
) {
    ALL_MUSIC("soundcloud:genres:all-music", "All Music"),
    ELECTRONIC("soundcloud:genres:electronic", "EDM"),
    HIP_HOP("soundcloud:genres:hiphoprap", "Hip-Hop & Rap"),
    POP("soundcloud:genres:pop", "Pop"),
    TRAP("soundcloud:genres:trap", "Trap"),
    HOUSE("soundcloud:genres:house", "House"),
}