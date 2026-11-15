# 🌊 Waveify

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-2.2-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Audio-Media3%20%7C%20ExoPlayer-FF6F00?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Theme-Cyberpunk%20Dark-FF2A54?style=for-the-badge" />
</p>

<p align="center">
  <b>Waveify</b> — современный музыкальный плеер для Android, объединяющий треки из <b>Spotify</b> и <b>SoundCloud</b> в едином футуристичном киберпанк-интерфейсе с уникальным рендерингом обложек в стиле <b>ASCII-арт</b> в реальном времени.
</p>

---

## ✨ Ключевые возможности

- 🔀 **Два стриминговых сервиса в одном плеере**:
  - Каталог и рекомендации из **Spotify** (через Spotify Web API и App Remote SDK).
  - Поиск, тренды и чарты из **SoundCloud** (через SoundCloud API v2).
- 📟 **ASCII Art Cover Engine**:
  - Конвертирует любые обложки альбомов в цветной или монохромный ASCII-арт.
  - 4 цветовых профиля: **Full Color**, **Waveify Neon Red**, **Matrix CRT Green** и **Monochrome**.
  - Кастомная трансформация Coil с аппаратным ускорением через `Canvas` и автоматическим LRU-кэшированием в памяти и на диске.
- 〰️ **Осциллограф и аудио-визуализатор**:
  - Анимированный экран осциллографа в стиле неонового дисплея.
  - Захват реальных данных аудиопотока через Android `Visualizer` API (`RECORD_AUDIO`) для треков SoundCloud.
  - Процедурный синтезатор волны с пульсирующим битом (`beatKick`) при воспроизведении через Spotify Remote.
- 🎵 **Продвинутый плеер**:
  - Полноэкранный модальный плеер (`PlayerModal`) с таймлайном, скраббингом и управлением очередью.
  - Компактный интерактивный мини-плеер (`MiniPlayer`) в нижней панели приложения.
  - Воспроизведение SoundCloud через **AndroidX Media3 (ExoPlayer)** с поддержкой Progressive MP3 и HLS-потоков.
- 🎨 **UI**:
  - 100% декларативный UI на **Jetpack Compose** и **Material 3**.
  - Неоновая темная палитра (`#0D0D11`, акцент `#FF2A54`), кастомная типографика (шрифты **Poppins**).
  - Встроенный экран библиотеки с поиском, чипами фильтрации источников и пагинацией.

---

## 🛠️ Стек технологий

| Категория | Технологии |
|---|---|
| **Язык** | Kotlin 2.2.10 (Coroutines, Flow, StateFlow) |
| **UI** | Jetpack Compose, Material 3, Navigation, Icons Extended |
| **Аудио** | AndroidX Media3 (ExoPlayer + HLS), Spotify Android App Remote SDK |
| **Изображения** | Coil Compose 2.7.0 + кастомный `AsciiTransformation` |
| **Сеть** | Retrofit 2.11.0, OkHttp 3 / OkHttp Logging Interceptor, Gson |
| **Хранилище** | SharedPreferences (`AsciiSettings`) |
| **Сборка** | Gradle (Kotlin DSL), Version Catalog (`libs.versions.toml`) |

---

## 📂 Архитектура и структура проекта

```text
com.beyondheavven.waveify/
├── data/
│   ├── model/               # Модели треков (TrackUiModel, HomeItemUiModel) и мапперы
│   └── remote/
│       ├── soundcloud/      # Retrofit-сервис, модели и клиент SoundCloud
│       └── spotify/         # OAuth2 авторизация (Custom Tabs / Deep Link), модели Spotify Web API
├── player/
│   ├── soundcloud/          # SoundCloudPlayerManager (ExoPlayer + AudioWaveformTracker)
│   ├── spotify/             # SpotifyPlayerManager (SpotifyAppRemote)
│   ├── visualizer/          # AudioWaveformTracker (Android Visualizer API)
│   └── waveify/             # WaveifyPlayer — глобальный оркестратор воспроизведения
├── ui/
│   ├── cover/               # ASCII Art Engine (AsciiTransformation, AsciiSettings, WaveifyCover)
│   ├── oscilloscope/        # WaveifyOscilloscope & OscilloscopeWave (Canvas визуализатор)
│   ├── player/              # MiniPlayer & PlayerModal
│   ├── screen/
│   │   ├── home/            # Главный экран (подборки, чарты, карусели треков)
│   │   ├── library/         # Медиатека с фильтрацией (Spotify / SoundCloud / All) и поиском
│   │   ├── profile/         # Профиль: авторизация в Spotify/SoundCloud, выбор ASCII-палитры
│   │   └── notification/    # Внутриигровой баннер уведомлений
│   ├── navigation/          # Кастомный нижний NavigationBar
│   └── theme/               # Цвета (Cyberpunk Dark), типографика (Poppins), тема Waveify
└── MainActivity.kt          # Корневая Activity (Edge-to-Edge, Scaffold)
```

---

## 🚀 Сборка и запуск

### 1. Требования
- **Android Studio** (Ladybug / Meerkat или новее)
- **JDK 17** или **JDK 21**
- Android SDK: `compileSdk = 37`, `minSdk = 28`
- Установленное приложение **Spotify** на устройстве/эмуляторе (для работы Spotify App Remote)

### 2. Клонирование репозитория
```bash
git clone https://github.com/beyondheavven/Waveify.git
cd Waveify
```

### 3. Настройка `local.properties`
В корне проекта скопируйте файл-шаблон `local.properties.example` в `local.properties`:

```bash
cp local.properties.example local.properties
```

Заполните ваши ключи API:
```properties
sdk.dir=C\:\\Users\\<username>\\AppData\\Local\\Android\\Sdk

# Spotify: https://developer.spotify.com/dashboard
SPOTIFY_CLIENT_ID=ваш_spotify_client_id
SPOTIFY_SECRET=ваш_spotify_client_secret
SPOTIFY_REDIRECT_URI=waveify://callback
SPOTIFY_BASE_URL=https://api.spotify.com/
ACCOUNTS_BASE_URL=https://accounts.spotify.com/

# SoundCloud: public client_id из DevTools soundcloud.com
SOUNDCLOUD_CLIENT_ID=ваш_soundcloud_client_id
SOUNDCLOUD_BASE_URL=https://api-v2.soundcloud.com/
```

> [!TIP]
> **Где взять SoundCloud Client ID?**
> Откройте [soundcloud.com](https://soundcloud.com) в браузере, откройте DevTools (**F12** -> вкладка **Network**), включите любой трек и найдите в URL любого запроса параметр `client_id`.

### 4. Сборка проекта
В терминале:
```bash
# Для Linux / macOS:
./gradlew assembleDebug

# Для Windows:
.\gradlew.bat assembleDebug
```
Или откройте проект в Android Studio и нажмите кнопку **Run ▶**.

---

## 📄 Лицензия

Проект распространяется под лицензией MIT. Подробности см. в файле [LICENSE](LICENSE).
