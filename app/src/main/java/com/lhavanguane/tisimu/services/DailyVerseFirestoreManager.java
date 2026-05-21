package com.lhavanguane.tisimu.services;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.lhavanguane.tisimu.models.DailyVerse;
import com.lhavanguane.tisimu.utils.LanguageManager;
import com.lhavanguane.tisimu.utils.PreferencesManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DailyVerseFirestoreManager {
    private static final String TAG = "DailyVerseFirestore";
    private static final String COLLECTION_DAILY_VERSES = "daily_verses";
    private static final String SUB_COLLECTION_VERSES = "verses";
    private static final long CACHE_DURATION_MS = 24 * 60 * 60 * 1000; // 24 hours

    private static DailyVerseFirestoreManager instance;
    private FirebaseFirestore db;
    private LanguageManager languageManager;
    private PreferencesManager preferencesManager;
    private DailyVerse cachedVerse;
    private long lastCacheTime;
    private Context context;

    private DailyVerseFirestoreManager(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        languageManager = LanguageManager.getInstance(context);
        preferencesManager = PreferencesManager.getInstance(context);
        lastCacheTime = 0;

        // Log Firestore initialization
        Log.d(TAG, "DailyVerseFirestoreManager initialized");
        Log.d(TAG, "Firestore instance: " + (db != null ? "available" : "null"));
    }

    public static synchronized DailyVerseFirestoreManager getInstance(Context context) {
        if (instance == null) {
            instance = new DailyVerseFirestoreManager(context.getApplicationContext());
        }
        return instance;
    }

    public interface DailyVerseCallback {
        void onSuccess(DailyVerse verse);
        void onError(Exception e);
    }

    public void getTodaysVerse(DailyVerseCallback callback) {
        String currentLanguage = languageManager.getCurrentLanguage();
        String todayDate = getTodayDateString();

        Log.d(TAG, "getTodaysVerse called - Language: " + currentLanguage + ", Date: " + todayDate);
        Log.d(TAG, "Cache valid: " + isCacheValid() + ", Cached verse: " + (cachedVerse != null));

        // Check cache first
        if (isCacheValid() && cachedVerse != null &&
                cachedVerse.getLanguage() != null && cachedVerse.getLanguage().equals(currentLanguage) &&
                cachedVerse.getDate() != null && cachedVerse.getDate().equals(todayDate)) {
            Log.d(TAG, "Returning cached verse");
            callback.onSuccess(cachedVerse);
            return;
        }

        // Fetch from Firestore
        fetchVerseFromFirestore(currentLanguage, todayDate, callback);
    }

    private void fetchVerseFromFirestore(String language, String date, DailyVerseCallback callback) {
        Log.d(TAG, "========== FIRESTORE DEBUG ==========");
        Log.d(TAG, "Fetching verse from Firestore");
        Log.d(TAG, "Collection path: " + COLLECTION_DAILY_VERSES + "/" + language + "/" + SUB_COLLECTION_VERSES + "/" + date);
        Log.d(TAG, "Full document path: " + COLLECTION_DAILY_VERSES + "/" + language + "/" + SUB_COLLECTION_VERSES + "/" + date);

        // First, check if the collection exists by trying to get any document
        db.collection(COLLECTION_DAILY_VERSES)
                .document(language)
                .collection(SUB_COLLECTION_VERSES)
                .limit(1)
                .get()
                .addOnSuccessListener(checkSnapshot -> {
                    Log.d(TAG, "Collection check - exists: " + !checkSnapshot.isEmpty());
                    Log.d(TAG, "Collection check - size: " + checkSnapshot.size());
                    if (!checkSnapshot.isEmpty()) {
                        DocumentSnapshot firstDoc = checkSnapshot.getDocuments().get(0);
                        Log.d(TAG, "Sample document ID: " + firstDoc.getId());
                        Log.d(TAG, "Sample document fields: " + firstDoc.getData());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Collection check failed", e);
                });

        // Now get the specific document
        db.collection(COLLECTION_DAILY_VERSES)
                .document(language)
                .collection(SUB_COLLECTION_VERSES)
                .document(date)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG, "Document exists: " + documentSnapshot.exists());
                    Log.d(TAG, "Document path: " + documentSnapshot.getReference().getPath());

                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Document data: " + documentSnapshot.getData());
                        DailyVerse verse = documentSnapshot.toObject(DailyVerse.class);
                        if (verse != null) {
                            verse.setId(documentSnapshot.getId());
                            verse.setLanguage(language);
                            verse.setDate(date);

                            // Cache the verse
                            cachedVerse = verse;
                            lastCacheTime = System.currentTimeMillis();

                            Log.d(TAG, "Verse loaded successfully: " + verse.getReference());
                            callback.onSuccess(verse);
                        } else {
                            Log.e(TAG, "Verse object is null after conversion");
                            getFallbackVerse(callback);
                        }
                    } else {
                        Log.e(TAG, "Document does not exist for date: " + date);
                        getLatestVerse(language, callback);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching verse", e);
                    Log.e(TAG, "Error message: " + e.getMessage());
                    Log.e(TAG, "Error cause: " + (e.getCause() != null ? e.getCause().getMessage() : "null"));

                    // Try to use cached verse even if expired
                    if (cachedVerse != null) {
                        Log.d(TAG, "Using cached verse as fallback");
                        callback.onSuccess(cachedVerse);
                    } else {
                        getFallbackVerse(callback);
                    }
                });
    }

    private void getLatestVerse(String language, DailyVerseCallback callback) {
        Log.d(TAG, "Getting latest verse for language: " + language);

        db.collection(COLLECTION_DAILY_VERSES)
                .document(language)
                .collection(SUB_COLLECTION_VERSES)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "Latest verse query - size: " + queryDocumentSnapshots.size());

                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        Log.d(TAG, "Latest verse document ID: " + doc.getId());
                        Log.d(TAG, "Latest verse data: " + doc.getData());

                        DailyVerse verse = doc.toObject(DailyVerse.class);
                        if (verse != null) {
                            verse.setId(doc.getId());
                            verse.setLanguage(language);
                            Log.d(TAG, "Latest verse loaded: " + verse.getReference());
                            callback.onSuccess(verse);
                        } else {
                            Log.e(TAG, "Latest verse object is null");
                            getFallbackVerse(callback);
                        }
                    } else {
                        Log.e(TAG, "No verses found for language: " + language);
                        getFallbackVerse(callback);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting latest verse", e);
                    getFallbackVerse(callback);
                });
    }

    private void getFallbackVerse(DailyVerseCallback callback) {
        Log.d(TAG, "Using fallback verse");
        DailyVerse fallback = createFallbackVerse();
        callback.onSuccess(fallback);
    }

    private DailyVerse createFallbackVerse() {
        String currentLanguage = languageManager.getCurrentLanguage();
        DailyVerse verse = new DailyVerse();

        if ("pt".equals(currentLanguage)) {
            verse.setVerse("Porque Deus amou o mundo de tal maneira que deu o seu Filho unigênito, para que todo aquele que nele crê não pereça, mas tenha a vida eterna.");
            verse.setReference("João 3:16");
            verse.setDevotionalTitle("O Amor de Deus");
            verse.setDevotionalBody("Deus te ama incondicionalmente. Ele deu o Seu melhor quando merecíamos o Seu pior.");
            verse.setApplication("Compartilhe esse amor com alguém hoje.");
            verse.setReflection("Como você tem experimentado o amor de Deus na sua vida?");
            verse.setPrayer("Pai, obrigado por me amar primeiro. Ajude-me a compartilhar esse amor hoje.");
        } else if ("es".equals(currentLanguage)) {
            verse.setVerse("Porque de tal manera amó Dios al mundo, que ha dado a su Hijo unigénito, para que todo aquel que en él cree, no se pierda, mas tenga vida eterna.");
            verse.setReference("Juan 3:16");
            verse.setDevotionalTitle("El Amor de Dios");
            verse.setDevotionalBody("Dios te ama incondicionalmente. Él dio lo mejor de Sí cuando merecíamos lo peor.");
            verse.setApplication("Comparte este amor con alguien hoy.");
            verse.setReflection("¿Cómo has experimentado el amor de Dios en tu vida?");
            verse.setPrayer("Padre, gracias por amarme primero. Ayúdame a compartir este amor hoy.");
        } else if ("ts".equals(currentLanguage)) {
            verse.setVerse("Hikuva Xikwembu xi rhandzile misava swonghasi, xi ko xi nyika N’wana wa xona la tswariweke a ri swakwe, leswaku un’wana ni un’wana loyi a pfumelaka eka yena a nga lovi, kambe a va ni vutomi lebyi nga heriki.");
            verse.setReference("Yohane 3:16");
            verse.setDevotionalTitle("Rirhandzu ra Xikwembu");
            verse.setDevotionalBody("Xikwembu xi ku rhandza hi rirhandzu leri nga heriki. Xi nyikele hi N'wana wa xona wa risima loko hina hi nga swi lulamelanga.");
            verse.setApplication("Avelana rirhandzu leri ni un'wana namuntlha.");
            verse.setReflection("Xana u ri vonisile ku yini rirhandzu ra Xikwembu evuton’wini bya wena?");
            verse.setPrayer("Tatana, ndza nkhensa hikuva u rhandzile ku sungula. Ndzi pfune ku avelana rirhandzu leri namuntlha.");
        } else {
            verse.setVerse("For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life.");
            verse.setReference("John 3:16");
            verse.setDevotionalTitle("God's Unconditional Love");
            verse.setDevotionalBody("God loves you unconditionally. He gave His best when we deserved His worst.");
            verse.setApplication("Share this love with someone today.");
            verse.setReflection("How have you experienced God's love in your life?");
            verse.setPrayer("Father, thank You for loving me first. Help me to share this love today.");
        }

        verse.setLanguage(currentLanguage);
        verse.setDate(getTodayDateString());

        Log.d(TAG, "Fallback verse created for language: " + currentLanguage);
        return verse;
    }

    private String getTodayDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(new Date());
    }

    private boolean isCacheValid() {
        return (System.currentTimeMillis() - lastCacheTime) < CACHE_DURATION_MS;
    }

    // Test method to check Firestore connection
    public void testFirestoreConnection() {
        Log.d(TAG, "Testing Firestore connection...");

        // Try to access any document to see if Firestore is working
        db.collection(COLLECTION_DAILY_VERSES)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "Firestore connection test SUCCESS");
                    Log.d(TAG, "Found " + queryDocumentSnapshots.size() + " documents in daily_verses collection");
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Log.d(TAG, "Document ID: " + doc.getId());
                        }
                    } else {
                        Log.d(TAG, "No documents found in daily_verses collection");
                        Log.d(TAG, "You need to add data to Firestore first!");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore connection test FAILED", e);
                    Log.e(TAG, "Error: " + e.getMessage());
                });
    }
}