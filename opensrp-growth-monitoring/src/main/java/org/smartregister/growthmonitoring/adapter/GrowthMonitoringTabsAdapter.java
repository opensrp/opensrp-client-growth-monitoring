package org.smartregister.growthmonitoring.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GrowthMonitoringTabsAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentCollection = new ArrayList<>();
    private List<String> titleCollection = new ArrayList<>();

    public GrowthMonitoringTabsAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void addFragment(String title, Fragment fragment) {
        titleCollection.add(title);
        fragmentCollection.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentCollection.get(position);
    }

    @Override
    public int getCount() {
        return fragmentCollection.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleCollection.get(position);
    }
}
