# Architecture & Design Documentation
## Namma-Raste Health — Road Maintenance Tracker

---

## 1. System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐   │
│  │Dashboard │  │RoadList  │  │ Report   │  │ Map Screen   │   │
│  │ Screen   │  │ Screen   │  │ Screen   │  │              │   │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └──────┬───────┘   │
│       │              │              │               │            │
│  ┌────▼─────┐  ┌────▼─────┐  ┌────▼─────┐  ┌──────▼───────┐   │
│  │Dashboard │  │RoadList  │  │ Report   │  │   Map        │   │
│  │ViewModel │  │ViewModel │  │ViewModel │  │  ViewModel   │   │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └──────┬───────┘   │
└───────┼──────────────┼──────────────┼───────────────┼───────────┘
        │              │              │               │
┌───────┼──────────────┼──────────────┼───────────────┼───────────┐
│                       DOMAIN LAYER                               │
│  ┌────▼──────────────▼──────────────▼───────────────▼───────┐  │
│  │  GetDashboardStatsUseCase    GetRoadHealthUseCase         │  │
│  │  ClassifyDamageUseCase                                    │  │
│  └──────────────────────────┬────────────────────────────────┘  │
└─────────────────────────────┼────────────────────────────────────┘
                              │
┌─────────────────────────────┼────────────────────────────────────┐
│                        DATA LAYER                                 │
│  ┌───────────────────────────▼────────────────────────────────┐  │
│  │                     RoadRepository                          │  │
│  └──────┬──────────────────┬─────────────────┬────────────────┘  │
│         │                  │                  │                   │
│  ┌──────▼──┐        ┌──────▼──┐       ┌──────▼──┐               │
│  │ RoadDao │        │ReportDao│       │Contractor│               │
│  └──────┬──┘        └──────┬──┘       │  Dao    │               │
│         │                  │          └──────┬──┘               │
│  ┌──────▼──────────────────▼─────────────────▼──────────────┐  │
│  │              AppDatabase (Room SQLite)                     │  │
│  │   roads │ damage_reports │ contractors                     │  │
│  └───────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 2. Use Case Diagram

```
                    ┌─────────────────────────────────────┐
                    │         Namma-Raste Health           │
                    └─────────────────────────────────────┘

  ┌──────────┐
  │  Citizen │──────┬────────────────────────────────────────
  └──────────┘      │
                    ├── [UC-01] Search and View Roads
                    │         └── Include: View Road Details
                    │         └── Include: View Health Score
                    │
                    ├── [UC-02] Report Road Damage
                    │         └── Include: Capture/Upload Photo
                    │         └── Include: Auto GPS Capture
                    │         └── Include: AI Damage Classify
                    │         └── Include: Submit Report
                    │
                    ├── [UC-03] View Road Map
                    │         └── Include: See Health Overlay
                    │         └── Include: Tap Road Segment
                    │
                    └── [UC-04] View Contractor Info

  ┌────────────────┐
  │  PWD Officer   │──────┬──────────────────────────────────
  └────────────────┘      │
  (Future Role)           ├── [UC-05] View Dashboard
                          ├── [UC-06] Acknowledge Reports
                          └── [UC-07] Update Report Status

  ┌────────────────┐
  │ Android System │──────┬──────────────────────────────────
  └────────────────┘      │
                          ├── [UC-08] Provide GPS Location
                          └── [UC-09] Provide Camera Access

  ┌────────────────┐
  │  AI Engine     │──────────────────────────────────────────
  └────────────────┘
                          └── [UC-10] Classify Damage Type + Severity
```

---

## 3. Flow Diagram — Report Damage Workflow

```
  Citizen Opens App
         │
         ▼
  ┌──────────────┐
  │   Dashboard  │
  │  or Road List│
  └──────┬───────┘
         │  Taps a Road
         ▼
  ┌──────────────┐
  │  Road Detail │
  │   Screen     │
  └──────┬───────┘
         │  Taps "Report Damage" FAB
         ▼
  ┌──────────────────────────────────────────┐
  │              Report Screen               │
  │                                          │
  │  1. Photo: Camera / Gallery              │
  │       │                                  │
  │       ▼                                  │
  │  2. AI Classification (automatic)        │
  │       ├── File size heuristic            │
  │       ├── Keyword detection              │
  │       └── Returns: Type + Severity       │
  │           (user can override)            │
  │                                          │
  │  3. GPS Location (automatic on load)     │
  │       └── FusedLocationProvider          │
  │                                          │
  │  4. Description (optional free text)     │
  │       └── Auto-classifies if no photo    │
  │                                          │
  │  5. Severity Picker (overridable)        │
  │                                          │
  │  6. Submit Button                        │
  └──────────────────────────────────────────┘
         │  Submit pressed
         ▼
  ┌──────────────────────────────────────────┐
  │  ReportViewModel.submitReport()          │
  │  ├── Validate: GPS must be available     │
  │  ├── Build DamageReport entity           │
  │  │   ├── roadId                          │
  │  │   ├── photoPath                       │
  │  │   ├── issueType (AI or user-selected) │
  │  │   ├── severity  (AI or user-selected) │
  │  │   ├── latitude / longitude (GPS)      │
  │  │   ├── timestamp (System.currentMs)    │
  │  │   └── status = "PENDING"              │
  │  └── Insert to Room via Repository       │
  └──────────────────────────────────────────┘
         │  Room DB updated
         ▼
  ┌──────────────────────────────────────────┐
  │  Road Health RECALCULATED automatically  │
  │  (Room Flow emits new value)             │
  │  new_score = complaints / road_length_km │
  └──────────────────────────────────────────┘
         │
         ▼
  Navigate back → Road Detail shows updated health score
```

