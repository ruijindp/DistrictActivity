package com.ljmob.districtactivity.entity;

import com.londonx.lutil.entity.LEntity;

import java.io.File;

/**
 * Created by london on 15/7/9.
 * 结果（文本+图片）
 */
public class Item extends LEntity {
    public String content;
    public String file_url;
    public String file_url_thumb;
    public String file_name;
    public String video_url;
    public String basic_video_url;
    public String file_type;
    public File file;
}
