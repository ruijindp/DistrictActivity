package com.ljmob.districtactivity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ljmob.districtactivity.R;

/**
 * Created by london on 15/7/17.
 * 排行榜
 */
public class RankFragment extends Fragment {
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_undone, container, false);
        return rootView;
    }
}
