//package com.lhavanguane.tisimu.services;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.lhavanguane.tisimu.data.models.HymnalData;
//import com.lhavanguane.tisimu.data.models.HymnalManifest;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.security.MessageDigest;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.ResponseBody;
//
//public class HymnalStorageManager {
//    private static final String TAG = "HymnalStorage";
//
//    // GitHub configuration - UPDATE THESE WITH YOUR INFO
//    private static final String GITHUB_USERNAME = "YOUR_USERNAME";
//    private static final String REPO_NAME = "tisimu";
//    private static final String BRANCH = "main";
//    private static final String HYMNAL_DIR = "hymnals";
//
//    // URLs
//    private static final String BASE_RAW_URL = "https://raw.githubusercontent.com/" +
//            GITHUB_USERNAME + "/" + REPO_NAME + "/" + BRANCH + "/";
//    private static final String MANIFEST_URL = BASE_RAW_URL + "hymnals/manifest.json";
//
//    // Cache duration (24 hours in milliseconds)
//    private static final long CACHE_DURATION = 24 * 60 * 60 * 1000;
//
//    private Context context;
//    private Gson gson;
//    private OkHttpClient client;
//    private ExecutorService executorService;
//    private File hymnalDirectory;
//
//    public HymnalStorageManager(Context context) {
//        this.context = context.getApplicationContext();
//
//        // Configure Gson with pretty printing for debugging
//        this.gson = new GsonBuilder()
//                .setPrettyPrinting()
//                .create();
//
//        // Configure OkHttp with timeouts
//        this.client = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
//
//        // Single thread executor for background tasks
//        this.executorService = Executors.newSingleThreadExecutor();
//
//        // Create hymnal directory if not exists
//        hymnalDirectory = new File(context.getFilesDir(), HYMNAL_DIR);
//        if (!hymnalDirectory.exists()) {
//            boolean created = hymnalDirectory.mkdirs();
//            if (created) {
//                Log.d(TAG, "Created hymnal directory: " + hymnalDirectory.getAbsolutePath());
//            } else {
//                Log.e(TAG, "Failed to create hymnal directory");
//            }
//        }
//    }
//
//    // ============================================
//    // CALLBACK INTERFACES
//    // ============================================
//
//    public interface ManifestCallback {
//        void onSuccess(HymnalManifest manifest);
//        void onFailure(String error);
//    }
//
//    public interface DownloadCallback {
//        void onStart();
//        void onProgress(int progress, long downloaded, long total);
//        void onSuccess(HymnalData hymnal);
//        void onFailure(String error);
//    }
//
//    public interface HymnalLoadCallback {
//        void onSuccess(HymnalData hymnal);
//        void onFailure(String error);
//    }
//
//    public interface HymnalListCallback {
//        void onSuccess(List<HymnalManifest.HymnalMetadata> downloadedHymnals);
//        void onFailure(String error);
//    }
//
//    public interface UpdateCheckCallback {
//        void onUpdateAvailable(String hymnalId, int currentVersion, int latestVersion);
//        void onUpToDate(String hymnalId);
//        void onFailure(String error);
//    }
//
//    // ============================================
//    // MANIFEST OPERATIONS
//    // ============================================
//
//    /**
//     * Fetch manifest from GitHub with caching
//     */
//    public void fetchManifest(ManifestCallback callback) {
//        fetchManifest(callback, true);
//    }
//
//    public void fetchManifest(ManifestCallback callback, boolean useCache) {
//        executorService.execute(() -> {
//            try {
//                // Check cache first
//                if (useCache) {
//                    HymnalManifest cachedManifest = loadCachedManifest();
//                    if (cachedManifest != null) {
//                        Log.d(TAG, "Using cached manifest");
//                        if (callback != null) {
//                            callback.onSuccess(cachedManifest);
//                        }
//                        return;
//                    }
//                }
//
//                Log.d(TAG, "Fetching manifest from: " + MANIFEST_URL);
//
//                Request request = new Request.Builder()
//                        .url(MANIFEST_URL)
//                        .header("Cache-Control", "no-cache")
//                        .build();
//
//                try (Response response = client.newCall(request).execute()) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        String json = response.body().string();
//                        HymnalManifest manifest = gson.fromJson(json, HymnalManifest.class);
//
//                        // Cache the manifest
//                        cacheManifest(json);
//
//                        // Update downloaded status for each hymnal
//                        for (HymnalManifest.HymnalMetadata hymnal : manifest.getHymnals()) {
//                            hymnal.setDownloaded(isHymnalDownloaded(hymnal.getId()));
//                            hymnal.setNeedsUpdate(checkForUpdate(hymnal));
//                        }
//
//                        if (callback != null) {
//                            callback.onSuccess(manifest);
//                        }
//                    } else {
//                        String error = "HTTP " + response.code() + ": " + response.message();
//                        Log.e(TAG, error);
//
//                        // Try to return cached manifest even if fetch failed
//                        HymnalManifest cachedManifest = loadCachedManifest();
//                        if (cachedManifest != null) {
//                            Log.d(TAG, "Returning cached manifest due to fetch failure");
//                            if (callback != null) {
//                                callback.onSuccess(cachedManifest);
//                            }
//                        } else if (callback != null) {
//                            callback.onFailure(error);
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "Error fetching manifest", e);
//
//                // Try to return cached manifest
//                HymnalManifest cachedManifest = loadCachedManifest();
//                if (cachedManifest != null) {
//                    Log.d(TAG, "Returning cached manifest due to exception");
//                    if (callback != null) {
//                        callback.onSuccess(cachedManifest);
//                    }
//                } else if (callback != null) {
//                    callback.onFailure(e.getMessage());
//                }
//            }
//        });
//    }
//
//    /**
//     * Load cached manifest from local storage
//     */
//    private HymnalManifest loadCachedManifest() {
//        try {
//            File cacheFile = getManifestCacheFile();
//            if (!cacheFile.exists()) {
//                return null;
//            }
//
//            // Check cache age
//            long lastModified = cacheFile.lastModified();
//            long now = System.currentTimeMillis();
//            if (now - lastModified > CACHE_DURATION) {
//                Log.d(TAG, "Manifest cache expired");
//                return null;
//            }
//
//            try (FileReader reader = new FileReader(cacheFile);
//                 BufferedReader bufferedReader = new BufferedReader(reader)) {
//                StringBuilder stringBuilder = new StringBuilder();
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    stringBuilder.append(line);
//                }
//                return gson.fromJson(stringBuilder.toString(), HymnalManifest.class);
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Error loading cached manifest", e);
//            return null;
//        }
//    }
//
//    /**
//     * Cache manifest to local storage
//     */
//    private void cacheManifest(String json) {
//        try {
//            File cacheFile = getManifestCacheFile();
//            try (FileWriter writer = new FileWriter(cacheFile)) {
//                writer.write(json);
//            }
//            Log.d(TAG, "Manifest cached successfully");
//        } catch (Exception e) {
//            Log.e(TAG, "Error caching manifest", e);
//        }
//    }
//
//    private File getManifestCacheFile() {
//        return new File(context.getCacheDir(), "manifest_cache.json");
//    }
//
//    // ============================================
//    // DOWNLOAD OPERATIONS
//    // ============================================
//
//    /**
//     * Download a hymnal from GitHub
//     */
//    public void downloadHymnal(HymnalManifest.HymnalMetadata metadata, DownloadCallback callback) {
//        executorService.execute(() -> {
//            try {
//                if (callback != null) {
//                    callback.onStart();
//                }
//
//                Log.d(TAG, "Downloading hymnal from: " + metadata.getFileUrl());
//
//                Request request = new Request.Builder()
//                        .url(metadata.getFileUrl())
//                        .build();
//
//                try (Response response = client.newCall(request).execute()) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        long contentLength = response.body().contentLength();
//                        long downloadedBytes = 0;
//
//                        // Save to temporary file first
//                        File tempFile = new File(context.getCacheDir(), metadata.getId() + "_temp.json");
//
//                        try (InputStream inputStream = response.body().byteStream();
//                             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
//
//                            byte[] buffer = new byte[8192];
//                            int bytesRead;
//                            int lastProgress = -1;
//
//                            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                                outputStream.write(buffer, 0, bytesRead);
//                                downloadedBytes += bytesRead;
//
//                                // Report progress
//                                if (callback != null && contentLength > 0) {
//                                    int progress = (int) ((downloadedBytes * 100) / contentLength);
//                                    if (progress != lastProgress) {
//                                        lastProgress = progress;
//                                        callback.onProgress(progress, downloadedBytes, contentLength);
//                                    }
//                                }
//                            }
//                        }
//
//                        // Verify file integrity (optional)
//                        String fileHash = calculateFileHash(tempFile);
//                        if (metadata.getFileHash() != null && !metadata.getFileHash().equals(fileHash)) {
//                            tempFile.delete();
//                            if (callback != null) {
//                                callback.onFailure("File integrity check failed");
//                            }
//                            return;
//                        }
//
//                        // Move to permanent location
//                        File finalFile = getHymnalFile(metadata.getId());
//                        if (finalFile.exists()) {
//                            finalFile.delete();
//                        }
//                        tempFile.renameTo(finalFile);
//
//                        // Parse and return the hymnal
//                        HymnalData hymnal = loadHymnalFromFile(finalFile);
//
//                        // Update downloaded status
//                        metadata.setDownloaded(true);
//
//                        if (callback != null) {
//                            callback.onSuccess(hymnal);
//                        }
//                    } else {
//                        String error = "HTTP " + response.code() + ": " + response.message();
//                        Log.e(TAG, error);
//                        if (callback != null) {
//                            callback.onFailure(error);
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "Error downloading hymnal", e);
//                if (callback != null) {
//                    callback.onFailure(e.getMessage());
//                }
//            }
//        });
//    }
//
//    // ============================================
//    // LOAD OPERATIONS
//    // ============================================
//
//    /**
//     * Load a hymnal from local storage
//     */
//    public void loadHymnal(String hymnalId, HymnalLoadCallback callback) {
//        executorService.execute(() -> {
//            try {
//                File hymnalFile = getHymnalFile(hymnalId);
//                if (!hymnalFile.exists()) {
//                    if (callback != null) {
//                        callback.onFailure("Hymnal not downloaded: " + hymnalId);
//                    }
//                    return;
//                }
//
//                HymnalData hymnal = loadHymnalFromFile(hymnalFile);
//
//                if (callback != null) {
//                    callback.onSuccess(hymnal);
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "Error loading hymnal", e);
//                if (callback != null) {
//                    callback.onFailure(e.getMessage());
//                }
//            }
//        });
//    }
//
//    /**
//     * Load hymnal synchronously (for background threads)
//     */
//    public HymnalData loadHymnalSync(String hymnalId) throws IOException {
//        File hymnalFile = getHymnalFile(hymnalId);
//        if (!hymnalFile.exists()) {
//            throw new IOException("Hymnal not downloaded: " + hymnalId);
//        }
//        return loadHymnalFromFile(hymnalFile);
//    }
//
//    /**
//     * Load hymnal from file
//     */
//    private HymnalData loadHymnalFromFile(File file) throws IOException {
//        try (FileReader reader = new FileReader(file);
//             BufferedReader bufferedReader = new BufferedReader(reader)) {
//            StringBuilder stringBuilder = new StringBuilder();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//            return gson.fromJson(stringBuilder.toString(), HymnalData.class);
//        }
//    }
//
//    // ============================================
//    // LIST OPERATIONS
//    // ============================================
//
//    /**
//     * Get list of all downloaded hymnals
//     */
//    public void getDownloadedHymnals(HymnalListCallback callback) {
//        executorService.execute(() -> {
//            try {
//                List<HymnalManifest.HymnalMetadata> downloadedHymnals = new ArrayList<>();
//
//                // First get manifest to have metadata
//                fetchManifest(new ManifestCallback() {
//                    @Override
//                    public void onSuccess(HymnalManifest manifest) {
//                        for (HymnalManifest.HymnalMetadata hymnal : manifest.getHymnals()) {
//                            if (isHymnalDownloaded(hymnal.getId())) {
//                                hymnal.setDownloaded(true);
//                                downloadedHymnals.add(hymnal);
//                            }
//                        }
//
//                        if (callback != null) {
//                            callback.onSuccess(downloadedHymnals);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(String error) {
//                        // If manifest fetch fails, just list files
//                        File[] files = hymnalDirectory.listFiles();
//                        if (files != null) {
//                            for (File file : files) {
//                                HymnalManifest.HymnalMetadata hymnal = new HymnalManifest.HymnalMetadata();
//                                String id = file.getName().replace(".json", "");
//                                hymnal.setId(id);
//                                hymnal.setName(id.replace("_", " "));
//                                hymnal.setDownloaded(true);
//                                downloadedHymnals.add(hymnal);
//                            }
//                        }
//
//                        if (callback != null) {
//                            callback.onSuccess(downloadedHymnals);
//                        }
//                    }
//                });
//            } catch (Exception e) {
//                Log.e(TAG, "Error getting downloaded hymnals", e);
//                if (callback != null) {
//                    callback.onFailure(e.getMessage());
//                }
//            }
//        });
//    }
//
//    /**
//     * Get list of downloaded hymnal IDs (synchronous)
//     */
//    public List<String> getDownloadedHymnalIds() {
//        List<String> downloaded = new ArrayList<>();
//        File[] files = hymnalDirectory.listFiles((dir, name) -> name.endsWith(".json"));
//
//        if (files != null) {
//            for (File file : files) {
//                String name = file.getName();
//                downloaded.add(name.substring(0, name.lastIndexOf('.')));
//            }
//        }
//        return downloaded;
//    }
//
//    /**
//     * Check if a hymnal is downloaded
//     */
//    public boolean isHymnalDownloaded(String hymnalId) {
//        return getHymnalFile(hymnalId).exists();
//    }
//
//    // ============================================
//    // UPDATE OPERATIONS
//    // ============================================
//
//    /**
//     * Check for updates to a downloaded hymnal
//     */
//    public void checkForUpdate(String hymnalId, UpdateCheckCallback callback) {
//        executorService.execute(() -> {
//            try {
//                // Load local version
//                HymnalData localHymnal = loadHymnalSync(hymnalId);
//                int localVersion = localHymnal.getVersion();
//
//                // Fetch latest version from manifest
//                fetchManifest(new ManifestCallback() {
//                    @Override
//                    public void onSuccess(HymnalManifest manifest) {
//                        for (HymnalManifest.HymnalMetadata hymnal : manifest.getHymnals()) {
//                            if (hymnal.getId().equals(hymnalId)) {
//                                int latestVersion = hymnal.getVersion();
//                                if (latestVersion > localVersion) {
//                                    if (callback != null) {
//                                        callback.onUpdateAvailable(hymnalId, localVersion, latestVersion);
//                                    }
//                                } else {
//                                    if (callback != null) {
//                                        callback.onUpToDate(hymnalId);
//                                    }
//                                }
//                                return;
//                            }
//                        }
//
//                        if (callback != null) {
//                            callback.onFailure("Hymnal not found in manifest");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(String error) {
//                        if (callback != null) {
//                            callback.onFailure(error);
//                        }
//                    }
//                });
//            } catch (Exception e) {
//                Log.e(TAG, "Error checking for update", e);
//                if (callback != null) {
//                    callback.onFailure(e.getMessage());
//                }
//            }
//        });
//    }
//
//    /**
//     * Check if an update is available (synchronous helper)
//     */
//    private boolean checkForUpdate(HymnalManifest.HymnalMetadata hymnal) {
//        if (!hymnal.isDownloaded()) return false;
//
//        try {
//            File hymnalFile = getHymnalFile(hymnal.getId());
//            if (!hymnalFile.exists()) return false;
//
//            HymnalData localHymnal = loadHymnalFromFile(hymnalFile);
//            return localHymnal.getVersion() < hymnal.getVersion();
//        } catch (Exception e) {
//            Log.e(TAG, "Error checking update for " + hymnal.getId(), e);
//            return false;
//        }
//    }
//
//    // ============================================
//    // DELETE OPERATIONS
//    // ============================================
//
//    /**
//     * Delete a downloaded hymnal
//     */
//    public boolean deleteHymnal(String hymnalId) {
//        File hymnalFile = getHymnalFile(hymnalId);
//        if (hymnalFile.exists()) {
//            boolean deleted = hymnalFile.delete();
//            if (deleted) {
//                Log.d(TAG, "Deleted hymnal: " + hymnalId);
//            } else {
//                Log.e(TAG, "Failed to delete hymnal: " + hymnalId);
//            }
//            return deleted;
//        }
//        return false;
//    }
//
//    /**
//     * Delete all downloaded hymnals
//     */
//    public int deleteAllHymnals() {
//        int deletedCount = 0;
//        File[] files = hymnalDirectory.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                if (file.isFile() && file.delete()) {
//                    deletedCount++;
//                }
//            }
//        }
//        Log.d(TAG, "Deleted " + deletedCount + " hymnals");
//        return deletedCount;
//    }
//
//    /**
//     * Get storage usage in bytes
//     */
//    public long getStorageUsage() {
//        long totalSize = 0;
//        File[] files = hymnalDirectory.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                totalSize += file.length();
//            }
//        }
//        return totalSize;
//    }
//
//    // ============================================
//    // SEARCH OPERATIONS
//    // ============================================
//
//    /**
//     * Search for songs across all downloaded hymnals
//     */
//    public void searchSongs(String query, SearchCallback callback) {
//        executorService.execute(() -> {
//            try {
//                List<SearchResult> results = new ArrayList<>();
//                String lowerQuery = query.toLowerCase();
//
//                File[] files = hymnalDirectory.listFiles((dir, name) -> name.endsWith(".json"));
//                if (files != null) {
//                    for (File file : files) {
//                        HymnalData hymnal = loadHymnalFromFile(file);
//
//                        for (HymnalData.Song song : hymnal.getSongs()) {
//                            if (song.getTitle().toLowerCase().contains(lowerQuery) ||
//                                    String.valueOf(song.getNumber()).equals(query) ||
//                                    (song.getLyrics() != null &&
//                                            song.getLyrics().toLowerCase().contains(lowerQuery))) {
//
//                                SearchResult result = new SearchResult();
//                                result.setHymnalId(hymnal.getId());
//                                result.setHymnalName(hymnal.getName());
//                                result.setSongNumber(song.getNumber());
//                                result.setSongTitle(song.getTitle());
//                                result.setLyrics(song.getLyrics());
//                                results.add(result);
//                            }
//                        }
//                    }
//                }
//
//                if (callback != null) {
//                    callback.onSuccess(results);
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "Error searching songs", e);
//                if (callback != null) {
//                    callback.onFailure(e.getMessage());
//                }
//            }
//        });
//    }
//
//    public interface SearchCallback {
//        void onSuccess(List<SearchResult> results);
//        void onFailure(String error);
//    }
//
//    public static class SearchResult {
//        private String hymnalId;
//        private String hymnalName;
//        private int songNumber;
//        private String songTitle;
//        private String lyrics;
//
//        // Getters and setters
//        public String getHymnalId() { return hymnalId; }
//        public void setHymnalId(String hymnalId) { this.hymnalId = hymnalId; }
//
//        public String getHymnalName() { return hymnalName; }
//        public void setHymnalName(String hymnalName) { this.hymnalName = hymnalName; }
//
//        public int getSongNumber() { return songNumber; }
//        public void setSongNumber(int songNumber) { this.songNumber = songNumber; }
//
//        public String getSongTitle() { return songTitle; }
//        public void setSongTitle(String songTitle) { this.songTitle = songTitle; }
//
//        public String getLyrics() { return lyrics; }
//        public void setLyrics(String lyrics) { this.lyrics = lyrics; }
//    }
//
//    // ============================================
//    // UTILITY METHODS
//    // ============================================
//
//    /**
//     * Get the file for a hymnal
//     */
//    private File getHymnalFile(String hymnalId) {
//        return new File(hymnalDirectory, hymnalId + ".json");
//    }
//
//    /**
//     * Calculate file hash for integrity checking
//     */
//    private String calculateFileHash(File file) throws Exception {
//        MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        try (FileInputStream fis = new FileInputStream(file)) {
//            byte[] byteArray = new byte[8192];
//            int bytesCount;
//            while ((bytesCount = fis.read(byteArray)) != -1) {
//                digest.update(byteArray, 0, bytesCount);
//            }
//        }
//        byte[] bytes = digest.digest();
//        StringBuilder sb = new StringBuilder();
//        for (byte b : bytes) {
//            sb.append(String.format("%02x", b));
//        }
//        return sb.toString();
//    }
//
//    /**
//     * Clear all cached data (manifest cache only)
//     */
//    public void clearCache() {
//        File cacheFile = getManifestCacheFile();
//        if (cacheFile.exists()) {
//            cacheFile.delete();
//        }
//        Log.d(TAG, "Cache cleared");
//    }
//
//    /**
//     * Refresh manifest (force download fresh copy)
//     */
//    public void refreshManifest(ManifestCallback callback) {
//        clearCache();
//        fetchManifest(callback, false);
//    }
//
//    /**
//     * Shutdown executor service (call when app closes)
//     */
//    public void shutdown() {
//        executorService.shutdown();
//    }
//}