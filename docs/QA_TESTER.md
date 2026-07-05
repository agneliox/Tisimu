# Tisimu — QA Tester Guide

> Role: Quality Assurance | Last updated: 2026-06-27

---

## QA Philosophy

- Test the golden path first, then edge cases and error states
- Verify offline behavior for every feature that reads data
- Test on minimum (API 24) and maximum (API 36) supported API levels
- Check both light and dark theme for every new UI
- Verify role-based permissions (member vs. manager) for community features

---

## Test Device Matrix

| Device | API Level | Android | Status |
|---|---|---|---|
| Pixel 7 Pro (physical or emulator) | 34 | Android 14 | ✅ Primary |
| Samsung S22 (or emulator) | 33 | Android 13 | ✅ |
| OnePlus 9 (or emulator) | 31 | Android 12 | ✅ |
| Pixel 4a (or emulator) | 30 | Android 11 | ⚠️ Investigate |
| Samsung A51 (or emulator) | 29 | Android 10 | ✅ |
| Generic Emulator | 24 | Android 7.0 | ✅ Min SDK |

---

## Test Suites

### Suite 1 — Authentication

| TC# | Test Case | Steps | Expected | Pass/Fail |
|---|---|---|---|---|
| TC-1.1 | Email/Password Login | Enter valid credentials → Login | Home screen displayed | |
| TC-1.2 | Wrong Password | Enter wrong password → Login | Error message, stays on login | |
| TC-1.3 | Empty Fields | Tap Login with empty fields | Inline validation shown | |
| TC-1.4 | Google Sign-In | Tap Google Sign-In → select account | Home screen displayed | |
| TC-1.5 | Registration | Fill form → Register | Account created, auto-login | |
| TC-1.6 | Logout | Drawer → Logout | Login screen shown, session cleared | |
| TC-1.7 | Session Persistence | Login → close app → reopen | Still logged in | |

---

### Suite 2 — Hymnal

| TC# | Test Case | Steps | Expected | Pass/Fail |
|---|---|---|---|---|
| TC-2.1 | Hymnal Selection Screen | Launch app (fresh install) → select hymnal | Hymnal downloads and opens | |
| TC-2.2 | Song List | Open hymnal | Songs listed by number | |
| TC-2.3 | Search by Number | Type "150" in search | Song 150 shown | |
| TC-2.4 | Search by Title | Type song title | Matching songs shown | |
| TC-2.5 | Search by Lyrics | Type lyrics fragment | Matching songs shown | |
| TC-2.6 | Song Detail | Tap a song | Lyrics displayed with verses/chorus | |
| TC-2.7 | Copy Verse | Long-press or tap copy icon on verse | Verse copied to clipboard | |
| TC-2.8 | Share Lyrics | Tap share on song | Share sheet shown | |
| TC-2.9 | Offline Reading | Download hymnal → disable WiFi → open song | Lyrics readable offline | |
| TC-2.10 | Switch Hymnal | Tap hymnal switch icon | Hymnal selection shown | |
| TC-2.11 | Section Navigation | Tap section header | Songs in section shown | |

---

### Suite 3 — Community

| TC# | Test Case | Steps | Expected | Pass/Fail |
|---|---|---|---|---|
| TC-3.1 | View Communities | Tap Community tab | List of joined + public communities | |
| TC-3.2 | Create Community (Public) | Tap + → fill name/desc → Public → Create | Community appears in list | |
| TC-3.3 | Create Community (Private) | Tap + → fill → Private → Create | Community with join code created | |
| TC-3.4 | Join Public Community | Browse → Join public community | Community added to My Communities | |
| TC-3.5 | Join Private with Code | Enter join code → Join | Joined successfully | |
| TC-3.6 | Join Private with Wrong Code | Enter wrong code → Join | Error message shown | |
| TC-3.7 | Leave Community | Long-press or menu → Leave | Community removed from list | |
| TC-3.8 | View Announcements | Open community → Announcements tab | Announcements listed newest first | |
| TC-3.9 | Add Announcement (Manager) | Login as manager → Add announcement | Announcement appears in real-time | |
| TC-3.10 | Add Announcement (Member) | Login as member → Try to add | Permission denied message | |
| TC-3.11 | Delete Announcement (Manager) | Manager → swipe/delete announcement | Announcement removed | |
| TC-3.12 | View Agenda | Open community → Agenda tab | Agenda items listed | |
| TC-3.13 | Add Agenda Item (Manager) | Manager → Add agenda | Item appears in real-time | |
| TC-3.14 | Delete Agenda Item (Manager) | Manager → Delete agenda item | Item removed | |
| TC-3.15 | View Members | Open community → Members tab | Member list shown | |
| TC-3.16 | Promote Member (Manager) | Manager → tap member → Promote | Member becomes manager | |
| TC-3.17 | Real-Time Sync | Two devices in same community → one posts announcement | Other device sees it immediately | |

