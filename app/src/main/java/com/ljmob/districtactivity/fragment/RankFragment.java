package com.ljmob.districtactivity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.districtactivity.DetailActivity;
import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.adapter.RankAdapter;
import com.ljmob.districtactivity.entity.Result;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by london on 15/7/17.
 * 排行榜
 */
public class RankFragment extends Fragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, LRequestTool.OnResponseListener {
    private static final int API_RANK = 1;

    View rootView;
    View footView;
    @InjectView(R.id.fragment_rank_lv)
    ListView fragmentRankLv;
    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private int currentPage;
    private boolean hasMore;
    private boolean isLoading;
    private boolean isDivPage;
    private LRequestTool lRequestTool;
    private List<Result> results;
    private RankAdapter rankAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        lRequestTool = new LRequestTool(this);
        rootView = inflater.inflate(R.layout.fragment_rank, container, false);
        initView(inflater);
        refreshData();
        return rootView;
    }

    private void initView(LayoutInflater inflater) {
        ButterKnife.inject(this, rootView);
        footView = inflater.inflate(R.layout.foot_more, fragmentRankLv, false);

        fragmentRankLv.addFooterView(footView);
        fragmentRankLv.setOnScrollListener(this);
        fragmentRankLv.setOnItemClickListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark0);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void refreshData() {
        currentPage = 1;
        hasMore = true;
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        loadPage(currentPage);
    }

    private void loadPage(int page) {
        isLoading = true;
        HashMap<String, Object> params = new DefaultParams();
        params.put("page", page);
        lRequestTool.doGet(NetConst.API_RANK, params, API_RANK);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState != SCROLL_STATE_IDLE || isLoading) {
            return;
        }
        if (isDivPage && hasMore) {
            isDivPage = false;
            currentPage++;
            loadPage(currentPage);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isDivPage = (firstVisibleItem + visibleItemCount == totalItemCount);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isLoading) {
            return;
        }
        Result result = results.get(position - 1);//-1 because of header view
        Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
        detailIntent.putExtra("result", result);
        startActivity(detailIntent);
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    @Override
    public void onResponse(LResponse response) {
        isLoading = false;
        if (response.responseCode != 200) {
            swipeRefreshLayout.setRefreshing(false);
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        final Gson gson = new Gson();
        switch (response.requestCode) {
            case API_RANK:
                swipeRefreshLayout.setRefreshing(false);
                List<Result> appendResults = gson.fromJson(response.body, new TypeToken<List<Result>>() {
                }.getType());
                if (results == null) {
                    results = new ArrayList<>();
                }
                if (currentPage == 1) {
                    results = appendResults;
                } else {
                    results.addAll(appendResults);
                }
                if (appendResults == null || appendResults.size() != 15) {
                    hasMore = false;
                    ((TextView) rootView.findViewById(R.id.foot_more_tv)).setText(R.string.no_more);
                } else {
                    hasMore = true;
                    ((TextView) rootView.findViewById(R.id.foot_more_tv)).setText(R.string.load_more);
                }
                if (rankAdapter == null) {
                    rankAdapter = new RankAdapter(results);
                    fragmentRankLv.setAdapter(rankAdapter);
                } else {
                    rankAdapter.setNewData(results);
                }
                break;
        }
    }
}
