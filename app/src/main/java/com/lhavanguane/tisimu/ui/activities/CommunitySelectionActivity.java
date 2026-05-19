package com.lhavanguane.tisimu.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.appbar.MaterialToolbar;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.Community;
import com.lhavanguane.tisimu.services.CommunityFirestoreManager;
import com.lhavanguane.tisimu.ui.adapters.CommunitySelectionAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommunitySelectionActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView rvCommunities;
    private MaterialButton btnCreateCommunity;

    private CommunityFirestoreManager communityManager;
    private CommunitySelectionAdapter adapter;
    private ProgressDialog progressDialog;

    private List<String> joinedCommunityIds = new ArrayList<>();

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
        loadUserJoinedCommunities(); // First load joined communities
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
        adapter = new CommunitySelectionAdapter();
        rvCommunities.setLayoutManager(new LinearLayoutManager(this));
        rvCommunities.setAdapter(adapter);

        adapter.setOnCommunityActionListener(new CommunitySelectionAdapter.OnCommunityActionListener() {
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

    private void loadUserJoinedCommunities() {
        communityManager.getUserJoinedCommunities(new CommunityFirestoreManager.CommunitiesCallback() {
            @Override
            public void onSuccess(List<Community> joinedCommunities) {
                // Extract IDs of joined communities
                joinedCommunityIds.clear();
                for (Community community : joinedCommunities) {
                    joinedCommunityIds.add(community.getId());
                }
                // Now load public communities
                loadPublicCommunities();
            }

            @Override
            public void onFailure(Exception e) {
                // Still try to load public communities even if this fails
                loadPublicCommunities();
            }
        });
    }

    private void loadPublicCommunities() {
        showProgress(true);
        communityManager.getAllPublicCommunities(new CommunityFirestoreManager.CommunitiesCallback() {
            @Override
            public void onSuccess(List<Community> communities) {
                showProgress(false);
                adapter.setCommunities(communities);
                adapter.setJoinedCommunityIds(joinedCommunityIds);

                if (communities.isEmpty()) {
                    Toast.makeText(CommunitySelectionActivity.this, "No public communities available yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                showProgress(false);
                Toast.makeText(CommunitySelectionActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

                // Update the adapter to show this community as joined
                joinedCommunityIds.add(community.getId());
                adapter.setJoinedCommunityIds(joinedCommunityIds);
                adapter.notifyDataSetChanged();

                // Optional: Close activity after a short delay
                // finish();
            }

            @Override
            public void onFailure(Exception e) {
                showProgress(false);
                Toast.makeText(CommunitySelectionActivity.this, "Failed to join: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openCommunityDetail(Community community) {
        // Navigate to CommunityDetailActivity
        Intent intent = new Intent(CommunitySelectionActivity.this, CommunityDetailActivity.class);
        intent.putExtra("COMMUNITY_ID", community.getId());
        intent.putExtra("COMMUNITY_NAME", community.getName());
        startActivity(intent);
    }

    private void showCreateCommunityDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Create New Community");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_community, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etCommunityName);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etCommunityDescription);
        com.google.android.material.chip.ChipGroup chipGroup = dialogView.findViewById(R.id.chipGroupVisibility);

        builder.setView(dialogView);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
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