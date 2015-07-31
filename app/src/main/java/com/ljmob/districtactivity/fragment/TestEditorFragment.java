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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.view.MediaDialog;
import com.ljmob.districtactivity.view.MediaView;
import com.londonx.lutil.util.FileUtil;
import com.londonx.lutil.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by london on 15/7/10.
 * 内容编辑界面
 */
public class TestEditorFragment extends Fragment implements View.OnClickListener, MediaDialog.OnTypeSelectListener {
    View rootView;
    EditText fragment_editor_etContent;
    View fragment_editor_btnAdd;
    LinearLayout fragment_editor_linearContent;
    MediaDialog mediaDialog;

    List<MediaView> mediaViews;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.test_fragment_editor, container, false);
        mediaViews = new ArrayList<>();
        initView(rootView);
        return rootView;
    }

    private void initView(View root) {
        fragment_editor_etContent = (EditText) root.findViewById(R.id.fragment_editor_etContent);
        fragment_editor_linearContent = (LinearLayout) root.findViewById(R.id.fragment_editor_linearContent);
        fragment_editor_btnAdd = root.findViewById(R.id.fragment_editor_btnAdd);

        fragment_editor_btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_editor_btnAdd:
                if (mediaDialog == null) {
                    mediaDialog = new MediaDialog(getActivity());
                    mediaDialog.setOnTypeSelectListener(this);
                }
                mediaDialog.show();
                break;
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
                intent.putExtra("return-data", true);
                requestCode = 1;
                break;
            case audio:
                intent.setType("audio/*");
                intent.putExtra("return-data", true);
                requestCode = 2;
                break;
            case video:
                intent.setType("video/*");
                intent.putExtra("return-data", true);
                requestCode = 3;
                break;
        }
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
            MediaView mediaView = (MediaView) LayoutInflater.from(getActivity())
                    .inflate(R.layout.item_media, fragment_editor_linearContent, false);
            mediaView.setFile(selectedFile);
            fragment_editor_linearContent.addView(mediaView, fragment_editor_linearContent.getChildCount() - 1);
            mediaViews.add(mediaView);
        }
    }
}
