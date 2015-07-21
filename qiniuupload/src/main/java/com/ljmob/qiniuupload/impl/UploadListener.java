package com.ljmob.qiniuupload.impl;

import com.ljmob.qiniuupload.entity.UploadInfo;

/**
 * Created by london on 15/7/15.
 * upload listener for upload progress
 */
public interface UploadListener {

    void onQiniuUploading(UploadInfo uploadInfo);
}
