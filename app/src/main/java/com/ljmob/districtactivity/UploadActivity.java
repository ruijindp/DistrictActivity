package com.ljmob.districtactivity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by london on 15/7/22.
 * 上传作品
 */
public class UploadActivity extends AppCompatActivity {
    EditText activity_post_etTitle;
    TextView activity_post_tvCategory;
    EditText activity_post_etContent;
    ImageView activity_post_imgPicture;
    ImageView activity_post_imgEmotion;
    ImageView activity_post_imgAudio;
    ImageView activity_post_imgVideo;
    LinearLayout activity_post_lnAttached;
    HorizontalScrollView activity_post_scAttached;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initView();
    }

    private void initView() {
        activity_post_etTitle = (EditText) findViewById(R.id.activity_post_etTitle);
        activity_post_tvCategory = (TextView) findViewById(R.id.activity_post_tvCategory);
        activity_post_etContent = (EditText) findViewById(R.id.activity_post_etContent);
        activity_post_imgPicture = (ImageView) findViewById(R.id.activity_post_imgPicture);
        activity_post_imgEmotion = (ImageView) findViewById(R.id.activity_post_imgEmotion);
        activity_post_imgAudio = (ImageView) findViewById(R.id.activity_post_imgAudio);
        activity_post_imgVideo = (ImageView) findViewById(R.id.activity_post_imgVideo);
        activity_post_lnAttached = (LinearLayout) findViewById(R.id.activity_post_lnAttached);
        activity_post_scAttached = (HorizontalScrollView) findViewById(R.id.activity_post_scAttached);

        Toolbar toolbar_root = (Toolbar) findViewById(R.id.toolbar_root);
        setSupportActionBar(toolbar_root);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
