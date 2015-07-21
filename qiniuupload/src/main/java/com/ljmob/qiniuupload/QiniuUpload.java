package com.ljmob.qiniuupload;

import com.ljmob.qiniuupload.impl.UploadListener;

import java.io.File;
import java.util.HashMap;

/**
 * Created by london on 15/7/15.
 * upload file to qiniu
 */
public class QiniuUpload {
    private static final String API_TOKEN = "";
    private static int tokenRequestCode = 0;

    UploadListener uploadListener;
    HashMap<String, File> tokenFile;

    public QiniuUpload(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }

    public void upload(File uploadFile) {

    }
}
