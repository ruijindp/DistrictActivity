package com.ljmob.districtactivity.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.ljmob.districtactivity.R;

/**
 * Created by london on 15/7/9.
 * 媒体类型选择器
 */
public class MediaDialog extends Dialog implements View.OnClickListener {
    View dialog_media_tvPicture;
    View dialog_media_tvRecorder;
    View dialog_media_tvVideo;

    OnTypeSelectListener onTypeSelectListener;

    public MediaDialog(Context context) {
        this(context, R.style.DefaultDialog);
    }

    public MediaDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_media);
        initView();
    }

    private void initView() {
        dialog_media_tvPicture = findViewById(R.id.dialog_media_tvPicture);
        dialog_media_tvRecorder = findViewById(R.id.dialog_media_tvRecorder);
        dialog_media_tvVideo = findViewById(R.id.dialog_media_tvVideo);

        dialog_media_tvPicture.setOnClickListener(this);
        dialog_media_tvRecorder.setOnClickListener(this);
        dialog_media_tvVideo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_media_tvPicture:
                onTypeSelectListener.onTypeSelect(Type.picture);
                break;
            case R.id.dialog_media_tvRecorder:
                onTypeSelectListener.onTypeSelect(Type.recorder);
                break;
            case R.id.dialog_media_tvVideo:
                onTypeSelectListener.onTypeSelect(Type.video);
                break;
        }
        dismiss();
    }

    public void setOnTypeSelectListener(OnTypeSelectListener onTypeSelectListener) {
        this.onTypeSelectListener = onTypeSelectListener;
    }

    public interface OnTypeSelectListener {
        void onTypeSelect(Type type);
    }

    public enum Type {
        picture, recorder, video
    }
}
