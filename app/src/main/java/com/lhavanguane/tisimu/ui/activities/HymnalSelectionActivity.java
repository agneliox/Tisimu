package com.lhavanguane.tisimu.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.ui.adapters.HymnalAdapter;
import com.lhavanguane.tisimu.utils.SelectionManager;
import com.lhavanguane.tisimu.viewmodels.HymnalViewModel;

public class HymnalSelectionActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView rvHymnals;
    private Chip chipSelectedCount;
    private MaterialButton btnContinue;

    private HymnalViewModel viewModel;
    private HymnalAdapter adapter;

    private int selectedCount = 0;
    private boolean isContinueButtonEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hymnal_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_hymnal_selector), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        observeData();
        setupListeners();

    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvHymnals = findViewById(R.id.rvHymnals);
        chipSelectedCount = findViewById(R.id.chipSelectedCount);
        btnContinue = findViewById(R.id.btnContinue);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new HymnalAdapter();
        rvHymnals.setLayoutManager(new LinearLayoutManager(this));
        rvHymnals.setAdapter(adapter);

        adapter.setOnHymnalSelectionListener((hymnal, isSelected) -> {
            viewModel.updateHymnalSelection(hymnal.getId(), isSelected);
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HymnalViewModel.class);
    }

    private void observeData() {
        viewModel.getAllHymnals().observe(this, hymnals -> {
            if (hymnals != null) {
                adapter.setHymnals(hymnals);
            }
        });

        viewModel.getSelectedCount().observe(this, count -> {
            if (count != null) {
                selectedCount = count;
                updateUIForSelection(count);
            }
        });
    }

    private void updateUIForSelection(int count) {
        if (count > 0) {
            chipSelectedCount.setVisibility(android.view.View.VISIBLE);
            chipSelectedCount.setText(count + " hymnal" + (count > 1 ? "s" : "") + " selected");
            btnContinue.setEnabled(true);
        } else {
            chipSelectedCount.setVisibility(android.view.View.GONE);
            btnContinue.setEnabled(false);
        }
    }

    // In HymnalSelectionActivity.java, update the btnContinue click listener:

    private void setupListeners() {
        btnContinue.setOnClickListener(v -> {
            if (selectedCount > 0) {
                // Save that user has made selections
                SelectionManager.getInstance(this).setHasSelectedHymnals(true);

                // Navigate to SongListActivity
                Intent intent = new Intent(HymnalSelectionActivity.this, SongListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Please select at least one hymnal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}