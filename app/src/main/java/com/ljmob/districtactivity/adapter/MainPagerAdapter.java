package com.ljmob.districtactivity.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.fragment.RankFragment;
import com.ljmob.districtactivity.fragment.ShowcaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by london on 15/7/17.
 * 主界面两页
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    public Context context;
    public List<Fragment> fragments;
    public RankFragment rankFragment;

    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        fragments = new ArrayList<>(2);
        rankFragment = new RankFragment();

        fragments.add(new ShowcaseFragment());
        fragments.add(rankFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case 0:
                title = context.getString(R.string.tab_showcase);
                break;
            case 1:
                title = context.getString(R.string.tab_rank);
                break;
        }
        return title;
    }
}
