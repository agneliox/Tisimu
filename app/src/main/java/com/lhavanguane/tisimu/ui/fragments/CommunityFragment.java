package com.lhavanguane.tisimu.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lhavanguane.tisimu.ui.activities.MainActivity;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.Community;
import com.lhavanguane.tisimu.services.CommunityFirestoreManager;
import com.lhavanguane.tisimu.ui.activities.CommunityDetailActivity;
import com.lhavanguane.tisimu.ui.activities.CommunitySelectionActivity;
import com.lhavanguane.tisimu.ui.adapters.CommunityAdapter;

import java.util.List;

public class CommunityFragment extends Fragment {

    private Toolbar toolbar;
    private RecyclerView rvCommunities;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmptyState;
    private FloatingActionButton fabDiscover;

    private CommunityFirestoreManager communityManager;
    private CommunityAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(requireActivity());
        setHasOptionsMenu(true);
        communityManager = CommunityFirestoreManager.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews(view);
        setupListeners();
        setupToolbar();
        setupRecyclerView();
        setupSwipeRefresh();
        loadJoinedCommunities();  // Changed from loadCommunities to loadJoinedCommunities

        return view;
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.communityToolbar);
        rvCommunities = view.findViewById(R.id.rvCommunities);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        fabDiscover = view.findViewById(R.id.fabDiscover);
    }

    private void setupToolbar() {
        if (getActivity() != null) {
            ((MainActivity) requireActivity()).setSupportActionBar(toolbar);

            toolbar.setNavigationIcon(R.drawable.ic_menu_2);
            toolbar.setNavigationOnClickListener(v -> {
                DrawerLayout drawerLayout = ((MainActivity) requireActivity()).getDrawerLayout();
                if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            });

            toolbar.setTitle("My Communities");
            toolbar.inflateMenu(R.menu.community_fragment_menu);
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_discover) {
                    openCommunitySelection();
                    return true;
                }
                return false;
            });
        }
    }

    private void setupRecyclerView() {
        adapter = new CommunityAdapter();
        rvCommunities.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCommunities.setAdapter(adapter);

        adapter.setOnCommunityActionListener(new CommunityAdapter.OnCommunityActionListener() {
            @Override
            public void onJoinClick(Community community) {
                // This should not be called in this fragment since we only show joined communities
                // But keeping for safety
                Toast.makeText(getContext(), "You are already a member", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onViewClick(Community community) {
                openCommunityDetail(community);
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.md_theme_primary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadJoinedCommunities();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void loadJoinedCommunities() {
        communityManager.getUserJoinedCommunities(new CommunityFirestoreManager.CommunitiesCallback() {
            @Override
            public void onSuccess(List<Community> communities) {
                adapter.setCommunities(communities);

                if (communities.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    rvCommunities.setVisibility(View.GONE);
                    tvEmptyState.setText("You haven't joined any communities yet.\n\nTap the + button to discover communities!");
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    rvCommunities.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Error loading communities: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        if (fabDiscover != null) {
            fabDiscover.setOnClickListener(v -> openCommunitySelection());
        }
    }

    private void openCommunitySelection() {
        Intent intent = new Intent(requireContext(), CommunitySelectionActivity.class);
        startActivity(intent);
    }

    private void openCommunityDetail(Community community) {
        Intent intent = new Intent(requireContext(), CommunityDetailActivity.class);
        intent.putExtra("COMMUNITY_ID", community.getId());
        intent.putExtra("COMMUNITY_NAME", community.getName());
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.community_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_discover) {
            openCommunitySelection();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh communities when returning to this fragment
        loadJoinedCommunities();
    }
}