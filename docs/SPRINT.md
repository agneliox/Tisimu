# Tisimu — Sprint Board

> Last updated: 2026-06-27

---

## Sprint 3 — Stability & Architecture Foundation
**Dates:** 2026-06-30 → 2026-07-13
**Goal:** Resolve all P0 blockers (User model, Room DAO layer, MVVM migration start) and fix the Files tab

---

### Sprint 3 Tasks

| # | Task | Priority | Estimate | Status | Notes |
|---|---|---|---|---|---|
| S3-01 | Implement `User.java` model with fields and Firestore document | P0 | 1 day | To Do | See BUG-002 |
| S3-02 | Create Firestore `/users` collection + `UserFirestoreManager` | P0 | 1 day | To Do | No Room — Firebase only |
| S3-03 | ~~TisimuDatabase (Room)~~ — CANCELLED | — | — | Cancelled | Firebase is the sole DB |
| S3-04 | ~~DAO interfaces~~ — CANCELLED | — | — | Cancelled | Firebase is the sole DB |
| S3-05 | ~~DAO interfaces~~ — CANCELLED | — | — | Cancelled | Firebase is the sole DB |
| S3-06 | Create `HymnalRepository` wrapping `HymnalStorageManager` | P1 | 1 day | To Do | No DAO layer |
| S3-07 | Create `HymnalViewModel`, `SongViewModel` | P1 | 1 day | To Do | Wire to Repository |
| S3-08 | Migrate `HymnalFragment` to use ViewModel | P1 | 0.5 day | To Do | |
| S3-MD | MD3 design upgrade — layout, color, typography | P0 | 1 day | Done ✅ | Completed 2026-06-27 |
| S3-09 | Implement `FilesFragment` with Firebase Storage upload | P1 | 2 days | To Do | See BUG-004 |
| S3-10 | Fix `isNavigating` dead code in `MainActivity` | P3 | 0.5 day | To Do | See BUG-008 |
| S3-11 | Update `ProfileActivity` to use real User model | P1 | 1 day | To Do | Depends on S3-01 |
| S3-12 | QA: Run full test suite from QA_TESTER.md | P0 | 1 day | To Do | All Suites 1–6 |

**Sprint 3 Estimated Capacity:** 10 working days (1 developer)
**Sprint 3 Estimated Load:** ~11 days — may need to defer S3-09 to Sprint 4 if capacity is tight

---

### Sprint 3 Definition of Done

- [ ] `User.java` has all profile fields and round-trips with Firestore
- [ ] Room database initializes without error on app start
- [ ] All DAO interfaces compile and pass unit tests
- [ ] `HymnalFragment` uses `HymnalViewModel` — no direct service calls
- [ ] Files tab shows upload button for managers and lists files
- [ ] QA Suite 1 and Suite 2 pass on API 34 and API 24
- [ ] `BUG_TRACKER.md` updated (BUG-002, BUG-003, BUG-008 moved to Fixed or Closed)

---

## Sprint 4 — Personalization Features
**Dates:** 2026-07-14 → 2026-07-27
**Goal:** Favorites, Recent Songs, Personal Notes, Font Size

| # | Task | Priority | Estimate | Status |
|---|---|---|---|---|
| S4-01 | Favorites entity + DAO + Repository | P1 | 1 day | To Do |
| S4-02 | Favorite button in SongDetailActivity | P1 | 0.5 day | To Do |
| S4-03 | Favorites list screen / fragment | P1 | 1 day | To Do |
| S4-04 | Recent songs tracking (Room) | P1 | 1 day | To Do |
| S4-05 | Recent songs list on Home screen | P1 | 0.5 day | To Do |
| S4-06 | Personal notes per song (Room) | P2 | 1 day | To Do |
| S4-07 | Font size preference (3 sizes) | P2 | 0.5 day | To Do |
| S4-08 | Song quick-jump by number | P2 | 1 day | To Do |
| S4-09 | Community cover image upload | P2 | 1 day | To Do |
| S4-10 | QA regression + new feature testing | P0 | 1 day | To Do |

---

## Sprint 5 — Push Notifications & Community Expansion
**Dates:** 2026-07-28 → 2026-08-10
**Goal:** FCM integration, community search, agenda improvements

| # | Task | Priority | Estimate | Status |
|---|---|---|---|---|
| S5-01 | Add Firebase Messaging dependency | P1 | 0.5 day | To Do |
| S5-02 | `TisimuFirebaseMessagingService` | P1 | 1 day | To Do |
| S5-03 | FCM token storage in Firestore `/users/{uid}` | P1 | 0.5 day | To Do |
| S5-04 | Cloud Function: new announcement → push | P1 | 1.5 days | To Do |
| S5-05 | Community search by name/topic | P2 | 1 day | To Do |
| S5-06 | Agenda date/time picker | P2 | 0.5 day | To Do |
| S5-07 | QA: notification test on physical device | P0 | 0.5 day | To Do |

---

## Completed Sprints

### Sprint 1 — Initial Release (v1.0.0)
**Dates:** (before 2026) | **Status:** Done
- Firebase Auth (email/password)
- Hymnal download from GitHub CDN
- Offline JSON file storage
- Song list + search
- Song detail with lyrics/verses
- Share and copy lyrics

### Sprint 2 — Beta Release (v1.1.x)
**Dates:** (before 2026) | **Status:** Done
- Communities (create, join, leave)
- Announcements (CRUD)
- Agenda (CRUD)
- Members management + roles
- Google Sign-In fix (BUG-001 closed)
- Daily verse with devotionals
- Material Design 3 theming
- Dark/light mode
- Language selection

---

## Backlog (Unscheduled)

| Item | Priority | Sprint Target |
|---|---|---|
| Audio playback (ExoPlayer) | P2 | Sprint 6+ |
| Melody upload by community members | P2 | Sprint 6+ |
| AI-powered search | P3 | Sprint 9+ |
| Song correction workflow (Suggestion approve/reject) | P2 | Sprint 6 |
| Cloud sync (favorites across devices) | P3 | Sprint 8+ |
| High contrast / accessibility mode | P2 | Sprint 7 |
| Search history | P3 | Sprint 5 |
| Pixel 4a (API 30) compatibility fix (BUG-007) | P2 | Sprint 3 or 4 |
