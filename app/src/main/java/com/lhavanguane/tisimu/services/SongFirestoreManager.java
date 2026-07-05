package com.lhavanguane.tisimu.services;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lhavanguane.tisimu.models.SongComment;
import com.lhavanguane.tisimu.models.SongFirestore;
import com.lhavanguane.tisimu.models.SongMelody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongFirestoreManager {

    private static final String TAG = "SongFirestore";
    private static final String COLLECTION_SONGS = "songs";
    private static final String COLLECTION_LIKES = "likes";
    private static final String COLLECTION_COMMENTS = "comments";
    private static final String COLLECTION_MELODIES = "melodies";

    private static SongFirestoreManager instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;

    private SongFirestoreManager() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public static synchronized SongFirestoreManager getInstance() {
        if (instance == null) {
            instance = new SongFirestoreManager();
        }
        return instance;
    }

    private String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    private String getCurrentUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                return user.getDisplayName();
            }
            String email = user.getEmail();
            if (email != null) {
                return email.split("@")[0];
            }
        }
        return "User";
    }

    // ── Static helpers ───────────────────────────────────────────────

    public static String buildHymnalSongId(String hymnalId, int songNumber) {
        return hymnalId + "_" + songNumber;
    }

    // ── Callback interfaces ──────────────────────────────────────��───

    public interface VoidCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface BooleanCallback {
        void onResult(boolean result);
    }

    public interface SongCallback {
        void onSuccess(SongFirestore song);
        void onFailure(Exception e);
    }

    public interface SongsCallback {
        void onSuccess(List<SongFirestore> songs);
        void onFailure(Exception e);
    }

    public interface CommentsCallback {
        void onSuccess(List<SongComment> comments);
        void onFailure(Exception e);
    }

    public interface MelodiesCallback {
        void onSuccess(List<SongMelody> melodies);
        void onFailure(Exception e);
    }

    // ── Song document (lazy init) ────────────────────────────────────

    public void ensureSongDocument(String songId, String title, boolean isCommunitySong,
                                   VoidCallback callback) {
        DocumentReference ref = db.collection(COLLECTION_SONGS).document(songId);
        ref.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                callback.onSuccess();
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("id", songId);
                data.put("title", title != null ? title : "");
                data.put("isCommunitySong", isCommunitySong);
                data.put("likesCount", 0);
                data.put("commentCount", 0);
                data.put("createdAt", FieldValue.serverTimestamp());
                ref.set(data)
                        .addOnSuccessListener(v -> callback.onSuccess())
                        .addOnFailureListener(callback::onFailure);
            }
        }).addOnFailureListener(callback::onFailure);
    }

    // ── Song real-time listener ──────────────────────────────────────

    public ListenerRegistration listenToSong(String songId, SongCallback callback) {
        return db.collection(COLLECTION_SONGS).document(songId)
                .addSnapshotListener((doc, e) -> {
                    if (e != null) { callback.onFailure(e); return; }
                    if (doc != null && doc.exists()) {
                        SongFirestore song = doc.toObject(SongFirestore.class);
                        if (song != null) {
                            song.setId(doc.getId());
                            callback.onSuccess(song);
                        }
                    }
                });
    }

    // ── Song likes ───────────────────────────────────────────────────

    public void toggleSongLike(String songId, VoidCallback callback) {
        String uid = getCurrentUserId();
        if (uid == null) { callback.onFailure(new Exception("Not signed in")); return; }
        DocumentReference likeRef = db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_LIKES).document(uid);
        DocumentReference songRef = db.collection(COLLECTION_SONGS).document(songId);
        likeRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                likeRef.delete().addOnSuccessListener(v ->
                        songRef.update("likesCount", FieldValue.increment(-1))
                                .addOnSuccessListener(v2 -> callback.onSuccess())
                                .addOnFailureListener(callback::onFailure))
                        .addOnFailureListener(callback::onFailure);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("userId", uid);
                data.put("likedAt", FieldValue.serverTimestamp());
                likeRef.set(data).addOnSuccessListener(v ->
                        songRef.update("likesCount", FieldValue.increment(1))
                                .addOnSuccessListener(v2 -> callback.onSuccess())
                                .addOnFailureListener(callback::onFailure))
                        .addOnFailureListener(callback::onFailure);
            }
        }).addOnFailureListener(callback::onFailure);
    }

    public void isCurrentUserLikedSong(String songId, BooleanCallback callback) {
        String uid = getCurrentUserId();
        if (uid == null) { callback.onResult(false); return; }
        db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_LIKES).document(uid)
                .get()
                .addOnSuccessListener(doc -> callback.onResult(doc.exists()))
                .addOnFailureListener(e -> callback.onResult(false));
    }

    // ── Comments ─────────────────────────────────────────────────────

    public ListenerRegistration listenToComments(String songId, CommentsCallback callback) {
        return db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_COMMENTS)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) { callback.onFailure(e); return; }
                    List<SongComment> comments = new ArrayList<>();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            SongComment c = doc.toObject(SongComment.class);
                            c.setId(doc.getId());
                            comments.add(c);
                        }
                    }
                    callback.onSuccess(comments);
                });
    }

    public void addComment(String songId, String text,
                           @Nullable String parentCommentId, VoidCallback callback) {
        String uid = getCurrentUserId();
        if (uid == null) { callback.onFailure(new Exception("Not signed in")); return; }
        DocumentReference ref = db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_COMMENTS).document();
        Map<String, Object> data = new HashMap<>();
        data.put("id", ref.getId());
        data.put("userId", uid);
        data.put("userName", getCurrentUserName());
        data.put("text", text);
        data.put("parentCommentId", parentCommentId);
        data.put("likesCount", 0);
        data.put("createdAt", FieldValue.serverTimestamp());
        ref.set(data).addOnSuccessListener(v -> {
            // Only increment top-level comment count
            if (parentCommentId == null) {
                db.collection(COLLECTION_SONGS).document(songId)
                        .update("commentCount", FieldValue.increment(1));
            }
            callback.onSuccess();
        }).addOnFailureListener(callback::onFailure);
    }

    public void deleteComment(String songId, String commentId,
                              boolean isTopLevel, VoidCallback callback) {
        db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_COMMENTS).document(commentId)
                .delete().addOnSuccessListener(v -> {
                    if (isTopLevel) {
                        db.collection(COLLECTION_SONGS).document(songId)
                                .update("commentCount", FieldValue.increment(-1));
                    }
                    callback.onSuccess();
                }).addOnFailureListener(callback::onFailure);
    }

    public void toggleCommentLike(String songId, String commentId, VoidCallback callback) {
        String uid = getCurrentUserId();
        if (uid == null) { callback.onFailure(new Exception("Not signed in")); return; }
        DocumentReference likeRef = db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_COMMENTS).document(commentId)
                .collection(COLLECTION_LIKES).document(uid);
        DocumentReference commentRef = db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_COMMENTS).document(commentId);
        likeRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                likeRef.delete().addOnSuccessListener(v ->
                        commentRef.update("likesCount", FieldValue.increment(-1))
                                .addOnSuccessListener(v2 -> callback.onSuccess())
                                .addOnFailureListener(callback::onFailure))
                        .addOnFailureListener(callback::onFailure);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("userId", uid);
                data.put("likedAt", FieldValue.serverTimestamp());
                likeRef.set(data).addOnSuccessListener(v ->
                        commentRef.update("likesCount", FieldValue.increment(1))
                                .addOnSuccessListener(v2 -> callback.onSuccess())
                                .addOnFailureListener(callback::onFailure))
                        .addOnFailureListener(callback::onFailure);
            }
        }).addOnFailureListener(callback::onFailure);
    }

    public void isCurrentUserLikedComment(String songId, String commentId,
                                          BooleanCallback callback) {
        String uid = getCurrentUserId();
        if (uid == null) { callback.onResult(false); return; }
        db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_COMMENTS).document(commentId)
                .collection(COLLECTION_LIKES).document(uid)
                .get()
                .addOnSuccessListener(doc -> callback.onResult(doc.exists()))
                .addOnFailureListener(e -> callback.onResult(false));
    }

    // ── Melodies ─────────────────────────────────────────────────────

    public ListenerRegistration listenToMelodies(String songId, MelodiesCallback callback) {
        return db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_MELODIES)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) { callback.onFailure(e); return; }
                    List<SongMelody> melodies = new ArrayList<>();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            SongMelody m = doc.toObject(SongMelody.class);
                            m.setId(doc.getId());
                            melodies.add(m);
                        }
                    }
                    callback.onSuccess(melodies);
                });
    }

    public void addMelody(String songId, String title, String url, String type,
                          @Nullable String description, VoidCallback callback) {
        String uid = getCurrentUserId();
        if (uid == null) { callback.onFailure(new Exception("Not signed in")); return; }
        DocumentReference ref = db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_MELODIES).document();
        Map<String, Object> data = new HashMap<>();
        data.put("id", ref.getId());
        data.put("userId", uid);
        data.put("userName", getCurrentUserName());
        data.put("type", type);
        data.put("url", url);
        data.put("title", title);
        data.put("description", description);
        data.put("likesCount", 0);
        data.put("createdAt", FieldValue.serverTimestamp());
        ref.set(data)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void deleteMelody(String songId, String melodyId, VoidCallback callback) {
        db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_MELODIES).document(melodyId)
                .delete()
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void toggleMelodyLike(String songId, String melodyId, VoidCallback callback) {
        String uid = getCurrentUserId();
        if (uid == null) { callback.onFailure(new Exception("Not signed in")); return; }
        DocumentReference likeRef = db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_MELODIES).document(melodyId)
                .collection(COLLECTION_LIKES).document(uid);
        DocumentReference melodyRef = db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_MELODIES).document(melodyId);
        likeRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                likeRef.delete().addOnSuccessListener(v ->
                        melodyRef.update("likesCount", FieldValue.increment(-1))
                                .addOnSuccessListener(v2 -> callback.onSuccess())
                                .addOnFailureListener(callback::onFailure))
                        .addOnFailureListener(callback::onFailure);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("userId", uid);
                data.put("likedAt", FieldValue.serverTimestamp());
                likeRef.set(data).addOnSuccessListener(v ->
                        melodyRef.update("likesCount", FieldValue.increment(1))
                                .addOnSuccessListener(v2 -> callback.onSuccess())
                                .addOnFailureListener(callback::onFailure))
                        .addOnFailureListener(callback::onFailure);
            }
        }).addOnFailureListener(callback::onFailure);
    }

    public void isCurrentUserLikedMelody(String songId, String melodyId,
                                         BooleanCallback callback) {
        String uid = getCurrentUserId();
        if (uid == null) { callback.onResult(false); return; }
        db.collection(COLLECTION_SONGS).document(songId)
                .collection(COLLECTION_MELODIES).document(melodyId)
                .collection(COLLECTION_LIKES).document(uid)
                .get()
                .addOnSuccessListener(doc -> callback.onResult(doc.exists()))
                .addOnFailureListener(e -> callback.onResult(false));
    }

    // ── Community songs ──────────────────────────────────────────────

    public void submitCommunitySong(String title, String lyrics,
                                    @Nullable String author,
                                    @Nullable String composer,
                                    SongCallback callback) {
        String uid = getCurrentUserId();
        if (uid == null) { callback.onFailure(new Exception("Not signed in")); return; }
        DocumentReference ref = db.collection(COLLECTION_SONGS).document();
        Map<String, Object> data = new HashMap<>();
        data.put("id", ref.getId());
        data.put("title", title);
        data.put("lyrics", lyrics);
        data.put("authorUid", uid);
        data.put("authorName", author != null && !author.isEmpty() ? author : getCurrentUserName());
        if (composer != null && !composer.isEmpty()) data.put("composer", composer);
        data.put("isCommunitySong", true);
        data.put("songNumber", 0);
        data.put("likesCount", 0);
        data.put("commentCount", 0);
        data.put("createdAt", FieldValue.serverTimestamp());
        ref.set(data).addOnSuccessListener(v -> {
            SongFirestore song = new SongFirestore();
            song.setId(ref.getId());
            song.setTitle(title);
            song.setLyrics(lyrics);
            song.setAuthorUid(uid);
            song.setAuthorName(author != null && !author.isEmpty() ? author : getCurrentUserName());
            song.setCommunitySong(true);
            callback.onSuccess(song);
        }).addOnFailureListener(callback::onFailure);
    }

    public ListenerRegistration listenToCommunitySongs(SongsCallback callback) {
        return db.collection(COLLECTION_SONGS)
                .whereEqualTo("isCommunitySong", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) { callback.onFailure(e); return; }
                    List<SongFirestore> songs = new ArrayList<>();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            SongFirestore s = doc.toObject(SongFirestore.class);
                            s.setId(doc.getId());
                            songs.add(s);
                        }
                    }
                    callback.onSuccess(songs);
                });
    }

    public void deleteCommunitySong(String songId, VoidCallback callback) {
        String uid = getCurrentUserId();
        if (uid == null) { callback.onFailure(new Exception("Not signed in")); return; }
        db.collection(COLLECTION_SONGS).document(songId)
                .delete()
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}
