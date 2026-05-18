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
        // Apply language before super.onCreate
        com.lhavanguane.tisimu.utils.LanguageManager.getInstance(this).updateAppLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hymnal_selection);

        storageManager = new HymnalStorageManager(this);
        preferencesManager = PreferencesManager.getInstance(this);
        hymnals = new ArrayList<>();
        selectedHymnals = new ArrayList<>();

        // Load previously selected hymnals
        loadPreviouslySelectedHymnals();

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        loadManifest();
    }

    private void loadPreviouslySelectedHymnals() {
        // Get previously selected hymnal IDs from preferences
        for (String id : preferencesManager.getSelectedHymnals()) {
            // Create placeholder objects for selected hymnals
            HymnalManifest.HymnalInfo hymnal = new HymnalManifest.HymnalInfo();
            hymnal.setId(id);
            hymnal.setDownloaded(storageManager.isHymnalDownloaded(id));
            selectedHymnals.add(hymnal);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.hymnalSelectionToolbar);
        rvHymnals = findViewById(R.id.rvHymnals);
        btnContinue = findViewById(R.id.btnContinue);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.select_hymnals);
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
    }

    private void loadManifest() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        storageManager.fetchManifest(new HymnalStorageManager.ManifestCallback() {
            @Override
            public void onSuccess(HymnalManifest manifest) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (manifest != null && manifest.getHymnals() != null) {
                    hymnals.clear();
                    hymnals.addAll(manifest.getHymnals());

                    // Mark which hymnals are already downloaded and selected
                    for (HymnalManifest.HymnalInfo hymnal : hymnals) {
                        hymnal.setDownloaded(storageManager.isHymnalDownloaded(hymnal.getId()));

                        // Check if this hymnal was previously selected
                        for (HymnalManifest.HymnalInfo selected : selectedHymnals) {
                            if (selected.getId().equals(hymnal.getId())) {
                                hymnal.setSelected(true);
                                break;
                            }
                        }
                    }

                    adapter.setHymnals(hymnals);
                    updateContinueButton();
                }
            }

            @Override
            public void onFailure(String error) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(HymnalSelectionActivity.this, getString(R.string.error_prefix, error), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void downloadHymnal(HymnalManifest.HymnalInfo hymnal) {
        ProgressDialog downloadDialog = new ProgressDialog(this);
        downloadDialog.setMessage(getString(R.string.downloading_hymnal, hymnal.getName()));
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
                adapter.notifyDataSetChanged();
                Toast.makeText(HymnalSelectionActivity.this, getString(R.string.hymnal_downloaded, hymnal.getName()), Toast.LENGTH_SHORT).show();
                updateContinueButton();
            }

            @Override
            public void onFailure(String error) {
                if (downloadDialog != null && downloadDialog.isShowing()) {
                    downloadDialog.dismiss();
                }
                Toast.makeText(HymnalSelectionActivity.this, getString(R.string.download_failed, error), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteHymnal(HymnalManifest.HymnalInfo hymnal) {
        storageManager.deleteHymnal(hymnal.getId());
        hymnal.setDownloaded(false);
        hymnal.setSelected(false);

        // Also remove from selected
        selectedHymnals.remove(hymnal);
        preferencesManager.removeSelectedHymnal(hymnal.getId());

        adapter.notifyDataSetChanged();
        Toast.makeText(this, getString(R.string.hymnal_removed, hymnal.getName()), Toast.LENGTH_SHORT).show();
        updateContinueButton();
    }

    private void updateContinueButton() {
        boolean hasSelected = !selectedHymnals.isEmpty();
        btnContinue.setEnabled(hasSelected);

        if (hasSelected) {
            btnContinue.setText(getString(R.string.continue_text) + " (" + selectedHymnals.size() + ")");
        } else {
            btnContinue.setText(R.string.select_hymnals);
        }
    }

    private void setupListeners() {
        btnContinue.setOnClickListener(v -> {
            if (!selectedHymnals.isEmpty()) {
                // Navigate to MainActivity which will show the HymnalFragment
                Intent intent = new Intent(HymnalSelectionActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("SELECTED_TAB", 1); // 1 = Hymnal tab (index of hymnal in bottom nav)
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}