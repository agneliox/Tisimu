# Tisimu — Requirements Document

> Last updated: 2026-06-27 | Version: 1.1.9

---

## 1. Product Overview

Tisimu is an offline-capable Android gospel hymnal reader with community features. It serves Christian individuals and communities who want to read, search, and share hymn lyrics, as well as organize and communicate within their congregation.

---

## 2. Stakeholders

| Role | Name | Interest |
|---|---|---|
| Product Owner / Developer | Agnelio Xavier | Deliver a functional, beautiful, easy-to-use hymnal app |
| Beta Testers | agnelio.lhavanguane@gmail.com, ndjanga@gmail.com | Test and validate features |
| End Users | Gospel community members | Read hymns, participate in community |

---

## 3. Functional Requirements

### 3.1 Authentication

| ID | Requirement | Priority | Status |
|---|---|---|---|
| FR-1.1 | User shall register with email and password | P0 | Implemented |
| FR-1.2 | User shall log in with email and password | P0 | Implemented |
| FR-1.3 | User shall log in with Google account | P0 | Implemented |
| FR-1.4 | User shall remain logged in across app restarts | P0 | Implemented |
| FR-1.5 | User shall be able to log out | P0 | Implemented |
| FR-1.6 | User shall be able to change password | P1 | Implemented (UI exists) |
| FR-1.7 | User shall have a profile with name, email, and profile image | P1 | Partial — `User.java` empty |

### 3.2 Hymnal Management

| ID | Requirement | Priority | Status |
|---|---|---|---|
| FR-2.1 | App shall fetch hymnal manifest from CDN | P0 | Implemented |
| FR-2.2 | User shall download a hymnal to local storage | P0 | Implemented |
| FR-2.3 | Downloaded hymnal shall be readable offline | P0 | Implemented |
| FR-2.4 | App shall support multiple hymnals | P0 | Implemented |
| FR-2.5 | User shall switch between downloaded hymnals | P0 | Implemented |
| FR-2.6 | Hymnal shall be organized by sections | P1 | Implemented |
| FR-2.7 | Songs shall display structured verses and chorus | P0 | Implemented |

### 3.3 Song Discovery & Reading

| ID | Requirement | Priority | Status |
|---|---|---|---|
| FR-3.1 | User shall search songs by number | P0 | Implemented |
| FR-3.2 | User shall search songs by title | P0 | Implemented |
| FR-3.3 | User shall search songs by lyrics fragment | P1 | Implemented |
| FR-3.4 | User shall copy a verse or full lyrics to clipboard | P0 | Implemented |
| FR-3.5 | User shall share lyrics via Android share sheet | P0 | Implemented |
| FR-3.6 | User shall adjust font size | P2 | Planned (Sprint 4) |
| FR-3.7 | User shall bookmark favorite songs | P1 | Planned (Sprint 4) |
| FR-3.8 | App shall track recently viewed songs | P2 | Planned (Sprint 4) |
| FR-3.9 | User shall add personal notes to a song | P2 | Planned (Sprint 4) |

### 3.4 Community Features

| ID | Requirement | Priority | Status |
|---|---|---|---|
| FR-4.1 | User shall create a public community | P0 | Implemented |
| FR-4.2 | User shall create a private community with join code | P0 | Implemented |
| FR-4.3 | User shall join a public community | P0 | Implemented |
| FR-4.4 | User shall join a private community with join code | P0 | Implemented |
| FR-4.5 | User shall leave a community | P0 | Implemented |
| FR-4.6 | Community manager shall post announcements | P0 | Implemented |
| FR-4.7 | Community manager shall delete announcements | P0 | Implemented |
| FR-4.8 | Community manager shall add agenda items | P0 | Implemented |
| FR-4.9 | Community manager shall delete agenda items | P0 | Implemented |
| FR-4.10 | Community manager shall promote members to manager | P1 | Implemented |
| FR-4.11 | User shall view community members | P0 | Implemented |
| FR-4.12 | Community shall support shared file uploads | P1 | Planned (Sprint 3) |
| FR-4.13 | Community shall have a cover image | P2 | Planned (Sprint 4) |
| FR-4.14 | User shall receive push notifications for announcements | P2 | Planned (Sprint 5) |
| FR-4.15 | Changes shall sync in real-time across devices | P0 | Implemented |

### 3.5 Daily Verse

| ID | Requirement | Priority | Status |
|---|---|---|---|
| FR-5.1 | App shall display a daily Bible verse on home screen | P1 | Implemented |
| FR-5.2 | Daily verse shall rotate daily | P1 | Implemented |
| FR-5.3 | Daily verse shall include devotional content | P2 | Implemented |

### 3.6 Settings & Personalization

| ID | Requirement | Priority | Status |
|---|---|---|---|
| FR-6.1 | User shall switch app language | P1 | Implemented |
| FR-6.2 | User shall switch light/dark theme | P1 | Implemented |
| FR-6.3 | Language preference shall persist across sessions | P1 | Implemented |
| FR-6.4 | User shall adjust text size | P2 | Planned (Sprint 4) |

---

## 4. Non-Functional Requirements

| ID | Requirement | Target |
|---|---|---|
| NFR-1 | Cold launch time | < 2 seconds |
| NFR-2 | App APK size | < 15MB |
| NFR-3 | RAM usage (idle) | < 100MB |
| NFR-4 | Search response time | < 300ms |
| NFR-5 | Minimum Android version supported | API 24 (Android 7.0) |
| NFR-6 | All network calls use HTTPS | Always |
| NFR-7 | No sensitive data stored in plain text locally | Always |
| NFR-8 | App must be usable fully offline (hymnal features) | Always |
| NFR-9 | Firebase Crashlytics integrated | Always |
| NFR-10 | Material Design 3 compliant UI | Always |

---

## 5. Architecture Requirements

| ID | Requirement | Status |
|---|---|---|
| AR-1 | MVVM pattern with ViewModel + LiveData | Partial — target Sprint 3 |
| AR-2 | Repository pattern between ViewModel and data sources | Planned Sprint 3 |
| AR-3 | Room for all local persistence | Partial — entities defined, DAOs missing |
| AR-4 | Firebase Firestore for all community/realtime data | Implemented |
| AR-5 | No direct Firestore calls from UI layer | Not yet — Sprint 3 target |
| AR-6 | Offline-first: Room as single source of truth for hymnal data | Partial |

---

## 6. Security Requirements

| ID | Requirement | Status |
|---|---|---|
| SEC-1 | Firebase Auth for all user authentication | Implemented |
| SEC-2 | Firestore security rules restrict reads/writes by role | Implemented |
| SEC-3 | Private community join code validated server-side | Implemented |
| SEC-4 | Only managers can create/delete announcements and agenda | Implemented |
| SEC-5 | Admin credentials not stored in source code | Implemented (signing.properties in .gitignore) |
| SEC-6 | Google Sign-In certificate fingerprints correct in Firebase | Implemented (fixed v1.1.9) |

---

## 7. Requirement Change Log

| Date | Change | Reason | Impact |
|---|---|---|---|
| 2026-06-27 | Added AR-1 to AR-6 (architecture requirements) | Code audit revealed missing DAO/MVVM layer | Sprint 3 scope extended |
| 2026-06-27 | FR-1.7 status changed to Partial | `User.java` found empty in audit | BUG-002 opened |
| 2026-06-27 | FR-4.12 status confirmed as Planned | Files tab is placeholder | BUG-004 open |

---

## 8. Out of Scope (Current Release Cycle)

- iOS version
- Web / PWA version
- Backend admin API
- Monetization / ads (SDK present in libs but not activated)
- Multi-tenant / multi-organization management
