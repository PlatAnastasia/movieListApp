# 🎬 Movie List Application

An Android app to browse and search movies, built using **Jetpack Compose**, **MVI**, and **Clean Architecture**. Users can search, view, and navigate between movies and their details.

---

## 🧱 Architecture

This project follows **Clean Architecture** and uses the **MVI (Model-View-Intent)** pattern to structure the UI and logic.

### Layers:
- **UI Layer**: Jetpack Compose UI + ViewModels
- **Domain Logic**: Encapsulated in ViewModels
- **Data Layer**: Repository, Model, API
- **DI Layer**: Hilt dependency injection
- **Shared Layer**: Resource provider abstraction

---

## 📁 Project Structure
```
com.android.movielistapp/
│
├── common/ # Shared resources
├── data/ # Models, repository, network
├── di/ # Dependency injection modules
├── navigation/ # Navigation setup
├── ui/
│ ├── movielist/ # Movie list screen + ViewModel
│ ├── moviedetail/ # Movie detail screen + ViewModel
│ └── theme/ # App theme (colors, typography)
├── MainActivity.kt
└── MovieApp.kt
```


## ⚙️ Tech Stack

- 🧱 **Jetpack Compose** – Declarative UI
- 🚀 **Kotlin Coroutines & Flow** – Asynchronous and reactive programming
- 🏗️ **MVI Pattern** – Clear separation of concerns and state management
- 🔧 **Gradle Kotlin DSL** – Build configuration

---

## 📲 Features

- 🔍 Search for movies
- 🎞️ View movie details (navigated via side-effect)
- 🔄 Refresh movie list

---

## 🛠️ Setup Instructions

1. Clone the repo:
   ```bash
   git clone https://github.com/YOUR_USERNAME/movie-list-app.git
