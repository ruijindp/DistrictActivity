package com.ljmob.districtactivity.entity;

import com.londonx.lutil.entity.LEntity;

/**
 * Created by london on 15/8/3.
 * 帖子内容和楼层
 */
public class FloorItem extends LEntity {
    public int resultId;
    public boolean isPraised;
    public int praiseCount;
    public ItemType itemType;
    public String title;
    public Author author;
    public int floor;
    public String created_at;//创建日期
    public Item item;

    public enum ItemType {
        userInfo, normal, options//分别是用户信息，内容，和观点
    }
}
