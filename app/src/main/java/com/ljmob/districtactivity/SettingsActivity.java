package com.ljmob.districtactivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.ljmob.districtactivity.util.MyApplication;
import com.ljmob.districtactivity.view.NameDialog;
import com.ljmob.districtactivity.view.PasswordDialog;
import com.londonx.lutil.Lutil;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.FileUtil;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by london on 15/7/22.
 * 设置
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, LRequestTool.OnResponseListener {
    public static final String ACTION_LOGOUT = "SettingsActivity:logout";
    public static final int RESULT_GET = 1;
    public static final int RESULT_CROP = 2;
    public static final int API_USER_INFO = 1;

    ImageView activity_settings_imgBg;
    CircleImageView activity_settings_imgHead;
    TextView activity_settings_tvChangeName;
    TextView activity_settings_tvChangePassword;
    SwitchCompat activity_settings_swPush;
    FrameLayout activity_settings_lnPush;
    FrameLayout activity_settings_flLogout;

    ImageLoader imageLoader = ImageLoader.getInstance();
    LRequestTool lRequestTool;

    NameDialog nameDialog;
    PasswordDialog passwordDialog;
    ProgressDialog uploadDialog;
    File cropFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        lRequestTool = new LRequestTool(this);
        initView();
    }

    private void initView() {
        activity_settings_imgBg = (ImageView) findViewById(R.id.activity_settings_imgBg);
        activity_settings_imgHead = (CircleImageView) findViewById(R.id.activity_settings_imgHead);
        activity_settings_tvChangeName = (TextView) findViewById(R.id.activity_settings_tvChangeName);
        activity_settings_tvChangePassword = (TextView) findViewById(R.id.activity_settings_tvChangePassword);
        activity_settings_swPush = (SwitchCompat) findViewById(R.id.activity_settings_swPush);
        activity_settings_lnPush = (FrameLayout) findViewById(R.id.activity_settings_lnPush);
        activity_settings_flLogout = (FrameLayout) findViewById(R.id.activity_settings_flLogout);

        Toolbar tool_bar_transparent = (Toolbar) findViewById(R.id.toolbar_root_transparent);
        setSupportActionBar(tool_bar_transparent);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        imageLoader.displayImage(NetConst.ROOT_URL + MyApplication.currentUser.user_avatar,
                activity_settings_imgHead);

        activity_settings_swPush.setChecked(!JPushInterface.isPushStopped(getApplicationContext()));
        activity_settings_tvChangeName.setOnClickListener(this);
        activity_settings_tvChangePassword.setOnClickListener(this);
        activity_settings_swPush.setOnCheckedChangeListener(this);
        activity_settings_lnPush.setOnClickListener(this);
        activity_settings_flLogout.setOnClickListener(this);
        // TODO: 8.15
//        activity_settings_imgHead.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_settings_tvChangeName:
                nameDialog = new NameDialog(this);
                nameDialog.show();
                break;
            case R.id.activity_settings_tvChangePassword:
                passwordDialog = new PasswordDialog(this);
                passwordDialog.show();
                break;
            case R.id.activity_settings_lnPush:
                activity_settings_swPush.setChecked(!activity_settings_swPush.isChecked());
                break;
            case R.id.activity_settings_flLogout:
//                HashMap<String, Object> params = new DefaultParams();
//                params.put("jpush_out", 0);
//                lRequestTool.doDelete(NetConst.API_SIGN_OUT, params, 0);
                SharedPreferences.Editor editor = Lutil.preferences.edit();
                editor.remove(Lutil.KEY_USER);
                editor.remove(MessageActivity.KEY_MESSAGE_COUNT);
                MyApplication.currentUser = null;
                editor.apply();
                finish();
                JPushInterface.setAlias(this, "visitor", null);
                sendBroadcast(new Intent(ACTION_LOGOUT));
                break;
            case R.id.activity_settings_imgHead:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("return-data", true);
                startActivityForResult(intent, RESULT_GET);
                cropFile = new File(getCacheDir(), System.currentTimeMillis() + "cropped.jpg");
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            JPushInterface.resumePush(getApplicationContext());
        } else {
            JPushInterface.stopPush(getApplicationContext());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(LResponse response) {
        if (uploadDialog != null && uploadDialog.isShowing()) {
            uploadDialog.dismiss();
        }
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        if (cropFile == null || !cropFile.exists()) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(response.body);
            MyApplication.currentUser.user_avatar = jsonObject.getString("avatar");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = Lutil.preferences.edit();
        editor.remove(Lutil.KEY_USER);
        editor.putString(Lutil.KEY_USER, new Gson().toJson(MyApplication.currentUser));
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case RESULT_GET:
                Uri uri = intent.getData();
                File selectedFile = FileUtil.getFileFromUri(uri);
                if (selectedFile == null || selectedFile.length() == 0) {
                    ToastUtil.show(R.string.toast_file_err);
                    return;
                }
                Uri destination = Uri.fromFile(cropFile);
                Crop.of(uri, destination).asSquare().start(this, RESULT_CROP);
                break;
            case RESULT_CROP:
                if (!cropFile.exists() || cropFile.length() == 0) {
                    ToastUtil.show(R.string.toast_file_err);
                    return;
                }
                if (uploadDialog == null) {
                    uploadDialog = new ProgressDialog(this);
                    uploadDialog.setCancelable(false);
                    uploadDialog.setMessage(getString(R.string.dialog_uploading));
                }
                uploadDialog.show();
                HashMap<String, Object> params = new DefaultParams();
                params.put("avatar", cropFile);
                lRequestTool.doPost(NetConst.API_USER_INFO, params, API_USER_INFO);
                imageLoader.displayImage("file://" + cropFile.getAbsolutePath(),
                        activity_settings_imgHead);
                break;
        }
    }
}
