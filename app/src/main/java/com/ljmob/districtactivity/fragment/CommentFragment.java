package com.ljmob.districtactivity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.adapter.CommentAdapter;
import com.ljmob.districtactivity.entity.Comment;
import com.ljmob.districtactivity.entity.Result;
import com.ljmob.districtactivity.impl.InsiderListScrollListener;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by london on 15/7/28.
 * 评论
 */
public class CommentFragment extends Fragment implements AbsListView.OnScrollListener, LRequestTool.OnResponseListener {
    private static final int API_COMMENT = 1;
    View rootView;
    View footView;
    ListView fragment_comment_lv;

    LRequestTool lRequestTool;
    List<Comment> comments;
    CommentAdapter commentAdapter;

    Result result;
    int currentPage;
    boolean isDivDPage;
    boolean isLoading;
    boolean hasMore;

    InsiderListScrollListener insiderListScrollListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_comment, container, false);
        lRequestTool = new LRequestTool(this);
        initView(inflater);
        initData();
        return rootView;
    }

    private void initData() {
        currentPage = 1;
        if (result == null) {
            return;
        }
        loadPage(currentPage);
    }

    public void setResult(Result result) {
        this.result = result;
        if (rootView == null) {
            return;
        }
        initData();
    }

    private void initView(LayoutInflater inflater) {
        fragment_comment_lv = (ListView) rootView.findViewById(R.id.fragment_comment_lv);
        footView = inflater.inflate(R.layout.foot_more, fragment_comment_lv, false);
        fragment_comment_lv.addFooterView(footView);

        if (insiderListScrollListener != null) {
            fragment_comment_lv.setOnScrollListener(insiderListScrollListener);
        }
    }

    private void loadPage(int page) {
        isLoading = true;
        HashMap<String, Object> params = new DefaultParams();
        params.put("page", page);
        params.put("activity_result_id", result.id);
        lRequestTool.doGet(NetConst.API_COMMENT, params, API_COMMENT);
    }

    public void setInsiderListScrollListener(InsiderListScrollListener insiderListScrollListener) {
        this.insiderListScrollListener = insiderListScrollListener;
        if (fragment_comment_lv == null) {
            return;
        }
        fragment_comment_lv.setOnScrollListener(insiderListScrollListener);
    }

    @Override
    public void onResponse(LResponse response) {
        isLoading = false;
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        final Gson gson = new Gson();
        switch (response.requestCode) {
            case API_COMMENT:
                List<Comment> appendResults = gson.fromJson(response.body, new TypeToken<List<Comment>>() {
                }.getType());
                if (comments == null) {
                    comments = new ArrayList<>();
                }
                if (currentPage == 1) {
                    comments = appendResults;
                } else {
                    comments.addAll(appendResults);
                }
                if (appendResults == null || appendResults.size() != 15) {
                    hasMore = false;
                    ((TextView) rootView.findViewById(R.id.foot_more_tv)).setText(R.string.no_more);
                } else {
                    hasMore = true;
                    ((TextView) rootView.findViewById(R.id.foot_more_tv)).setText(R.string.load_more);
                }
                if (commentAdapter == null) {
                    commentAdapter = new CommentAdapter(comments);
                    fragment_comment_lv.setAdapter(commentAdapter);
                } else {
                    commentAdapter.setNewData(comments);
                }
                break;
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
            loadPage(currentPage);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isDivDPage = (firstVisibleItem + visibleItemCount == totalItemCount);
    }
}
