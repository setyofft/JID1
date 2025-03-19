package com.jasamarga.jid.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class TabAdapter extends FragmentStatePagerAdapter {

    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    ArrayList<String> stringArrayList = new ArrayList<>();

    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

    }

    public void UpdateFragment(Fragment fragment, int position) {
        fragmentArrayList.set(position, fragment);
        notifyDataSetChanged();
    }
    public void AddFragment(Fragment fragment,String s){
        fragmentArrayList.add(fragment);
        stringArrayList.add(s);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return stringArrayList.get(position);
    }

    // Add this method to retrieve fragment by position
    public Fragment getFragment(int position) {
        if (position >= 0 && position < fragmentArrayList.size()) {
            return fragmentArrayList.get(position);
        } else {
            return null;
        }
    }
}
