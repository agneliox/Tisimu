package com.lhavanguane.tisimu.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lhavanguane.tisimu.models.Announcement;
import com.lhavanguane.tisimu.models.Community;
import com.lhavanguane.tisimu.models.CommunityMember;
import com.lhavanguane.tisimu.models.AgendaItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CommunityFirestoreManager {
    private static final String TAG = "CommunityFirestore";
    private static final String COLLECTION_COMMUNITIES = "communities";
    private static final String COLLECTION_MEMBERS = "members";
    private static final String COLLECTION_ANNOUNCEMENTS = "announcements";
    private static final String COLLECTION_FILES = "files";
    private static final String COLLECTION_USER_COMMUNITIES = "user_communities";

    private static CommunityFirestoreManager instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private static final String COLLECTION_AGENDA = "agenda";

    private CommunityFirestoreManager() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public static synchronized CommunityFirestoreManager getInstance() {
        if (instance == null) {
            instance = new CommunityFirestoreManager();
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

    private String getCurrentUserEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getEmail() : "";
    }

    // ==================== CALLBACK INTERFACES ====================

    public interface CommunityCallback {
        void onSuccess(Community community);
        void onFailure(Exception e);
    }

    public interface CommunitiesCallback {
        void onSuccess(List<Community> communities);
        void onFailure(Exception e);
    }

    public interface VoidCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface AnnouncementsCallback {
        void onSuccess(List<Announcement> items);
        void onFailure(Exception e);
    }

    public interface MembersCallback {
        void onSuccess(List<CommunityMember> members);
        void onFailure(Exception e);
    }

    public interface BooleanCallback {
        void onResult(boolean isManager);
    }

    public interface StringCallback {
        void onSuccess(String result);
        void onFailure(Exception e);
    }

    // ==================== COMMUNITY CRUD ====================

    public void createCommunity(String name, String description, boolean isPrivate, String joinCode, CommunityCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }

        String communityId = UUID.randomUUID().toString();

        Map<String, Object> community = new HashMap<>();
        community.put("id", communityId);
        community.put("name", name);
        community.put("description", description);
        community.put("createdBy", userId);
        community.put("createdByUserName", getCurrentUserName());
        community.put("createdAt", FieldValue.serverTimestamp());
        community.put("isPrivate", isPrivate);
        community.put("joinCode", isPrivate ? joinCode : null);
        community.put("memberCount", 1);

        db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .set(community)
                .addOnSuccessListener(aVoid -> {
                    // Add creator as member
                    addMemberToCommunity(communityId, userId, "manager");

                    // Add to user's communities sub-collection
                    addToUserCommunities(userId, communityId, name, "manager");

                    Community newCommunity = new Community();
                    newCommunity.setId(communityId);
                    newCommunity.setName(name);
                    newCommunity.setDescription(description);
                    newCommunity.setCreatedBy(userId);
                    newCommunity.setCreatedByUserName(getCurrentUserName());
                    newCommunity.setPrivate(isPrivate);
                    newCommunity.setJoinCode(joinCode);
                    newCommunity.setMemberCount(1);
                    newCommunity.setUserRole("manager");

                    callback.onSuccess(newCommunity);
                })
                .addOnFailureListener(callback::onFailure);
    }

    private void addMemberToCommunity(String communityId, String userId, String role) {
        Map<String, Object> member = new HashMap<>();
        member.put("userId", userId);
        member.put("userName", getCurrentUserName());
        member.put("userEmail", getCurrentUserEmail());
        member.put("role", role);
        member.put("joinedAt", FieldValue.serverTimestamp());

        db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .collection(COLLECTION_MEMBERS).document(userId)
                .set(member);
    }

    private void addToUserCommunities(String userId, String communityId, String communityName, String role) {
        Map<String, Object> userCommunity = new HashMap<>();
        userCommunity.put("communityId", communityId);
        userCommunity.put("communityName", communityName);
        userCommunity.put("role", role);
        userCommunity.put("joinedAt", FieldValue.serverTimestamp());

        db.collection(COLLECTION_USER_COMMUNITIES).document(userId)
                .collection(COLLECTION_COMMUNITIES).document(communityId)
                .set(userCommunity);
    }

    public void getAllPublicCommunities(CommunitiesCallback callback) {
        db.collection(COLLECTION_COMMUNITIES)
                .whereEqualTo("isPrivate", false)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Community> communities = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Community community = doc.toObject(Community.class);
                        if (community != null) {
                            communities.add(community);
                        }
                    }
                    callback.onSuccess(communities);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getUserJoinedCommunities(CommunitiesCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        db.collection(COLLECTION_USER_COMMUNITIES).document(userId)
                .collection(COLLECTION_COMMUNITIES)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        callback.onSuccess(new ArrayList<>());
                        return;
                    }

                    List<String> communityIds = new ArrayList<>();
                    Map<String, String> communityRoles = new HashMap<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String communityId = doc.getString("communityId");
                        String role = doc.getString("role");
                        if (communityId != null) {
                            communityIds.add(communityId);
                            communityRoles.put(communityId, role);
                        }
                    }

                    if (communityIds.isEmpty()) {
                        callback.onSuccess(new ArrayList<>());
                        return;
                    }

                    db.collection(COLLECTION_COMMUNITIES)
                            .whereIn("id", communityIds)
                            .get()
                            .addOnSuccessListener(communitySnapshots -> {
                                List<Community> communities = new ArrayList<>();
                                for (QueryDocumentSnapshot doc : communitySnapshots) {
                                    Community community = doc.toObject(Community.class);
                                    if (community != null) {
                                        String role = communityRoles.get(community.getId());
                                        community.setUserRole(role);
                                        communities.add(community);
                                    }
                                }
                                callback.onSuccess(communities);
                            })
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // In CommunityFirestoreManager.java, verify the joinCommunity method

    public void joinCommunity(String communityId, String joinCode, VoidCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }

        // First verify community exists and join code is correct
        db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Community community = documentSnapshot.toObject(Community.class);
                    if (community == null) {
                        callback.onFailure(new Exception("Community not found"));
                        return;
                    }

                    if (community.isPrivate()) {
                        if (joinCode == null || !joinCode.equals(community.getJoinCode())) {
                            callback.onFailure(new Exception("Invalid join code"));
                            return;
                        }
                    }

                    // Check if already a member
                    db.collection(COLLECTION_COMMUNITIES).document(communityId)
                            .collection(COLLECTION_MEMBERS).document(userId)
                            .get()
                            .addOnSuccessListener(memberDoc -> {
                                if (memberDoc.exists()) {
                                    callback.onFailure(new Exception("Already a member"));
                                    return;
                                }

                                // Add member
                                addMemberToCommunity(communityId, userId, "member");
                                addToUserCommunities(userId, communityId, community.getName(), "member");

                                // Increment member count
                                db.collection(COLLECTION_COMMUNITIES).document(communityId)
                                        .update("memberCount", FieldValue.increment(1))
                                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                                        .addOnFailureListener(callback::onFailure);
                            }).addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void leaveCommunity(String communityId, VoidCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }

        // Remove from community members
        db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .collection(COLLECTION_MEMBERS).document(userId)
                .delete();

        // Remove from user's communities
        db.collection(COLLECTION_USER_COMMUNITIES).document(userId)
                .collection(COLLECTION_COMMUNITIES).document(communityId)
                .delete();

        // Decrement member count
        db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .update("memberCount", FieldValue.increment(-1))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void getCommunity(String communityId, CommunityCallback callback) {
        db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Community community = documentSnapshot.toObject(Community.class);
                    if (community != null && getCurrentUserId() != null) {
                        // Get user's role
                        db.collection(COLLECTION_COMMUNITIES).document(communityId)
                                .collection(COLLECTION_MEMBERS).document(getCurrentUserId())
                                .get()
                                .addOnSuccessListener(memberDoc -> {
                                    if (memberDoc.exists()) {
                                        String role = memberDoc.getString("role");
                                        community.setUserRole(role);
                                    }
                                    callback.onSuccess(community);
                                })
                                .addOnFailureListener(e -> callback.onSuccess(community));
                    } else {
                        callback.onSuccess(community);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

// ==================== AGENDA OPERATIONS ====================

    public interface AgendaCallback {
        void onSuccess(List<AgendaItem> items);
        void onFailure(Exception e);
    }

    public void getAgendaItems(String communityId, AgendaCallback callback) {
        db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .collection(COLLECTION_AGENDA)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<AgendaItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        AgendaItem item = doc.toObject(AgendaItem.class);
                        if (item != null) {
                            item.setId(doc.getId());
                            items.add(item);
                        }
                    }
                    callback.onSuccess(items);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void addAgendaItem(String communityId, String title, String content, VoidCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }

        isUserManager(communityId, userId, isManager -> {
            if (!isManager) {
                callback.onFailure(new Exception("Only managers can add agenda items"));
                return;
            }

            Map<String, Object> agendaItem = new HashMap<>();
            agendaItem.put("title", title);
            agendaItem.put("content", content);
            agendaItem.put("createdBy", userId);
            agendaItem.put("createdByUserName", getCurrentUserName());
            agendaItem.put("createdAt", FieldValue.serverTimestamp());
            agendaItem.put("date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

            db.collection(COLLECTION_COMMUNITIES).document(communityId)
                    .collection(COLLECTION_AGENDA)
                    .add(agendaItem)
                    .addOnSuccessListener(documentReference -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        });
    }

    public void deleteAgendaItem(String communityId, String itemId, VoidCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }

        isUserManager(communityId, userId, isManager -> {
            if (!isManager) {
                callback.onFailure(new Exception("Only managers can delete agenda items"));
                return;
            }

            db.collection(COLLECTION_COMMUNITIES).document(communityId)
                    .collection(COLLECTION_AGENDA).document(itemId)
                    .delete()
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        });
    }

    // ==================== REAL-TIME LISTENERS ====================

    public ListenerRegistration listenToAgendaItems(String communityId, AgendaCallback callback) {
        return db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .collection(COLLECTION_AGENDA)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        callback.onFailure(error);
                        return;
                    }

                    List<AgendaItem> items = new ArrayList<>();
                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            AgendaItem item = doc.toObject(AgendaItem.class);
                            if (item != null) {
                                item.setId(doc.getId());
                                items.add(item);
                            }
                        }
                    }
                    callback.onSuccess(items);
                });
    }


    // ==================== ANNOUNCEMENTS OPERATIONS ====================

    public void getAnnouncements(String communityId, AnnouncementsCallback callback) {
        db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .collection(COLLECTION_ANNOUNCEMENTS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Announcement> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Announcement item = doc.toObject(Announcement.class);
                        if (item != null) {
                            item.setId(doc.getId());
                            items.add(item);
                        }
                    }
                    callback.onSuccess(items);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void addAnnouncement(String communityId, String title, String content, boolean isImportant, VoidCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }

        isUserManager(communityId, userId, isManager -> {
            if (!isManager) {
                callback.onFailure(new Exception("Only managers can add announcements"));
                return;
            }

            Map<String, Object> announcement = new HashMap<>();
            announcement.put("title", title);
            announcement.put("content", content);
            announcement.put("createdBy", userId);
            announcement.put("createdByUserName", getCurrentUserName());
            announcement.put("createdAt", FieldValue.serverTimestamp());
            announcement.put("isImportant", isImportant);

            db.collection(COLLECTION_COMMUNITIES).document(communityId)
                    .collection(COLLECTION_ANNOUNCEMENTS)
                    .add(announcement)
                    .addOnSuccessListener(documentReference -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        });
    }

    public void deleteAnnouncement(String communityId, String announcementId, VoidCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }

        isUserManager(communityId, userId, isManager -> {
            if (!isManager) {
                callback.onFailure(new Exception("Only managers can delete announcements"));
                return;
            }

            db.collection(COLLECTION_COMMUNITIES).document(communityId)
                    .collection(COLLECTION_ANNOUNCEMENTS).document(announcementId)
                    .delete()
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        });
    }

    // ==================== MEMBERS OPERATIONS ====================

    public void getMembers(String communityId, MembersCallback callback) {
        db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .collection(COLLECTION_MEMBERS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CommunityMember> members = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        CommunityMember member = doc.toObject(CommunityMember.class);
                        if (member != null) {
                            members.add(member);
                        }
                    }
                    callback.onSuccess(members);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void promoteToManager(String communityId, String targetUserId, VoidCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }

        isUserManager(communityId, userId, isManager -> {
            if (!isManager) {
                callback.onFailure(new Exception("Only managers can promote members"));
                return;
            }

            db.collection(COLLECTION_COMMUNITIES).document(communityId)
                    .collection(COLLECTION_MEMBERS).document(targetUserId)
                    .update("role", "manager")
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        });
    }

    // ==================== PERMISSION CHECK ====================

    public void isUserManager(String communityId, String userId, BooleanCallback callback) {
        db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .collection(COLLECTION_MEMBERS).document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        callback.onResult("manager".equals(role));
                    } else {
                        callback.onResult(false);
                    }
                })
                .addOnFailureListener(e -> callback.onResult(false));
    }

    // ==================== REAL-TIME LISTENERS ====================
    public ListenerRegistration listenToAnnouncements(String communityId, AnnouncementsCallback callback) {
        return db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .collection(COLLECTION_ANNOUNCEMENTS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        callback.onFailure(error);
                        return;
                    }

                    List<Announcement> items = new ArrayList<>();
                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Announcement item = doc.toObject(Announcement.class);
                            if (item != null) {
                                item.setId(doc.getId());
                                items.add(item);
                            }
                        }
                    }
                    callback.onSuccess(items);
                });
    }

    public ListenerRegistration listenToMembers(String communityId, MembersCallback callback) {
        return db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .collection(COLLECTION_MEMBERS)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        callback.onFailure(error);
                        return;
                    }

                    List<CommunityMember> members = new ArrayList<>();
                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            CommunityMember member = doc.toObject(CommunityMember.class);
                            if (member != null) {
                                members.add(member);
                            }
                        }
                    }
                    callback.onSuccess(members);
                });
    }

    public ListenerRegistration listenToCommunity(String communityId, CommunityCallback callback) {
        return db.collection(COLLECTION_COMMUNITIES).document(communityId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        callback.onFailure(error);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Community community = documentSnapshot.toObject(Community.class);
                        callback.onSuccess(community);
                    }
                });
    }

    // ==================== UTILITY ====================

    public String generateJoinCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}