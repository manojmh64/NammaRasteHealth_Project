# Namma-Raste Health 🛣️
### Rural Road Maintenance Tracker — Android Application

> **"Namma Raste"** means **"Our Road"** in Kannada — empowering citizens to monitor and report rural road conditions across Karnataka.

---

## 📋 Project Overview

**Namma-Raste Health** is a production-grade Android application that enables citizens to monitor rural road conditions, report road damage, and improve transparency in public infrastructure management. It provides real-time road health scoring, AI-assisted damage classification, and a comprehensive dashboard for taluka-level oversight.

---

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| Database | Room 2.6 (local-first) |
| DI | Hilt |
| Maps | Google Maps SDK + Maps Compose |
| Location | FusedLocationProviderClient |
| Navigation | Jetpack Navigation Compose |
| Image Loading | Coil 2.7 |
| Async | Kotlin Coroutines + StateFlow |
| Permissions | Accompanist Permissions |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 35 (Android 15) |

---

## 📁 Project Structure

```
app/src/main/java/com/nammaraste/health/
├── NammaRasteApp.kt              # Application class + Hilt + DB seeding
├── MainActivity.kt               # Single activity entry point
│
├── data/
│   ├── db/
│   │   ├── AppDatabase.kt        # Room database (singleton)
│   │   ├── entity/
│   │   │   ├── Road.kt           # Road entity
│   │   │   ├── DamageReport.kt   # Damage report entity
│   │   │   └── Contractor.kt     # Contractor entity
│   │   └── dao/
│   │       ├── RoadDao.kt        # Road CRUD + health queries
│   │       ├── ReportDao.kt      # Report CRUD + aggregation
│   │       └── ContractorDao.kt  # Contractor CRUD
│   ├── repository/
│   │   └── RoadRepository.kt     # Single source of truth
│   └── seed/
│       └── SeedData.kt           # Demo data (5 contractors, 12 roads, 30+ reports)
│
├── domain/
│   ├── model/
│   │   ├── DomainEnums.kt        # IssueType, Severity, RoadHealth enums
│   │   └── DomainModels.kt       # RoadHealthInfo, TalukaSummary, DashboardStats
│   └── usecase/
│       ├── GetRoadHealthUseCase.kt     # Health score algorithm
│       ├── ClassifyDamageUseCase.kt    # Mock AI classifier
│       └── GetDashboardStatsUseCase.kt # Dashboard aggregation
│
├── di/
│   └── DatabaseModule.kt         # Hilt module for DB/DAOs
│
└── ui/
    ├── theme/
    │   ├── Color.kt              # Brand + health + severity colors
    │   ├── Type.kt               # Material3 typography
    │   └── Theme.kt              # Dark/light color schemes
    ├── navigation/
    │   ├── Screen.kt             # Route definitions
    │   └── NammaRasteNavGraph.kt # Nav graph + bottom bar
    ├── components/
    │   └── CommonComponents.kt   # HealthBadge, HealthScoreRing, RoadHealthCard...
    └── screens/
        ├── dashboard/            # DashboardScreen + ViewModel
        ├── road/                 # RoadListScreen + RoadDetailScreen + ViewModels
        ├── report/               # ReportScreen + ViewModel
        ├── map/                  # MapScreen + ViewModel
        └── contractor/           # ContractorListScreen + ContractorDetailScreen + ViewModel
```

---

## 🗄️ Database Schema

### `roads`
| Column | Type | Description |
|---|---|---|
| id | INTEGER PK | Auto-generated |
| name | TEXT | Official road name |
| taluka | TEXT | Administrative block |
| district | TEXT | District name |
| lengthKm | REAL | Road length in km |
| surfaceType | TEXT | Asphalt/Concrete/Gravel/Dirt |
| contractorId | INTEGER FK | Links to contractors |
| warrantyExpiryMs | INTEGER | Warranty end date (epoch ms) |
| constructionYear | INTEGER | Year road was built |
| startLat/Lng | REAL | GPS start point |
| endLat/Lng | REAL | GPS end point |

### `damage_reports`
| Column | Type | Description |
|---|---|---|
| id | INTEGER PK | Auto-generated |
| roadId | INTEGER FK | Links to roads (CASCADE delete) |
| photoPath | TEXT | Absolute path to captured photo |
| issueType | TEXT | POTHOLE / CRACK / WATERLOGGING / DEBRIS |
| severity | TEXT | LOW / MEDIUM / HIGH / CRITICAL |
| description | TEXT | Citizen's free-text description |
| latitude | REAL | GPS latitude of issue |
| longitude | REAL | GPS longitude of issue |
| timestamp | INTEGER | Epoch ms — auto-captured |
| status | TEXT | PENDING / ACKNOWLEDGED / IN_PROGRESS / RESOLVED |

