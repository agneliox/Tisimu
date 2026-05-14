package com.lhavanguane.tisimu.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lhavanguane.tisimu.R;

public class HomeFragment extends Fragment {

    private TextView tvDailyVerse, tvVerseReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvDailyVerse = view.findViewById(R.id.tvDailyVerse);
        tvVerseReference = view.findViewById(R.id.tvVerseReference);

        loadDailyVerse();

        return view;
    }

    private void loadDailyVerse() {
        // TODO: Implement daily verse from API or local storage
        tvDailyVerse.setText("For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life.");
        tvVerseReference.setText("John 3:16");
    }

    private void refreshVerse() {
        loadDailyVerse();
        Toast.makeText(getContext(), "Verse refreshed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh_verse) {
            refreshVerse();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}