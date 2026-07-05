package com.lhavanguane.tisimu.firebase;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public abstract class FirebaseTestBase {

    protected static FirebaseAuth auth;
    protected static FirebaseFirestore firestore;
    protected Context context;

    @BeforeClass
    public static void setUpClass() {
        Context appContext = ApplicationProvider.getApplicationContext();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyBzr_1dpbU41InaLGsyexNjysb9h5EP-p4")
                .setApplicationId("1:636197267643:android:ba2ffeefcf5c570216e19e")
                .setProjectId("tisimu-app")
                .setGcmSenderId("636197267643")
                .setStorageBucket("tisimu-app.firebasestorage.app")
                .build();

        FirebaseApp app;
        try {
            app = FirebaseApp.getInstance("TISIMU_TEST");
        } catch (IllegalStateException e) {
            try {
                app = FirebaseApp.initializeApp(appContext, options, "TISIMU_TEST");
            } catch (IllegalStateException e2) {
                // This might happen if multiple test classes run in parallel in the same process
                app = FirebaseApp.getInstance("TISIMU_TEST");
            }
        }

        auth = FirebaseAuth.getInstance(app);
        firestore = FirebaseFirestore.getInstance(app);
    }

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        if (firestore != null) {
            clearFirestoreData();
        }
        if (auth != null) {
            signOut();
        }
    }

    @After
    public void tearDown() {
        if (firestore != null) {
            clearFirestoreData();
        }
        if (auth != null) {
            signOut();
        }
    }

    protected void clearFirestoreData() {
        if (firestore == null) return;
        CountDownLatch latch = new CountDownLatch(1);

        firestore.collection("communities")
                .limit(50)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().delete();
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void signOut() {
        if (auth != null && auth.getCurrentUser() != null) {
            auth.signOut();
        }
    }

    protected void waitForOperation(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void runOnMainThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}