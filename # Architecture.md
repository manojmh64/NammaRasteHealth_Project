# Architecture Overview

Namma Raste Health follows MVVM Architecture.

## Flow

UI Layer
↓
ViewModel
↓
Repository
↓
Room Database / Firebase

## Components

### UI Layer
Handles user interaction and screens.

### ViewModel
Manages UI-related data.

### Repository
Acts as single source of truth.

### Database
Stores complaint and road data locally.

## Advantages

- Clean architecture
- Easy maintenance
- Better scalability
- Separation of concerns