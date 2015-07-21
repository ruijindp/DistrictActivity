package com.ljmob.districtactivity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ljmob.districtactivity.MainActivity;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by london on 15/7/15.
 * 推送广播
 */
public class PushMessageReceiver extends BroadcastReceiver {
    private static final List<PushMessageListener> PUSH_MESSAGE_LISTENERS = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_OPENED)) {
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
            return;
        }
        String title = intent.getStringExtra(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        String alert = intent.getStringExtra(JPushInterface.EXTRA_ALERT);
        String extra = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
        PushMessage pushMessage = new PushMessage();
        pushMessage.title = title;
        pushMessage.alert = alert;
        pushMessage.extra = extra;
        synchronized (PUSH_MESSAGE_LISTENERS) {
            for (PushMessageListener pushMessageListener : PUSH_MESSAGE_LISTENERS) {
                pushMessageListener.onPushMessageReceived(pushMessage);
            }
        }
    }

    public static void addPushMessageListener(PushMessageListener listener) {
        PUSH_MESSAGE_LISTENERS.add(listener);
    }

    public interface PushMessageListener {
        void onPushMessageReceived(PushMessage message);
    }
}
