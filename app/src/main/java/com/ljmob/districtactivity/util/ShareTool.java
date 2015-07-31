package com.ljmob.districtactivity.util;

import android.content.Context;

import com.ljmob.districtactivity.entity.Shareable;
import com.ljmob.districtactivity.net.NetConst;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by london on 15/7/29.
 * 分享
 */
public class ShareTool implements LRequestTool.OnResponseListener, LRequestTool.OnDownloadListener {
    private Platform platform;
    private Context context;
    private LRequestTool lRequestTool;
    private Shareable shareable;

    public ShareTool(Context context, Platform platform, Shareable shareable) {
        this.context = context;
        this.platform = platform;
        this.shareable = shareable;
        lRequestTool = new LRequestTool(this);
        lRequestTool.setOnDownloadListener(this);
    }

    public void share() {
        lRequestTool.download(NetConst.ROOT_URL + shareable.imgUrl, 0);
    }

    @Override
    public void onResponse(LResponse response) {
    }

    @Override
    public void onStartDownload(LResponse response) {
    }

    @Override
    public void onDownloading(float progress) {
    }

    @Override
    public void onDownloaded(LResponse response) {
        OnekeyShare oks = new OnekeyShare();
        oks.setSilent(true);
        oks.setTitle(shareable.title);
        oks.setText(shareable.content + "\n" + shareable.url);
//        oks.setImageUrl(shareable.imgUrl);
        oks.setImagePath(response.downloadFile.getAbsolutePath());
        oks.setTitleUrl(shareable.url);
        oks.setPlatform(platform.getName());
        oks.show(context);
    }
}
