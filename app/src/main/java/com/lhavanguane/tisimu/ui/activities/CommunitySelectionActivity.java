package com.lhavanguane.tisimu.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.Community;
import com.lhavanguane.tisimu.services.CommunityFirestoreManager;
import com.lhavanguane.tisimu.ui.adapters.CommunityAdapter;

import java.util.List;
import java.util.Objects;

public class CommunitySelectionActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView rvCommunities;
    private MaterialButton btnCreateCommunity;

    private CommunityFirestoreManager communityManager;
    private CommunityAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_community_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_community_selection), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        communityManager = CommunityFirestoreManager.getInstance();

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadCommunities();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.communitySelectionToolbar);
        rvCommunities = findViewById(R.id.rvCommunities);
        btnCreateCommunity = findViewById(R.id.btnCreateCommunity);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Discover Communities");
        }
    }

    private void setupRecyclerView() {
        adapter = new CommunityAdapter();
        rvCommunities.setLayoutManager(new LinearLayoutManager(this));
        rvCommunities.setAdapter(adapter);

        adapter.setOnCommunityActionListener(new CommunityAdapter.OnCommunityActionListener() {
            @Override
            public void onJoinClick(Community community) {
                showJoinDialog(community);
            }

            @Override
            public void onViewClick(Community community) {
                openCommunityDetail(community);
            }
        });
    }

    private void loadCommunities() {
        showProgress(true);
        communityManager.getAllPublicCommunities(new CommunityFirestoreManager.CommunitiesCallback() {
            @Override
            public void onSuccess(List<Community> communities) {
                showProgress(false);
                adapter.setCommunities(communities);
                if (communities.isEmpty()) {
                    Toast.makeText(CommunitySelectionActivity.this, "No public communities available yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                showProgress(false);
                Toast.makeText(CommunitySelectionActivity.this, "Error Check: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("Error Check","Error Check: " + e.getMessage());
            }
        });
    }

    private void showJoinDialog(Community community) {
        if (community.isPrivate()) {
            // Private community - ask for join code
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Join " + community.getName());
            builder.setMessage("This is a private community. Please enter the join code:");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint("Enter 6-digit code");
            builder.setView(input);

            builder.setPositiveButton("Join", (dialog, which) -> {
                String code = input.getText().toString();
                joinCommunity(community, code);
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        } else {
            // Public community - join directly
            joinCommunity(community, null);
        }
    }

    private void joinCommunity(Community community, String joinCode) {
        showProgress(true);
        communityManager.joinCommunity(community.getId(), joinCode, new CommunityFirestoreManager.VoidCallback() {
            @Override
            public void onSuccess() {
                showProgress(false);
                Toast.makeText(CommunitySelectionActivity.this, "Joined " + community.getName() + "!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                showProgress(false);
                Toast.makeText(CommunitySelectionActivity.this, "Failed to join: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showCreateCommunityDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Create New Community");

        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_community, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etCommunityName);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etCommunityDescription);
        com.google.android.material.chip.ChipGroup chipGroup = dialogView.findViewById(R.id.chipGroupVisibility);

        builder.setView(dialogView);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = Objects.requireNonNull(etName.getText()).toString().trim();
            String description = Objects.requireNonNull(etDescription.getText()).toString().trim();
            boolean isPrivate = chipGroup.getCheckedChipId() == R.id.chipPrivate;

            if (name.isEmpty()) {
                Toast.makeText(this, "Community name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            String joinCode = isPrivate ? communityManager.generateJoinCode() : "";
            createCommunity(name, description, isPrivate, joinCode);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void createCommunity(String name, String description, boolean isPrivate, String joinCode) {
        showProgress(true);
        communityManager.createCommunity(name, description, isPrivate, joinCode, new CommunityFirestoreManager.CommunityCallback() {
            @Override
            public void onSuccess(Community community) {
                showProgress(false);
                String message = isPrivate ? "Community created! Share this code with members: " + joinCode : "Community created!";
                Toast.makeText(CommunitySelectionActivity.this, message, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                showProgress(false);
                Toast.makeText(CommunitySelectionActivity.this, "Failed to create: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openCommunityDetail(Community community) {
        // Navigate to CommunityDetailActivity
        android.content.Intent intent = new android.content.Intent(CommunitySelectionActivity.this, CommunityDetailActivity.class);
        intent.putExtra("COMMUNITY_ID", community.getId());
        intent.putExtra("COMMUNITY_NAME", community.getName());
        startActivity(intent);
    }

    private void setupListeners() {
        btnCreateCommunity.setOnClickListener(v -> showCreateCommunityDialog());
    }

    private void showProgress(boolean show) {
        if (show && progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else if (!show && progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
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