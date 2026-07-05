# Tisimu - Gospel Hymnal Reader

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com)
[![Firebase](https://img.shields.io/badge/Firebase-039BE5?style=for-the-badge&logo=Firebase&logoColor=white)](https://firebase.google.com)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)

**Tisimu** is a modern, offline-capable gospel hymnal reader for Android. It allows users to download hymnals from GitHub CDN, store them locally as JSON files, and read them offline with a beautiful, user-friendly interface.

---

## 📱 Screenshots

| Login Screen | Hymnal Selection | Song List | Song Detail |
| --- | --- | --- | --- |
| Coming Soon | Coming Soon | Coming Soon | Coming Soon |

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
   ```

2. **Set up Firebase**

   - Create a project in Firebase Console.
   - Add an Android app with package name `com.lhavanguane.tisimu`.
   - Download `google-services.json` and place it in the `app/` directory.
   - Enable Email/Password authentication in Firebase Console.

3. **Configure GitHub URLs**

   Update `GITHUB_USERNAME` in `HymnalStorageManager.java`:

   ```java
   private static final String GITHUB_USERNAME = "your_username";
   ```

4. **Build and run**

   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

## 📦 JSON Schema for Hymnals

### Repository Structure

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
```

### `manifest.json`

```json
{
  "version": 1,
  "lastUpdated": "2024-01-15T10:00:00Z",
  "hymnals": [
    {
      "id": "harpa_crista",
      "name": "Harpa Cristã",
      "description": "Traditional Brazilian gospel hymnal",
      "language": "Portuguese",
      "totalSongs": 640,
      "fileUrl": "https://raw.githubusercontent.com/USER/REPO/main/hymnals/harpa_crista.json",
      "coverUrl": "https://raw.githubusercontent.com/USER/REPO/main/hymnals/covers/harpa_crista.jpg",
      "fileSize": 2450000,
      "version": 1,
      "author": "Various Authors"
    }
  ]
}
```

### Individual Hymnal JSON (Basic)

```json
{
  "id": "harpa_crista",
  "name": "Harpa Cristã",
  "version": 1,
  "metadata": {
    "description": "Traditional Brazilian gospel hymnal",
    "language": "Portuguese",
    "totalSongs": 640,
    "year": 1922,
    "publisher": "Editora Betânia"
  },
  "sections": [
    {
      "id": 1,
      "sequence": 1,
      "name": "Louvor e Adoração",
      "startNumber": 1,
      "endNumber": 150
    }
  ],
  "songs": [
    {
      "number": 1,
      "title": "Grandioso És Tu",
      "lyrics": "Grandioso és Tu, Senhor da criação...\n\nGrandioso és Tu, grandioso és Tu\nTodo meu ser Te adora, meu Rei",
      "author": "Carl Boberg",
      "composer": "Traditional"
    }
  ]
}
```

### Individual Hymnal JSON (Advanced with Verses/Chorus)

```json
{
  "id": "harpa_crista",
  "name": "Harpa Cristã",
  "version": 2,
  "metadata": {
    "description": "Traditional Brazilian gospel hymnal",
    "language": "Portuguese",
    "totalSongs": 640
  },
  "songs": [
    {
      "number": 1,
      "title": "Grandioso És Tu",
      "author": "Carl Boberg",
      "composer": "Traditional Swedish",
      "verses": [
        {
          "type": "verse",
          "number": 1,
          "label": "1",
          "lines": [
            "Grandioso és Tu, Senhor da criação,",
            "Que os céus e a terra formaste com amor;",
            "As estrelas, os montes, o mar e o sertão,",
            "Revelam Teu poder, Teu esplendor."
          ]
        },
        {
          "type": "chorus",
          "label": "Coro",
          "lines": [
            "Grandioso és Tu, grandioso és Tu!",
            "Todo meu ser Te adora, meu Rei!",
            "Grandioso és Tu, grandioso és Tu!",
            "Pra sempre eu Te louvarei, amém!"
          ]
        }
      ]
    }
  ]
}
```

## 🏗️ Project Architecture

```text
com.lhavanguane.tisimu/
├── models/                 # Data models
│   ├── HymnalManifest.java
│   ├── HymnalData.java
│   └── SongItem.java
├── services/               # Business logic
│   └── HymnalStorageManager.java
├── ui/
│   ├── activities/         # Activities
│   │   ├── SplashActivity.java
│   │   ├── LoginActivity.java
│   │   ├── RegisterActivity.java
│   │   ├── HymnalSelectionActivity.java
│   │   ├── SongListActivity.java
│   │   └── SongDetailActivity.java
│   ├── adapters/           # RecyclerView adapters
│   │   ├── HymnalAdapter.java
│   │   ├── SongAdapter.java
│   │   └── VerseAdapter.java
│   └── fragments/          # Fragments
│       ├── LyricsFragment.java
│       ├── CommentsFragment.java
│       └── MelodiesFragment.java
└── utils/                  # Utility classes
    └── PreferencesManager.java
```

## Tech Stack

| Component | Technology |
| --- | --- |
| Language | Java 8+ |
| Minimum SDK | API 24 (Android 7.0) |
| Target SDK | API 34 (Android 14) |
| Authentication | Firebase Auth |
| Networking | OkHttp 4.12.0 |
| JSON Parsing | Gson 2.10.1 |
| Image Loading | Glide 4.16.0 |
| UI | Material Design Components |

## 🗺️ Roadmap

### Version 1.0 (Current) ✅

- Firebase Authentication (Login/Register)
- Hymnal download from GitHub CDN
- Offline JSON file storage
- Hymnal selection management
- Song list with search functionality
- Song detail with selectable verses
- Share lyrics to social media
- Copy verses to clipboard
- Multi-hymnal library support

### Version 1.1 (Next Release)

- Favorites System - Bookmark favorite songs for quick access
- Continue Reading - Remember last opened song and verse position
- Font Size Adjustment - Increase/decrease text size for readability
- Dark/Night Mode - Theme switcher for comfortable night reading
- Recent Songs - Track and display recently viewed hymns
- Song Index - Quick jump by hymn number or alphabetical index

### Version 1.2 (Planned)

- Comments & Notes - Add personal notes to hymns
- Audio Playback - Play recorded melodies or link to YouTube
- Song Suggestions - Suggest corrections to lyrics
- Community Contributions - Upload user-created hymns
- Search History - Track and save recent searches
- Export/Import - Backup and restore favorites and notes

### Version 2.0 (Future)

- Cloud Sync - Sync favorites and notes across devices
- Progressive Web App (PWA) - Web version of the app
- iOS Version - Port to iOS using Kotlin Multiplatform
- Backend API - Admin dashboard for managing hymnals
- Analytics - Usage statistics and popular hymns
- Push Notifications - New hymnal available notifications

### Long-term Vision

- AI-Powered Search - Search by lyrics fragment or theme
- Transliteration - Convert lyrics to different scripts
- Multi-language Support - Interface in multiple languages
- Accessibility Features - Screen reader support, high contrast mode
- Offline Audio Downloads - Download melody files for offline listening
- Social Features - Share favorite verses, comment on hymns

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository.
2. Create your feature branch: `git checkout -b feature/AmazingFeature`.
3. Commit your changes: `git commit -m 'Add some AmazingFeature'`.
4. Push to the branch: `git push origin feature/AmazingFeature`.
5. Open a Pull Request.

### Development Guidelines

- Follow Java coding conventions.
- Add comments for complex logic.
- Test on multiple API levels (24-34).
- Update documentation when adding features.
- Write meaningful commit messages.

## 🐛 Reporting Issues

If you find a bug or have a feature request, please:

1. Check if the issue already exists in Issues.
2. If not, create a new issue with:
   - Clear description of the problem.
   - Steps to reproduce.
   - Screenshots if applicable.
   - Device and Android version.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```text
MIT License

Copyright (c) 2024 Agnelio Xavier

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 🙏 Acknowledgments

- Material Design Components - For beautiful UI components
- Firebase Team - For authentication services
- OkHttp & Gson - For networking and JSON parsing
- Glide - For image loading
- All Contributors - For testing and feedback

## 📞 Contact

- Developer: Agnelio Xavier
- GitHub: [@agneliox](https://github.com/agneliox)
- Project Link: <https://github.com/agneliox/Tisimu>

## ⭐ Show Your Support

If you find this project useful, please give it a star ⭐ on GitHub!

[![GitHub stars](https://img.shields.io/github/stars/agneliox/Tisimu?style=social)](https://github.com/agneliox/Tisimu/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/agneliox/Tisimu?style=social)](https://github.com/agneliox/Tisimu/network/members)

## 📊 Project Status

![Last commit](https://img.shields.io/github/last-commit/agneliox/Tisimu)
![Issues](https://img.shields.io/github/issues/agneliox/Tisimu)
![Pull requests](https://img.shields.io/github/issues-pr/agneliox/Tisimu)

Built with ❤️ for gospel music lovers around the world.
