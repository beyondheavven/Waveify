# Waveify

<div align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Kotlin-2.2-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Audio-Media3-FF2A54?style=for-the-badge" alt="Media3" />
</div>

Waveify is an Android music player that brings together music from Spotify and SoundCloud into one modern listening experience.

It is built with Kotlin and Jetpack Compose, with a cyberpunk-inspired dark theme, personalized playback controls, and a unified media UI.

## Features

- Spotify and SoundCloud integration
- Unified music library and player interface
- Background playback using AndroidX Media3 / ExoPlayer
- Modern UI built with Jetpack Compose and Material 3
- Neon cyberpunk dark theme
- Album art processing and stylized cover presentation
- Mini-player and fullscreen player modes
- Profile and account management
- Support for streaming catalogs and track playback

## Screenshots

> Add screenshots here for:
> - Home screen
> - Player view
> - Library / liked content
> - Profile screen

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX Lifecycle
- Media3 / ExoPlayer
- Retrofit + OkHttp
- Gson
- Coil
- Spotify SDK / API integration
- SoundCloud API integration

## Project Structure

```text
Waveify/
├─ app/
│  ├─ src/
│  │  ├─ main/
│  │  │  ├─ java/                  # App source code
│  │  │  ├─ res/                   # Resources
│  │  │  └─ AndroidManifest.xml
│  │  ├─ test/                     # Unit tests
│  │  └─ androidTest/              # Instrumentation tests
│  ├─ build.gradle.kts
│  └─ proguard-rules.pro
├─ gradle/
├─ build.gradle.kts
├─ gradle.properties
├─ settings.gradle.kts
├─ gradlew
├─ gradlew.bat
├─ local.properties.example
├─ README.md
├─ LICENSE
└─ .gitignore
