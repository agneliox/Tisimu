package com.lhavanguane.tisimu.services;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.lhavanguane.tisimu.R;

public class GoogleSignInHelper {
    private static final String TAG = "GoogleSignIn";
    private static final int RC_SIGN_IN = 9001;

    private static GoogleSignInHelper instance;
    private GoogleSignInClient googleSignInClient;
    private final FirebaseAuth firebaseAuth;
    private OnGoogleSignInListener listener;
    private Activity currentActivity;
    private Fragment currentFragment;

    public interface OnGoogleSignInListener {
        void onSignInSuccess(FirebaseUser user);
        void onSignInFailure(String errorMessage);
        void onSignInCancelled();
    }

    private GoogleSignInHelper() {
        firebaseAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("YOUR_WEB_CLIENT_ID") // Will be auto-configured
                .requestEmail()
                .requestProfile()
                .build();

        // Note: The requestIdToken doesn't need a specific string - it will be auto-configured
        // If you have issues, get your web client ID from Firebase Console
        // Project Settings → Your app → Web client ID
    }

    public static synchronized GoogleSignInHelper getInstance() {
        if (instance == null) {
            instance = new GoogleSignInHelper();
        }
        return instance;
    }

    public void init(Activity activity) {
        this.currentActivity = activity;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public void init(Fragment fragment) {
        this.currentFragment = fragment;
        this.currentActivity = fragment.requireActivity();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(fragment.getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso);
    }

    public void setOnGoogleSignInListener(OnGoogleSignInListener listener) {
        this.listener = listener;
    }

    public void signIn() {
        if (googleSignInClient == null) {
            if (currentActivity != null) {
                init(currentActivity);
            } else if (currentFragment != null) {
                init(currentFragment);
            } else {
                if (listener != null) {
                    listener.onSignInFailure("Google Sign-In not initialized");
                }
                return;
            }
        }

        Intent signInIntent = googleSignInClient.getSignInIntent();
        if (currentFragment != null) {
            currentFragment.startActivityForResult(signInIntent, RC_SIGN_IN);
        } else if (currentActivity != null) {
            currentActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account);
            } else {
                if (listener != null) {
                    listener.onSignInFailure("Google Sign-In account is null");
                }
            }
        } catch (ApiException e) {
            Log.e(TAG, "Google sign-in failed", e);
            if (e.getStatusCode() == 12501) {
                // User cancelled sign-in
                if (listener != null) {
                    listener.onSignInCancelled();
                }
            } else {
                if (listener != null) {
                    listener.onSignInFailure("Google Sign-In failed: " + e.getMessage());
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        // Update display name from Google if not set
                        if (user != null && (user.getDisplayName() == null || user.getDisplayName().isEmpty())) {
                            String name = acct.getDisplayName();
                            String email = acct.getEmail();
                            if (name != null && !name.isEmpty()) {
                                // Name is already set by Google
                            }
                        }

                        if (listener != null) {
                            listener.onSignInSuccess(user);
                        }
                    } else {
                        // Sign in failed
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (listener != null) {
                            listener.onSignInFailure(task.getException() != null ?
                                    task.getException().getMessage() : "Authentication failed");
                        }
                    }
                });
    }

    public void signOut() {
        // Sign out from Firebase
        firebaseAuth.signOut();

        // Sign out from Google
        if (googleSignInClient != null) {
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Log.d(TAG, "Google sign-out completed");
            });
        }
    }

    public void revokeAccess() {
        if (googleSignInClient != null) {
            googleSignInClient.revokeAccess().addOnCompleteListener(task -> {
                Log.d(TAG, "Google access revoked");
            });
        }
    }

    public boolean isSignedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
}