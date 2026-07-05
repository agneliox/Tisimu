# Tisimu — Bug Tracker

> Last updated: 2026-06-27 | Managed by: Scrum Master / QA Tester

---

## Status Legend

| Status | Meaning |
|---|---|
| `Open` | Reported, not yet assigned |
| `In Progress` | Being actively fixed |
| `Fixed` | Fix applied, awaiting QA verification |
| `Verified` | QA confirmed fix, ready to close |
| `Closed` | Resolved and released |
| `Won't Fix` | Acknowledged, not going to fix |

## Priority Legend

| Priority | Meaning |
|---|---|
| P0 | Critical — app crash or data loss |
| P1 | High — core feature broken |
| P2 | Medium — feature degraded or UX issue |
| P3 | Low — cosmetic or minor inconvenience |

---

## Open Bugs

### BUG-001 — Google Sign-In Error Code 10
| Field | Value |
|---|---|
| **ID** | BUG-001 |
| **Priority** | P1 |
| **Status** | Closed (Fixed in v1.1.9) |
| **Component** | Authentication |
| **Version Found** | 1.1.8 |
| **Version Fixed** | 1.1.9 |
| **Reporter** | Agnelio Xavier |
| **Assigned To** | Agnelio Xavier |

**Description:**  
Google Sign-In failed with error code 10 (`DEVELOPER_ERROR`). Caused by missing or incorrect SHA-1/SHA-256 certificate fingerprints in Firebase project settings.

**Steps to Reproduce:**
1. Launch app
2. Tap "Sign in with Google"
3. Error dialog appears with code 10

**Fix Applied:**
- Updated `google-services.json` with correct SHA-1 fingerprint
- Updated Firebase project settings with matching certificate

**Notes:** Verify on both debug and release builds after any keystore change.

---

### BUG-002 — User.java Is Empty
| Field | Value |
|---|---|
| **ID** | BUG-002 |
| **Priority** | P0 |
| **Status** | Open |
| **Component** | User Model / Profile |
| **Version Found** | 1.1.9 |
| **Reporter** | Claude (code audit 2026-06-27) |
| **Assigned To** | Agnelio Xavier |

**Description:**  
`models/User.java` contains only a package declaration and empty class body. Any feature requiring a user profile object will fail at runtime or cannot be built.

**Impact:**
- Profile screen cannot display user data beyond what FirebaseAuth provides
- No local user preferences or profile fields stored
- Community member profile images cannot be linked

**Fix Required:**
- Define `User` fields: `uid`, `displayName`, `email`, `profileImageUrl`, `joinedAt`, `bio`
- Decide storage: Firebase Auth display name + Firestore document `/users/{uid}` (recommended)
- Create Firestore service method to fetch/update user profile

---

### BUG-003 — Room DAO Layer Missing
| Field | Value |
|---|---|
| **ID** | BUG-003 |
| **Priority** | P0 |
| **Status** | Open |
| **Component** | Local Database (Room) |
| **Version Found** | 1.1.9 |
| **Reporter** | Claude (code audit 2026-06-27) |
| **Assigned To** | Agnelio Xavier |

**Description:**  
Room `@Entity` classes exist (Song, Hymnal, Section, Comment, MelodyProposal, Suggestion, UserHymnalSelection) but no `@Dao` interfaces, `@Database` class, or Repository classes are present. Room is set up in `build.gradle.kts` with `annotationProcessor` but not wired into the app.

**Impact:**
- Comments cannot be persisted locally
- Song corrections (Suggestions) cannot be saved
- MelodyProposals cannot be stored
- Favorites (planned) have no storage layer

**Fix Required:**
- Create `TisimuDatabase.java` (Room `@Database`)
- Create DAO interfaces: `SongDao`, `HymnalDao`, `SectionDao`, `CommentDao`, `SuggestionDao`, `MelodyProposalDao`
- Create Repository classes for each domain
- Register database in `TisimuApplication.java`

---

