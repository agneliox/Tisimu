package com.lhavanguane.tisimu.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

    private MaterialToolbar toolbar;
    private RecyclerView rvHymnals;
    private MaterialButton btnContinue;

    private HymnalStorageManager storageManager;
    private PreferencesManager preferencesManager;
    private HymnalAdapter adapter;
    private List<HymnalManifest.HymnalInfo> hymnals;
    private List<HymnalManifest.HymnalInfo> selectedHymnals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hymnal_selection);

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
        toolbar = findViewById(R.id.toolbar);
        rvHymnals = findViewById(R.id.rvHymnals);
        btnContinue = findViewById(R.id.btnContinue);
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
                    selectedHymnals.add(hymnal);
                    preferencesManager.addSelectedHymnal(hymnal.getId());
                } else {
                    selectedHymnals.remove(hymnal);
                    preferencesManager.removeSelectedHymnal(hymnal.getId());
                }
                updateContinueButton();
            }
        });
    }

    private void loadManifest() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading hymnals...");
        progressDialog.show();

        storageManager.fetchManifest(new HymnalStorageManager.ManifestCallback() {
            @Override
            public void onSuccess(HymnalManifest manifest) {
                progressDialog.dismiss();
                hymnals.clear();
                hymnals.addAll(manifest.getHymnals());

                // Check which hymnals are already selected
                for (HymnalManifest.HymnalInfo hymnal : hymnals) {
                    if (preferencesManager.isHymnalSelected(hymnal.getId())) {
                        selectedHymnals.add(hymnal);
                    }
                }

                adapter.setHymnals(hymnals);
                updateContinueButton();
            }

            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
//                Toast.makeText(HymnalSelectionActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void downloadHymnal(HymnalManifest.HymnalInfo hymnal) {
        ProgressDialog downloadDialog = new ProgressDialog(this);
        downloadDialog.setMessage("Downloading " + hymnal.getName() + "...");
        downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadDialog.setMax(100);
        downloadDialog.show();

        storageManager.downloadHymnal(hymnal, new HymnalStorageManager.DownloadCallback() {
            @Override
            public void onProgress(int progress) {
                downloadDialog.setProgress(progress);
            }

            @Override
            public void onSuccess(HymnalData hymnalData) {
                downloadDialog.dismiss();
                hymnal.setDownloaded(true);
                adapter.notifyDataSetChanged();
                Toast.makeText(HymnalSelectionActivity.this, hymnal.getName() + " downloaded!", Toast.LENGTH_SHORT).show();
                updateContinueButton();
            }

            @Override
            public void onFailure(String error) {
                downloadDialog.dismiss();
                Toast.makeText(HymnalSelectionActivity.this, "Download failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteHymnal(HymnalManifest.HymnalInfo hymnal) {
        storageManager.deleteHymnal(hymnal.getId());
        hymnal.setDownloaded(false);

        // Also remove from selected
        selectedHymnals.remove(hymnal);
        preferencesManager.removeSelectedHymnal(hymnal.getId());

        adapter.notifyDataSetChanged();
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
            Intent intent = new Intent(HymnalSelectionActivity.this, SongListActivity.class);
            startActivity(intent);
            finish();
        });
    }
}