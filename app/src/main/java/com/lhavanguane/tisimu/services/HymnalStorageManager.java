package com.lhavanguane.tisimu.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lhavanguane.tisimu.models.HymnalData;
import com.lhavanguane.tisimu.models.HymnalManifest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HymnalStorageManager {
    private static final String TAG = "HymnalStorage";

    // Update this with your GitHub username
    private static final String GITHUB_USERNAME = "agneliox";
    private static final String REPO_NAME = "Tisimu";
    private static final String BRANCH = "master";
    private static final String HYMNAL_DIR = "hymnals";

    private static final String MANIFEST_URL = "https://raw.githubusercontent.com/" +
            GITHUB_USERNAME + "/" + REPO_NAME + "/" + BRANCH + "/hymnals/manifest.json";

    private final Context context;
    private final Gson gson;
    private final OkHttpClient client;
    private final ExecutorService executorService;
    private final File hymnalDirectory;
    private final Handler mainHandler;

    public interface ManifestCallback {
        void onSuccess(HymnalManifest manifest);
        void onFailure(String error);
    }

    public interface DownloadCallback {
        void onProgress(int progress);
        void onSuccess(HymnalData hymnal);
        void onFailure(String error);
    }

    public interface HymnalLoadCallback {
        void onSuccess(HymnalData hymnal);
        void onFailure(String error);
    }

    public HymnalStorageManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
        Log.d(TAG, "HymnalStorageManager constructor called");

        // Initialize all fields in constructor
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.executorService = Executors.newSingleThreadExecutor();

        this.hymnalDirectory = new File(context.getFilesDir(), HYMNAL_DIR);
        if (!hymnalDirectory.exists()) {
            boolean created = hymnalDirectory.mkdirs();
            Log.d(TAG, "Hymnal directory created: " + created);
        }

        Log.d(TAG, "HymnalStorageManager initialized successfully");
    }

    public void fetchManifest(ManifestCallback callback) {
        if (executorService == null) {
            Log.e(TAG, "executorService is null!");
            if (callback != null) {
                mainHandler.post(() -> callback.onFailure("Service not initialized"));
            }
            return;
        }

        executorService.execute(() -> {
            try {
                Log.d(TAG, "Fetching manifest from: " + MANIFEST_URL);

                Request request = new Request.Builder()
                        .url(MANIFEST_URL)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    Log.d(TAG, "Response code: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        String json = response.body().string();
                        Log.d(TAG, "JSON received, length: " + json.length());

                        HymnalManifest manifest = gson.fromJson(json, HymnalManifest.class);

                        if (manifest != null && manifest.getHymnals() != null) {
                            Log.d(TAG, "Parsed " + manifest.getHymnals().size() + " hymnals");

                            // Update downloaded status
                            for (HymnalManifest.HymnalInfo hymnal : manifest.getHymnals()) {
                                hymnal.setDownloaded(isHymnalDownloaded(hymnal.getId()));
                                Log.d(TAG, "Hymnal: " + hymnal.getName() + " - Downloaded: " + hymnal.isDownloaded());
                            }

                            // Callback on main thread
                            if (callback != null) {
                                mainHandler.post(() -> callback.onSuccess(manifest));
                            }
                        } else {
                            Log.e(TAG, "Manifest or hymnals list is null");
                            if (callback != null) {
                                mainHandler.post(() -> callback.onFailure("Invalid manifest format"));
                            }
                        }
                    } else {
                        String error = "HTTP " + response.code();
                        Log.e(TAG, error);
                        if (callback != null) {
                            mainHandler.post(() -> callback.onFailure(error));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching manifest", e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onFailure(e.getMessage()));
                }
            }
        });
    }

    public void downloadHymnal(HymnalManifest.HymnalInfo metadata, DownloadCallback callback) {
        if (executorService == null) {
            if (callback != null) {
                mainHandler.post(() -> callback.onFailure("Service not initialized"));
            }
            return;
        }

        executorService.execute(() -> {
            try {
                String fileUrl = metadata.getFileUrl();
                Log.d(TAG, "========== DOWNLOAD DEBUG ==========");
                Log.d(TAG, "Hymnal Name: " + metadata.getName());
                Log.d(TAG, "Hymnal ID: " + metadata.getId());
                Log.d(TAG, "File URL: " + fileUrl);
                Log.d(TAG, "====================================");
                Log.d(TAG, "========== DOWNLOAD DEBUG ==========");

//                Log.d(TAG, "Downloading: " + metadata.getName() + " from " + metadata.getFileUrl());

                String altUrl = "https://raw.githubusercontent.com/agneliox/Tisimu/main/hymnals/" + metadata.getId() + ".json";
                Log.d(TAG, "Alternative URL: " + altUrl);

                Request request = new Request.Builder()
                        .url(metadata.getFileUrl())
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    Log.d(TAG, "Response Code: " + response.code());
                    Log.d(TAG, "Response Message: " + response.message());

                    if (response.isSuccessful() && response.body() != null) {
                        long contentLength = response.body().contentLength();
                        long downloaded = 0;

                        StringBuilder jsonBuilder = new StringBuilder();
                        try (InputStream inputStream = response.body().byteStream();
                             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                            String line;
                            while ((line = reader.readLine()) != null) {
                                jsonBuilder.append(line);
                                downloaded += line.getBytes().length;

                                if (callback != null && contentLength > 0) {
                                    int progress = (int) ((downloaded * 100) / contentLength);
                                    mainHandler.post(() -> callback.onProgress(progress));
                                }
                            }
                        }

                        String json = jsonBuilder.toString();

                        // Save to file
                        File hymnalFile = new File(hymnalDirectory, metadata.getId() + ".json");
                        try (FileWriter writer = new FileWriter(hymnalFile)) {
                            writer.write(json);
                        }

                        HymnalData hymnal = gson.fromJson(json, HymnalData.class);
                        metadata.setDownloaded(true);

                        Log.d(TAG, "Download complete: " + metadata.getName());

                        if (callback != null) {
                            mainHandler.post(() -> callback.onSuccess(hymnal));
                        }
                    } else {
                        String error = "Download failed: " + response.code();
                        Log.e(TAG, error);
                        if (callback != null) {
                            mainHandler.post(() -> callback.onFailure(error));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error downloading hymnal", e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onFailure(e.getMessage()));
                }
            }
        });
    }

    public void loadHymnal(String hymnalId, HymnalLoadCallback callback) {
        if (executorService == null) {
            if (callback != null) {
                mainHandler.post(() -> callback.onFailure("Service not initialized"));
            }
            return;
        }

        executorService.execute(() -> {
            try {
                File hymnalFile = new File(hymnalDirectory, hymnalId + ".json");
                if (!hymnalFile.exists()) {
                    if (callback != null) {
                        mainHandler.post(() -> callback.onFailure("Hymnal not found"));
                    }
                    return;
                }

                try (FileReader reader = new FileReader(hymnalFile);
                     BufferedReader bufferedReader = new BufferedReader(reader)) {

                    StringBuilder jsonBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        jsonBuilder.append(line);
                    }

                    HymnalData hymnal = gson.fromJson(jsonBuilder.toString(), HymnalData.class);

//                    if (callback != null) {
//                        mainHandler.post(() -> callback.onSuccess(hymnal));
//                    }
                    // In the loadHymnal method, ensure callbacks are only called if not null
                    if (callback != null) {
                        mainHandler.post(() -> {
                            if (callback != null) {
                                callback.onSuccess(hymnal);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading hymnal", e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onFailure(e.getMessage()));
                }
            }
        });
    }

    public List<String> getDownloadedHymnalIds() {
        List<String> ids = new ArrayList<>();
        File[] files = hymnalDirectory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                ids.add(file.getName().replace(".json", ""));
            }
        }
        return ids;
    }

    public boolean isHymnalDownloaded(String hymnalId) {
        return new File(hymnalDirectory, hymnalId + ".json").exists();
    }

    public void deleteHymnal(String hymnalId) {
        File hymnalFile = new File(hymnalDirectory, hymnalId + ".json");
        if (hymnalFile.exists()) {
            boolean deleted = hymnalFile.delete();
            Log.d(TAG, "Deleted hymnal " + hymnalId + ": " + deleted);
        }
    }

    public long getTotalStorageUsed() {
        long total = 0;
        File[] files = hymnalDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                total += file.length();
            }
        }
        return total;
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    // In HymnalStorageManager.java, in the downloadHymnal method
    private void saveHymnalToFile(String hymnalId, String json) {
        try {
            File hymnalFile = new File(hymnalDirectory, hymnalId + ".json");
            try (FileWriter writer = new FileWriter(hymnalFile)) {
                writer.write(json);
            }
            android.util.Log.d(TAG, "Hymnal saved to: " + hymnalFile.getAbsolutePath());
            android.util.Log.d(TAG, "File size: " + hymnalFile.length() + " bytes");
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error saving hymnal", e);
        }
    }

    // In HymnalStorageManager.java
    public void listDownloadedHymnals() {
        File[] files = hymnalDirectory.listFiles();
        if (files != null) {
            android.util.Log.d(TAG, "Downloaded hymnals:");
            for (File file : files) {
                android.util.Log.d(TAG, "  - " + file.getName() + " (" + file.length() + " bytes)");
            }
        } else {
            android.util.Log.d(TAG, "No downloaded hymnals found");
        }
    }
}