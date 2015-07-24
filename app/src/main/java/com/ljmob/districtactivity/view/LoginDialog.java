package com.ljmob.districtactivity.view;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by london on 15/7/9.
 * 登录对话框
 * Update at 2015-07-21 20:17:46
 */
public class LoginDialog extends Dialog implements View.OnClickListener, LRequestTool.OnResponseListener, AdapterView.OnItemClickListener {
    private static final int API_SIGN_IN = 1;

    AutoCompleteTextView dialog_name_etUserName;
    EditText dialog_name_etPassword;
    TextView dialog_name_tvLogin;

    LoginListener loginListener;
    LRequestTool lRequestTool;
    boolean isDoingLogin;
    List<UserTool.SimpleUser> simpleUsers;

    public LoginDialog(Context context) {
        this(context, R.style.DefaultDialog);
    }

    public LoginDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_login);
        simpleUsers = UserTool.getSimpleUsers(getContext());
        lRequestTool = new LRequestTool(this);
        initView();
    }

    private void initView() {
        dialog_name_etUserName = (AutoCompleteTextView) findViewById(R.id.dialog_name_etUserName);
        dialog_name_etPassword = (EditText) findViewById(R.id.dialog_name_etPassword);
        dialog_name_tvLogin = (TextView) findViewById(R.id.dialog_name_tvLogin);

        dialog_name_tvLogin.setOnClickListener(this);
        List<String> userNames = new ArrayList<>(simpleUsers.size());
        for (int i = 0; i < simpleUsers.size(); i++) {
            userNames.add(simpleUsers.get(i).username);
        }
        dialog_name_etUserName.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, userNames));
        dialog_name_etUserName.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_name_tvLogin:
                if (isDoingLogin) {
                    return;
                }
                dialog_name_tvLogin.setText(R.string.login_ing);
                HashMap<String, Object> params = new DefaultParams();
                params.put("account_id", dialog_name_etUserName.getText().toString());
                params.put("password", dialog_name_etPassword.getText().toString());
                params.put("jpush_login", Build.VERSION.SDK_INT);
                lRequestTool.doPost(NetConst.API_SIGN_IN, params, API_SIGN_IN);
                isDoingLogin = true;
                break;
        }
    }

    @Override
    public void onResponse(LResponse response) {
        isDoingLogin = false;
        dialog_name_tvLogin.setText(R.string.login);
        if (response.responseCode == 401) {
            ToastUtil.show(R.string.err_login);
            return;
        }
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        switch (response.requestCode) {
            case API_SIGN_IN:
                MyApplication.currentUser = new Gson().fromJson(response.body, User.class);
                if (MyApplication.currentUser.token == null) {
                    ToastUtil.serverErr(response.responseCode);
                    return;
                }
                if (MyApplication.currentUser.token.contains("-")) {//jpush别名不可包含"-"
                    HashMap<String, Object> params = new DefaultParams();
                    params.put("jpush_out", 0);
                    lRequestTool.doDelete(NetConst.API_SIGN_OUT, params, 0);
                    dialog_name_tvLogin.performClick();
                    return;
                }
                if (loginListener != null) {
                    loginListener.loginSuccess(this);
                }
                JPushInterface.setAlias(getContext().getApplicationContext(), MyApplication.currentUser.token, null);
                dismiss();
                UserTool.rememberUser(getContext(),
                        dialog_name_etUserName.getText().toString(),
                        dialog_name_etPassword.getText().toString());
                SharedPreferences.Editor editor = Lutil.preferences.edit();
                editor.remove(Lutil.KEY_USER);
                editor.putString(Lutil.KEY_USER, response.body);
                editor.apply();
                break;
        }
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dialog_name_etPassword.setText(simpleUsers.get(position).password);
    }

    public interface LoginListener {
        void loginSuccess(LoginDialog dialog);
    }
}
