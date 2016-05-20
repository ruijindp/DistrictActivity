package com.ljmob.districtactivity.view;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.entity.User;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.ljmob.districtactivity.util.MyApplication;
import com.londonx.lutil.Lutil;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.londonx.lutil.util.UserTool;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by LanJing_ on 2016/4/25.
 */
public class RegisterDialog extends Dialog implements LRequestTool.OnResponseListener, View.OnClickListener {

    LRequestTool lRequestTool;
    EditText dialogReginsterEtUserName;
    EditText dialogReginsterEtPassword;
    EditText dialogReginsterEtConfimPassword;
    EditText dialogReginsterEtNickname;

    boolean isDoingRegister;
    private static final int API_REGISTER = 1;
    private TextView dialog_register_tvRegister;

    public RegisterDialog(Context context) {
        this(context, R.style.DefaultDialog);
    }

    public RegisterDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_register);

        lRequestTool = new LRequestTool(this);
        initView();
    }

    private void initView() {
        dialog_register_tvRegister = (TextView) findViewById(R.id.dialog_register_tvRegister);
        dialogReginsterEtUserName = (EditText) findViewById(R.id.dialog_register_etUserName);
        dialogReginsterEtPassword = (EditText) findViewById(R.id.dialog_register_etPassword);
        dialogReginsterEtConfimPassword = (EditText) findViewById(R.id.dialog_register_etConfimPassword);
        dialogReginsterEtNickname = (EditText) findViewById(R.id.dialog_register_etNickname);

        dialog_register_tvRegister.setOnClickListener(this);
    }

    @Override
    public void onResponse(LResponse response) {
        isDoingRegister = false;
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        switch (response.requestCode){
            case API_REGISTER:
                MyApplication.currentUser = new Gson().fromJson(response.body, User.class);
                if (MyApplication.currentUser.token == null) {
                    ToastUtil.serverErr(response.responseCode);
                    return;
                }
                if (MyApplication.currentUser.token.contains("-")) {//jpush别名不可包含"-"
                    HashMap<String, Object> params = new DefaultParams();
                    params.put("jpush_out", 0);
                    lRequestTool.doDelete(NetConst.API_SIGN_OUT, params, 0);
                    return;
                }
                JPushInterface.setAlias(getContext().getApplicationContext(), MyApplication.currentUser.token, null);

                dismiss();
                UserTool.rememberUser(getContext(), dialogReginsterEtUserName.getText().toString(),
                        dialogReginsterEtPassword.getText().toString());
                SharedPreferences.Editor editor = Lutil.preferences.edit();
                editor.remove(Lutil.KEY_USER);
                editor.putString(Lutil.KEY_USER, response.body);
                editor.apply();
                ToastUtil.show(R.string.toast_register_success);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (isDoingRegister) {
            return;
        }
        if (dialogReginsterEtUserName.getText().toString().length() == 0) {
            ToastUtil.show(R.string.toast_registr_username);
            return;
        }
        if (dialogReginsterEtPassword.getText().toString().length() == 0) {
            ToastUtil.show(R.string.toast_registr_password);
        } else {
            if (dialogReginsterEtPassword.getText().toString().length() < 6){
                ToastUtil.show(R.string.toast_register_passwordLength);
            } else if (!dialogReginsterEtPassword.getText().toString().equals(dialogReginsterEtConfimPassword.getText().toString())) {
                ToastUtil.show(R.string.toast_different_password);
                return;
            }
        }
        if (dialogReginsterEtNickname.getText().toString().length() == 0){
            ToastUtil.show(R.string.toast_registr_nickname);
            return;
        }

        HashMap<String, Object> params = new DefaultParams();
        params.put("account_id", dialogReginsterEtUserName.getText().toString());
        params.put("password", dialogReginsterEtPassword.getText().toString());
        params.put("password_confirmation", dialogReginsterEtConfimPassword.getText().toString());
        params.put("name", dialogReginsterEtNickname.getText().toString());
        lRequestTool.doPost(NetConst.API_USER, params, API_REGISTER);
        isDoingRegister = true;
    }
}
