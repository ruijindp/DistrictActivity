package com.ljmob.districtactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.districtactivity.adapter.MainPagerAdapter;
import com.ljmob.districtactivity.entity.MessageBox;
import com.ljmob.districtactivity.fragment.ShowcaseFragment;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.ljmob.districtactivity.util.MyApplication;
import com.ljmob.districtactivity.view.LoginDialog;
import com.londonx.lutil.Lutil;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoginDialog.LoginListener, DrawerLayout.DrawerListener, ViewPager.OnPageChangeListener, LRequestTool.OnResponseListener {
    //firim token:e9400a3620552593c1851beecb8431a0
    //firim appId:
    private static final int API_MESSAGE = 1;
    public static boolean isOnFront;

    Toolbar toolbar_root;
    PagerSlidingTabStrip activity_main_tabStrip;
    ViewPager activity_main_pager;
    FrameLayout activity_main_fl;
    FrameLayout activity_main_flUserInfo;
    CircleImageView activity_main_imgHead;
    TextView activity_main_tvUserName;
    LinearLayout activity_main_lnOptions;
    LinearLayout activity_main_lnUpload;
    LinearLayout activity_main_lnMyUpload;
    TextView activity_main_tvMyUpload;
    LinearLayout activity_main_lnMessage;
    LinearLayout activity_main_lnJoined;
    LinearLayout activity_main_lnSettings;
    DrawerLayout activity_main_drawer;
    View activity_main_dotMessage;

    LRequestTool lRequestTool;
    LoginDialog loginDialog;
    ImageLoader imageLoader;
    MainBroadcastReceiver receiver;

    List<MessageBox> messages;

    boolean willInitDrawer = true;
    MenuItem postMenuItem;
    int messageSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageLoader = ImageLoader.getInstance();
        receiver = new MainBroadcastReceiver();
        lRequestTool = new LRequestTool(this);
        registerReceiver(receiver, new IntentFilter(SettingsActivity.ACTION_LOGOUT));
        initView();
        initFragment(Page.showcase);
        fetchMessage();
        if (getIntent().getBooleanExtra("message", false) &&
                MyApplication.currentUser != null) {
            startActivity(new Intent(this, MessageActivity.class));
            if (isOnFront) {
                finish();
            }
        }
    }

    private void fetchMessage() {
        if (MyApplication.currentUser == null) {
            return;
        }
        lRequestTool.doGet(NetConst.API_MESSAGE, new DefaultParams(), API_MESSAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        postMenuItem = menu.findItem(R.id.action_make_post);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_make_post:
                if (ShowcaseFragment.activities == null) {
                    break;
                }
                Intent intent = new Intent(this, UploadActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
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
        activity_main_flUserInfo = (FrameLayout) findViewById(R.id.activity_main_flUserInfo);
        activity_main_imgHead = (CircleImageView) findViewById(R.id.activity_main_imgHead);
        activity_main_tvUserName = (TextView) findViewById(R.id.activity_main_tvUserName);
        activity_main_lnOptions = (LinearLayout) findViewById(R.id.activity_main_lnOptions);
        activity_main_lnUpload = (LinearLayout) findViewById(R.id.activity_main_lnUpload);
        activity_main_lnMyUpload = (LinearLayout) findViewById(R.id.activity_main_lnMyUpload);
        activity_main_tvMyUpload = (TextView) findViewById(R.id.activity_main_tvMyUpload);
        activity_main_lnMessage = (LinearLayout) findViewById(R.id.activity_main_lnMessage);
        activity_main_lnJoined = (LinearLayout) findViewById(R.id.activity_main_lnJoined);
        activity_main_lnSettings = (LinearLayout) findViewById(R.id.activity_main_lnSettings);
        activity_main_drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer);
        toolbar_root = (Toolbar) findViewById(R.id.toolbar_root);
        activity_main_dotMessage = findViewById(R.id.activity_main_dotMessage);

        setSupportActionBar(toolbar_root);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, activity_main_drawer, toolbar_root, 0, 0);
        activity_main_drawer.setDrawerListener(toggle);
        toggle.syncState();

        activity_main_pager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), this));
        activity_main_tabStrip.setViewPager(activity_main_pager);
        activity_main_flUserInfo.setOnClickListener(this);
        activity_main_drawer.setDrawerListener(this);

        activity_main_lnUpload.setOnClickListener(this);
        activity_main_lnMyUpload.setOnClickListener(this);
        activity_main_lnMessage.setOnClickListener(this);
        activity_main_lnJoined.setOnClickListener(this);
        activity_main_lnSettings.setOnClickListener(this);
        activity_main_pager.addOnPageChangeListener(this);

        if (MyApplication.currentUser == null) {
            return;
        }
        if (MyApplication.currentUser.roles.equals("student")) {
            activity_main_tvMyUpload.setText(R.string.activity_myUpload);
        } else {
            activity_main_tvMyUpload.setText(R.string.activity_myUpload_teacher);
        }
    }

    @Override
    public void onBackPressed() {
        if (activity_main_drawer.isDrawerOpen(Gravity.LEFT)) {
            activity_main_drawer.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        isOnFront = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onDestroy() {
        isOnFront = false;
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_flUserInfo:
                if (MyApplication.currentUser == null) {
                    doLogin();
                    activity_main_drawer.closeDrawers();
                    break;
                }
                return;
            case R.id.activity_main_lnUpload:
                Intent uploadIntent = new Intent(this, UploadActivity.class);
                startActivity(uploadIntent);
                break;
            case R.id.activity_main_lnMyUpload:
                Intent myUploadIntent = new Intent(this, MyUploadActivity.class);
                startActivity(myUploadIntent);
                break;
            case R.id.activity_main_lnMessage:
                Intent messageIntent = new Intent(this, MessageActivity.class);
                startActivity(messageIntent);
                break;
            case R.id.activity_main_lnJoined:
                Intent joinedIntent = new Intent(this, JoinedActivity.class);
                startActivity(joinedIntent);
                break;
            case R.id.activity_main_lnSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        activity_main_drawer.closeDrawers();
    }

    private void doLogin() {
        if (loginDialog == null) {
            loginDialog = new LoginDialog(this);
            loginDialog.setLoginListener(this);
        }
        activity_main_drawer.closeDrawers();
        loginDialog.show();
    }

    @Override
    public void loginSuccess(LoginDialog dialog) {
//        if (MyApplication.currentUser != null) {
//            restartActivity();
//        }
    }

    private void restartActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        if (willInitDrawer) {
            willInitDrawer = false;
            if (MyApplication.currentUser == null) {
                activity_main_lnOptions.setVisibility(View.INVISIBLE);
                activity_main_tvUserName.setText(R.string.login_please);
            } else {
                activity_main_lnOptions.setVisibility(View.VISIBLE);
                imageLoader.displayImage(NetConst.ROOT_URL +
                        MyApplication.currentUser.user_avatar, activity_main_imgHead);
                activity_main_tvUserName.setText(MyApplication.currentUser.user_name);
            }
        }
    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        willInitDrawer = true;
        fetchMessage();
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            postMenuItem.setVisible(true);
        } else {
            postMenuItem.setVisible(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onResponse(LResponse response) {
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        switch (response.requestCode) {
            case API_MESSAGE:
                messages = new Gson().fromJson(response.body, new TypeToken<List<MessageBox>>() {
                }.getType());

                messageSize = Lutil.preferences.getInt(MessageActivity.KEY_MESSAGE_COUNT, 0);
                int currentMessageSize = messages.size() == 0 ? 0 : messages.get(0).message_size;
                if (currentMessageSize != messageSize) {
                    activity_main_dotMessage.setVisibility(View.VISIBLE);
                } else {
                    activity_main_dotMessage.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    private class MainBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SettingsActivity.ACTION_LOGOUT)) {
                restartActivity();
            }
        }
    }

    private enum Page {
        showcase,
        rank
    }
}
