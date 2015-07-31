package com.ljmob.districtactivity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.adapter.DetailAdapter;
import com.ljmob.districtactivity.entity.Result;
import com.ljmob.districtactivity.impl.InsiderListScrollListener;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.ljmob.districtactivity.util.MyApplication;
import com.ljmob.districtactivity.view.LoginDialog;
import com.ljmob.lemoji.LEmoji;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by london on 15/7/28.
 * 帖子详情
 */
public class DetailFragment extends Fragment implements View.OnClickListener, LRequestTool.OnResponseListener, LoginDialog.LoginListener {
    private static final int API_PRAISE = 1;
    private static final int API_VOTE = 2;

    View rootView;
    ListView fragment_detail_lvContent;
    View head_detail;
    View foot_detail;
    TextView head_detail_tvTitle;
    CircleImageView head_detail_imgHead;
    TextView head_detail_tvAuthor;
    TextView head_detail_tvFloorDate;
    TextView head_detail_tvTextContent;
    TextView foot_detail_tvPraise;
    LinearLayout foot_detail_lnPraise;

    LoginDialog loginDialog;

    LRequestTool lRequestTool;
    ImageLoader imageLoader;
    DetailAdapter detailAdapter;
    Result result;

    InsiderListScrollListener insiderListScrollListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        imageLoader = ImageLoader.getInstance();
        lRequestTool = new LRequestTool(this);
        initView(inflater);
        if (result != null) {
            initData();
        }
        return rootView;
    }

    public void setResult(Result result) {
        this.result = result;
        if (rootView != null) {
            initData();
        }
    }

    private void initView(LayoutInflater inflater) {
        fragment_detail_lvContent = (ListView) rootView.findViewById(R.id.fragment_detail_lvContent);

        head_detail = inflater.inflate(R.layout.head_detail, fragment_detail_lvContent, false);
        foot_detail = inflater.inflate(R.layout.foot_detail, fragment_detail_lvContent, false);
        fragment_detail_lvContent.addHeaderView(head_detail);
        fragment_detail_lvContent.addFooterView(foot_detail);

        initViewInHead();
        initViewInFoot();
    }

    private void initViewInHead() {
        head_detail_tvTitle = (TextView) rootView.findViewById(R.id.head_detail_tvTitle);
        head_detail_imgHead = (CircleImageView) rootView.findViewById(R.id.head_detail_imgHead);
        head_detail_tvAuthor = (TextView) rootView.findViewById(R.id.head_detail_tvAuthor);
        head_detail_tvFloorDate = (TextView) rootView.findViewById(R.id.head_detail_tvFloorDate);
        head_detail_tvTextContent = (TextView) rootView.findViewById(R.id.head_detail_tvTextContent);
    }

    private void initViewInFoot() {
        foot_detail_tvPraise = (TextView) rootView.findViewById(R.id.foot_detail_tvPraise);
        foot_detail_lnPraise = (LinearLayout) rootView.findViewById(R.id.foot_detail_lnPraise);

        foot_detail_lnPraise.setOnClickListener(this);
    }

    private void initData() {
        if (result.items == null) {
            result.items = new ArrayList<>(0);
        }
        imageLoader.displayImage(NetConst.ROOT_URL + result.author.avatar, head_detail_imgHead);
        head_detail_tvAuthor.setText(result.author.name);
        head_detail_tvFloorDate.setText("1" + getString(R.string.floor_) + " " + result.created_at);
        String content = result.items.size() == 0 ? result.description : result.items.get(0).content;
        if (content.length() == 0) {
            head_detail_tvTextContent.setVisibility(View.GONE);
        } else {
            head_detail_tvTextContent.setVisibility(View.VISIBLE);
            head_detail_tvTextContent.setText(LEmoji.translate(content));
        }
        head_detail_tvTitle.setText(result.title);
        foot_detail_tvPraise.setText(result.praise_count + "");

        detailAdapter = new DetailAdapter(result.items);
        fragment_detail_lvContent.setAdapter(detailAdapter);
        if (insiderListScrollListener != null) {
            fragment_detail_lvContent.setOnScrollListener(insiderListScrollListener);
        }
    }

    private void addPraiseCount() {
        result.praise_count++;
        result.is_praise = true;
        foot_detail_tvPraise.setText(result.praise_count + "");
    }

    private void showLoginDialog() {
        if (loginDialog == null) {
            loginDialog = new LoginDialog(getActivity());
            loginDialog.setLoginListener(this);
        }
        loginDialog.show();
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
                    ToastUtil.show(R.string.had_praised);
                    return;
                }
                addPraiseCount();
                HashMap<String, Object> praiseParams = new DefaultParams();
                praiseParams.put("activity_result_id", result.id);
                lRequestTool.doPost(NetConst.API_PRAISE, praiseParams, API_PRAISE);
                break;
        }
    }

    @Override
    public void loginSuccess(LoginDialog dialog) {
        if (MyApplication.currentUser != null) {
            ToastUtil.show(R.string.login_ed);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        detailAdapter.stopAllMusic();
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
        }
    }
}
