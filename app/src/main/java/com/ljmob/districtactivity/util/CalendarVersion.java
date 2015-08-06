package com.ljmob.districtactivity.util;

import java.util.Calendar;

/**
 * Created by london on 15/8/6.
 * 日期工具
 */
public class CalendarVersion {
    public boolean isOpenedNow() {
        Calendar current = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(2015, 8, 15);

        return current.after(target);
    }
}
