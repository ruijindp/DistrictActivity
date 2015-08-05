package com.ljmob.districtactivity.util;

import android.content.Context;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.entity.Shareable;
import com.ljmob.districtactivity.net.NetConst;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by london on 15/7/29.
 * 分享
 */
public class ShareTool implements
        LRequestTool.OnResponseListener,
        LRequestTool.OnDownloadListener {
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
        Platform.ShareParams shareParams=new Platform.ShareParams();
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        shareParams.setTitle(shareable.title);
        shareParams.setText(shareable.content + "\n" + shareable.url);
        shareParams.setImagePath(response.downloadFile.getAbsolutePath());
        shareParams.setUrl(shareable.url);
        shareParams.setTitleUrl(shareable.url);
        platform.share(shareParams);


//        if (platform instanceof Wechat) {
//            Wechat.ShareParams shareParams = new Wechat.ShareParams();
//            shareParams.setShareType(Platform.SHARE_WEBPAGE);
//            shareParams.setTitle(shareable.title);
//            shareParams.setText(shareable.content + "\n" + shareable.url);
//            shareParams.setImagePath(response.downloadFile.getAbsolutePath());
//            shareParams.setUrl(shareable.url);
//            platform.share(shareParams);
//        } else if (platform instanceof WechatMoments) {
//            WechatMoments.ShareParams shareParams = new WechatMoments.ShareParams();
//            shareParams.setShareType(Platform.SHARE_WEBPAGE);
//            shareParams.setTitle(shareable.title);
//            shareParams.setText(shareable.content + "\n" + shareable.url);
//            shareParams.setImagePath(response.downloadFile.getAbsolutePath());
//            shareParams.setUrl(shareable.url);
//            platform.share(shareParams);
//        } else if (platform instanceof SinaWeibo) {
//            SinaWeibo.ShareParams shareParams = new SinaWeibo.ShareParams();
//            shareParams.setText(shareable.content + "\n" + shareable.url);
//            platform.share(shareParams);
//        } else if (platform instanceof QQ) {
//            QQ.ShareParams shareParams = new QQ.ShareParams();
//            shareParams.setTitle(shareable.title);
//            shareParams.setText(shareable.content + "\n" + shareable.url);
//            shareParams.setImagePath(response.downloadFile.getAbsolutePath());
//            shareParams.setTitleUrl(shareable.url);
//            platform.share(shareParams);
//        }
        ToastUtil.show(R.string.shared);
    }
}
