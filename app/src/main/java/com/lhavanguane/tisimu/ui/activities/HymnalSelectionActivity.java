package com.lhavanguane.tisimu.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.appbar.MaterialToolbar;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.HymnalData;
import com.lhavanguane.tisimu.models.HymnalManifest;
import com.lhavanguane.tisimu.services.HymnalStorageManager;
import com.lhavanguane.tisimu.ui.adapters.HymnalAdapter;
import com.lhavanguane.tisimu.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class HymnalSelectionActivity extends AppCompatActivity {

    private static final String TAG = "HymnalSelection";

    private MaterialToolbar toolbar;
    private RecyclerView rvHymnals;
    private MaterialButton btnContinue;

    private HymnalStorageManager storageManager;
    private PreferencesManager preferencesManager;
    private HymnalAdapter adapter;
    private List<HymnalManifest.HymnalInfo> hymnals;
    private List<HymnalManifest.HymnalInfo> selectedHymnals;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hymnal_selection);

        Log.d(TAG, "onCreate started");

        // Initialize managers
        storageManager = new HymnalStorageManager(this);
        preferencesManager = PreferencesManager.getInstance(this);
        hymnals = new ArrayList<>();
        selectedHymnals = new ArrayList<>();

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        loadManifest();
    }

    private void initViews() {
        toolbar = findViewById(R.id.hymnalSelectionToolbar);
        rvHymnals = findViewById(R.id.rvHymnals);
        btnContinue = findViewById(R.id.btnContinue);
        Log.d(TAG, "Views initialized");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Select Hymnals");
        }
    }

    private void setupRecyclerView() {
        adapter = new HymnalAdapter();
        rvHymnals.setLayoutManager(new LinearLayoutManager(this));
        rvHymnals.setAdapter(adapter);

        adapter.setOnHymnalActionListener(new HymnalAdapter.OnHymnalActionListener() {
            @Override
            public void onDownloadClick(HymnalManifest.HymnalInfo hymnal) {
                downloadHymnal(hymnal);
            }

            @Override
            public void onDeleteClick(HymnalManifest.HymnalInfo hymnal) {
                deleteHymnal(hymnal);
            }

            @Override
            public void onSelectClick(HymnalManifest.HymnalInfo hymnal, boolean isSelected) {
                if (isSelected) {
                    if (!selectedHymnals.contains(hymnal)) {
                        selectedHymnals.add(hymnal);
                        preferencesManager.addSelectedHymnal(hymnal.getId());
                    }
                } else {
                    selectedHymnals.remove(hymnal);
                    preferencesManager.removeSelectedHymnal(hymnal.getId());
                }
                updateContinueButton();
            }
        });

        Log.d(TAG, "RecyclerView setup complete");
    }

    private void loadManifest() {
        Log.d(TAG, "Loading manifest...");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading hymnals...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        storageManager.fetchManifest(new HymnalStorageManager.ManifestCallback() {
            @Override
            public void onSuccess(HymnalManifest manifest) {
                Log.d(TAG, "Manifest loaded successfully");

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (manifest != null && manifest.getHymnals() != null && !manifest.getHymnals().isEmpty()) {
                    Log.d(TAG, "Found " + manifest.getHymnals().size() + " hymnals");

                    hymnals.clear();
                    hymnals.addAll(manifest.getHymnals());

                    // Check which hymnals are already selected
                    for (HymnalManifest.HymnalInfo hymnal : hymnals) {
                        if (preferencesManager.isHymnalSelected(hymnal.getId())) {
                            selectedHymnals.add(hymnal);
                            Log.d(TAG, "Previously selected: " + hymnal.getName());
                        }
                        Log.d(TAG, "Hymnal: " + hymnal.getName() + " - Downloaded: " + hymnal.isDownloaded());
                    }

                    adapter.setHymnals(hymnals);
                    updateContinueButton();

                    if (hymnals.isEmpty()) {
                        Toast.makeText(HymnalSelectionActivity.this, "No hymnals available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Manifest or hymnals list is null or empty");
                    Toast.makeText(HymnalSelectionActivity.this, "No hymnals found in manifest", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to load manifest: " + error);

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                Toast.makeText(HymnalSelectionActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void downloadHymnal(HymnalManifest.HymnalInfo hymnal) {
        Log.d(TAG, "Downloading: " + hymnal.getName());

        ProgressDialog downloadDialog = new ProgressDialog(this);
        downloadDialog.setMessage("Downloading " + hymnal.getName() + "...");
        downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadDialog.setMax(100);
        downloadDialog.setCancelable(false);
        downloadDialog.show();

        storageManager.downloadHymnal(hymnal, new HymnalStorageManager.DownloadCallback() {
            @Override
            public void onProgress(int progress) {
                if (downloadDialog != null && downloadDialog.isShowing()) {
                    downloadDialog.setProgress(progress);
                }
            }

            @Override
            public void onSuccess(HymnalData hymnalData) {
                if (downloadDialog != null && downloadDialog.isShowing()) {
                    downloadDialog.dismiss();
                }
                hymnal.setDownloaded(true);

                // Refresh the adapter to show the updated state
                int position = hymnals.indexOf(hymnal);
                if (position != -1) {
                    adapter.notifyItemChanged(position);
                }

                Toast.makeText(HymnalSelectionActivity.this, hymnal.getName() + " downloaded!", Toast.LENGTH_SHORT).show();
                updateContinueButton();
            }

            @Override
            public void onFailure(String error) {
                if (downloadDialog != null && downloadDialog.isShowing()) {
                    downloadDialog.dismiss();
                }
                Log.e(TAG, "Download failed: " + error);
                Toast.makeText(HymnalSelectionActivity.this, "Download failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteHymnal(HymnalManifest.HymnalInfo hymnal) {
        storageManager.deleteHymnal(hymnal.getId());
        hymnal.setDownloaded(false);

        // Also remove from selected
        selectedHymnals.remove(hymnal);
        preferencesManager.removeSelectedHymnal(hymnal.getId());

        // Refresh the adapter
        int position = hymnals.indexOf(hymnal);
        if (position != -1) {
            adapter.notifyItemChanged(position);
        }

        Toast.makeText(this, hymnal.getName() + " removed", Toast.LENGTH_SHORT).show();
        updateContinueButton();
    }

    private void updateContinueButton() {
        boolean hasSelected = !selectedHymnals.isEmpty();
        btnContinue.setEnabled(hasSelected);

        if (hasSelected) {
            btnContinue.setText("Continue (" + selectedHymnals.size() + " selected)");
        } else {
            btnContinue.setText("Select a hymnal to continue");
        }
    }

    private void setupListeners() {
        btnContinue.setOnClickListener(v -> {
            if (!selectedHymnals.isEmpty()) {
                Intent intent = new Intent(HymnalSelectionActivity.this, SongListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}