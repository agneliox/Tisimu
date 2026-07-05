# Tisimu — Entity Model

> Last updated: 2026-06-27 | Version: 1.1.9

---

## Storage Strategy

| Layer | Technology | Used For |
|---|---|---|
| Local (offline) | Room (SQLite) | Hymnal content, songs, sections, comments, suggestions, melody proposals, user hymnal selection |
| Remote (realtime) | Firebase Firestore | Communities, announcements, agenda, members |
| Authentication | Firebase Auth | User identity (email/password + Google Sign-In) |
| File storage | Firebase Storage | Community covers, audio files (planned) |
| JSON CDN | GitHub raw content | Hymnal manifests and hymnal data files |

---

## Room Entities (Local Database)

### Hymnal
```
Table: hymnals
───────────────────────────────────────────────────────
Field            Type       Notes
───────────────────────────────────────────────────────
id               int        PK, auto-generated
name             String
description      String
language         String     e.g. "Portuguese", "Tswa"
totalSongs       int
coverImageUrl    String
isActive         boolean
isCollaborative  boolean
isSelected       boolean    User's active hymnal flag
───────────────────────────────────────────────────────
```

### Song
```
Table: songs
───────────────────────────────────────────────────────
Field       Type      Notes
───────────────────────────────────────────────────────
id          int       PK, auto-generated
hymnalId    int       FK → hymnals.id
sectionId   int       FK → sections.id
number      int       Hymn number
title       String
lyrics      String    Full plain-text lyrics
author      String
composer    String
isOfficial  boolean   true = curated, false = user-contributed
userId      Integer   null if official hymn
createdAt   String
───────────────────────────────────────────────────────
```

### Section
```
Table: sections
───────────────────────────────────────────────────────
Field        Type    Notes
───────────────────────────────────────────────────────
id           int     PK, auto-generated
hymnalId     int     FK → hymnals.id
sequence     int
name         String  e.g. "Praise & Worship"
startNumber  int
endNumber    int
───────────────────────────────────────────────────────
```

### Comment
```
Table: comments
───────────────────────────────────────────────────────
Field           Type      Notes
───────────────────────────────────────────────────────
id              int       PK, auto-generated
songId          int       FK → songs.id
userId          String    Firebase UID
userName        String
text            String
parentCommentId Integer   null = top-level comment; else = reply
createdAt       long      System.currentTimeMillis()
likesCount      int
───────────────────────────────────────────────────────
```

### MelodyProposal
```
Table: melody_proposals
───────────────────────────────────────────────────────
Field       Type    Notes
───────────────────────────────────────────────────────
id          int     PK, auto-generated
songId      int     FK → songs.id
userId      String
userName    String
type        String  "audio" | "video"
url         String  YouTube URL or audio file path
title       String
description String
createdAt   long
likesCount  int
───────────────────────────────────────────────────────
```

### Suggestion
```
Table: suggestions
───────────────────────────────────────────────────────
Field         Type    Notes
───────────────────────────────────────────────────────
id            int     PK, auto-generated
songId        int     FK → songs.id
userId        String
userName      String
verseNumber   int
currentText   String
suggestedText String
justification String
status        String  "pending" | "approved" | "rejected"
createdAt     long
───────────────────────────────────────────────────────
```

### UserHymnalSelection
```
Table: user_hymnal_selections
───────────────────────────────────────────────────────
Field      Type    Notes
───────────────────────────────────────────────────────
id         int     PK, auto-generated
hymnalId   int     FK → hymnals.id
userId     String  Firebase UID
isActive   boolean
───────────────────────────────────────────────────────
```

---

## Firestore Documents (Remote)

### Community `/communities/{communityId}`
```
Field              Type      Notes
───────────────────────────────────────────────────────
id                 String    UUID
name               String
description        String
createdBy          String    Firebase UID
createdByUserName  String
createdAt          Timestamp @ServerTimestamp
isPrivate          boolean
joinCode           String    null if public
coverImageUrl      String    (planned)
memberCount        int       denormalized count
```
**Sub-collections:**
- `/communities/{id}/members` → CommunityMember
- `/communities/{id}/announcements` → Announcement
- `/communities/{id}/agenda` → AgendaItem
- `/communities/{id}/files` → CommunityFile (placeholder)

### CommunityMember `/communities/{communityId}/members/{userId}`
```
Field           Type      Notes
───────────────────────────────────────────────────────
userId          String    Firebase UID
userName        String
userEmail       String
role            String    "member" | "manager"
joinedAt        Timestamp @ServerTimestamp
profileImageUrl String
```

### Announcement `/communities/{communityId}/announcements/{id}`
```
Field              Type      Notes
───────────────────────────────────────────────────────
id                 String    Firestore doc ID
communityId        String
title              String
content            String
createdBy          String    Firebase UID
createdByUserName  String
createdAt          Timestamp @ServerTimestamp
isImportant        boolean
```

### AgendaItem `/communities/{communityId}/agenda/{id}`
```
Field              Type      Notes
───────────────────────────────────────────────────────
id                 String    Firestore doc ID
communityId        String
title              String
content            String
createdBy          String    Firebase UID
createdByUserName  String
createdAt          Timestamp @ServerTimestamp
date               String    "yyyy-MM-dd"
```

### UserCommunities `/user_communities/{userId}/communities/{communityId}`
```
Field         Type      Notes
───────────────────────────────────────────────────────
communityId   String
communityName String
role          String    "member" | "manager"
joinedAt      Timestamp @ServerTimestamp
```

---

## JSON CDN Models (Hymnal Files)

### HymnalManifest
```json
{
  "version": 1,
  "lastUpdated": "2024-01-15T10:00:00Z",
  "hymnals": [ HymnalData... ]
}
```

### HymnalData
```json
{
  "id": "harpa_crista",
  "name": "Harpa Cristã",
  "description": "...",
  "language": "Portuguese",
  "totalSongs": 640,
  "fileUrl": "...",
  "coverUrl": "...",
  "fileSize": 2450000,
  "version": 1,
  "author": "Various Authors"
}
```

### SongItem (inside hymnal JSON)
```json
{
  "number": 1,
  "title": "Grandioso És Tu",
  "author": "Carl Boberg",
  "composer": "Traditional",
  "verses": [
    { "type": "verse", "number": 1, "label": "1", "lines": ["..."] },
    { "type": "chorus", "label": "Coro", "lines": ["..."] }
  ]
}
```

---

## Relationships Diagram

```
Hymnal ──────< Section
Hymnal ──────< Song ────────< Comment
                   └──────< MelodyProposal
                   └──────< Suggestion
                   
Community ───< CommunityMember
          ───< Announcement
          ───< AgendaItem
          ───< CommunityFile (planned)

User (Firebase Auth) ──── UserHymnalSelection ──── Hymnal
                     ──── CommunityMember (via Firestore)
```

---

## Planned Entities (Future Sprints)

| Entity | Storage | Purpose |
|---|---|---|
| Favorite | Room | User-bookmarked songs |
| RecentSong | Room | Last viewed songs list |
| UserNote | Room | Personal annotations per song |
| PushToken | Firestore | FCM token for push notifications |
| CommunityFile | Firestore + Storage | Shared PDF/audio files per community |
