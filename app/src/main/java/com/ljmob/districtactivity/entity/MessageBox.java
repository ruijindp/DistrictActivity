package com.ljmob.districtactivity.entity;

import com.londonx.lutil.entity.LEntity;

/**
 * Created by london on 15/7/22.
 * 消息(我参与的)
 */
public class MessageBox extends LEntity {
    public int message_size;
    public String message_type;
    public String sender;
    public String comment_description;
    public String created_at;
    public Result activity_result;
}
