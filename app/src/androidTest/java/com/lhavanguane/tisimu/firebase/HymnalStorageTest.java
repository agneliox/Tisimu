package com.lhavanguane.tisimu.firebase;

import com.lhavanguane.tisimu.models.HymnalData;
import com.lhavanguane.tisimu.models.HymnalManifest;
import com.lhavanguane.tisimu.services.HymnalStorageManager;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class HymnalStorageTest extends FirebaseTestBase {

    private HymnalStorageManager storageManager;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        storageManager = new HymnalStorageManager(context);
    }

    @Test
    public void testFetchManifest_Success() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        storageManager.fetchManifest(new HymnalStorageManager.ManifestCallback() {
            @Override
            public void onSuccess(HymnalManifest manifest) {
                assertThat(manifest).isNotNull();
                assertThat(manifest.getHymnals()).isNotNull();
                latch.countDown();
            }

            @Override
            public void onFailure(String error) {
                latch.countDown();
                throw new AssertionError("Fetch manifest failed: " + error);
            }
        });

        assertThat(latch.await(30, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public void testDownloadHymnal_Success() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        // First get manifest to find a hymnal
        storageManager.fetchManifest(new HymnalStorageManager.ManifestCallback() {
            @Override
            public void onSuccess(HymnalManifest manifest) {
                if (manifest.getHymnals().isEmpty()) {
                    latch.countDown();
                    return;
                }

                HymnalManifest.HymnalInfo hymnal = manifest.getHymnals().get(0);

                storageManager.downloadHymnal(hymnal, new HymnalStorageManager.DownloadCallback() {
                    @Override
                    public void onProgress(int progress) {
                        // Progress update
                    }

                    @Override
                    public void onSuccess(HymnalData hymnalData) {
                        assertThat(hymnalData).isNotNull();
                        assertThat(hymnalData.getName()).isEqualTo(hymnal.getName());
                        assertThat(storageManager.isHymnalDownloaded(hymnal.getId())).isTrue();
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(String error) {
                        latch.countDown();
                        throw new AssertionError("Download failed: " + error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                latch.countDown();
                throw new AssertionError("Fetch manifest failed: " + error);
            }
        });

        assertThat(latch.await(60, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public void testLoadDownloadedHymnal_Success() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        // First download a hymnal
        HymnalManifest.HymnalInfo hymnalInfo = downloadFirstHymnalSync();

        storageManager.loadHymnal(hymnalInfo.getId(), new HymnalStorageManager.HymnalLoadCallback() {
            @Override
            public void onSuccess(HymnalData hymnal) {
                assertThat(hymnal).isNotNull();
                assertThat(hymnal.getId()).isEqualTo(hymnalInfo.getId());
                assertThat(hymnal.getSongs()).isNotEmpty();
                latch.countDown();
            }

            @Override
            public void onFailure(String error) {
                latch.countDown();
                throw new AssertionError("Load hymnal failed: " + error);
            }
        });

        assertThat(latch.await(30, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public void testDeleteHymnal_Success() throws Exception {
        // First download a hymnal
        HymnalManifest.HymnalInfo hymnalInfo = downloadFirstHymnalSync();

        assertThat(storageManager.isHymnalDownloaded(hymnalInfo.getId())).isTrue();

        storageManager.deleteHymnal(hymnalInfo.getId());

        assertThat(storageManager.isHymnalDownloaded(hymnalInfo.getId())).isFalse();
    }

    @Test
    public void testGetDownloadedHymnalIds() throws Exception {
        // Download a hymnal first
        HymnalManifest.HymnalInfo hymnalInfo = downloadFirstHymnalSync();

        List<String> downloaded = storageManager.getDownloadedHymnalIds();

        assertThat(downloaded).contains(hymnalInfo.getId());
    }

    private HymnalManifest.HymnalInfo downloadFirstHymnalSync() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final HymnalManifest.HymnalInfo[] result = new HymnalManifest.HymnalInfo[1];

        storageManager.fetchManifest(new HymnalStorageManager.ManifestCallback() {
            @Override
            public void onSuccess(HymnalManifest manifest) {
                if (manifest.getHymnals().isEmpty()) {
                    latch.countDown();
                    return;
                }

                HymnalManifest.HymnalInfo hymnal = manifest.getHymnals().get(0);

                storageManager.downloadHymnal(hymnal, new HymnalStorageManager.DownloadCallback() {
                    @Override
                    public void onProgress(int progress) {}

                    @Override
                    public void onSuccess(HymnalData hymnalData) {
                        result[0] = hymnal;
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(String error) {
                        latch.countDown();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                latch.countDown();
            }
        });

        latch.await(60, TimeUnit.SECONDS);
        return result[0];
    }
}