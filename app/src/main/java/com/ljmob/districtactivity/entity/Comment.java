package com.ljmob.districtactivity.entity;

import com.londonx.lutil.entity.LEntity;

import java.util.List;

/**
 * Created by london on 15/7/28.
 * 评论
 */
public class Comment extends LEntity {
    public int comments_size;
    public List<Item> comment_item;
    public String reviewer;
    public String reviewer_avatar;
    public String content;
    public String created_at;
    public int floor;
}
