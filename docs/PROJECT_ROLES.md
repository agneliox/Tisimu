# Tisimu — Project Roles & Responsibilities

> Last updated: 2026-06-27

---

## Role Overview

| Role | Holder | Primary Document |
|---|---|---|
| Product Owner | Agnelio Xavier | `REQUIREMENTS.md`, `ROADMAP.md` |
| Developer | Agnelio Xavier | Source code under `app/src/` |
| Scrum Master | Claude (AI) | `SCRUM_MASTER.md` |
| QA Tester | Claude (AI) / Agnelio Xavier | `QA_TESTER.md` |
| Bug Manager | Claude (AI) / Agnelio Xavier | `BUG_TRACKER.md` |
| Architect | Agnelio Xavier (guided by Claude) | `ENTITY_MODEL.md`, `REQUIREMENTS.md` |

---

## Product Owner

**Responsibilities:**
- Define and prioritize the product backlog
- Write and refine user stories and acceptance criteria
- Make go/no-go decisions for each release
- Approve feature scope changes
- Own `REQUIREMENTS.md` — update when scope changes

**Current Focus:** Closing P0 bugs (BUG-002, BUG-003) before any new feature work

---

## Developer

**Responsibilities:**
- Implement features per sprint plan in `SPRINT.md`
- Follow MVVM + Repository architecture pattern
- Write unit and instrumented tests for new code
- Keep code clean: no direct Firestore calls from UI layer
- Update `BUG_TRACKER.md` when a fix is applied (change status to `Fixed`)
- Update `ENTITY_MODEL.md` when adding new entities or fields

**Coding Standards:**
- Language: Java 17
- UI: Material Design 3 components only
- Architecture: ViewModel → Repository → Service/DAO
- No hardcoded strings — use `strings.xml`
- No magic numbers — use constants
- Null safety: always null-check Firebase user before Firestore calls
- Offline-first: cache data in Room before displaying

---

## Scrum Master (Claude AI)

**Responsibilities:**
- Maintain `SCRUM_MASTER.md` — backlog, blockers, working agreements
- Maintain `SPRINT.md` — sprint tasks, estimates, status
- Surface blockers and risks proactively
- Ensure Definition of Done is applied consistently
- Conduct retrospective analysis after each sprint
- Flag any requirement changes and update `REQUIREMENTS.md`

**How to engage:** In any conversation, ask Claude to:
- Review sprint status
- Groom the backlog
- Plan the next sprint
- Identify blockers in the current code

---

## QA Tester (Claude AI / Agnelio Xavier)

**Responsibilities:**
- Maintain `QA_TESTER.md` — test cases, test matrix, regression checklist
- Execute full test suite before each release
- Report new bugs to `BUG_TRACKER.md`
- Verify fixes (change bug status from `Fixed` to `Verified`)
- Update performance benchmarks after optimization work

**Test Cadence:**
- Smoke test: on every build
- Full regression: before each Firebase App Distribution push
- Performance test: before each public release

---

## Bug Manager (Claude AI / Agnelio Xavier)

**Responsibilities:**
- Maintain `BUG_TRACKER.md`
- Ensure all reported issues are logged with correct priority
- Review and re-prioritize bugs weekly
- Close verified bugs after QA confirmation
- Track recurring issues for root cause analysis

---

## Architect

**Responsibilities:**
- Maintain `ENTITY_MODEL.md` — all data models, relationships, storage strategy
- Enforce MVVM + Repository pattern in code reviews
- Evaluate new library additions (check size impact, licence, maintenance)
- Document architecture decisions in `REQUIREMENTS.md` (Section 5)

**Current Architecture Goals:**
1. Complete MVVM migration by v1.2
2. Room DAO layer complete by Sprint 3 end
3. Repository pattern enforced before Sprint 4

---

## Escalation Path

| Issue | Who to Flag |
|---|---|
| Scope change request | Product Owner → update REQUIREMENTS.md |
| P0 bug during sprint | Developer + Scrum Master → pause sprint if needed |
| Architecture concern | Architect → document in REQUIREMENTS.md Section 7 |
| Release decision | Product Owner final call |
