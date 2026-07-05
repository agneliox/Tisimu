package com.lhavanguane.tisimu.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.lhavanguane.tisimu.R;

public class FilesFragment extends Fragment {

    private String communityId;

    public static FilesFragment newInstance(String communityId) {
        FilesFragment fragment = new FilesFragment();
        Bundle args = new Bundle();
        args.putString("communityId", communityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        TextView tvContent = view.findViewById(R.id.tvContent);
        tvContent.setText("File sharing coming soon!\n\nCommunity ID: " + communityId);
        return view;
    }
}