# AI Health Symptom Tracker

An Android application developed as part of the CS6051 Mobile Applications module at London Metropolitan University (Spring Semester 2025/26).

The app allows users to log daily health symptoms with severity ratings, view their symptom history, receive AI-generated informational insights via the Groq API (Llama 3.1), and set daily reminder notifications.

---

## Prerequisites

- **Android Studio** (Hedgehog or later recommended)
- **Android SDK** — minimum API 26 (Android 8.0)
- **A Groq API key** — free to obtain at [https://console.groq.com](https://console.groq.com)

---

## Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/RafaelArmelin/AIHealthSymptomTracker.git
cd AIHealthSymptomTracker
```

### 2. Open in Android Studio

File → Open → select the `AIHealthSymptomTracker` folder → click OK.

Wait for the Gradle sync to complete.

### 3. Add your Groq API key

Open the `local.properties` file in the **root** of the project (alongside `settings.gradle.kts`) and add the following line:

```
GROQ_API_KEY=your_groq_api_key_here
```

> **Note:** `local.properties` is excluded from version control for security. You must add your own key before running the app. Without it, the AI Insights screen will not return results.

### 4. Create an emulator (if not already set up)

Tools → Device Manager → Create Virtual Device → Pixel 6 → select API 35 → Finish.

### 5. Run the application

Press the green **Run** button in Android Studio, or use `Shift + F10`.

---

## Features

| Feature | Description |
|---|---|
| Symptom Logging | Log symptoms with a name, severity (1–5) and optional notes |
| Symptom History | View all entries in reverse chronological order with colour-coded severity |
| Severity Distribution Chart | Home screen chart showing weekly low / medium / high symptom breakdown |
| AI Insights | Select a logged symptom to receive an AI-generated informational overview (requires internet) |
| Daily Reminder | Toggle a background notification reminder via WorkManager |
| Settings | Clear all data with confirmation dialog |

---

## Project Structure

```
app/src/main/java/com/rafaelarmelin/aihealthsymptomtracker/
├── data/               # Room entity, DAO, and database
├── repository/         # SymptomRepository and GroqRepository
├── network/            # Retrofit interface and request/response models
├── viewmodel/          # SymptomViewModel
├── worker/             # ReminderWorker (WorkManager)
└── ui/
    ├── home/           # HomeFragment
    ├── log/            # LogSymptomFragment
    ├── history/        # HistoryFragment + SymptomAdapter
    ├── insights/       # InsightsFragment
    └── settings/       # SettingsFragment
```

---

## Architecture

The application follows the **MVVM (Model-View-ViewModel)** pattern using Android Jetpack components:

- **View** — Fragments with ViewBinding
- **ViewModel** — `SymptomViewModel` scoped to the Activity
- **Repository** — abstracts Room database and Groq API access
- **Room** — local SQLite database for persistent symptom storage
- **Retrofit** — HTTP client for Groq API communication
- **WorkManager** — background scheduling for daily notifications
- **Navigation Component** — single-activity navigation with bottom nav bar

---

## Dependencies

| Library | Version | Purpose |
|---|---|---|
| Room | 2.6.1 | Local database |
| Retrofit | 2.9.0 | Network calls |
| OkHttp Logging Interceptor | 4.12.0 | API debugging |
| Navigation Component | 2.7.7 | Screen navigation |
| ViewModel + LiveData | 2.7.0 | Reactive UI state |
| WorkManager | 2.9.0 | Background notifications |
| Kotlin Coroutines | 1.7.3 | Async operations |
| Material Components | 1.10.0 | UI design system |

---

## Notes

- The app has been tested on a **Pixel 6 API 37** Android emulator.
- The AI Insights feature requires a live internet connection and a valid Groq API key in `local.properties`.
- The daily reminder is configured to fire every **15 minutes** for demonstration purposes. In a production release this would be set to 24 hours.
- All symptom data is stored **locally on the device** and is never transmitted to any server other than the symptom name, severity and notes sent to the Groq API for insight generation.

---