### `contractors`
| Column | Type | Description |
|---|---|---|
| id | INTEGER PK | Auto-generated |
| name | TEXT | Company name |
| contactPerson | TEXT | Primary contact |
| phone | TEXT | Contact number |
| email | TEXT | Email address |
| address | TEXT | Registered address |
| registrationNumber | TEXT | GSTIN / Reg no. |
| specialisation | TEXT | Domain expertise |
| rating | REAL | Score out of 5.0 |
| projectsCompleted | INTEGER | Count of past projects |

---

## 🧠 Road Health Algorithm

```
complaintsPerKm = totalComplaints / roadLengthKm

EXCELLENT : complaintsPerKm = 0       → Score: 100
GOOD      : complaintsPerKm ≤ 0.5    → Score: 75–100
FAIR      : complaintsPerKm ≤ 1.0    → Score: 50–75
POOR      : complaintsPerKm ≤ 2.0    → Score: 25–50
CRITICAL  : complaintsPerKm > 2.0    → Score: 0–25
```

Health scores **update automatically** in real-time via Room's reactive `Flow` queries — every new report instantly recalculates the affected road's score.

---

## 🤖 GenAI / AI Features

### Damage Classifier (`ClassifyDamageUseCase`)
A **mock AI classifier** that simulates on-device image analysis:

- **Input**: Camera/gallery photo file or text description
- **File-size heuristic**: Larger images → more severe damage predicted
- **Text keyword detection**: "water/flood" → Waterlogging, "pothole/hole" → Pothole, etc.
- **Weighted random sampling**: Biased probability distributions per image size band
- **Output**:
  - `IssueType`: POTHOLE | CRACK | WATERLOGGING | DEBRIS
  - `Severity`: LOW | MEDIUM | HIGH | CRITICAL
  - `Confidence`: 0–100%
  - `Reasoning`: Human-readable explanation string

> In production, replace with **TensorFlow Lite** (on-device) or **Vertex AI Vision API** (cloud).

---

## 🗺️ Map Features

- Google Maps polylines drawn for every road (startLat/Lng → endLat/Lng)
- Polyline color = road health color (Green → Red spectrum)
- Tap a polyline or marker → bottom sheet shows road summary
- Legend overlaid on map showing color meanings

---

## 📱 Screens

| Screen | Description |
|---|---|
| **Dashboard** | Hero banner, stat cards, worst/best roads, taluka summary |
| **Road Directory** | Searchable list with taluka filter chips and health cards |
| **Road Detail** | Health ring, road info, contractor info, damage report timeline |
| **Report Damage** | Camera/gallery, AI tag suggestion, severity picker, GPS capture |
| **Map** | Colored road polylines, legend, selected road bottom sheet |
| **Contractors** | Searchable contractor cards with rating |
| **Contractor Detail** | Full profile with contact info |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK API 26–35
- Google Maps API Key

### Setup Steps

1. **Clone / open the project** in Android Studio

2. **Add your Maps API Key** in `local.properties`:
   ```properties
   MAPS_API_KEY=your_actual_api_key_here
   ```
   > Get a key at: https://console.cloud.google.com → Maps SDK for Android

3. **Sync Gradle** (File → Sync Project with Gradle Files)

4. **Run the app** on an emulator (API 26+) or physical device

   The app auto-seeds 5 contractors, 12 roads, and 30+ reports on first launch.

---

## 🔮 Future Enhancements

| Feature | Description |
|---|---|
| Firebase Firestore | Cloud sync for cross-device reports |
| Firebase Auth | Citizen login & report ownership |
| TensorFlow Lite | Real on-device image damage classification |
| Push Notifications | Alert contractors on critical reports |
| Offline Maps | Cached tile layers for rural areas with no internet |
| Admin Dashboard | Web portal for PWD officers to manage reports |
| OCR Road Signs | Auto-populate road name from sign photos |
| Analytics | Trend analysis — seasonal damage patterns |
| Export Reports | PDF generation for taluka-level reports |

---

## 📊 Sample Dummy Data

The app seeds the following on first install:
- **5 Contractors** across Dharwad, Belagavi, Vijayapura, Hubballi, Mysuru
- **12 Roads** across 4 talukas: Dharwad, Belagavi, Vijayapura, Gadag
- **30+ Damage Reports** covering all 4 issue types and all severity levels
- Warranty statuses include both expired and active warranties for realistic testing

---

## 👨‍💻 Author

**Namma-Raste Health** — Built as a capstone project demonstrating modern Android development with MVVM, Clean Architecture, Jetpack Compose, and GenAI integration.

---

## 📜 License

This project is for educational/demonstration purposes.
