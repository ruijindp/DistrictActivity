package com.ljmob.districtactivity.view;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.ljmob.districtactivity.util.MyApplication;
import com.londonx.lutil.Lutil;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.HashMap;

/**
 * Created by london on 15/7/22.
 * 更用户名
 */
public class NameDialog extends Dialog implements LRequestTool.OnResponseListener, View.OnClickListener {
    private static final int API_USER_INFO = 1;

    TextView dialog_name_tvOld;
    EditText dialog_name_etNew;
    TextView dialog_name_tvChange;

    LRequestTool lRequestTool;

    boolean isChanging = false;
    String newName;

    public NameDialog(Context context) {
        this(context, R.style.DefaultDialog);
    }

    public NameDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_name);
        lRequestTool = new LRequestTool(this);
        initView();
    }

    private void initView() {
        dialog_name_tvOld = (TextView) findViewById(R.id.dialog_name_tvOld);
        dialog_name_etNew = (EditText) findViewById(R.id.dialog_name_etNew);
        dialog_name_tvChange = (TextView) findViewById(R.id.dialog_name_tvChange);

        dialog_name_tvOld.setText(MyApplication.currentUser.user_name);
        dialog_name_tvChange.setOnClickListener(this);
    }

    @Override
    public void onResponse(LResponse response) {
        isChanging = false;
        dialog_name_tvChange.setText(R.string.change);
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        if (response.requestCode != API_USER_INFO) {
            return;
        }
        MyApplication.currentUser.user_name = newName;
        SharedPreferences.Editor editor = Lutil.preferences.edit();
        editor.remove(Lutil.KEY_USER);
        editor.putString(Lutil.KEY_USER, new Gson().toJson(MyApplication.currentUser));
        editor.apply();
        dismiss();
        ToastUtil.show(R.string.changed);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_name_tvChange:
                if (isChanging) {
                    return;
                }
                newName = dialog_name_etNew.getText().toString();
                if (newName.length() == 0) {
                    ToastUtil.show(R.string.err_name);
                    return;
                }
                isChanging = true;
                dialog_name_tvChange.setText(R.string.changing);
                HashMap<String, Object> params = new DefaultParams();
                params.put("name", newName);
                lRequestTool.doPost(NetConst.API_USER_INFO, params, API_USER_INFO);
                break;
        }
    }
}
