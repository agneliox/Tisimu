# Tisimu — Scrum Master Guide

> Role Owner: Claude (AI Scrum Master) | Developer: Agnelio Xavier
> Last updated: 2026-06-27

---

## Project Identity

| Item | Value |
|---|---|
| App Name | Tisimu |
| Platform | Android (Java) |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 36 |
| Current Version | 1.1.9 (versionCode 9) |
| Stage | Closed Beta |
| Repository | github.com/agneliox/Tisimu |

---

## Team Roles

| Role | Person / Tool | Responsibilities |
|---|---|---|
| Product Owner | Agnelio Xavier | Feature prioritization, user stories, release decisions |
| Developer | Agnelio Xavier | All coding, architecture, Firebase setup |
| Scrum Master | Claude (AI) | Sprint planning, backlog grooming, blocker surfacing, docs maintenance |
| QA Tester | See `QA_TESTER.md` | Test cases, acceptance criteria, regression testing |
| Bug Tracker | See `BUG_TRACKER.md` | Issue logging, priority, status |

---

## Working Agreements

1. **Sprint length:** 2 weeks
2. **Definition of Done:** Feature coded → tested on at least 2 API levels → no regressions in core flows → `BUG_TRACKER.md` updated → `SPRINT.md` task marked `Done`
3. **Design pattern:** MVVM with Repository — all new screens must use ViewModel + LiveData
4. **UI framework:** Material Design 3 only — no custom themes that diverge from MD3
5. **Architecture rule:** No direct Firestore calls from Activities or Fragments — route through ViewModel → Repository → Service
6. **Documentation rule:** Any new entity or feature change must update `ENTITY_MODEL.md` and `REQUIREMENTS.md`
7. **Code language:** Java (Kotlin allowed only for Gradle scripts)
8. **Offline-first:** Every feature that reads data must gracefully degrade when offline

---

## Sprint Ceremonies

| Ceremony | Cadence | Duration | Purpose |
|---|---|---|---|
| Sprint Planning | Start of sprint | 1–2 hrs | Pick backlog items, assign story points |
| Daily Check-in | Daily (async OK) | 15 min | What was done, what's next, any blockers |
| Sprint Review | End of sprint | 1 hr | Demo completed features |
| Retrospective | End of sprint | 30 min | What worked, what to improve |
| Backlog Grooming | Mid-sprint | 1 hr | Refine upcoming items, re-prioritize |

---

## Backlog Prioritization Framework

Use MoSCoW:

| Priority | Label | Meaning |
|---|---|---|
| P0 | Must Have | Blocking release or critical bug |
| P1 | Should Have | Core feature for next sprint |
| P2 | Could Have | Nice-to-have, schedule if capacity allows |
| P3 | Won't Have Now | Parking lot for future sprints |

---

## Current Sprint Reference

→ See `SPRINT.md` for active sprint tasks

---

## Backlog (Prioritized)

### P0 — Critical / Release Blockers
- [ ] Implement `User.java` model — currently an empty class (Firestore `/users/{uid}`, not Room)
- [ ] Files tab: implement or hide — currently a broken placeholder
- [x] Fix Google Sign-In error code 10 (closed in v1.1.9)
- [x] MD3 design enforcement — layout/color/typography upgrade (done 2026-06-27)

> **Architecture decision (2026-06-27):** Room DAO layer will NOT be built. All persistence goes through Firebase (Firestore + Storage). The Room `@Entity` classes remain as Firestore POJO helpers but no `@Database` or DAO is needed.

### P1 — Sprint 3 Candidates
- [ ] Full MVVM migration: move Firestore calls out of Activities into ViewModels
- [ ] Repository pattern layer between ViewModel and Service classes
- [ ] Favorites system (bookmark songs)
- [ ] Push notifications (FCM integration)
- [ ] Community cover image upload (Firebase Storage)

### P2 — Sprint 4+ Candidates
- [ ] Font size adjustment (accessibility)
- [ ] Recent songs history (Room)
- [ ] Personal notes per song (Room)
- [ ] Song index / quick-jump by hymn number
- [ ] Export/import favorites

### P3 — Future Vision
- [ ] Audio playback (ExoPlayer — library already in deps, not wired)
- [ ] AI-powered lyrics search (Firebase AI dep already included)
- [ ] iOS port (Kotlin Multiplatform)
- [ ] Progressive Web App
- [ ] Admin dashboard / backend API

---

## Blocker Log

| Date | Blocker | Impact | Resolution |
|---|---|---|---|
| 2026-06-27 | `User.java` is empty — no user profile model | Profile screen non-functional | P0 — Sprint 3 |
| 2026-06-27 | Room DAO missing — entities defined but not queryable | Comments/Suggestions/Melodies not persisted | P0 — Sprint 3 |
| 2026-06-27 | Files tab is a placeholder | Community Files feature broken | P1 — Sprint 3 |

---

## Scrum Master Actions (2026-06-27 Review)

1. Created all project documentation (`docs/` folder)
2. Audited code against roadmap — identified gaps between defined entities and missing DAO layer
3. Classified blockers by priority
4. Established Definition of Done and working agreements
5. Set Sprint 3 scope (see `SPRINT.md`)

---

## How to Update This File

After each sprint: update the Blocker Log, move completed P0/P1 items to Done in `SPRINT.md`, and re-prioritize the backlog. Capture any requirement changes in `REQUIREMENTS.md`.
