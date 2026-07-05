# Tisimu — Product Roadmap

> Last updated: 2026-06-27 | Current version: 1.1.9 (Beta)

---

## Vision

Tisimu is the go-to offline gospel hymnal reader for Christian communities — offering curated hymnals, collaborative community features, and a beautiful, accessible reading experience.

---

## Version History

| Version | Code | Status | Key Deliverables |
|---|---|---|---|
| 1.0.0 | 1 | Released | Firebase Auth, hymnal download, offline reading, song list/detail, share/copy |
| 1.0.1 | 2 | Released | Navigation fix, fragment crash fix, daily verses, Material Design 3 |
| 1.1.x | 3–9 | Beta (current) | Communities, Google Sign-In, real-time Firestore, announcements, agenda, members, dark mode |

---

## Release 1.2 — Stability & Foundation
**Target:** Sprint 3–4 (Q3 2026)
**Theme:** Fix all P0 blockers and complete the architecture foundation

### Goals
- [ ] Implement `User.java` model with Firestore `/users` collection
- [ ] Create Room DAO layer (all entities have DAOs and a Database class)
- [ ] Full MVVM migration — no Firestore calls in Activities/Fragments
- [ ] Repository pattern — ViewModels depend on Repository, not Service directly
- [ ] Files tab: functional file sharing within communities (Firebase Storage)
- [ ] Community cover image upload

### Non-Goals for 1.2
- Audio playback
- Push notifications
- AI features

---

## Release 1.3 — Engagement & Personalization
**Target:** Sprint 5–6 (Q4 2026)
**Theme:** Give users personal ownership of their experience

### Goals
- [ ] **Favorites** — bookmark songs; favorites stored in Room
- [ ] **Recent Songs** — track last 20 viewed songs; stored in Room
- [ ] **Personal Notes** — text notes per song; stored in Room
- [ ] **Font Size Adjustment** — persisted in PreferencesManager
- [ ] **Song Quick-Jump** — jump by hymn number from song list header
- [ ] **Continue Reading** — reopen last-viewed song and verse position
- [ ] **Search History** — save and clear recent searches

---

## Release 1.4 — Community Expansion
**Target:** Sprint 7–8 (Q1 2027)
**Theme:** Make communities richer and more useful

### Goals
- [ ] **Push Notifications** — FCM for announcements and agenda changes
- [ ] **Community Files** — upload/download PDFs and audio per community
- [ ] **Agenda Date Picker** — proper date/time selection for agenda items
- [ ] **Member Messaging** — lightweight in-community direct message (optional)
- [ ] **Community Search** — find public communities by name or topic

---

## Release 2.0 — Audio & Media
**Target:** Sprint 9–10 (Q2 2027)
**Theme:** Bring hymnals to life with audio

### Goals
- [ ] **Audio Playback** — play melody proposals using ExoPlayer (dep already in place)
- [ ] **Melody Upload** — community members upload audio recordings
- [ ] **Video Links** — embed YouTube links for each song's melody
- [ ] **Offline Audio** — download audio files for offline playback

---

## Release 2.1 — AI & Accessibility
**Target:** Sprint 11–12 (Q3 2027)
**Theme:** Smart search and universal access

### Goals
- [ ] **AI-Powered Search** — search by theme, fragment, or meaning (Firebase AI dep in place)
- [ ] **Song Corrections Workflow** — full suggest → review → approve/reject UI
- [ ] **High Contrast Mode** — accessibility for visually impaired
- [ ] **Screen Reader Support** — content descriptions for all interactive elements
- [ ] **Multi-language UI** — full interface translation (Portuguese, English, Tswa)
- [ ] **Transliteration** — convert lyrics to different scripts

---

## Long-Term Vision (v3.0+)

| Feature | Notes |
|---|---|
| Cloud Sync | Sync favorites and notes across devices via Firestore |
| Progressive Web App | Web version for desktop access |
| iOS Port | Kotlin Multiplatform or React Native |
| Backend API | Admin dashboard for hymnal management |
| Analytics Dashboard | Popular hymns, usage statistics |
| Community Events | Live worship event coordination with RSVP |

---

## Architecture Roadmap

| Phase | Pattern | Target |
|---|---|---|
| Current | Mixed (some MVVM, some direct calls) | 1.1.9 |
| Phase 1 | Full MVVM + Repository | v1.2 |
| Phase 2 | Clean Architecture (Domain layer) | v2.0 |
| Phase 3 | Jetpack Compose (UI migration) | v3.0+ |

---

## Supported Hymnals Roadmap

| Hymnal | Language | Status |
|---|---|---|
| Harpa Cristã | Portuguese | ✅ Available |
| Cantor Cristão | Portuguese | ✅ Available |
| Hinário Adventista | Portuguese | ✅ Available |
| Hinos Evangélicos | Portuguese | ✅ Available |
| Mhalamhala | Tswa | ✅ Available |
| English Standard Hymnal | English | 📋 Planned |
| Cânticos de Louvor | Portuguese | 📋 Planned |
| User-contributed hymnals | Any | 📋 v2.0+ |
