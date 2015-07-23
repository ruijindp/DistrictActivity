package com.ljmob.districtactivity.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.HashMap;

/**
 * Created by london on 15/7/22.
 * 更密码
 */
public class PasswordDialog extends Dialog implements LRequestTool.OnResponseListener, View.OnClickListener {
    private static final int API_USER_INFO = 1;

    View dialog_password_lnErr;
    EditText dialog_password_etOld;
    EditText dialog_password_etNew;
    EditText dialog_password_etConfirm;
    TextView dialog_password_tvChange;

    LRequestTool lRequestTool;

    boolean isChanging = false;
    String newPassword;

    public PasswordDialog(Context context) {
        this(context, R.style.DefaultDialog);
    }

    public PasswordDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_password);
        lRequestTool = new LRequestTool(this);
        initView();
    }

    private void initView() {
        dialog_password_lnErr = findViewById(R.id.dialog_password_lnErr);
        dialog_password_etOld = (EditText) findViewById(R.id.dialog_password_etOld);
        dialog_password_etNew = (EditText) findViewById(R.id.dialog_password_etNew);
        dialog_password_etConfirm = (EditText) findViewById(R.id.dialog_password_etConfirm);
        dialog_password_tvChange = (TextView) findViewById(R.id.dialog_password_tvChange);

        dialog_password_tvChange.setOnClickListener(this);
    }

    @Override
    public void onResponse(LResponse response) {
        isChanging = false;
        dialog_password_tvChange.setText(R.string.change);
        if (response.responseCode == 401) {
            dialog_password_lnErr.setVisibility(View.VISIBLE);
            return;
        }
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        if (response.requestCode != API_USER_INFO) {
            return;
        }
        dismiss();
        ToastUtil.show(R.string.changed);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_password_tvChange:
                if (isChanging) {
                    return;
                }
                String oldPassword = dialog_password_etOld.getText().toString();
                newPassword = dialog_password_etNew.getText().toString();
                String confirmPassword = dialog_password_etConfirm.getText().toString();
                if (newPassword.length() < 6) {
                    ToastUtil.show(R.string.err_pass_short);
                    return;
                }
                if (newPassword.length() == 0) {
                    ToastUtil.show(R.string.err_pass_null);
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    ToastUtil.show(R.string.err_pass_match);
                    return;
                }
                isChanging = true;
                dialog_password_tvChange.setText(R.string.changing);
                HashMap<String, Object> params = new DefaultParams();
                params.put("old_password", oldPassword);
                params.put("password", newPassword);
                params.put("password_confirmation", confirmPassword);
                lRequestTool.doPost(NetConst.API_USER_INFO, params, API_USER_INFO);
                dialog_password_lnErr.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
