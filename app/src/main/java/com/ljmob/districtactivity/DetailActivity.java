package com.ljmob.districtactivity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ljmob.districtactivity.adapter.DetailAdapter;
import com.ljmob.districtactivity.entity.Result;
import com.ljmob.districtactivity.fragment.ShowcaseFragment;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.ljmob.districtactivity.util.MyApplication;
import com.ljmob.districtactivity.view.LoginDialog;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by london on 15/7/23.
 * 帖子详情
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener, LoginDialog.LoginListener, LRequestTool.OnResponseListener {
    private static final int API_PRAISE = 1;
    private static final int API_VOTE = 1;

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
    View head_detail;
    View foot_detail;
    TextView head_detail_tvTitle;
    CircleImageView head_detail_imgHead;
    TextView head_detail_tvAuthor;
    TextView head_detail_tvFloorDate;
    TextView head_detail_tvTextContent;
    TextView foot_detail_tvPraise;
    LinearLayout foot_detail_lnPraise;

    Result result;
    ImageLoader imageLoader;
    LRequestTool lRequestTool;
    LoginDialog loginDialog;
    DetailAdapter detailAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        result = (Result) getIntent().getSerializableExtra("result");
        imageLoader = ImageLoader.getInstance();
        lRequestTool = new LRequestTool(this);
        if (result == null) {
            finish();
        }
        initView();
        initData();
    }

    private void initData() {
        if (result.items == null) {
            result.items = new ArrayList<>(0);
        }
        imageLoader.displayImage(NetConst.ROOT_URL + result.author.avatar, head_detail_imgHead);
        head_detail_tvAuthor.setText(result.author.name);
        head_detail_tvFloorDate.setText(getString(R.string.floor_) + result.created_at);
        head_detail_tvTextContent.setText(result.items.size() == 0 ? result.description : result.items.get(0).content);
        head_detail_tvTitle.setText(result.title);
        foot_detail_tvPraise.setText(result.praise_count + "");
        detailAdapter = new DetailAdapter(result.items);
        activity_detail_lvContent.setAdapter(detailAdapter);
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

        head_detail = getLayoutInflater().inflate(R.layout.head_detail, activity_detail_lvContent, false);
        foot_detail = getLayoutInflater().inflate(R.layout.foot_detail, activity_detail_lvContent, false);
        activity_detail_lvContent.addHeaderView(head_detail);
        activity_detail_lvContent.addFooterView(foot_detail);
        initViewInHead();
        initViewInFoot();

        activity_detail_lnReply.setVisibility(View.GONE);

        Toolbar toolbar_root = (Toolbar) findViewById(R.id.toolbar_root);
        setSupportActionBar(toolbar_root);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        activity_detail_tvReply.setOnClickListener(this);
        activity_detail_tvVote.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:

                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViewInHead() {
        head_detail_tvTitle = (TextView) findViewById(R.id.head_detail_tvTitle);
        head_detail_imgHead = (CircleImageView) findViewById(R.id.head_detail_imgHead);
        head_detail_tvAuthor = (TextView) findViewById(R.id.head_detail_tvAuthor);
        head_detail_tvFloorDate = (TextView) findViewById(R.id.head_detail_tvFloorDate);
        head_detail_tvTextContent = (TextView) findViewById(R.id.head_detail_tvTextContent);
    }

    private void initViewInFoot() {
        foot_detail_tvPraise = (TextView) findViewById(R.id.foot_detail_tvPraise);
        foot_detail_lnPraise = (LinearLayout) findViewById(R.id.foot_detail_lnPraise);

        foot_detail_lnPraise.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (MyApplication.currentUser == null) {
            showLoginDialog();
            return;
        }
        switch (v.getId()) {
            case R.id.foot_detail_lnPraise:
                if (result.is_praise) {
                    ToastUtil.show(R.string.had_voted);
                    return;
                }
                addPraiseCount();
                HashMap<String, Object> praiseParams = new DefaultParams();
                praiseParams.put("activity_result_id", result.id);
                lRequestTool.doPost(NetConst.API_PRAISE, praiseParams, API_PRAISE);
                break;
            case R.id.activity_detail_tvVote:
                if (result.is_vote) {
                    ToastUtil.show(R.string.had_voted);
                    return;
                }
                addVoteCount();
                HashMap<String, Object> voteParams = new DefaultParams();
                voteParams.put("activity_result_id", result.id);
                lRequestTool.doPost(NetConst.API_VOTE, voteParams, API_VOTE);
                break;
            case R.id.activity_detail_tvReply:
                break;
        }
    }

    private void addPraiseCount() {
        result.praise_count++;
        result.is_praise = true;
        foot_detail_tvPraise.setText(result.praise_count + "");
    }

    private void addVoteCount() {
        result.vote_count++;
        result.is_vote = true;
    }

    private void showLoginDialog() {
        if (loginDialog == null) {
            loginDialog = new LoginDialog(this);
            loginDialog.setLoginListener(this);
        }
        loginDialog.show();
    }

    @Override
    public void loginSuccess(LoginDialog dialog) {
        if (MyApplication.currentUser != null) {
            ToastUtil.show(R.string.login_ed);
        }
    }

    @Override
    public void onResponse(LResponse response) {
        if (response.responseCode == 401) {
            showLoginDialog();
            return;
        }
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        ShowcaseFragment.isResultChanged = true;
        switch (response.requestCode) {
            case API_PRAISE:
                ToastUtil.show(R.string.praise_add);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detailAdapter.stopAllMusic();
    }
}
