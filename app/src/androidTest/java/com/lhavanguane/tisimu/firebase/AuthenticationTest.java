package com.lhavanguane.tisimu.firebase;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.truth.Truth.assertThat;

public class AuthenticationTest extends FirebaseTestBase {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";

    @Test
    public void testUserRegistration_Success() throws Exception {
        // Create user
        var authResult = Tasks.await(
                auth.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD),
                10, TimeUnit.SECONDS
        );

        assertThat(authResult.getUser()).isNotNull();
        assertThat(authResult.getUser().getEmail()).isEqualTo(TEST_EMAIL);

        // Clean up
        authResult.getUser().delete();
    }

    @Test(expected = ExecutionException.class)
    public void testUserRegistration_DuplicateEmail_Fails() throws Exception {
        // First registration
        var firstUser = Tasks.await(
                auth.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD),
                10, TimeUnit.SECONDS
        );

        try {
            // Second registration with same email
            Tasks.await(
                    auth.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD),
                    10, TimeUnit.SECONDS
            );
        } finally {
            firstUser.getUser().delete();
        }
    }

    @Test
    public void testUserLogin_Success() throws Exception {
        // Register first
        var registeredUser = Tasks.await(
                auth.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD),
                10, TimeUnit.SECONDS
        );

        // Sign out
        auth.signOut();

        // Login
        var loginResult = Tasks.await(
                auth.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD),
                10, TimeUnit.SECONDS
        );

        assertThat(loginResult.getUser()).isNotNull();
        assertThat(loginResult.getUser().getEmail()).isEqualTo(TEST_EMAIL);

        // Clean up
        registeredUser.getUser().delete();
    }

    @Test
    public void testUserLogin_InvalidPassword_Fails() throws Exception {
        // Register first
        var registeredUser = Tasks.await(
                auth.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD),
                10, TimeUnit.SECONDS
        );

        // Sign out
        auth.signOut();

        // Try login with wrong password
        try {
            Tasks.await(
                    auth.signInWithEmailAndPassword(TEST_EMAIL, "wrongpassword"),
                    10, TimeUnit.SECONDS
            );
            assertThat(false).isTrue(); // Should not reach here
        } catch (ExecutionException e) {
            assertThat(e.getMessage()).contains("password");
        } finally {
            registeredUser.getUser().delete();
        }
    }

    @Test
    public void testSignOut_Success() throws Exception {
        // Register and login
        var user = Tasks.await(
                auth.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD),
                10, TimeUnit.SECONDS
        );

        assertThat(auth.getCurrentUser()).isNotNull();

        // Sign out
        auth.signOut();
        assertThat(auth.getCurrentUser()).isNull();

        // Clean up (user may already be signed out, but we need to delete)
        Tasks.await(user.getUser().delete(), 10, TimeUnit.SECONDS);
    }

    @Test
    public void testGetCurrentUser_AfterLogin_Success() throws Exception {
        // Register
        Tasks.await(auth.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD), 10, TimeUnit.SECONDS);

        FirebaseUser currentUser = auth.getCurrentUser();

        assertThat(currentUser).isNotNull();
        assertThat(currentUser.getEmail()).isEqualTo(TEST_EMAIL);

        // Clean up
        currentUser.delete();
    }
}