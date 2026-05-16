package com.lhavanguane.tisimu.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.lhavanguane.tisimu.ui.fragments.AnnouncementsFragment;
import com.lhavanguane.tisimu.ui.fragments.FilesFragment;
import com.lhavanguane.tisimu.ui.fragments.LiturgyFragment;
import com.lhavanguane.tisimu.ui.fragments.MembersFragment;

public class CommunityPagerAdapter extends FragmentStateAdapter {

    private String communityId;

    public CommunityPagerAdapter(@NonNull FragmentActivity fragmentActivity, String communityId) {
        super(fragmentActivity);
        this.communityId = communityId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return LiturgyFragment.newInstance(communityId);
            case 1:
                return AnnouncementsFragment.newInstance(communityId);
            case 2:
                return FilesFragment.newInstance(communityId);
            case 3:
                return MembersFragment.newInstance(communityId);
            default:
                return LiturgyFragment.newInstance(communityId);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}