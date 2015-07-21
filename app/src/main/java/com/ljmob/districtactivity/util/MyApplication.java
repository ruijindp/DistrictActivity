package com.ljmob.districtactivity.util;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ljmob.districtactivity.entity.User;
import com.londonx.lutil.Lutil;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by london on 15/7/9.
 * MyApplication
 */
public class MyApplication extends Application {
    public static User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        Lutil.init(this);
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this);
        DisplayImageOptions.Builder imageOptions = new DisplayImageOptions.Builder();
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = 2;

        imageOptions.bitmapConfig(Bitmap.Config.RGB_565)
                .decodingOptions(options)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(0);

        builder.diskCacheFileCount(1000)
                .threadPoolSize(6)
                .memoryCache(new WeakMemoryCache())
                .defaultDisplayImageOptions(imageOptions.build());
        ImageLoader.getInstance().init(builder.build());

        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
        JPushInterface.setAlias(this, "Nx9ny1C9DW82g5nyTYMG", null);

//        BasicPushNotificationBuilder notificationBuilder = new BasicPushNotificationBuilder(this);
//        notificationBuilder.statusBarDrawable = R.mipmap.ic_launcher;

//        this.layout = var2;
//        this.layoutIconId = var3;
//        this.layoutTitleId = var4;
//        this.layoutContentId = var5;
//        CustomPushNotificationBuilder notificationBuilder=new CustomPushNotificationBuilder(this,0,0,0,0);

//        JPushInterface.setDefaultPushNotificationBuilder(notificationBuilder);
    }
}
