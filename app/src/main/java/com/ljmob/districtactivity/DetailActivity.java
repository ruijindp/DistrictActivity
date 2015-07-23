package com.ljmob.districtactivity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ljmob.districtactivity.entity.Result;

/**
 * Created by london on 15/7/23.
 * 帖子详情
 */
public class DetailActivity extends AppCompatActivity {
    ListView activity_detail_lvContent;
    TextView activity_detail_tvReply;
    TextView activity_detail_tvVote;
    LinearLayout activity_detail_lnOptions;
    ImageView activity_detail_btnAttach;
    EditText activity_detail_tvComment;
    ImageView activity_detail_btnSend;
    LinearLayout activity_detail_lnAttached;
    HorizontalScrollView activity_detail_scAttached;
    LinearLayout activity_detail_lnPicture;
    LinearLayout activity_detail_lnEmotion;
    LinearLayout activity_detail_lnAudio;
    LinearLayout activity_detail_lnVideo;
    LinearLayout activity_detail_lnAttach;
    LinearLayout activity_detail_lnReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Result result = (Result) getIntent().getSerializableExtra("result");
        if (result == null) {
            finish();
        }
        initView();
    }

    private void initView() {
        activity_detail_lvContent = (ListView) findViewById(R.id.activity_detail_lvContent);
        activity_detail_tvReply = (TextView) findViewById(R.id.activity_detail_tvReply);
        activity_detail_tvVote = (TextView) findViewById(R.id.activity_detail_tvVote);
        activity_detail_lnOptions = (LinearLayout) findViewById(R.id.activity_detail_lnOptions);
        activity_detail_btnAttach = (ImageView) findViewById(R.id.activity_detail_btnAttach);
        activity_detail_tvComment = (EditText) findViewById(R.id.activity_detail_tvComment);
        activity_detail_btnSend = (ImageView) findViewById(R.id.activity_detail_btnSend);
        activity_detail_lnAttached = (LinearLayout) findViewById(R.id.activity_detail_lnAttached);
        activity_detail_scAttached = (HorizontalScrollView) findViewById(R.id.activity_detail_scAttached);
        activity_detail_lnPicture = (LinearLayout) findViewById(R.id.activity_detail_lnPicture);
        activity_detail_lnEmotion = (LinearLayout) findViewById(R.id.activity_detail_lnEmotion);
        activity_detail_lnAudio = (LinearLayout) findViewById(R.id.activity_detail_lnAudio);
        activity_detail_lnVideo = (LinearLayout) findViewById(R.id.activity_detail_lnVideo);
        activity_detail_lnAttach = (LinearLayout) findViewById(R.id.activity_detail_lnAttach);
        activity_detail_lnReply = (LinearLayout) findViewById(R.id.activity_detail_lnReply);

        Toolbar toolbar_root = (Toolbar) findViewById(R.id.toolbar_root);
        setSupportActionBar(toolbar_root);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:

                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
