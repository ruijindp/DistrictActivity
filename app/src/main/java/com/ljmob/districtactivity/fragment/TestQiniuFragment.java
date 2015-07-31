package com.ljmob.districtactivity.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.view.MediaDialog;
import com.londonx.lutil.util.FileUtil;
import com.londonx.lutil.util.ToastUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by london on 15/7/15.
 * 七牛
 */
public class TestQiniuFragment extends Fragment
        implements View.OnClickListener,
        UpCompletionHandler,
        MediaDialog.OnTypeSelectListener {
    View rootView;
    Button fragment_qiniu_btnAdd;
    LinearLayout fragment_qiniu_linear;
    MediaDialog mediaDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.test_fragment_qiniu, container, false);
        return rootView;
    }

    private void initView(View root) {
        fragment_qiniu_btnAdd = (Button) root.findViewById(R.id.fragment_qiniu_btnAdd);
        fragment_qiniu_linear = (LinearLayout) root.findViewById(R.id.fragment_qiniu_linear);

        fragment_qiniu_btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mediaDialog == null) {
            mediaDialog = new MediaDialog(getActivity());
            mediaDialog.setOnTypeSelectListener(this);
        }
    }

    @Override
    public void onTypeSelect(MediaDialog.Type type) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        int requestCode = 0;
        switch (type) {
            case picture:
                intent.setType("image/*");
                intent.putExtra("crop", true);
                requestCode = 1;
                break;
            case audio:
                intent.setType("audio/*");
                requestCode = 2;
                break;
            case video:
                intent.setType("video/*");
                requestCode = 3;
                break;
        }
        intent.putExtra("return-data", true);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            File selectedFile = FileUtil.getFileFromUri(uri);
            if (selectedFile == null || selectedFile.length() == 0) {
                ToastUtil.show(R.string.toast_file_err);
                return;
            }
            UploadManager uploadManager = new UploadManager();
            uploadManager.put(selectedFile, null, "", this, null);
        }
    }

    @Override
    public void complete(String key, ResponseInfo info, JSONObject response) {
    }
}
