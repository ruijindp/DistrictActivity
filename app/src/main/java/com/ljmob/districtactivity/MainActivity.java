package com.ljmob.districtactivity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.ljmob.districtactivity.adapter.MainPagerAdapter;

import cn.jpush.android.api.JPushInterface;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar_root;
    PagerSlidingTabStrip activity_main_tabStrip;
    ViewPager activity_main_pager;
    FrameLayout activity_main_fl;
    CircleImageView activity_main_imgHead;
    TextView activity_main_tvUserName;
    LinearLayout activity_main_lnUpload;
    LinearLayout activity_main_lnMyUpload;
    LinearLayout activity_main_lnMessage;
    LinearLayout activity_main_lnJoined;
    LinearLayout activity_main_lnSettings;
    DrawerLayout activity_main_drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment(Page.showcase);
    }

    private void initFragment(Page page) {
        switch (page) {
            case showcase:
                break;
            case rank:
                break;
        }
    }

    private void initView() {
        activity_main_tabStrip = (PagerSlidingTabStrip) findViewById(R.id.activity_main_tabStrip);
        activity_main_pager = (ViewPager) findViewById(R.id.activity_main_pager);
        activity_main_fl = (FrameLayout) findViewById(R.id.activity_main_fl);
        activity_main_imgHead = (CircleImageView) findViewById(R.id.activity_main_imgHead);
        activity_main_tvUserName = (TextView) findViewById(R.id.activity_main_tvUserName);
        activity_main_lnUpload = (LinearLayout) findViewById(R.id.activity_main_lnUpload);
        activity_main_lnMyUpload = (LinearLayout) findViewById(R.id.activity_main_lnMyUpload);
        activity_main_lnMessage = (LinearLayout) findViewById(R.id.activity_main_lnMessage);
        activity_main_lnJoined = (LinearLayout) findViewById(R.id.activity_main_lnJoined);
        activity_main_lnSettings = (LinearLayout) findViewById(R.id.activity_main_lnSettings);
        activity_main_drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer);
        toolbar_root = (Toolbar) findViewById(R.id.toolbar_root);

        setSupportActionBar(toolbar_root);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, activity_main_drawer, toolbar_root, 0, 0);
        activity_main_drawer.setDrawerListener(toggle);
        toggle.syncState();

        activity_main_pager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), this));
        activity_main_tabStrip.setViewPager(activity_main_pager);
    }


    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }


    private enum Page {
        showcase,
        rank
    }
}
