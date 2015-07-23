package com.ljmob.districtactivity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.util.DefaultParams;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.HashMap;

/**
 * Created by london on 15/7/9.
 * 登录界面
 */
public class TestLoginFragment extends Fragment implements View.OnClickListener, LRequestTool.OnResponseListener {
    View rootView;
    EditText fragment_login_etUserName;
    EditText fragment_login_etPassword;
    CheckBox fragment_login_rbRemember;
    CheckBox fragment_login_rbAutoLogin;
    Button fragment_login_btnLogin;

    LRequestTool lRequestTool;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.test_fragment_login, container, false);
        lRequestTool = new LRequestTool(this);
        initView(rootView);
        return rootView;
    }

    private void initView(View root) {
        fragment_login_etUserName = (EditText) root.findViewById(R.id.fragment_login_etUserName);
        fragment_login_etPassword = (EditText) root.findViewById(R.id.fragment_login_etPassword);
        fragment_login_rbRemember = (CheckBox) root.findViewById(R.id.fragment_login_rbRemember);
        fragment_login_rbAutoLogin = (CheckBox) root.findViewById(R.id.fragment_login_rbAutoLogin);
        fragment_login_btnLogin = (Button) root.findViewById(R.id.fragment_login_btnLogin);

        fragment_login_btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_login_btnLogin:
                HashMap<String, Object> params = new DefaultParams();
                params.put("account_id", fragment_login_etUserName.getText().toString());
                params.put("password", fragment_login_etPassword.getText().toString());
                lRequestTool.doPost("http://192.168.31.148:3002/users/sign_in.json", params, 0);
                break;
        }
    }

    @Override
    public void onResponse(LResponse response) {
        ToastUtil.show(response.responseCode + "");
    }
}