---

### Suite 4 — Home & Daily Verse

| TC# | Test Case | Steps | Expected | Pass/Fail |
|---|---|---|---|---|
| TC-4.1 | Home Screen | Navigate to Home | Daily verse and user greeting shown | |
| TC-4.2 | Daily Verse Rotation | Open app on different days | Different verse shown per day | |
| TC-4.3 | Verse Display | View daily verse | Full devotional text readable | |

---

### Suite 5 — Profile & Settings

| TC# | Test Case | Steps | Expected | Pass/Fail |
|---|---|---|---|---|
| TC-5.1 | Profile Screen | Tap Profile tab | User info shown | |
| TC-5.2 | Edit Profile | Tap Edit → change name → Save | Name updated | |
| TC-5.3 | Change Password | Settings → Change password | Password changed (email/pass accounts) | |
| TC-5.4 | Language Change | Settings → Language → select | App restarts in selected language | |
| TC-5.5 | Dark Mode | Settings → Dark Mode | Theme switches correctly | |
| TC-5.6 | About Tisimu | Drawer → About | About dialog shown | |
| TC-5.7 | Share App | Drawer → Share | Share sheet opens | |

---

### Suite 6 — Offline & Edge Cases

| TC# | Test Case | Steps | Expected | Pass/Fail |
|---|---|---|---|---|
| TC-6.1 | No Internet on Launch | Disable internet → launch | Offline hymnal readable; Firestore shows cached data | |
| TC-6.2 | Community Offline | In community → disable internet → view announcements | Last fetched data shown; no crash | |
| TC-6.3 | Restore Connection | Go offline → come back online | Data syncs automatically | |
| TC-6.4 | Empty Community | Create new community → view all tabs | Empty states shown, no crash | |
| TC-6.5 | Long Hymnal (640 songs) | Open Harpa Cristã (640 songs) | List scrolls smoothly, no ANR | |
| TC-6.6 | Back Navigation | Deep navigation → back press | Returns correctly to previous screen | |
| TC-6.7 | Rotation | Rotate device mid-flow | State preserved, no crash | |

---

## Regression Checklist (Before Each Release)

Run after every build before distribution:

- [ ] Login and logout flow
- [ ] Hymnal downloads and opens offline
- [ ] Search returns results
- [ ] Community list loads
- [ ] Announcements load in real-time
- [ ] No ANR or crash in first 60 seconds of use
- [ ] Dark and light themes render correctly
- [ ] Back navigation works from all screens

---

## Performance Benchmarks

| Metric | Target | Current |
|---|---|---|
| Cold launch time | < 2s | ~1.2s ✅ |
| App size | < 15MB | ~8.2MB ✅ |
| RAM (idle) | < 100MB | 45–60MB ✅ |
| Search response | < 300ms | < 200ms ✅ |
| Firestore first load | < 3s | TBD |

---

## How to Report a Bug Found During QA

1. Open `BUG_TRACKER.md`
2. Add a new entry using the template at the bottom of that file
3. Set status to `Open`, assign priority, note the TC# that surfaced it
4. Update this file's test case row with `Fail — see BUG-XXX`
