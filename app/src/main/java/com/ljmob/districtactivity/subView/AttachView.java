package com.ljmob.districtactivity.subView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljmob.districtactivity.R;
import com.londonx.lutil.util.FileUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * Created by london on 15/7/27.
 * 附件
 */
public class AttachView implements View.OnClickListener {
    View rootView;
    ImageView item_detail_attach_imgAttach;
    TextView item_detail_attach_tvAttach;
    ImageLoader imageLoader;
    AttachViewDeleteListener attachViewDeleteListener;

    public File displayFile;
    public String displayUrl;

    public AttachView(Context context, ViewGroup parent, AttachViewDeleteListener attachViewDeleteListener) {
        this.attachViewDeleteListener = attachViewDeleteListener;
        imageLoader = ImageLoader.getInstance();
        rootView = LayoutInflater.from(context).inflate(R.layout.item_detail_attach, parent, false);
        rootView.findViewById(R.id.item_detail_attach_imgDelete).setOnClickListener(this);
        item_detail_attach_imgAttach = (ImageView) rootView.findViewById(R.id.item_detail_attach_imgAttach);
        item_detail_attach_tvAttach = (TextView) rootView.findViewById(R.id.item_detail_attach_tvAttach);
    }

    public void setMedia(File file) {
        displayFile = file;
        setMedia("file://" + file.getAbsolutePath());
    }

    public void setMedia(String url) {
        displayUrl = url;
        switch (FileUtil.getFileType(url)) {
            case music:
                String filePath = url;
                filePath = filePath.substring(filePath.lastIndexOf("/") + 1);
                filePath = filePath.substring(0, filePath.lastIndexOf("."));
                item_detail_attach_tvAttach.setText(filePath);
                break;
            case video:
                imageLoader.displayImage(url, item_detail_attach_imgAttach);
                break;
            case picture:
                imageLoader.displayImage(url, item_detail_attach_imgAttach);
                break;
            case web:
                item_detail_attach_tvAttach.setText(R.string.web_video);
                break;
            case unknown:
                break;
        }
    }

    public View getView() {
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_detail_attach_imgDelete:
                if (attachViewDeleteListener != null) {
                    attachViewDeleteListener.onDeleted(this);
                }
                break;
        }
    }

    public interface AttachViewDeleteListener {
        void onDeleted(AttachView attachView);
    }
}
