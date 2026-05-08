package com.lhavanguane.tisimu.services;

import android.content.Context;
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
import java.net.HttpURLConnection;
import java.net.URL;
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

    // TODO: Replace with your GitHub username
    private static final String GITHUB_USERNAME = "agneliox";
    private static final String REPO_NAME = "tisimu";
    private static final String BRANCH = "main";
    private static final String HYMNAL_DIR = "hymnals";

    private static final String MANIFEST_URL = "https://raw.githubusercontent.com/" +
            GITHUB_USERNAME + "/" + REPO_NAME + "/" + BRANCH + "/hymnals/manifest.json";

    private Context context;
    private Gson gson;
    private OkHttpClient client;
    private ExecutorService executorService;
    private File hymnalDirectory;

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
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.executorService = Executors.newSingleThreadExecutor();

        this.hymnalDirectory = new File(context.getFilesDir(), HYMNAL_DIR);
        if (!hymnalDirectory.exists()) {
            hymnalDirectory.mkdirs();
        }
    }

    public void fetchManifest(ManifestCallback callback) {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Fetching manifest from: " + MANIFEST_URL);

                Request request = new Request.Builder()
                        .url(MANIFEST_URL)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String json = response.body().string();
                        HymnalManifest manifest = gson.fromJson(json, HymnalManifest.class);

                        // Update downloaded status
                        for (HymnalManifest.HymnalInfo hymnal : manifest.getHymnals()) {
                            hymnal.setDownloaded(isHymnalDownloaded(hymnal.getId()));
                        }

                        if (callback != null) {
                            callback.onSuccess(manifest);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailure("Failed to fetch manifest: " + response.code());
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching manifest", e);
                if (callback != null) {
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }

    public void downloadHymnal(HymnalManifest.HymnalInfo metadata, DownloadCallback callback) {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Downloading: " + metadata.getName());

                Request request = new Request.Builder()
                        .url(metadata.getFileUrl())
                        .build();

                try (Response response = client.newCall(request).execute()) {
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
                                    callback.onProgress(progress);
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

                        if (callback != null) {
                            callback.onSuccess(hymnal);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailure("Download failed: " + response.code());
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error downloading hymnal", e);
                if (callback != null) {
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }

    public void loadHymnal(String hymnalId, HymnalLoadCallback callback) {
        executorService.execute(() -> {
            try {
                File hymnalFile = new File(hymnalDirectory, hymnalId + ".json");
                if (!hymnalFile.exists()) {
                    if (callback != null) {
                        callback.onFailure("Hymnal not found");
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

                    if (callback != null) {
                        callback.onSuccess(hymnal);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading hymnal", e);
                if (callback != null) {
                    callback.onFailure(e.getMessage());
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
            hymnalFile.delete();
            Log.d(TAG, "Deleted: " + hymnalId);
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
        executorService.shutdown();
    }
}