package com.ljmob.districtactivity.entity;

import com.londonx.lutil.entity.LEntity;

import java.util.List;

/**
 * Created by london on 15/7/9.
 * 用户
 */
public class User extends LEntity {
    public String token;
    public String user_name;
    public String user_avatar;
    public String roles;
    public List<TeamClass> team_class;
}
