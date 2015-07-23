package com.ljmob.districtactivity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.receiver.PushMessage;
import com.ljmob.districtactivity.receiver.PushMessageReceiver;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by london on 15/7/15.
 * 推送
 */
public class TestJPushFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, PushMessageReceiver.PushMessageListener {
    View rootView;
    TextView fragment_jpush_tvMessage;
    Switch fragment_jpush_swToggle;

    boolean isPushStopped;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        initJPush();
        rootView = inflater.inflate(R.layout.test_fragment_jpush, container, false);
        initView(rootView);
        return rootView;
    }

    private void initJPush() {
        isPushStopped = JPushInterface.isPushStopped(getActivity());
        PushMessageReceiver.addPushMessageListener(this);
    }

    private void initView(View root) {
        fragment_jpush_tvMessage = (TextView) root.findViewById(R.id.fragment_jpush_tvMessage);
        fragment_jpush_swToggle = (Switch) root.findViewById(R.id.fragment_jpush_swToggle);

        fragment_jpush_swToggle.setChecked(!isPushStopped);
        fragment_jpush_swToggle.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            JPushInterface.resumePush(getActivity());
        } else {
            JPushInterface.stopPush(getActivity());
        }
    }

    @Override
    public void onPushMessageReceived(PushMessage message) {
        fragment_jpush_tvMessage.setText(message.title + ":" + message.alert + ":" + message.extra);
    }
}
