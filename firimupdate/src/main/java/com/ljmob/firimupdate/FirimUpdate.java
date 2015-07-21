package com.ljmob.firimupdate;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.ljmob.firimupdate.entity.Update;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by london on 15/6/19.
 * check update from Fir.im
 */
public class FirimUpdate {
    private OnUpdateListener onUpdateListener;

    public void check(final Context context) {
        FirimUpdate firimUpdate = new FirimUpdate();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://fir.im/api/v2/app/version/" + context.getPackageName();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (onUpdateListener == null) {
                    return;
                }
                Update update = new Update();
                try {
                    update.changelog = response.getString("changelog");
                    update.installUrl = response.getString("installUrl");
                    update.name = response.getString("name");
                    update.update_url = response.getString("update_url");
                    update.version = response.getInt("version");
                    update.versionShort = response.getString("versionShort");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PackageManager manager = context.getPackageManager();
                int currentVersion = -1;
                try {
                    PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                    currentVersion = info.versionCode;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

//                if (update.version > 1) {
                if (update.version > currentVersion) {
                    onUpdateListener.onUpdateFound(update);
                }
            }
        });
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public interface OnUpdateListener {
        void onUpdateFound(Update newUpdate);
    }
}