---

## 4. MVVM Layer Interaction

```
  ┌─────────────────────────────────────────────────────┐
  │                    Composable (View)                  │
  │   - collectAsStateWithLifecycle()                    │
  │   - Renders UI based on UiState                      │
  │   - Calls ViewModel functions on user events         │
  └───────────────────────────┬─────────────────────────┘
                              │ observe StateFlow
                              │ invoke functions
  ┌───────────────────────────▼─────────────────────────┐
  │                    ViewModel                         │
  │   - Holds MutableStateFlow<UiState>                 │
  │   - Launches coroutines in viewModelScope            │
  │   - Calls UseCases                                   │
  │   - Maps domain models to UiState                    │
  └───────────────────────────┬─────────────────────────┘
                              │ invoke / collect Flow
  ┌───────────────────────────▼─────────────────────────┐
  │                    UseCase                           │
  │   - Pure business logic                             │
  │   - Returns Flow / suspend fun                      │
  │   - No Android dependencies                          │
  └───────────────────────────┬─────────────────────────┘
                              │ calls
  ┌───────────────────────────▼─────────────────────────┐
  │                    Repository                        │
  │   - Single source of truth                          │
  │   - Abstracts Room DAO calls                        │
  │   - Returns Flow (reactive)                         │
  └───────────────────────────┬─────────────────────────┘
                              │
  ┌───────────────────────────▼─────────────────────────┐
  │               Room DAO + SQLite                      │
  │   - @Query annotations                               │
  │   - Flow-backed reactive queries                    │
  │   - Auto-notifies on any table change               │
  └─────────────────────────────────────────────────────┘
```

---

## 5. Road Health Score Algorithm

```
INPUT:  totalComplaints (INT), roadLengthKm (DOUBLE)

complaintsPerKm = totalComplaints / roadLengthKm

IF complaintsPerKm == 0      → EXCELLENT, score = 100
IF complaintsPerKm ≤ 0.5    → GOOD,      score = 75 + (1 - perKm/0.5) * 25
IF complaintsPerKm ≤ 1.0    → FAIR,      score = 50 + (1 - (perKm-0.5)/0.5) * 25
IF complaintsPerKm ≤ 2.0    → POOR,      score = 25 + (1 - (perKm-1.0)/1.0) * 25
IF complaintsPerKm > 2.0    → CRITICAL,  score = max(0, 25 - (perKm-2.0)/2.0 * 25)

UI Color Mapping:
  EXCELLENT → #00C853 (Bright Green)
  GOOD      → #69F0AE (Light Green)
  FAIR      → #FFD740 (Amber)
  POOR      → #FF6D00 (Deep Orange)
  CRITICAL  → #D50000 (Red)
```

---

## 6. AI Classification Pipeline

```
  User Photo / Description
         │
         ▼
  ┌──────────────────────────────────┐
  │    ClassifyDamageUseCase         │
  │                                  │
  │  Branch A: Image File Input      │
  │  ├── fileSizeKb < 100            │
  │  │   → Weights: Crack 45%,      │
  │  │              Debris 30%,     │
  │  │              Pothole 20%,    │
  │  │              Water 5%        │
  │  ├── fileSizeKb 100–500          │
  │  │   → Weights: Pothole 40%,    │
  │  │              Crack 35%,      │
  │  │              Water 15%,      │
  │  │              Debris 10%      │
  │  ├── fileSizeKb 500–1500         │
  │  │   → HIGH/CRITICAL bias       │
  │  └── fileSizeKb > 1500           │
  │      → CRITICAL severity bias   │
  │                                  │
  │  Branch B: Text Description      │
  │  ├── "water/flood/log" → WATER   │
  │  ├── "pothole/hole"   → POTHOLE  │
  │  ├── "crack/split"    → CRACK    │
  │  └── "debris/stone"   → DEBRIS   │
  └──────────────────────────────────┘
         │
         ▼
  ClassificationResult {
    issueType  : IssueType
    severity   : Severity
    confidence : Float (0.0–1.0)
    reasoning  : String
  }
         │
         ▼
  UI pre-fills IssueType + Severity
  (User can override before submitting)
```

---

## 7. Navigation Graph

```
  DashboardScreen ──────┐
       │                │
  RoadListScreen        │
       │                │
  [RoadDetailScreen] ◄──┘  (roadId arg)
       │
  [ReportScreen]            (roadId arg)

  MapScreen ─────────────► [RoadDetailScreen]

  ContractorListScreen ──► [ContractorDetailScreen] (contractorId arg)

  Bottom Navigation: Dashboard | Roads | Map | Contractors
```

---

## 8. Future Enhancements Roadmap

```
  Phase 1 (Current) ──────────────────────────────
  ✅ Local Room DB
  ✅ MVVM + Clean Architecture
  ✅ Mock AI Classification
  ✅ GPS capture
  ✅ Google Maps overlay
  ✅ Road Health Score
  ✅ Dashboard + Taluka summary

  Phase 2 ─────────────────────────────────────────
  ○ Firebase Firestore sync
  ○ Firebase Authentication (citizen/officer roles)
  ○ Push notifications for critical reports
  ○ Admin web dashboard

  Phase 3 ─────────────────────────────────────────
  ○ TensorFlow Lite real image classification
  ○ Offline map tiles
  ○ PDF report generation
  ○ Historical trend analysis
  ○ OCR road sign detection
```
