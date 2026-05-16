package com.lhavanguane.tisimu.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.LiturgyItem;
import com.lhavanguane.tisimu.services.CommunityFirestoreManager;
import com.lhavanguane.tisimu.ui.adapters.LiturgyAdapter;

import java.util.List;

public class LiturgyFragment extends Fragment {

    private String communityId;
    private RecyclerView rvItems;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabAdd;

    private CommunityFirestoreManager communityManager;
    private LiturgyAdapter adapter;
    private boolean isManager = false;

    public static LiturgyFragment newInstance(String communityId) {
        LiturgyFragment fragment = new LiturgyFragment();
        Bundle args = new Bundle();
        args.putString("communityId", communityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            communityId = getArguments().getString("communityId");
        }
        communityManager = CommunityFirestoreManager.getInstance();
        checkIfManager();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_content, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();
        loadLiturgyItems();

        return view;
    }

    private void initViews(View view) {
        rvItems = view.findViewById(R.id.rvItems);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        fabAdd = view.findViewById(R.id.fabAdd);
    }

    private void setupRecyclerView() {
        adapter = new LiturgyAdapter();
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItems.setAdapter(adapter);

        adapter.setOnLiturgyActionListener(new LiturgyAdapter.OnLiturgyActionListener() {
            @Override
            public void onDeleteClick(LiturgyItem item) {
                if (isManager) {
                    deleteLiturgyItem(item);
                }
            }
        });
    }

    private void setupListeners() {
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> showAddLiturgyDialog());
        }
    }

    private void checkIfManager() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        communityManager.isUserManager(communityId, userId, isManager -> {
            this.isManager = isManager;
            if (fabAdd != null) {
                fabAdd.setVisibility(isManager ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void loadLiturgyItems() {
        showProgress(true);
        communityManager.getLiturgyItems(communityId, new CommunityFirestoreManager.LiturgyCallback() {
            @Override
            public void onSuccess(List<LiturgyItem> items) {
                showProgress(false);
                adapter.setItems(items);

                if (items.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    rvItems.setVisibility(View.GONE);
                    tvEmptyState.setText("No liturgy items yet.\n\nTap + to add the first one.");
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    rvItems.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                showProgress(false);
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddLiturgyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Liturgy Item");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_liturgy, null);
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etContent = dialogView.findViewById(R.id.etContent);

        builder.setView(dialogView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            addLiturgyItem(title, content);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addLiturgyItem(String title, String content) {
        showProgress(true);
        communityManager.addLiturgyItem(communityId, title, content, new CommunityFirestoreManager.VoidCallback() {
            @Override
            public void onSuccess() {
                showProgress(false);
                loadLiturgyItems();
                Toast.makeText(getContext(), "Liturgy item added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                showProgress(false);
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteLiturgyItem(LiturgyItem item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this liturgy item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    showProgress(true);
                    communityManager.deleteLiturgyItem(communityId, item.getId(), new CommunityFirestoreManager.VoidCallback() {
                        @Override
                        public void onSuccess() {
                            showProgress(false);
                            loadLiturgyItems();
                            Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            showProgress(false);
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvItems != null) {
            rvItems.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}