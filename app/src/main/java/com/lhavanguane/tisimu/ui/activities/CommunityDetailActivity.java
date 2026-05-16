package com.lhavanguane.tisimu.ui.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.services.CommunityFirestoreManager;
import com.lhavanguane.tisimu.ui.adapters.CommunityPagerAdapter;

public class CommunityDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private CommunityFirestoreManager communityManager;
    private String communityId;
    private String communityName;
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
        setupToolbar();
        setupViewPager();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(communityName != null ? communityName : "Community");
        }
    }

    private void setupViewPager() {
        CommunityPagerAdapter pagerAdapter = new CommunityPagerAdapter(this, communityId);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Liturgy");
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}