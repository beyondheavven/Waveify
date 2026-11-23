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
  - Подключение аккаунтов: Spotify — OAuth 2.0 Authorization Code + PKCE напрямую, SoundCloud — по нику с публичным client ID из soundcloud.com (без секретов и сервера).
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
├── core/
│   ├── model/               # Общие модели треков (TrackUiModel, HomeItemUiModel) и мапперы
│   ├── network/oauth/       # PKCE и разбор ошибок OAuth (используется Spotify)
│   ├── storage/             # Настройки ASCII-обложек в SharedPreferences
│   └── ui/
│       ├── component/       # Общие карточки треков
│       ├── cover/           # ASCII Art Engine (AsciiTransformation, WaveifyCover)
│       ├── notification/    # Внутриигровой баннер уведомлений
│       └── oscilloscope/    # WaveifyOscilloscope & OscilloscopeWave (Canvas визуализатор)
├── data/
│   ├── repository/          # HomeRepository, LibraryRepository, LibraryFilter
│   ├── soundcloud/          # Клиент, сервисы, модели и подключение по нику
│   └── spotify/             # OAuth2 Authorization Code + PKCE, Web API и модели
├── feature/
│   ├── home/                # Главный экран (подборки, чарты, карусели треков)
│   ├── library/             # Медиатека с фильтрацией (Spotify / SoundCloud / All) и поиском
│   ├── player/              # MiniPlayer, PlayerModal, менеджеры и визуализатор
│   ├── profile/             # Профиль: подключение Spotify/SoundCloud, выбор ASCII-палитры
│   ├── search/              # Поиск по каталогу
│   └── settings/            # Настройки темы и внешнего вида
├── navigation/              # Кастомный нижний NavigationBar
├── theme/                   # Цвета, типографика (Poppins), тема Waveify
└── MainActivity.kt          # Корневая Activity (Edge-to-Edge, Scaffold)
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

Публичные значения — Spotify client ID, redirect URI, base URLs и SoundCloud client ID — заданы дефолтами в `app/build.gradle.kts`. При необходимости их можно переопределить переменными окружения: `SPOTIFY_CLIENT_ID`, `SPOTIFY_REDIRECT_URI`, `SPOTIFY_BASE_URL`, `ACCOUNTS_BASE_URL`, `SOUNDCLOUD_CLIENT_ID`, `SOUNDCLOUD_BASE_URL`.

Redirect URI:
- Spotify: `waveify://callback` — зарегистрирован в Spotify Dashboard.

Секретов в APK нет. Spotify использует OAuth 2.0 Authorization Code + PKCE напрямую. SoundCloud подключается по нику: приложение получает публичный client ID из разметки soundcloud.com и запрашивает профиль через API v2. Секрет, регистрируемое приложение и отдельный сервер SoundCloud не нужны. `SOUNDCLOUD_CLIENT_ID` задаёт резервное публичное значение из soundcloud.com, `SOUNDCLOUD_BASE_URL` — адрес API.

### 4. Подключение SoundCloud

На экране профиля введите ник или ссылку вида `https://soundcloud.com/<nickname>`. Приложение получит публичный client ID с soundcloud.com, найдёт профиль и сохранит сессию локально. Повторное подключение не требуется до выхода из аккаунта; для отключения используйте кнопку выхода в профиле.

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
./gradlew assembleDebug testDebugUnitTest lintDebug
```

---

## 📄 Лицензия

Проект распространяется под лицензией MIT. Подробности см. в файле [LICENSE](LICENSE).
