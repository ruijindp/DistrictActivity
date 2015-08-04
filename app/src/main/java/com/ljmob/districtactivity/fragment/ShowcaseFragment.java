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
import com.ljmob.districtactivity.CategoryActivity;
import com.ljmob.districtactivity.DetailActivity;
import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.adapter.MainHeadPagerAdapter;
import com.ljmob.districtactivity.adapter.ShowcaseAdapter;
import com.ljmob.districtactivity.entity.Activity;
import com.ljmob.districtactivity.entity.Notice;
import com.ljmob.districtactivity.entity.Result;
import com.ljmob.districtactivity.impl.UiChangeRequest;
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
public class ShowcaseFragment extends Fragment implements LRequestTool.OnResponseListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener, MainHeadPagerAdapter.OnActivitySelectListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private static final int API_SEARCH_RESULT = 1;
    private static final int API_ACTIVITY = 2;
    private static final int API_PUBLIC_NOTICES = 3;
    public static boolean isResultChanged = false;

    View rootView;
    View headViewEmpty;
    View headView;
    View footView;
    ListView fragment_showcase_lv;
    SwipeRefreshLayout swipeRefreshLayout;
    ViewPager head_showcase_pager;
    View head_showcase_lnBroadcast;
    View head_showcase_cardBroadcast;
    TextView head_showcase_tvPreview;
    LRequestTool lRequestTool;
    List<Result> results;
    ShowcaseAdapter showcaseAdapter;
    UiChangeRequest uiChangeRequest;

    int currentPage;
    boolean isDivDPage;
    boolean isLoading;
    boolean hasMore;
    public static List<Activity> activities;
    float dp;
    int firstIndex = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_showcase, container, false);
        lRequestTool = new LRequestTool(this);
        dp = getResources().getDimension(R.dimen.one);
        initView(inflater);
        refreshData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isResultChanged) {
            refreshData();
            isResultChanged = false;
        }
    }

    private void initView(LayoutInflater inflater) {
        fragment_showcase_lv = (ListView) rootView.findViewById(R.id.fragment_showcase_lv);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        headViewEmpty = inflater.inflate(R.layout.head_main_empty, fragment_showcase_lv, false);
        headView = inflater.inflate(R.layout.head_showcase, fragment_showcase_lv, false);
        footView = inflater.inflate(R.layout.foot_more, fragment_showcase_lv, false);

        initViewInHead();

        fragment_showcase_lv.addHeaderView(headViewEmpty);//空的头部
        fragment_showcase_lv.addHeaderView(headView);
        fragment_showcase_lv.addFooterView(footView);
        fragment_showcase_lv.setOnScrollListener(this);
        fragment_showcase_lv.setOnItemClickListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark0);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressViewOffset(false, (int) (56 * dp), (int) (128 * dp));
    }

    private void initViewInHead() {
        head_showcase_pager = (ViewPager) headView.findViewById(R.id.head_showcase_pager);
        head_showcase_lnBroadcast = headView.findViewById(R.id.head_showcase_lnBroadcast);
        head_showcase_cardBroadcast = headView.findViewById(R.id.head_showcase_cardBroadcast);
        head_showcase_tvPreview = (TextView) headView.findViewById(R.id.head_showcase_tvPreview);

        head_showcase_lnBroadcast.setOnClickListener(this);
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
        loadBroadcast();
        loadPage(currentPage);
    }

    private void loadBroadcast() {
        lRequestTool.doGet(NetConst.API_PUBLIC_NOTICES, new DefaultParams(), API_PUBLIC_NOTICES);
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
        params.put("activity", 0);
        lRequestTool.doGet(NetConst.API_SEARCH_RESULT, params, API_SEARCH_RESULT);
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
            case API_PUBLIC_NOTICES:
                List<Notice> notices = gson.fromJson(response.body, new TypeToken<List<Notice>>() {
                }.getType());
                if (notices.size() == 0) {
                    head_showcase_cardBroadcast.setVisibility(View.GONE);
                } else {
                    head_showcase_cardBroadcast.setVisibility(View.VISIBLE);
                    head_showcase_tvPreview.setText(notices.get(0).description);
                }
                break;
            case API_ACTIVITY://Head data
                activities = gson.fromJson(response.body, new TypeToken<List<Activity>>() {
                }.getType());
                head_showcase_pager.setAdapter(new MainHeadPagerAdapter(activities, this));
                break;
            case API_SEARCH_RESULT:
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

    public void setUiChangeRequest(UiChangeRequest uiChangeRequest) {
        this.uiChangeRequest = uiChangeRequest;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isDivDPage = (firstVisibleItem + visibleItemCount == totalItemCount);
        //收起toolbar
        if (uiChangeRequest == null) {
            return;
        }
        if (firstIndex == -1) {
            firstIndex = firstVisibleItem;
            return;
        }
        if (firstIndex < firstVisibleItem) {
            uiChangeRequest.onHide();

        } else if (firstIndex > firstVisibleItem) {
            uiChangeRequest.onShow();
        }
        firstIndex = firstVisibleItem;
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    @Override
    public void onActivitySelected(MainHeadPagerAdapter adapter, Activity activity) {
        Intent cateIntent = new Intent(getActivity(), CategoryActivity.class);
        cateIntent.putExtra("activity", activity);
        startActivity(cateIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isLoading) {
            return;
        }
        Result result = results.get(position - 2);//-2 because of 2 header view
        Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
        detailIntent.putExtra("result", result);
        startActivity(detailIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_showcase_lnBroadcast:
                break;
        }
    }
}
