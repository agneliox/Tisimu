package com.lhavanguane.tisimu.ui.adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

//import com.lhavanguane.tisimu.ui.fragments.CommentsFragment;
import com.lhavanguane.tisimu.ui.fragments.LyricsFragment;
import com.lhavanguane.tisimu.ui.fragments.MelodiesFragment;

public class SongDetailPagerAdapter extends FragmentStateAdapter {

    private int songId;

    public SongDetailPagerAdapter(@NonNull FragmentActivity fragmentActivity, int songId) {
        super(fragmentActivity);
        this.songId = songId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                Fragment lyricsFragment = new LyricsFragment();
                Bundle lyricsBundle = new Bundle();
                lyricsBundle.putInt("SONG_ID", songId);
                lyricsFragment.setArguments(lyricsBundle);
                return lyricsFragment;
            case 1:
//                return new CommentsFragment();
            case 2:
                return new MelodiesFragment();
            default:
                return new LyricsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}