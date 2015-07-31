package com.ljmob.lemoji.entity;

import android.graphics.Bitmap;

import com.londonx.lutil.entity.LEntity;

/**
 * Created by london on 15/7/30.
 * emoji from assets
 */
public class Emoji extends LEntity {
    public static final String START = "[<*";
    public static final String END = "*>]";

    public String tag;
    public Bitmap picture;
}
