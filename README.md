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
  - Подключение аккаунтов через OAuth: Spotify — PKCE напрямую, SoundCloud — PKCE через модуль `:server`, где хранится `client_secret`.
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
| **Сеть** | Retrofit 2.11.0, OkHttp 4.12.0 / Logging Interceptor, Gson |
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
│       └── spotify/         # OAuth2 Authorization Code + PKCE (Deep Link), модели Spotify Web API
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
├── MainActivity.kt          # Корневая Activity (Edge-to-Edge, Scaffold)
└── server/                   # JDK HttpServer backend для SoundCloud OAuth
    └── Main.kt               # /token, /refresh, /disconnect; секрет только в env
```

---

## 🚀 Сборка и запуск

### 1. Требования
- **Android Studio** (Ladybug / Meerkat или новее)
- **JDK 21** или новее
- Android SDK: `compileSdk = 37`, `minSdk = 28`
- Установленное приложение **Spotify** на устройстве/эмуляторе (для работы Spotify App Remote)

### 2. Клонирование репозитория
```bash
git clone https://github.com/beyondheavven/Waveify.git
cd Waveify
```

### 3. Настройка `local.properties`
Android Studio создаёт `local.properties` автоматически. Если собираете из терминала, скопируйте шаблон:

```bash
cp local.properties.example local.properties
```

В файле нужен только путь к SDK:

```properties
sdk.dir=C\:\\Users\\<username>\\AppData\\Local\\Android\\Sdk
```

Публичные значения — Spotify client ID, redirect URI, base URLs и SoundCloud client ID — заданы дефолтами в `app/build.gradle.kts`. При необходимости их можно переопределить переменными окружения: `SPOTIFY_CLIENT_ID`, `SPOTIFY_REDIRECT_URI`, `SPOTIFY_BASE_URL`, `ACCOUNTS_BASE_URL`, `SOUNDCLOUD_CLIENT_ID`, `SOUNDCLOUD_BASE_URL`, `SOUNDCLOUD_AUTHORIZE_URL`, `SOUNDCLOUD_REDIRECT_URI`, `SOUNDCLOUD_AUTH_BASE_URL`.

Redirect URI:
- Spotify: `waveify://callback` — зарегистрирован в Spotify Dashboard.
- SoundCloud: `waveify://soundcloud/callback` — наш собственный custom scheme по образцу `my-app://soundcloud/callback` из доков SoundCloud; его нужно зарегистрировать в Your Apps.

Секретов в APK нет. Spotify использует OAuth 2.0 Authorization Code + PKCE напрямую. SoundCloud использует PKCE, но обмен кода требует `client_secret`, поэтому он идёт через модуль `:server`. Для SoundCloud `SOUNDCLOUD_CLIENT_ID` должен быть одинаковым в приложении и на сервере; дефолтный публичный client ID soundcloud.com для OAuth не подходит.

### 4. Backend для SoundCloud OAuth

Запустите минимальный сервер с секретом только в окружении:

```bash
export SOUNDCLOUD_CLIENT_ID=<your_client_id>
export SOUNDCLOUD_CLIENT_SECRET=<your_client_secret>
export SOUNDCLOUD_REDIRECT_URI=waveify://soundcloud/callback
./gradlew :server:run
```

Для Windows (PowerShell):

```powershell
$env:SOUNDCLOUD_CLIENT_ID="<your_client_id>"
$env:SOUNDCLOUD_CLIENT_SECRET="<your_client_secret>"
$env:SOUNDCLOUD_REDIRECT_URI="waveify://soundcloud/callback"
.\gradlew.bat :server:run
```

Сервер слушает `8080` (переопределяется через `PORT`) и предоставляет `POST /api/auth/soundcloud/token`, `POST /api/auth/soundcloud/refresh`, `POST /api/auth/soundcloud/disconnect` и `GET /health`. По умолчанию приложение обращается к `http://10.0.2.2:8080/` (эмулятор Android; cleartext разрешён только в debug-манифесте). Для реального устройства или продакшена задайте `SOUNDCLOUD_AUTH_BASE_URL` с доступным HTTPS-адресом и не публикуйте `SOUNDCLOUD_CLIENT_SECRET` в клиенте.

### 5. Сборка проекта
В терминале:
```bash
# Для Linux / macOS:
./gradlew assembleDebug

# Для Windows:
.\gradlew.bat assembleDebug
```
Или откройте проект в Android Studio и нажмите кнопку **Run ▶**.

Полная проверка перед коммитом:
```bash
./gradlew assembleDebug testDebugUnitTest lintDebug :server:test
```

---

## 📄 Лицензия

Проект распространяется под лицензией MIT. Подробности см. в файле [LICENSE](LICENSE).
