package com.ljmob.districtactivity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by london on 15/7/28.
 * 在VerticalViewPager中使用
 */
public class InsiderVerticalViewPage extends VerticalViewPager {
    public boolean needToStop;

    public InsiderVerticalViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return needToStop;
//    }
}
