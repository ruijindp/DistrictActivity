package com.ljmob.districtactivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.districtactivity.adapter.EmojiAdapter;
import com.ljmob.districtactivity.adapter.FloorItemAdapter;
import com.ljmob.districtactivity.entity.Author;
import com.ljmob.districtactivity.entity.Comment;
import com.ljmob.districtactivity.entity.FloorItem;
import com.ljmob.districtactivity.entity.Item;
import com.ljmob.districtactivity.entity.Result;
import com.ljmob.districtactivity.entity.Shareable;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.subView.AttachView;
import com.ljmob.districtactivity.util.DefaultParams;
import com.ljmob.districtactivity.util.MyApplication;
import com.ljmob.districtactivity.util.ShareTool;
import com.ljmob.districtactivity.view.LoginDialog;
import com.ljmob.districtactivity.view.MediaDialog;
import com.ljmob.lemoji.LEmoji;
import com.ljmob.lemoji.entity.Emoji;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.FileUtil;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by london on 15/7/23.
 * 帖子详情和评论
 */
public class DetailActivity extends AppCompatActivity implements
        LRequestTool.OnResponseListener,
        SwipeRefreshLayout.OnRefreshListener,
        LoginDialog.LoginListener,
        AbsListView.OnScrollListener,
        View.OnClickListener,
        TextWatcher,
        AdapterView.OnItemClickListener, AttachView.AttachViewDeleteListener {
    private static final int RESULT_GET = 1;
    private static final int API_PRAISE = 1;
    private static final int API_VOTE = 2;
    private static final int API_COMMENT = 3;
    private static final int API_COMMENT_SEND = 4;
    private static final int API_CHECK = 5;

    @InjectView(R.id.toolbar_root)
    Toolbar toolbarRoot;
    @InjectView(R.id.activity_detail_lv)
    ListView activityDetailLv;
    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.activity_detail_btnReply)
    FrameLayout activityDetailBtnReply;
    @InjectView(R.id.activity_detail_tvVote)
    TextView activityDetailTvVote;
    @InjectView(R.id.activity_detail_btnVote)
    FrameLayout activityDetailBtnVote;
    @InjectView(R.id.activity_detail_lnOptions)
    LinearLayout activityDetailLnOptions;
    @InjectView(R.id.activity_detail_btnAttach)
    ImageView activityDetailBtnAttach;
    @InjectView(R.id.activity_detail_etComment)
    EditText activityDetailEtComment;
    @InjectView(R.id.activity_detail_btnEmoji)
    ImageView activityDetailBtnEmoji;
    @InjectView(R.id.activity_detail_btnSend)
    ImageView activityDetailBtnSend;
    @InjectView(R.id.activity_detail_lnAttached)
    LinearLayout activityDetailLnAttached;
    @InjectView(R.id.activity_detail_scAttached)
    HorizontalScrollView activityDetailScAttached;
    @InjectView(R.id.activity_detail_gvEmoji)
    GridView activityDetailGvEmoji;
    @InjectView(R.id.activity_detail_lnReply)
    LinearLayout activityDetailLnReply;
    @InjectView(R.id.activity_detail_tvCheck)
    TextView activityDetailTvCheck;
    @InjectView(R.id.activity_detail_tvUnchecked)
    TextView activityDetailTvUnchecked;
    @InjectView(R.id.activity_detail_lnCheck)
    LinearLayout activityDetailLnCheck;

    View footView;
    TextView foot_more_tv;
    MaterialDialog shareDialog;
    LoginDialog loginDialog;
    MaterialDialog exitDialog;
    MaterialDialog checkDialog;
    MaterialDialog uncheckedDialog;
    ProgressDialog commentDialog;

    LRequestTool lRequestTool;
    Result result;
    FloorItemAdapter floorItemAdapter;
    Gson gson;
    List<FloorItem> floorItems;
    Shareable shareable;
    List<AttachView> attachViews;
    List<Emoji> emojiList;
    EmojiAdapter emojiAdapter;

    int currentPage = 1;
    boolean isLoading;
    int currentMaxFloor;
    boolean isDivDPage;
    boolean hasMore;
    String checkVisibleLevel = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        result = (Result) getIntent().getSerializableExtra("result");
        if (result == null) {
            ToastUtil.show("result:null");
            finish();
            return;
        }
        lRequestTool = new LRequestTool(this);
        floorItems = new ArrayList<>();
        attachViews = new ArrayList<>();

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
        init();
        initViewsWithResult();
        loadComment(currentPage);
    }

    private void init() {
        ButterKnife.inject(this);
        setSupportActionBar(toolbarRoot);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        footView = LayoutInflater.from(this).inflate(R.layout.foot_more, activityDetailLv, false);
        foot_more_tv = (TextView) footView.findViewById(R.id.foot_more_tv);

        activityDetailLv.addFooterView(footView);
        footView.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark0);
        activityDetailTvVote.setText(result.vote_count + getString(R.string._vote));

        swipeRefreshLayout.setOnRefreshListener(this);
        activityDetailBtnReply.setOnClickListener(this);
        activityDetailBtnVote.setOnClickListener(this);
        activityDetailBtnAttach.setOnClickListener(this);
        activityDetailBtnSend.setOnClickListener(this);
        activityDetailEtComment.addTextChangedListener(this);
        activityDetailGvEmoji.setOnItemClickListener(this);
        activityDetailBtnEmoji.setOnClickListener(this);
        activityDetailTvCheck.setOnClickListener(this);
        activityDetailTvUnchecked.setOnClickListener(this);
    }

    private void initViewsWithResult() {
        activityDetailLnCheck.setVisibility(View.GONE);
        activityDetailLnOptions.setVisibility(View.GONE);
        boolean isTeacher = MyApplication.currentUser != null &&
                MyApplication.currentUser.roles.equals("teacher");
        switch (result.is_check) {
            case "checking":
                if (isTeacher) {
                    activityDetailLnCheck.setVisibility(View.VISIBLE);
                }
                break;
            case "true":
                activityDetailLnOptions.setVisibility(View.VISIBLE);
                break;
            case "false":
                break;
        }

        currentMaxFloor = 1;
        //第一行（标题和用户信息）
        FloorItem floorItemUser = new FloorItem();
        floorItemUser.title = result.title;
        floorItemUser.author = result.author;
        floorItemUser.floor = currentMaxFloor;//1楼
        floorItemUser.created_at = result.created_at;
        floorItemUser.itemType = FloorItem.ItemType.userInfo;
        currentMaxFloor++;//变为2楼
        floorItems.add(floorItemUser);
        //内容行（多行）
        for (
                Item item
                : result.items)

        {
            FloorItem floorItem = new FloorItem();
            floorItem.item = item;
            floorItem.itemType = FloorItem.ItemType.normal;
            floorItems.add(floorItem);
        }

        //点赞键（行）
        FloorItem floorItemOption = new FloorItem();
        floorItemOption.itemType = FloorItem.ItemType.options;
        floorItemOption.resultId = result.id;
        floorItemOption.isPraised = result.is_praise;
        floorItemOption.praiseCount = result.praise_count;
        floorItems.add(floorItemOption);

        floorItemAdapter = new

                FloorItemAdapter(floorItems, lRequestTool, API_PRAISE);

        activityDetailLv.setAdapter(floorItemAdapter);
        activityDetailLv.setOnScrollListener(this);
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

    @Override
    public void onResponse(LResponse response) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        if (commentDialog != null && commentDialog.isShowing()) {
            commentDialog.dismiss();
        }
        isLoading = false;
        if (response.responseCode == 401) {
            showLoginDialog();
            return;
        }
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        if (gson == null) {
            gson = new Gson();
        }
        switch (response.requestCode) {
            case API_PRAISE:
                ToastUtil.show(R.string.praise_add);
                break;
            case API_VOTE:
                ToastUtil.show(R.string.vote_add);
                break;
            case API_COMMENT_SEND:
                ToastUtil.show(R.string.commented);
                activityDetailEtComment.setText("");
                activityDetailLnAttached.removeAllViews();
                attachViews.clear();
                hideReply();
                if (currentPage == 1) {
                    onRefresh();
                }
                break;
            case API_COMMENT:
                footView.setVisibility(View.VISIBLE);
                List<Comment> comments = gson.fromJson(response.body,
                        new TypeToken<List<Comment>>() {
                        }.getType());
                hasMore = comments.size() == 15;
                if (comments.size() == 0) {
                    foot_more_tv.setText(currentPage == 1 ? R.string.no_comment : R.string.no_more);
                    break;
                }
                if (hasMore) {
                    foot_more_tv.setText(R.string.load_more);
                } else {
                    foot_more_tv.setText(R.string.no_more);
                }
                if (currentPage == 1) {
                    currentMaxFloor = 2;
                    boolean isComment = false;
                    List<FloorItem> toBeRemove = new ArrayList<>();
                    for (FloorItem floorItem : floorItems) {
                        if (!isComment) {
                            isComment = floorItem.itemType == FloorItem.ItemType.options;
                            continue;
                        }
                        toBeRemove.add(floorItem);
                    }
                    floorItems.removeAll(toBeRemove);
                }
                for (Comment comment : comments) {
                    FloorItem commentUserFloorItem = new FloorItem();
                    commentUserFloorItem.itemType = FloorItem.ItemType.userInfo;
                    Author author = new Author();
                    author.avatar = comment.reviewer_avatar;
                    author.name = comment.reviewer;
                    commentUserFloorItem.author = author;
                    commentUserFloorItem.created_at = comment.created_at;
                    commentUserFloorItem.floor = currentMaxFloor;
                    currentMaxFloor++;
                    floorItems.add(commentUserFloorItem);

                    FloorItem textFloorItem = new FloorItem();//纯文本
                    textFloorItem.itemType = FloorItem.ItemType.normal;
                    Item textItem = new Item();
                    textItem.content = comment.content;
                    textFloorItem.item = textItem;
                    floorItems.add(textFloorItem);
                    if (comment.comment_item != null) {
                        for (Item item : comment.comment_item) {
                            FloorItem commentItemFloorItem = new FloorItem();
                            commentItemFloorItem.itemType = FloorItem.ItemType.normal;
                            commentItemFloorItem.item = item;
                            floorItems.add(commentItemFloorItem);
                        }
                    }
                }
                floorItemAdapter.setNewData(floorItems);
                break;
            case API_CHECK:
                ToastUtil.show(R.string.toast_checked);
                MyUploadActivity.isResultChanged = true;
                initViewsWithResult();
                //TODO
                break;
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        currentPage = 1;
        loadComment(currentPage);
    }

    private void loadComment(int page) {
        HashMap<String, Object> params = new DefaultParams();
        isLoading = true;
        params.put("page", page);
        params.put("activity_result_id", result.id);
        lRequestTool.doGet(NetConst.API_COMMENT, params, API_COMMENT);
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
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState != SCROLL_STATE_IDLE || isLoading) {
            return;
        }
        if (isDivDPage && hasMore) {
            isDivDPage = false;
            currentPage++;
            loadComment(currentPage);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isDivDPage = (firstVisibleItem + visibleItemCount == totalItemCount);
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
                activityDetailLnReply.setVisibility(View.VISIBLE);
                activityDetailLnOptions.setVisibility(View.GONE);
                activityDetailEtComment.requestFocus();
                showKeyBoard();
                break;
            case R.id.activity_detail_btnAttach:
                activityDetailGvEmoji.setVisibility(View.GONE);
                if (attachViews.size() != 0) {
                    activityDetailScAttached.setVisibility(View.VISIBLE);
                }
                onTypeSelect(MediaDialog.Type.picture);
                break;
            case R.id.activity_detail_btnEmoji:
                if (emojiAdapter == null) {
                    emojiList = LEmoji.getAllEmoji();
                    emojiAdapter = new EmojiAdapter(emojiList);
                    activityDetailGvEmoji.setAdapter(emojiAdapter);
                }
                activityDetailScAttached.setVisibility(View.GONE);
                activityDetailGvEmoji.setVisibility(View.VISIBLE);
                hideKeyBoard();
                break;
            case R.id.activity_detail_btnSend:
                if (activityDetailEtComment.getText().length() == 0) {
                    break;
                }
                if (commentDialog == null) {
                    commentDialog = new ProgressDialog(this);
                    commentDialog.setMessage(getString(R.string.dialog_uploading));
                }
                commentDialog.show();
                HashMap<String, Object> commentParams = new DefaultParams();
                commentParams.put("activity_result_id", result.id);
                for (int i = 0; i < attachViews.size(); i++) {
                    commentParams.put("file" + i, attachViews.get(i).displayFile);
                    String filePath = attachViews.get(i).displayFile.getAbsolutePath();
                    filePath = filePath.substring(filePath.lastIndexOf("/") + 1);
                    filePath = filePath.substring(0, filePath.lastIndexOf("."));
                    commentParams.put("file_name_" + i, filePath);
                }
                commentParams.put("content", activityDetailEtComment.getText().toString());
                commentParams.put("file_size", attachViews.size());
                lRequestTool.doPost(NetConst.API_COMMENT, commentParams, API_COMMENT_SEND);
                onBackPressed();//hide lnReply
                hideKeyBoard();
                break;
            case R.id.activity_detail_tvCheck:
                checkDialog = new MaterialDialog.Builder(this)
                        .theme(Theme.LIGHT)
                        .title(R.string.visible_level)
                        .items(R.array.visible_level)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog materialDialog,
                                                       View view, int i, CharSequence charSequence) {
                                switch (i) {
                                    case 0:
                                        checkVisibleLevel = "all";
                                        break;
                                    case 1:
                                        checkVisibleLevel = "school";
                                        break;
                                    case 2:
                                        checkVisibleLevel = "team_class";
                                        break;
                                }
                                return false;
                            }
                        })
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                if (checkVisibleLevel == null) {
                                    ToastUtil.show(R.string.visible_level_please);
                                    return;
                                }
                                HashMap<String, Object> params = new DefaultParams();
                                params.put("activity_result_id", result.id);
                                params.put("is_check", "true");
                                params.put("level", checkVisibleLevel);
                                result.is_check = "true";
                                lRequestTool.doPost(NetConst.API_CHECK, params, API_CHECK);
                                super.onPositive(dialog);
                            }
                        })
                        .build();
                checkDialog.setSelectedIndex(0);
                checkDialog.show();
                break;
            case R.id.activity_detail_tvUnchecked:
                if (uncheckedDialog == null) {
                    uncheckedDialog = new MaterialDialog.Builder(this)
                            .theme(Theme.LIGHT)
                            .title(R.string.dialog_confirm)
                            .content(R.string.dialog_unchecked)
                            .positiveText(android.R.string.yes)
                            .negativeText(android.R.string.no)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    HashMap<String, Object> params = new DefaultParams();
                                    params.put("activity_result_id", result.id);
                                    params.put("is_check", "false");
                                    result.is_check = "false";
                                    lRequestTool.doPost(NetConst.API_CHECK, params, API_CHECK);
                                    super.onPositive(dialog);
                                }
                            })
                            .build();
                }
                uncheckedDialog.show();
                break;
        }
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Emoji emoji = emojiList.get(position);
        int index = activityDetailEtComment.getSelectionStart();
        Editable editable = activityDetailEtComment.getText();
        editable.insert(index, emoji.tag);
        activityDetailEtComment.setText(LEmoji.translate(editable.toString()));
        activityDetailEtComment.setSelection(index + emoji.tag.length());
    }

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


    private void hideKeyBoard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(activityDetailEtComment.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showKeyBoard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(activityDetailEtComment.getWindowToken(),
                        InputMethodManager.SHOW_FORCED);
    }

    private void hideReply() {
        activityDetailLnReply.setVisibility(View.GONE);
        activityDetailLnOptions.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != Activity.RESULT_OK) {
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
        AttachView attachView = new AttachView(this, activityDetailLnAttached, this);
        attachView.setMedia(selectedFile);
        attachViews.add(attachView);
        activityDetailLnAttached.addView(attachView.getView());
        activityDetailScAttached.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (activityDetailGvEmoji.getVisibility() == View.VISIBLE) {
            activityDetailGvEmoji.setVisibility(View.GONE);
            if (attachViews.size() != 0) {
                activityDetailScAttached.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (activityDetailLnReply.getVisibility() == View.VISIBLE) {
            hideReply();
        } else {
            if (activityDetailEtComment.getText().length() != 0
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
    public void onDeleted(AttachView attachView) {
        int index = attachViews.indexOf(attachView);
        activityDetailLnAttached.removeViewAt(index);
        attachViews.remove(index);
        if (attachViews.size() == 0) {
            activityDetailScAttached.setVisibility(View.GONE);
        }
    }
}