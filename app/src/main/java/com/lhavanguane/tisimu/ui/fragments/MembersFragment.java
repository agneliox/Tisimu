package com.lhavanguane.tisimu.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.CommunityMember;
import com.lhavanguane.tisimu.services.CommunityFirestoreManager;
import com.lhavanguane.tisimu.ui.adapters.MemberAdapter;

import java.util.List;

public class MembersFragment extends Fragment {

    private String communityId;
    private RecyclerView rvMembers;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    private CommunityFirestoreManager communityManager;
    private MemberAdapter adapter;

    public static MembersFragment newInstance(String communityId) {
        MembersFragment fragment = new MembersFragment();
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_content, container, false);

        initViews(view);
        setupRecyclerView();
        loadMembers();

        return view;
    }

    private void initViews(View view) {
        rvMembers = view.findViewById(R.id.rvItems);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        view.findViewById(R.id.fabAdd).setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        adapter = new MemberAdapter();
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMembers.setAdapter(adapter);
    }

    private void loadMembers() {
        showProgress(true);
        communityManager.getMembers(communityId, new CommunityFirestoreManager.MembersCallback() {
            @Override
            public void onSuccess(List<CommunityMember> members) {
                showProgress(false);
                adapter.setMembers(members);

                if (members.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    rvMembers.setVisibility(View.GONE);
                    tvEmptyState.setText("No members yet");
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    rvMembers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                showProgress(false);
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvMembers != null) {
            rvMembers.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}