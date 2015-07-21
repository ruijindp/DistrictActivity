package com.ljmob.districtactivity.util;

import java.util.HashMap;

/**
 * Created by london on 15/7/9.
 * 默认请求参数
 */
public class DefaultParams extends HashMap<String, Object> {
    public DefaultParams() {
        if (MyApplication.currentUser != null) {
            put("token", MyApplication.currentUser.token);
        }
    }
}
