package com.ljmob.districtactivity.entity;

import com.londonx.lutil.entity.LEntity;

import java.util.List;

/**
 * Created by london on 15/7/17.
 * 活动结果
 */
public class Result extends LEntity {
    public String title;
    public String description;
    public String activity;
    public String level;
    public String created_at;
    public int activity_result_size;
    public int comments_size;
    public int vote_count;
    public int praise_count;
    public boolean is_vote;
    public boolean is_praise;
    public String is_check;
    public Author author;
    public List<Item> items;
    public List<CheckScores> check_scores;
}
