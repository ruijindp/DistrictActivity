package com.ljmob.districtactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ljmob.districtactivity.adapter.EmojiAdapter;
import com.ljmob.districtactivity.entity.Activity;
import com.ljmob.districtactivity.fragment.ShowcaseFragment;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.subView.AttachView;
import com.ljmob.districtactivity.util.DefaultParams;
import com.ljmob.districtactivity.util.FileTypeChecker;
import com.ljmob.districtactivity.util.MyApplication;
import com.ljmob.districtactivity.view.LoginDialog;
import com.ljmob.districtactivity.view.SimpleStringPopup;
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

/**
 * Created by london on 15/7/22.
 * 上传作品
 */
public class UploadActivity extends AppCompatActivity implements
        View.OnClickListener,
        LRequestTool.OnResponseListener,
        SimpleStringPopup.SimpleStringListener, AttachView.AttachViewDeleteListener, LoginDialog.LoginListener, AdapterView.OnItemClickListener {
    private static final int RESULT_GET = 1;
    private static final int API_RESULT = 1;

    View activity_post_root;
    EditText activity_post_etTitle;
    TextView activity_post_tvCategory;
    EditText activity_post_etContent;
    ImageView activity_post_imgPicture;
    ImageView activity_post_imgEmotion;
    ImageView activity_post_imgAudio;
    ImageView activity_post_imgVideo;
    LinearLayout activity_post_lnAttached;
    View activity_post_viewAnchor;
    LinearLayout activity_post_lnAttachType;
    HorizontalScrollView activity_post_scAttached;
    View dialog_video;
    GridView activity_post_gvEmoji;
    EditText dialog_video_et;

    LRequestTool lRequestTool;
    List<Activity> activities;
    List<String> activityNames;
    List<AttachView> attaches;
    List<Emoji> emojiList;

    SimpleStringPopup popup;
    LoginDialog loginDialog;
    AttachView videoAttachView;

    Activity selectedActivity;
    ProgressDialog uploadingDialog;
    MaterialDialog materialDialog;
    MaterialDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        lRequestTool = new LRequestTool(this);
        attaches = new ArrayList<>();
        uploadingDialog = new ProgressDialog(this);
        uploadingDialog.setMessage(getString(R.string.dialog_uploading));
        emojiList = LEmoji.getAllEmoji();
        initView();
        initData();
    }

    private void initData() {
        activities = ShowcaseFragment.activities;
        activityNames = new ArrayList<>();
        for (Activity activity : activities) {
            if (activity.id == 0) {
                continue;
            }
            activityNames.add(activity.name);
        }
    }

    private void initView() {
        activity_post_root = findViewById(R.id.activity_post_root);
        activity_post_etTitle = (EditText) findViewById(R.id.activity_post_etTitle);
        activity_post_tvCategory = (TextView) findViewById(R.id.activity_post_tvCategory);
        activity_post_etContent = (EditText) findViewById(R.id.activity_post_etContent);
        activity_post_imgPicture = (ImageView) findViewById(R.id.activity_post_imgPicture);
        activity_post_imgEmotion = (ImageView) findViewById(R.id.activity_post_imgEmotion);
        activity_post_imgAudio = (ImageView) findViewById(R.id.activity_post_imgAudio);
        activity_post_imgVideo = (ImageView) findViewById(R.id.activity_post_imgVideo);
        activity_post_lnAttached = (LinearLayout) findViewById(R.id.activity_post_lnAttached);
        activity_post_viewAnchor = findViewById(R.id.activity_post_viewAnchor);
        activity_post_lnAttachType = (LinearLayout) findViewById(R.id.activity_post_lnAttachType);
        activity_post_scAttached = (HorizontalScrollView) findViewById(R.id.activity_post_scAttached);
        activity_post_gvEmoji = (GridView) findViewById(R.id.activity_post_gvEmoji);

        dialog_video = LayoutInflater.from(this)
                .inflate(R.layout.dialog_video, (ViewGroup) activity_post_root, false);
        dialog_video_et = (EditText) dialog_video.findViewById(R.id.dialog_video_et);

        activity_post_gvEmoji.setAdapter(new EmojiAdapter(emojiList));

        popup = new SimpleStringPopup(this, (ViewGroup) activity_post_tvCategory.getParent());
        popup.setSimpleStringListener(this);

        Toolbar toolbar_root = (Toolbar) findViewById(R.id.toolbar_root);
        setSupportActionBar(toolbar_root);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        activity_post_tvCategory.setOnClickListener(this);
        activity_post_imgPicture.setOnClickListener(this);
        activity_post_imgEmotion.setOnClickListener(this);
        activity_post_imgAudio.setOnClickListener(this);
        activity_post_imgVideo.setOnClickListener(this);
        activity_post_gvEmoji.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_post:
                if (MyApplication.currentUser == null) {
                    showLoginDialog();
                    break;
                }
                if (!MyApplication.currentUser.roles.equals("student")) {
                    MaterialDialog userDialog = new MaterialDialog.Builder(this)
                            .theme(Theme.LIGHT)
                            .title(R.string.dialog_switch_user)
                            .content(R.string.dialog_switch_user_desc)
                            .positiveText(android.R.string.ok)
                            .negativeText(android.R.string.cancel)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    showLoginDialog();
                                    super.onPositive(dialog);
                                }
                            })
                            .build();
                    userDialog.show();
                    break;
                }
                send();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void send() {
        if (activity_post_etTitle.getText().toString().length() == 0) {
            ToastUtil.show(R.string.toast_err_title);
            return;
        }
        if (activity_post_tvCategory.getText().toString().length() == 0) {
            ToastUtil.show(R.string.toast_err_cate);
            return;
        }
        if (activity_post_etContent.getText().toString().length() == 0) {
            ToastUtil.show(R.string.toast_err_content);
            return;
        }
        uploadingDialog.show();
        HashMap<String, Object> params = new DefaultParams();
        params.put("activity_id", selectedActivity.id);
        params.put("title", activity_post_etTitle.getText().toString());
        params.put("content", activity_post_etContent.getText().toString());
        params.put("video_url", "");
        params.put("file_size", attaches.size());
        for (int i = 0; i < attaches.size(); i++) {
            AttachView attachView = attaches.get(i);
            if (attachView.displayFile == null) {
                params.put("video_url", attachView.displayUrl);
            } else {
                params.put("file" + i, attachView.displayFile);
                String filePath = attachView.displayFile.getAbsolutePath();
                filePath = filePath.substring(filePath.lastIndexOf("/") + 1);
                filePath = filePath.substring(0, filePath.lastIndexOf("."));
                params.put("file_name_" + i, filePath);
            }
        }
        lRequestTool.doPost(NetConst.API_RESULT, params, API_RESULT);
    }

    @Override
    public void onClick(View v) {
        activity_post_gvEmoji.setVisibility(View.GONE);//先隐藏
        if (attaches.size() == 0) {
            activity_post_scAttached.setVisibility(View.GONE);
        } else {
            activity_post_scAttached.setVisibility(View.VISIBLE);
        }
        switch (v.getId()) {
            case R.id.activity_post_tvCategory:
                popup.setStrings(activityNames);
                popup.showAsDropDown(activity_post_viewAnchor);
                break;
            case R.id.activity_post_imgPicture://选择图片
                Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pictureIntent.setType("image/*");
                pictureIntent.putExtra("return-data", true);
                startActivityForResult(pictureIntent, RESULT_GET);
                break;
            case R.id.activity_post_imgEmotion:
                activity_post_scAttached.setVisibility(View.GONE);
                activity_post_gvEmoji.setVisibility(View.VISIBLE);
                break;
            case R.id.activity_post_imgAudio://选择声音
                Intent audioIntent = new Intent(Intent.ACTION_GET_CONTENT);
                audioIntent.setType("audio/*");
                audioIntent.putExtra("return-data", true);
                startActivityForResult(audioIntent, RESULT_GET);
                break;
            case R.id.activity_post_imgVideo://选择视频
                int titleRes = videoAttachView == null ? R.string.dialog_video : R.string.dialog_video_replace;
                if (materialDialog == null) {
                    materialDialog = new MaterialDialog.Builder(this)
                            .title(titleRes)
                            .theme(Theme.LIGHT)
                            .positiveText(R.string.dialog_positive)
                            .customView(dialog_video, false)
                            .callback(new PositiveClickListener())
                            .build();
                }
                materialDialog.setTitle(titleRes);
                UploadActivity.this.materialDialog.show();
//                MaterialDialog videoTypeDialog = new MaterialDialog.Builder(this)
//                        .theme(Theme.LIGHT)
//                        .title(R.string.video_source)
//                        .items(R.array.video_source)
//                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog materialDialog, View view,
//                                                       int i, CharSequence charSequence) {
//                                switch (i) {
//                                    case 0://优酷
//                                        UploadActivity.this.materialDialog.show();
//                                        break;
//                                    case 1://相册
//                                        Intent audioIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                                        audioIntent.setType("video/*");
//                                        audioIntent.putExtra("return-data", true);
//                                        startActivityForResult(audioIntent, RESULT_GET);
//                                        break;
//                                }
//                                return true;
//                            }
//                        })
//                        .build();
//                videoTypeDialog.show();
//                break;
        }
    }

    @Override
    public void onResponse(LResponse response) {
        uploadingDialog.dismiss();
        if (response.responseCode == 401) {
            showLoginDialog();
            return;
        }
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        switch (response.requestCode) {
            case API_RESULT:
                ShowcaseFragment.isResultChanged = true;
                ToastUtil.show(R.string.toast_uploaded);
                finish();
                break;
        }
    }

    private void showLoginDialog() {
        if (loginDialog == null) {
            loginDialog = new LoginDialog(this);
            loginDialog.setLoginListener(this);
        }
        loginDialog.show();
    }

    @Override
    public void selectStringAt(SimpleStringPopup popup, int index) {
        selectedActivity = activities.get(index);
        activity_post_tvCategory.setText(selectedActivity.name);
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
        if (!FileTypeChecker.isFileTypeAvailable(selectedFile.getAbsolutePath())) {
            ToastUtil.show(R.string.toast_file_not_support);
            return;
        }
        AttachView attachView = new AttachView(this, activity_post_lnAttached, this);
        attachView.setMedia(selectedFile);
        attaches.add(attachView);
        activity_post_lnAttached.addView(attachView.getView());
        activity_post_scAttached.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeleted(AttachView attachView) {
        int index = attaches.indexOf(attachView);
        activity_post_lnAttached.removeViewAt(index);
        attaches.remove(index);
        if (attaches.size() == 0) {
            activity_post_scAttached.setVisibility(View.GONE);
        }
    }

    @Override
    public void loginSuccess(LoginDialog dialog) {
        if (MyApplication.currentUser != null) {
            ToastUtil.show(R.string.login_ed);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Emoji emoji = emojiList.get(position);
        int index = activity_post_etContent.getSelectionStart();
        Editable editable = activity_post_etContent.getText();
        editable.insert(index, emoji.tag);
        activity_post_etContent.setText(LEmoji.translate(editable.toString()));
        activity_post_etContent.setSelection(index + emoji.tag.length());
    }

    private class PositiveClickListener extends MaterialDialog.ButtonCallback {
        @Override
        public void onPositive(MaterialDialog dialog) {
            String videoUrl = dialog_video_et.getText().toString();
            if (videoUrl.length() == 0) {
                ToastUtil.show(R.string.video_url_err);
                return;
            }
            if (videoUrl.contains("?")) {
                videoUrl = videoUrl.substring(0, videoUrl.indexOf("?"));
            }
            if (!videoUrl.contains("youku") && !videoUrl.contains("YOUKU")) {
                ToastUtil.show(R.string.video_url_err);
                return;
            }
            dialog.dismiss();

            if (videoAttachView == null) {
                videoAttachView = new AttachView(UploadActivity.this,
                        activity_post_lnAttached, UploadActivity.this);
                attaches.add(videoAttachView);
                activity_post_lnAttached.addView(videoAttachView.getView());
                activity_post_scAttached.setVisibility(View.VISIBLE);
            }
            videoAttachView.setMedia(videoUrl);
            super.onPositive(dialog);
        }
    }

    @Override
    public void onBackPressed() {
        if (activity_post_gvEmoji.getVisibility() == View.VISIBLE) {
            activity_post_gvEmoji.setVisibility(View.GONE);
            if (attaches.size() == 0) {
                activity_post_scAttached.setVisibility(View.GONE);
            } else {
                activity_post_scAttached.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (activity_post_etTitle.getText().length() != 0
                || activity_post_tvCategory.getText().length() != 0
                || activity_post_etContent.length() != 0
                || attaches.size() != 0) {
            if (MyApplication.currentUser == null ||
                    !MyApplication.currentUser.roles.equals("student")) {
                finish();
            }
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