### BUG-004 — Files Tab is a Non-Functional Placeholder
| Field | Value |
|---|---|
| **ID** | BUG-004 |
| **Priority** | P1 |
| **Status** | Open |
| **Component** | Community — Files |
| **Version Found** | 1.0.1 |
| **Reporter** | Release Notes |
| **Assigned To** | Agnelio Xavier |

**Description:**  
The Files tab inside Community Detail shows a placeholder fragment with no functionality. `CommunityFile.java` model exists but no upload/download logic is implemented.

**Fix Required:**
- Implement `CommunityFile` Firestore operations in `CommunityFirestoreManager`
- Add Firebase Storage upload/download for PDF and audio files
- Build `FilesFragment` with RecyclerView and upload FAB (manager-only)

---

### BUG-005 — Community Cover Images Not Supported
| Field | Value |
|---|---|
| **ID** | BUG-005 |
| **Priority** | P2 |
| **Status** | Open |
| **Component** | Community |
| **Version Found** | 1.0.1 |
| **Reporter** | Release Notes |
| **Assigned To** | Agnelio Xavier |

**Description:**  
`Community.coverImageUrl` field exists in the model but no upload mechanism or display logic is wired. Community cards show no cover image.

**Fix Required:**
- Add image picker to community creation / edit dialog
- Upload image to Firebase Storage on community create/update
- Load `coverImageUrl` with Glide in `CommunityAdapter`

---

### BUG-006 — No Push Notifications
| Field | Value |
|---|---|
| **ID** | BUG-006 |
| **Priority** | P2 |
| **Status** | Open |
| **Component** | Notifications |
| **Version Found** | All versions |
| **Reporter** | Release Notes |
| **Assigned To** | Agnelio Xavier |

**Description:**  
No FCM (Firebase Cloud Messaging) integration exists. Users receive no notifications for new announcements, agenda changes, or community activity.

**Fix Required:**
- Add Firebase Messaging dependency
- Implement `FirebaseMessagingService`
- Create Cloud Functions or backend trigger for new announcements → push

---

### BUG-007 — Pixel 4a (API 30) Warning in Test Matrix
| Field | Value |
|---|---|
| **ID** | BUG-007 |
| **Priority** | P2 |
| **Status** | Open |
| **Component** | Compatibility |
| **Version Found** | 1.0.1 |
| **Reporter** | Release Notes (⚠️ status) |
| **Assigned To** | Agnelio Xavier |

**Description:**  
Pixel 4a on Android 11 (API 30) shows ⚠️ in the test matrix without details. Root cause unknown.

**Fix Required:**
- Run instrumented tests on API 30 emulator
- Capture logcat output
- Identify and fix specific issue

---

### BUG-008 — `isNavigating` Flag Always False (Dead Code)
| Field | Value |
|---|---|
| **ID** | BUG-008 |
| **Priority** | P3 |
| **Status** | Open |
| **Component** | Navigation — MainActivity |
| **Version Found** | 1.1.9 |
| **Reporter** | Claude (code audit 2026-06-27) |
| **Assigned To** | Agnelio Xavier |

**Description:**  
In `MainActivity.java:35`, `isNavigating` is declared as `final boolean isNavigating = false`. It is checked in the destination changed listener but can never be `true`, making the guard condition dead code.

**Fix Required:**
- Either remove the guard or change `isNavigating` to a mutable field that is properly set during navigation transitions.

---

## Closed Bugs

| ID | Summary | Fixed In |
|---|---|---|
| BUG-001 | Google Sign-In error code 10 | v1.1.9 |
| — | Navigation loop after hymnal selection | v1.0.1 |
| — | Fragment detachment crashes | v1.0.1 |
| — | Menu item responsiveness | v1.0.1 |
| — | Language persistence issues | v1.0.1 |

---

## How to Add a Bug

Copy this template and increment the ID:

```
### BUG-XXX — Short Title
| Field | Value |
|---|---|
| **ID** | BUG-XXX |
| **Priority** | P0/P1/P2/P3 |
| **Status** | Open |
| **Component** | |
| **Version Found** | |
| **Reporter** | |
| **Assigned To** | |

**Description:**

**Steps to Reproduce:**

**Expected:** 
**Actual:** 

**Fix Required:**
```
