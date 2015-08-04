package com.ljmob.districtactivity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.ljmob.districtactivity.entity.Notice;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by london on 15/8/4.
 * 公告详情
 */
public class NoticeActivity extends AppCompatActivity {
    Notice notice;
    @InjectView(R.id.toolbar_root)
    Toolbar toolbarRoot;
    @InjectView(R.id.activity_notice_tvTitle)
    TextView activityNoticeTvTitle;
    @InjectView(R.id.activity_notice_tvDate)
    TextView activityNoticeTvDate;
    @InjectView(R.id.activity_notice_tvContent)
    TextView activityNoticeTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notice = (Notice) getIntent().getSerializableExtra("notice");
        setContentView(R.layout.activity_notice);
        ButterKnife.inject(this);
        activityNoticeTvTitle.setText(notice.title);
        activityNoticeTvDate.setText(notice.created_at);
        activityNoticeTvContent.setText(notice.description);
        setSupportActionBar(toolbarRoot);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
