package com.ljmob.districtactivity.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ljmob.districtactivity.entity.Result;

import java.util.List;

/**
 * Created by london on 15/7/17.
 * 主界面两页
 */
public class DetailPagerAdapter extends FragmentPagerAdapter {
    Context context;
    List<Fragment> fragments;
    Result result;

    public DetailPagerAdapter(Result result, FragmentManager fm, Context context, List<Fragment> fragments) {
        super(fm);
        this.context = context;
        this.result = result;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
