package com.lhavanguane.tisimu.firebase;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lhavanguane.tisimu.models.Community;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class CommunityFirestoreTest extends FirebaseTestBase {

    private String testUserId;
    private String testUserEmail = "test@example.com";
    private String testPassword = "password123";

    @Before
    @Override
    public void setUp() {
        super.setUp();

        // Create test user
        try {
            var authResult = Tasks.await(
                    auth.createUserWithEmailAndPassword(testUserEmail, testPassword),
                    10, TimeUnit.SECONDS
            );
            testUserId = authResult.getUser().getUid();
        } catch (Exception e) {
            // User might already exist, try to sign in
            try {
                var authResult = Tasks.await(
                        auth.signInWithEmailAndPassword(testUserEmail, testPassword),
                        10, TimeUnit.SECONDS
                );
                testUserId = authResult.getUser().getUid();
            } catch (Exception ex) {
                throw new RuntimeException("Failed to set up test user", ex);
            }
        }
    }

    @Test
    public void testCreateCommunity_Success() throws Exception {
        String communityId = UUID.randomUUID().toString();

        Map<String, Object> community = new HashMap<>();
        community.put("id", communityId);
        community.put("name", "Test Community");
        community.put("description", "This is a test community");
        community.put("createdBy", testUserId);
        community.put("createdByUserName", "Test User");
        community.put("createdAt", com.google.firebase.Timestamp.now());
        community.put("isPrivate", false);
        community.put("memberCount", 1);

        // Create community
        Tasks.await(firestore.collection("communities").document(communityId).set(community), 10, TimeUnit.SECONDS);

        // Verify creation
        DocumentSnapshot doc = Tasks.await(
                firestore.collection("communities").document(communityId).get(),
                10, TimeUnit.SECONDS
        );

        assertThat(doc.exists()).isTrue();
        assertThat(doc.getString("name")).isEqualTo("Test Community");
        assertThat(doc.getString("createdBy")).isEqualTo(testUserId);

        // Add member subcollection
        Map<String, Object> member = new HashMap<>();
        member.put("userId", testUserId);
        member.put("userName", "Test User");
        member.put("role", "manager");
        member.put("joinedAt", com.google.firebase.Timestamp.now());

        Tasks.await(
                firestore.collection("communities").document(communityId)
                        .collection("members").document(testUserId).set(member),
                10, TimeUnit.SECONDS
        );

        // Verify member was added
        DocumentSnapshot memberDoc = Tasks.await(
                firestore.collection("communities").document(communityId)
                        .collection("members").document(testUserId).get(),
                10, TimeUnit.SECONDS
        );

        assertThat(memberDoc.exists()).isTrue();
        assertThat(memberDoc.getString("role")).isEqualTo("manager");

        // Clean up
        firestore.collection("communities").document(communityId).delete();
    }

    @Test
    public void testCreatePrivateCommunity_WithJoinCode() throws Exception {
        String communityId = UUID.randomUUID().toString();
        String joinCode = "123456";

        Map<String, Object> community = new HashMap<>();
        community.put("id", communityId);
        community.put("name", "Private Community");
        community.put("description", "Private test community");
        community.put("createdBy", testUserId);
        community.put("createdByUserName", "Test User");
        community.put("createdAt", com.google.firebase.Timestamp.now());
        community.put("isPrivate", true);
        community.put("joinCode", joinCode);
        community.put("memberCount", 1);

        Tasks.await(firestore.collection("communities").document(communityId).set(community), 10, TimeUnit.SECONDS);

        // Verify private community was created with join code
        DocumentSnapshot doc = Tasks.await(
                firestore.collection("communities").document(communityId).get(),
                10, TimeUnit.SECONDS
        );

        assertThat(doc.getBoolean("isPrivate")).isTrue();
        assertThat(doc.getString("joinCode")).isEqualTo(joinCode);

        // Clean up
        firestore.collection("communities").document(communityId).delete();
    }

    @Test
    public void testAddLiturgyItem_Success() throws Exception {
        // First create a community
        String communityId = UUID.randomUUID().toString();

        Map<String, Object> community = new HashMap<>();
        community.put("id", communityId);
        community.put("name", "Liturgy Test Community");
        community.put("createdBy", testUserId);
        community.put("createdByUserName", "Test User");
        community.put("createdAt", com.google.firebase.Timestamp.now());
        community.put("isPrivate", false);
        community.put("memberCount", 1);

        Tasks.await(firestore.collection("communities").document(communityId).set(community), 10, TimeUnit.SECONDS);

        // Add liturgy item
        String liturgyId = UUID.randomUUID().toString();
        Map<String, Object> liturgy = new HashMap<>();
        liturgy.put("title", "Sunday Service");
        liturgy.put("content", "Opening hymn, Prayer, Sermon");
        liturgy.put("createdBy", testUserId);
        liturgy.put("createdByUserName", "Test User");
        liturgy.put("createdAt", com.google.firebase.Timestamp.now());

        Tasks.await(
                firestore.collection("communities").document(communityId)
                        .collection("liturgy").document(liturgyId).set(liturgy),
                10, TimeUnit.SECONDS
        );

        // Verify liturgy was added
        DocumentSnapshot liturgyDoc = Tasks.await(
                firestore.collection("communities").document(communityId)
                        .collection("liturgy").document(liturgyId).get(),
                10, TimeUnit.SECONDS
        );

        assertThat(liturgyDoc.exists()).isTrue();
        assertThat(liturgyDoc.getString("title")).isEqualTo("Sunday Service");

        // Clean up
        firestore.collection("communities").document(communityId).delete();
    }

    @Test
    public void testAddAnnouncement_Success() throws Exception {
        // First create a community
        String communityId = UUID.randomUUID().toString();

        Map<String, Object> community = new HashMap<>();
        community.put("id", communityId);
        community.put("name", "Announcement Test Community");
        community.put("createdBy", testUserId);
        community.put("createdByUserName", "Test User");
        community.put("createdAt", com.google.firebase.Timestamp.now());
        community.put("isPrivate", false);
        community.put("memberCount", 1);

        Tasks.await(firestore.collection("communities").document(communityId).set(community), 10, TimeUnit.SECONDS);

        // Add announcement
        String announcementId = UUID.randomUUID().toString();
        Map<String, Object> announcement = new HashMap<>();
        announcement.put("title", "Important Meeting");
        announcement.put("content", "Community meeting this Friday at 7 PM");
        announcement.put("createdBy", testUserId);
        announcement.put("createdByUserName", "Test User");
        announcement.put("createdAt", com.google.firebase.Timestamp.now());
        announcement.put("isImportant", true);

        Tasks.await(
                firestore.collection("communities").document(communityId)
                        .collection("announcements").document(announcementId).set(announcement),
                10, TimeUnit.SECONDS
        );

        // Verify announcement was added
        DocumentSnapshot announcementDoc = Tasks.await(
                firestore.collection("communities").document(communityId)
                        .collection("announcements").document(announcementId).get(),
                10, TimeUnit.SECONDS
        );

        assertThat(announcementDoc.exists()).isTrue();
        assertThat(announcementDoc.getString("title")).isEqualTo("Important Meeting");
        assertThat(announcementDoc.getBoolean("isImportant")).isTrue();

        // Clean up
        firestore.collection("communities").document(communityId).delete();
    }

    @Test
    public void testGetMembers_Success() throws Exception {
        // Create community
        String communityId = UUID.randomUUID().toString();

        Map<String, Object> community = new HashMap<>();
        community.put("id", communityId);
        community.put("name", "Members Test Community");
        community.put("createdBy", testUserId);
        community.put("createdByUserName", "Test User");
        community.put("createdAt", com.google.firebase.Timestamp.now());
        community.put("isPrivate", false);
        community.put("memberCount", 1);

        Tasks.await(firestore.collection("communities").document(communityId).set(community), 10, TimeUnit.SECONDS);

        // Add member
        Map<String, Object> member = new HashMap<>();
        member.put("userId", testUserId);
        member.put("userName", "Test User");
        member.put("role", "manager");
        member.put("joinedAt", com.google.firebase.Timestamp.now());

        Tasks.await(
                firestore.collection("communities").document(communityId)
                        .collection("members").document(testUserId).set(member),
                10, TimeUnit.SECONDS
        );

        // Get members
        var members = Tasks.await(
                firestore.collection("communities").document(communityId)
                        .collection("members").get(),
                10, TimeUnit.SECONDS
        );

        assertThat(members.getDocuments()).isNotEmpty();
        assertThat(members.getDocuments().get(0).getString("userId")).isEqualTo(testUserId);

        // Clean up
        firestore.collection("communities").document(communityId).delete();
    }
}