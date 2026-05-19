package com.lhavanguane.tisimu.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.appbar.MaterialToolbar;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.Community;
import com.lhavanguane.tisimu.services.CommunityFirestoreManager;
import com.lhavanguane.tisimu.ui.adapters.CommunityPagerAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommunityDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CircleImageView ivCommunityAvatar;
    private ImageView ivCommunityCover;
    private TextView tvCommunityNameExpanded;
    private ChipGroup chipGroupStats;
    private Chip chipMemberCount;
    private Chip chipVisibility;
    private Chip chipJoinCode;

    private CommunityFirestoreManager communityManager;
    private String communityId;
    private String communityName;
    private Community currentCommunity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_community_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_community_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        communityManager = CommunityFirestoreManager.getInstance();
        communityId = getIntent().getStringExtra("COMMUNITY_ID");
        communityName = getIntent().getStringExtra("COMMUNITY_NAME");

        if (communityId == null) {
            finish();
            return;
        }

        initViews();
        loadCommunityDetails();
        setupToolbar();
        setupViewPager();
    }

    private void initViews() {
        toolbar = findViewById(R.id.communityDetailToolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setupViewPager() {
        CommunityPagerAdapter pagerAdapter = new CommunityPagerAdapter(this, communityId);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Agenda");
                            break;
                        case 1:
                            tab.setText("Announcements");
                            break;
                        case 2:
                            tab.setText("Files");
                            break;
                        case 3:
                            tab.setText("Members");
                            break;
                    }
                }
        ).attach();
    }

    private void loadCommunityDetails() {
        communityManager.getCommunity(communityId, new CommunityFirestoreManager.CommunityCallback() {
            @Override
            public void onSuccess(Community community) {
                currentCommunity = community;
                displayCommunityDetails();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(CommunityDetailActivity.this, "Error loading community: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCommunityDetails() {
        if (currentCommunity == null) return;

        toolbar.setTitle(currentCommunity.getName());

        // Update visibility chip
        if (currentCommunity.isPrivate()) {

        } else {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.community_detail_menu, menu);

        // Show/hide manage menu based on user role
        if (currentCommunity != null && currentCommunity.isUserManager()) {
            menu.findItem(R.id.action_manage).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            shareCommunity();
            return true;
        } else if (item.getItemId() == R.id.action_manage) {
            showManageDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareCommunity() {
        if (currentCommunity != null) {
            String shareText = "Join me on Tisimu: " + currentCommunity.getName() + "\n\n" +
                    currentCommunity.getDescription();

            if (currentCommunity.isPrivate()) {
                shareText += "\n\nJoin Code: " + currentCommunity.getJoinCode();
            }

            android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Join my community on Tisimu");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            startActivity(android.content.Intent.createChooser(shareIntent, "Share via"));
        }
    }

    private void showManageDialog() {
        // TODO: Implement manage dialog (edit community, delete, etc.)
        Toast.makeText(this, "Manage community coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}