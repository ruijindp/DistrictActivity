package com.ljmob.districtactivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ljmob.districtactivity.adapter.DetailPagerAdapter;
import com.ljmob.districtactivity.adapter.EmojiAdapter;
import com.ljmob.districtactivity.entity.Item;
import com.ljmob.districtactivity.entity.Result;
import com.ljmob.districtactivity.entity.Shareable;
import com.ljmob.districtactivity.fragment.CommentFragment;
import com.ljmob.districtactivity.fragment.DetailFragment;
import com.ljmob.districtactivity.fragment.ShowcaseFragment;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.subView.AttachView;
import com.ljmob.districtactivity.util.DefaultParams;
import com.ljmob.districtactivity.util.MyApplication;
import com.ljmob.districtactivity.util.ShareTool;
import com.ljmob.districtactivity.view.InsiderVerticalViewPage;
import com.ljmob.districtactivity.view.LoginDialog;
import com.ljmob.districtactivity.view.MediaDialog;
import com.ljmob.lemoji.LEmoji;
import com.ljmob.lemoji.entity.Emoji;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.FileUtil;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by london on 15/7/23.
 * 帖子详情和评论
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener, LoginDialog.LoginListener, LRequestTool.OnResponseListener, TextWatcher, MediaDialog.OnTypeSelectListener, AttachView.AttachViewDeleteListener, ViewPager.OnPageChangeListener, AdapterView.OnItemClickListener {
    private static final int RESULT_GET = 1;
    private static final int API_PRAISE = 1;
    private static final int API_VOTE = 2;
    private static final int API_COMMENT = 3;

    View activity_detail_btnReply;
    View activity_detail_btnVote;
    View activity_detail_btnEmoji;
    TextView activity_detail_tvVote;
    LinearLayout activity_detail_lnOptions;
    ImageView activity_detail_btnAttach;
    EditText activity_detail_etComment;
    ImageView activity_detail_btnSend;
    LinearLayout activity_detail_lnAttached;
    HorizontalScrollView activity_detail_scAttached;
    LinearLayout activity_detail_lnReply;
    InsiderVerticalViewPage activity_detail_pagerContent;
    GridView activity_detail_gvEmoji;

    MediaDialog mediaDialog;
    DetailFragment detailFragment;
    CommentFragment commentFragment;
    MaterialDialog shareDialog;

    LoginDialog loginDialog;
    LRequestTool lRequestTool;
    EmojiAdapter emojiAdapter;
    Result result;
    ImageLoader imageLoader;
    List<AttachView> attachViews;
    Shareable shareable;
    List<Emoji> emojiList;
    MaterialDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        result = (Result) getIntent().getSerializableExtra("result");
        imageLoader = ImageLoader.getInstance();
        lRequestTool = new LRequestTool(this);
        loginDialog = new LoginDialog(this);
        mediaDialog = new MediaDialog(this);
        attachViews = new ArrayList<>();

        mediaDialog.setOnTypeSelectListener(this);
        loginDialog.setLoginListener(this);
        if (result == null) {
            finish();
        }
        shareable = new Shareable();
        shareable.title = result.title;
        shareable.content = result.description;
        shareable.url = NetConst.PAGE_SHARE + "?id=" + result.id;
        for (Item item : result.items) {
            if (FileUtil.getFileType(item.file_url) == FileUtil.FileType.picture) {
                shareable.imgUrl = item.file_url;
                break;
            }
        }

        initView();
        resetVoteCount();
        initPager();
    }

    private void initView() {
        activity_detail_btnReply = findViewById(R.id.activity_detail_btnReply);
        activity_detail_btnVote = findViewById(R.id.activity_detail_btnVote);
        activity_detail_btnEmoji = findViewById(R.id.activity_detail_btnEmoji);
        activity_detail_tvVote = (TextView) findViewById(R.id.activity_detail_tvVote);
        activity_detail_lnOptions = (LinearLayout) findViewById(R.id.activity_detail_lnOptions);
        activity_detail_btnAttach = (ImageView) findViewById(R.id.activity_detail_btnAttach);
        activity_detail_etComment = (EditText) findViewById(R.id.activity_detail_etComment);
        activity_detail_btnSend = (ImageView) findViewById(R.id.activity_detail_btnSend);
        activity_detail_lnAttached = (LinearLayout) findViewById(R.id.activity_detail_lnAttached);
        activity_detail_scAttached = (HorizontalScrollView) findViewById(R.id.activity_detail_scAttached);
        activity_detail_lnReply = (LinearLayout) findViewById(R.id.activity_detail_lnReply);
        activity_detail_pagerContent = (InsiderVerticalViewPage) findViewById(R.id.activity_detail_pagerContent);
        activity_detail_gvEmoji = (GridView) findViewById(R.id.activity_detail_gvEmoji);

        activity_detail_lnReply.setVisibility(View.GONE);

        Toolbar toolbar_root = (Toolbar) findViewById(R.id.toolbar_root);
        setSupportActionBar(toolbar_root);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        activity_detail_btnReply.setOnClickListener(this);
        activity_detail_btnVote.setOnClickListener(this);
        activity_detail_btnAttach.setOnClickListener(this);
        activity_detail_btnSend.setOnClickListener(this);
        activity_detail_etComment.addTextChangedListener(this);
        activity_detail_gvEmoji.setOnItemClickListener(this);
        activity_detail_btnEmoji.setOnClickListener(this);

        activity_detail_pagerContent.setOnPageChangeListener(this);
    }

    private void resetVoteCount() {
        if (result.vote_count < 999) {
            String _vote = getString(R.string._vote);
            activity_detail_tvVote.setText(result.vote_count + _vote);
        } else {
            activity_detail_tvVote.setText(result.vote_count + "");
        }
    }

    private void initPager() {
        detailFragment = new DetailFragment();//内容页
        detailFragment.setResult(result);

        commentFragment = new CommentFragment();//评论页
        commentFragment.setResult(result);

        List<Fragment> fragments = new ArrayList<>(2);
        fragments.add(detailFragment);
        fragments.add(commentFragment);
        activity_detail_pagerContent.setAdapter(new DetailPagerAdapter(result,
                getSupportFragmentManager(), this, fragments));
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
                showShare();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showShare() {
        if (shareDialog == null) {
            shareDialog = new MaterialDialog.Builder(this)
                    .title(R.string.share_to)
                    .customView(R.layout.dialog_share, false)
                    .theme(Theme.LIGHT)
                    .show();

            View shareView = shareDialog.getCustomView();
            if (shareView != null) {
                shareView.findViewById(R.id.dialog_share_lnWechat).setOnClickListener(this);
                shareView.findViewById(R.id.dialog_share_lnMoment).setOnClickListener(this);
                shareView.findViewById(R.id.dialog_share_lnWeibo).setOnClickListener(this);
                shareView.findViewById(R.id.dialog_share_lnQQ).setOnClickListener(this);
            }
        }
        shareDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_share_lnWechat:
                new ShareTool(this, new Wechat(this), shareable).share();
                shareDialog.dismiss();
                return;
            case R.id.dialog_share_lnMoment:
                new ShareTool(this, new WechatMoments(this), shareable).share();
                shareDialog.dismiss();
                return;
            case R.id.dialog_share_lnWeibo:
                new ShareTool(this, new SinaWeibo(this), shareable).share();
                shareDialog.dismiss();
                return;
            case R.id.dialog_share_lnQQ:
                new ShareTool(this, new QQ(this), shareable).share();
                shareDialog.dismiss();
                return;
            case R.id.activity_detail_btnVote:
                ToastUtil.show(R.string.cannot_vote);
//                if (result.is_vote) {
//                    ToastUtil.show(R.string.had_voted);
//                    return;
//                }
//                addVoteCount();
//                HashMap<String, Object> voteParams = new DefaultParams();
//                voteParams.put("activity_result_id", result.id);
//                lRequestTool.doPost(NetConst.API_VOTE, voteParams, API_VOTE);
                return;
        }
        if (MyApplication.currentUser == null) {
            showLoginDialog();
            return;
        }
        switch (v.getId()) {
            case R.id.activity_detail_btnReply:
                activity_detail_pagerContent.setCurrentItem(1);
                activity_detail_lnReply.setVisibility(View.VISIBLE);
                activity_detail_lnOptions.setVisibility(View.GONE);
                activity_detail_etComment.requestFocus();
                showKeyBoard();
                break;
            case R.id.activity_detail_btnAttach:
                activity_detail_gvEmoji.setVisibility(View.GONE);
                if (attachViews.size() != 0) {
                    activity_detail_scAttached.setVisibility(View.VISIBLE);
                }
                onTypeSelect(MediaDialog.Type.picture);
                break;
            case R.id.activity_detail_btnEmoji:
                if (emojiAdapter == null) {
                    emojiList = LEmoji.getAllEmoji();
                    emojiAdapter = new EmojiAdapter(emojiList);
                    activity_detail_gvEmoji.setAdapter(emojiAdapter);
                }
                activity_detail_scAttached.setVisibility(View.GONE);
                activity_detail_gvEmoji.setVisibility(View.VISIBLE);
                hideKeyBoard();
                break;
            case R.id.activity_detail_btnSend:
                if (activity_detail_etComment.getText().length() == 0) {
                    break;
                }
                HashMap<String, Object> commentParams = new DefaultParams();
                commentParams.put("activity_result_id", result.id);
                for (int i = 0; i < attachViews.size(); i++) {
                    commentParams.put("file" + i, attachViews.get(i).displayFile);
                    String filePath = attachViews.get(i).displayFile.getAbsolutePath();
                    filePath = filePath.substring(filePath.lastIndexOf("/") + 1);
                    filePath = filePath.substring(0, filePath.lastIndexOf("."));
                    commentParams.put("file_name_" + i, filePath);
                }
                commentParams.put("content", activity_detail_etComment.getText().toString());
                commentParams.put("file_size", attachViews.size());
                lRequestTool.doPost(NetConst.API_COMMENT, commentParams, API_COMMENT);
                onBackPressed();//hide lnReply
                hideKeyBoard();
                break;
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
            case API_VOTE:
                ToastUtil.show(R.string.vote_add);
                break;
            case API_COMMENT:
                ToastUtil.show(R.string.commented);
                activity_detail_etComment.setText("");
                activity_detail_lnAttached.removeAllViews();
                attachViews.clear();
                hideReply();
                break;
        }
    }

    private void addVoteCount() {
        result.vote_count++;
        result.is_vote = true;
        resetVoteCount();
    }

    private void showLoginDialog() {
        if (loginDialog == null) {
            loginDialog = new LoginDialog(this);
            loginDialog.setLoginListener(this);
        }
        loginDialog.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
            hideReply();
            hideKeyBoard();
        }
    }


    @Override
    public void onTypeSelect(MediaDialog.Type type) {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        switch (type) {
            case picture:
                fileIntent.setType("image/*");
                break;
            case audio:
                fileIntent.setType("audio/*");
                break;
            case video:
                fileIntent.setType("video/*");
                break;
        }
        fileIntent.putExtra("return-data", true);
        startActivityForResult(fileIntent, RESULT_GET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != android.app.Activity.RESULT_OK) {
            return;
        }
        File selectedFile = null;
        Uri uri = intent.getData();
        if (uri == null) {
            return;
        }
        switch (requestCode) {
            case RESULT_GET:
                selectedFile = FileUtil.getFileFromUri(uri);
                break;
        }
        if (selectedFile == null || selectedFile.length() == 0) {
            ToastUtil.show(R.string.toast_file_err);
            return;
        }
        AttachView attachView = new AttachView(this, activity_detail_lnAttached, this);
        attachView.setMedia(selectedFile);
        attachViews.add(attachView);
        activity_detail_lnAttached.addView(attachView.getView());
        activity_detail_scAttached.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeleted(AttachView attachView) {
        int index = attachViews.indexOf(attachView);
        activity_detail_lnAttached.removeViewAt(index);
        attachViews.remove(index);
        if (attachViews.size() == 0) {
            activity_detail_scAttached.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (activity_detail_gvEmoji.getVisibility() == View.VISIBLE) {
            activity_detail_gvEmoji.setVisibility(View.GONE);
            if (attachViews.size() != 0) {
                activity_detail_scAttached.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (activity_detail_lnReply.getVisibility() == View.VISIBLE) {
            hideReply();
        } else {
            if (activity_detail_etComment.getText().length() != 0
                    || attachViews.size() != 0) {
                if (exitDialog == null) {
                    exitDialog = new MaterialDialog.Builder(this)
                            .theme(Theme.LIGHT)
                            .title(R.string.dialog_exit)
                            .content(R.string.dialog_exit_desc)
                            .positiveText(android.R.string.ok)
                            .negativeText(android.R.string.cancel)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    finish();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                exitDialog.show();
            } else {
                finish();
            }
        }
    }

    @Override
    public void loginSuccess(LoginDialog dialog) {
        if (MyApplication.currentUser != null) {
            ToastUtil.show(R.string.login_ed);
        }
    }

    private void hideKeyBoard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(activity_detail_etComment.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showKeyBoard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(activity_detail_etComment.getWindowToken(),
                        InputMethodManager.SHOW_FORCED);
    }

    private void hideReply() {
        activity_detail_lnReply.setVisibility(View.GONE);
        activity_detail_lnOptions.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        activity_detail_pagerContent.needToStop = false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Emoji emoji = emojiList.get(position);
        int index = activity_detail_etComment.getSelectionStart();
        Editable editable = activity_detail_etComment.getText();
        editable.insert(index, emoji.tag);
        activity_detail_etComment.setText(LEmoji.translate(editable.toString()));
        activity_detail_etComment.setSelection(index + emoji.tag.length());
    }

}
