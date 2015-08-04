package com.ljmob.districtactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.districtactivity.adapter.NoticeAdapter;
import com.ljmob.districtactivity.entity.Notice;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by london on 15/8/4.
 * 公告列表
 */
public class NoticeListActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        AbsListView.OnScrollListener,
        LRequestTool.OnResponseListener,
        AdapterView.OnItemClickListener {
    private static final int API_PUBLIC_NOTICES = 1;

    ListView activity_broadcast_list_lv;
    SwipeRefreshLayout swipeRefreshLayout;
    View foot_more;

    LRequestTool lRequestTool;
    List<Notice> notices;
    NoticeAdapter noticeAdapter;

    int currentPage;
    boolean hasMore;
    boolean isLoading;
    boolean isDivDPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);
        lRequestTool = new LRequestTool(this);
        initView();
        refreshData();
    }

    private void initView() {
        activity_broadcast_list_lv = (ListView) findViewById(R.id.activity_broadcast_list_lv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        foot_more = getLayoutInflater().inflate(R.layout.foot_more, activity_broadcast_list_lv, false);

        Toolbar toolbar_root = (Toolbar) findViewById(R.id.toolbar_root);
        setSupportActionBar(toolbar_root);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark0);
        activity_broadcast_list_lv.addFooterView(foot_more);
        activity_broadcast_list_lv.setOnScrollListener(this);
        activity_broadcast_list_lv.setOnItemClickListener(this);
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
        HashMap<String, Object> params = new DefaultParams();
        params.put("page", page);
        lRequestTool.doGet(NetConst.API_PUBLIC_NOTICES, params, API_PUBLIC_NOTICES);
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
    public void onRefresh() {
        refreshData();
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
            case API_PUBLIC_NOTICES:
                List<Notice> appendResults = gson.fromJson(response.body, new TypeToken<List<Notice>>() {
                }.getType());
                if (notices == null) {
                    notices = new ArrayList<>();
                }
                if (currentPage == 1) {
                    notices = appendResults;
                } else {
                    notices.addAll(appendResults);
                }
                if (appendResults == null || appendResults.size() != 15) {
                    hasMore = false;
                    ((TextView) foot_more.findViewById(R.id.foot_more_tv)).setText(R.string.no_more);
                } else {
                    hasMore = true;
                    ((TextView) foot_more.findViewById(R.id.foot_more_tv)).setText(R.string.load_more);
                }
                if (noticeAdapter == null) {
                    noticeAdapter = new NoticeAdapter(notices);
                    activity_broadcast_list_lv.setAdapter(noticeAdapter);
                } else {
                    noticeAdapter.setNewData(notices);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Notice notice = notices.get(position);
        Intent detailIntent = new Intent(this, NoticeActivity.class);
        detailIntent.putExtra("notice", notice);
        startActivity(detailIntent);
    }
}
