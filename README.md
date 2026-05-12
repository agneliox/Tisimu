# Tisimu - Gospel Hymnal Reader

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com)
[![Firebase](https://img.shields.io/badge/Firebase-039BE5?style=for-the-badge&logo=Firebase&logoColor=white)](https://firebase.google.com)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**Tisimu** is a modern, offline-capable gospel hymnal reader for Android. It allows users to download hymnals from GitHub CDN, store them locally as JSON files, and read them offline with a beautiful, user-friendly interface.

---

## 📱 Screenshots

| Login Screen | Hymnal Selection | Song List | Song Detail |
|-------------|-----------------|-----------|-------------|
| (Screenshot) | (Screenshot)     | (Screenshot) | (Screenshot) |

---

## ✨ Features

### Core Features
- 🔐 **Firebase Authentication** - Secure login and registration with email/password
- 📚 **Multi-Hymnal Support** - Download and manage multiple hymnals from GitHub CDN
- 💾 **Offline First** - Downloaded hymnals work completely offline
- 📖 **Song Viewer** - Beautifully formatted lyrics with verse/chorus separation
- 🔍 **Search Functionality** - Search songs by number, title, or lyrics
- 📤 **Share Lyrics** - Share individual verses or entire songs via social media
- 📋 **Copy to Clipboard** - Copy verses or entire lyrics with one tap
- 🎨 **Material Design** - Modern, clean interface following Material Design guidelines

### Technical Features
- 📦 **JSON-Based Storage** - No database required, pure JSON file storage
- 🌐 **GitHub CDN Hosting** - Free and fast hymn distribution
- 🔄 **Automatic Updates** - Check for hymn updates without app updates
- 📱 **Responsive Layout** - Works on phones and tablets
- 💪 **Offline Capable** - Full functionality without internet connection

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog | 2023.1.1 or later
- Android SDK API 24 or higher
- Firebase account (for authentication)
- GitHub account (for hosting hymnals)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/agneliox/Tisimu.git
   cd Tisimu

2. **Set up Firebase**

- Create a project in Firebase Console
- Add an Android app with package name com.lhavanguane.tisimu
- Download google-services.json and place it in the app/ directory
- Enable Email/Password authentication in Firebase Console

3. **Configure GitHub URLs**
- Update GITHUB_USERNAME in HymnalStorageManager.java:
   ```bash
      java
      private static final String GITHUB_USERNAME = "your_username";

4. **Build and Run**
   ```bash
   ./gradlew build
   ./gradlew installDebug

### JSON Schema for Hymnals

**Repository Structure**
Create this structure in your GitHub repository:

   ```text
   your-repo/
   └── hymnals/
      ├── manifest.json
      ├── hymnal_1.json
      ├── hymnal_2.json
      └── covers/
         ├── cover_1.jpg
         └── cover_2.jpg