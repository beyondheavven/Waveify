plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.beyondheavven.waveify"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.beyondheavven.waveify"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["redirectSchemeName"] = "waveify"
        manifestPlaceholders["redirectHostName"] = "callback"

        val spotifyClientId = System.getenv("SPOTIFY_CLIENT_ID")?.takeIf { it.isNotBlank() }
            ?: "958af1c2daa4465a94e6926323e27d6a"
        val spotifyRedirectUri = System.getenv("SPOTIFY_REDIRECT_URI")?.takeIf { it.isNotBlank() }
            ?: "waveify://callback"
        val spotifyBaseUrl = System.getenv("SPOTIFY_BASE_URL")?.takeIf { it.isNotBlank() }
            ?: "https://api.spotify.com/"
        val soundcloudBaseUrl = System.getenv("SOUNDCLOUD_BASE_URL")?.takeIf { it.isNotBlank() }
            ?: "https://api-v2.soundcloud.com/"
        val soundcloudClientId = System.getenv("SOUNDCLOUD_CLIENT_ID")?.takeIf { it.isNotBlank() }
            ?: "QhVEWrrJKvW4uSU5u5EoI0FTXYUGzy5k"
        val soundcloudAuthorizeUrl = System.getenv("SOUNDCLOUD_AUTHORIZE_URL")?.takeIf { it.isNotBlank() }
            ?: "https://secure.soundcloud.com/authorize"
        val soundcloudRedirectUri = System.getenv("SOUNDCLOUD_REDIRECT_URI")?.takeIf { it.isNotBlank() }
            ?: "waveify://soundcloud/callback"
        val soundcloudAuthBaseUrl = System.getenv("SOUNDCLOUD_AUTH_BASE_URL")?.takeIf { it.isNotBlank() }
            ?: "http://10.0.2.2:8080/"
        val accountsBaseUrl = System.getenv("ACCOUNTS_BASE_URL")?.takeIf { it.isNotBlank() }
            ?: "https://accounts.spotify.com/"


        buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"$spotifyClientId\"")
        buildConfigField("String", "SPOTIFY_REDIRECT_URI", "\"$spotifyRedirectUri\"")
        buildConfigField("String", "SPOTIFY_BASE_URL", "\"$spotifyBaseUrl\"")
        buildConfigField("String", "SOUNDCLOUD_BASE_URL", "\"$soundcloudBaseUrl\"")
        buildConfigField("String", "SOUNDCLOUD_CLIENT_ID", "\"$soundcloudClientId\"")
        buildConfigField("String", "SOUNDCLOUD_AUTHORIZE_URL", "\"$soundcloudAuthorizeUrl\"")
        buildConfigField("String", "SOUNDCLOUD_REDIRECT_URI", "\"$soundcloudRedirectUri\"")
        buildConfigField("String", "SOUNDCLOUD_AUTH_BASE_URL", "\"$soundcloudAuthBaseUrl\"")
        buildConfigField("String", "ACCOUNTS_BASE_URL", "\"$accountsBaseUrl\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    buildToolsVersion = "36.0.0"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(libs.google.gson)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.coil.compose)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.hls)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}