package com.ljmob.districtactivity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ljmob.districtactivity.R;

/**
 * Created by london on 15/7/8.
 * TestFragment
 */
public class TestFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.test_fragment_test, container, false);
        return rootView;
    }
}
