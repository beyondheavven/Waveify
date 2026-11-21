plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

dependencies {
    implementation(libs.google.gson)
    testImplementation(libs.junit)
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "com.beyondheavven.waveify.server.MainKt"
}
