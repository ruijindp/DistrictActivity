package com.ljmob.districtactivity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.ljmob.districtactivity.adapter.MainHeadPagerAdapter;
import com.ljmob.districtactivity.adapter.ShowcaseAdapter;
import com.ljmob.districtactivity.entity.Activity;
import com.ljmob.districtactivity.entity.Result;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by london on 15/7/17.
 * 作品展示
 */
public class ShowcaseFragment extends Fragment implements LRequestTool.OnResponseListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener, MainHeadPagerAdapter.OnActivitySelectListener, AdapterView.OnItemClickListener {
    private static final int API_SEARCH_RESULT = 1;
    private static final int API_ACTIVITY = 2;
    public static boolean isResultChanged = false;

    View rootView;
    ViewPager headView;
    View footView;
    ListView fragment_showcase_lv;
    SwipeRefreshLayout swipeRefreshLayout;
    LRequestTool lRequestTool;
    List<Result> results;
    ShowcaseAdapter showcaseAdapter;

    int currentPage;
    boolean isDivDPage;
    boolean isLoading;
    boolean hasMore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_showcase, container, false);
        lRequestTool = new LRequestTool(this);
        initView(inflater);
        refreshData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isResultChanged) {
            refreshData();
        }
    }

    private void initView(LayoutInflater inflater) {
        fragment_showcase_lv = (ListView) rootView.findViewById(R.id.fragment_showcase_lv);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        headView = (ViewPager) inflater.inflate(R.layout.head_showcase, fragment_showcase_lv, false);
        footView = inflater.inflate(R.layout.foot_more, fragment_showcase_lv, false);

        fragment_showcase_lv.addHeaderView(headView);
        fragment_showcase_lv.addFooterView(footView);
        fragment_showcase_lv.setOnScrollListener(this);
        fragment_showcase_lv.setOnItemClickListener(this);
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
        loadActivities();
        loadPage(currentPage);
    }

    private void loadActivities() {
        HashMap<String, Object> params = new DefaultParams();
        params.put("activity_type_id", 1);
        lRequestTool.doGet(NetConst.API_ACTIVITY, params, API_ACTIVITY);
    }

    private void loadPage(int page) {
        isLoading = true;
        HashMap<String, Object> params = new DefaultParams();
        params.put("page", page);
        lRequestTool.doGet(NetConst.API_SEARCH_RESULT, params, API_SEARCH_RESULT);
    }

    @Override
    public void onResponse(LResponse response) {
        swipeRefreshLayout.setRefreshing(false);
        isLoading = false;
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        final Gson gson = new Gson();
        switch (response.requestCode) {
            case API_ACTIVITY://Head data
                List<Activity> activities = gson.fromJson(response.body, new TypeToken<List<Activity>>() {
                }.getType());
                headView.setAdapter(new MainHeadPagerAdapter(activities, this));
                break;
            case API_SEARCH_RESULT:
                List<Result> appendResults = gson.fromJson(response.body, new TypeToken<List<Result>>() {
                }.getType());
                if (results == null) {
                    results = new ArrayList<>();
                }
                if (currentPage == 1) {
                    results = appendResults;
                }else {
                    results.addAll(appendResults);
                }
                if (appendResults == null || appendResults.size() != 15) {
                    hasMore = false;
                    ((TextView) rootView.findViewById(R.id.foot_more_tv)).setText(R.string.no_more);
                } else {
                    hasMore = true;
                    ((TextView) rootView.findViewById(R.id.foot_more_tv)).setText(R.string.load_more);
                }
                if (showcaseAdapter == null) {
                    showcaseAdapter = new ShowcaseAdapter(results);
                    fragment_showcase_lv.setAdapter(showcaseAdapter);
                } else {
                    showcaseAdapter.setNewData(results);
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

    @Override
    public void onRefresh() {
        refreshData();
    }

    @Override
    public void onActivitySelected(MainHeadPagerAdapter adapter, Activity activity) {
        ToastUtil.show(activity.id + "" + activity.name);
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
}
