package com.example.scales_2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public static final int CONNECT_FRAGMENT = 1;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {

        super(fragmentActivity);
    }

    @Override
    public int getItemCount() {

        return 2;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if (position == CONNECT_FRAGMENT) {
            return new ConnectFragment();
        }
        return new WorkFragment();
    }
}
