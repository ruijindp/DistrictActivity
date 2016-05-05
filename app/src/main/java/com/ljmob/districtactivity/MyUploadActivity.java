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
import com.ljmob.districtactivity.adapter.ShowcaseAdapter;
import com.ljmob.districtactivity.entity.Result;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.ljmob.districtactivity.util.MyApplication;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by london on 15/7/22.
 * 我的上传
 */
public class MyUploadActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        LRequestTool.OnResponseListener,
        AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener {

    public static boolean isResultChanged = false;
    private static final int API_SEARCH_RESULT = 1;

    ListView activity_my_lv;
    SwipeRefreshLayout swipeRefreshLayout;
    View foot_more;

    LRequestTool lRequestTool;

    List<Result> results;
    ShowcaseAdapter showcaseAdapter;

    int currentPage;
    boolean hasMore;
    boolean isLoading;
    boolean isDivDPage;

    public static String EXPERT_FLAG = "EXPERT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        lRequestTool = new LRequestTool(this);
        initView();
        refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isResultChanged) {
            refreshData();
        }
    }

    private void initView() {
        activity_my_lv = (ListView) findViewById(R.id.activity_my_lv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        foot_more = getLayoutInflater().inflate(R.layout.foot_more, activity_my_lv, false);

        Toolbar toolbar_root = (Toolbar) findViewById(R.id.toolbar_root);
        setSupportActionBar(toolbar_root);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            if (MyApplication.currentUser.roles.equals("teacher") ||
                    MyApplication.currentUser.roles.equals("grade")) {
                ab.setTitle(R.string.activity_myUpload_teacher);
            } else if (MyApplication.currentUser.roles.equals("expert")) {
                ab.setTitle(R.string.activity_expert);
            }
        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark0);
        activity_my_lv.addFooterView(foot_more);
        activity_my_lv.setOnScrollListener(this);
        activity_my_lv.setOnItemClickListener(this);
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
        params.put("inspect_result", 0);
        params.put("user_id", MyApplication.currentUser.id);
        params.put("roles", MyApplication.currentUser.roles);
        params.put("page", page);
        if(MyApplication.currentUser.roles.equals("expert") ||
                MyApplication.currentUser.roles.equals("teacher") ||
                MyApplication.currentUser.roles.equals("grade") ||
                MyApplication.currentUser.roles.equals("general")){
            params.put("status", "checking");
        }
        lRequestTool.doGet(NetConst.API_SEARCH_RESULT, params, API_SEARCH_RESULT);
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
            case API_SEARCH_RESULT:
                List<Result> appendResults = gson.fromJson(response.body, new TypeToken<List<Result>>() {
                }.getType());

                if (MyApplication.currentUser != null && (MyApplication.currentUser.roles.equals("teacher")
                        || MyApplication.currentUser.roles.equals("grade")|| MyApplication.currentUser.roles.equals("general"))) {
                    List<Result> list = null;
                    if (appendResults != null || appendResults.size() > 0) {
                        list = new ArrayList<>();
                        for (int i = 0; i < appendResults.size(); i++) {
                            Result result = appendResults.get(i);
                            if (result.is_check.equals("true")) {
                                list.add(result);
                            }
                        }
                        for (int i = 0; i < list.size(); i++) {
                            appendResults.remove(list.get(i));
                        }
                    }
                }
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
                    ((TextView) foot_more.findViewById(R.id.foot_more_tv)).setText(R.string.no_more);
                } else {
                    hasMore = true;
                    ((TextView) foot_more.findViewById(R.id.foot_more_tv)).setText(R.string.load_more);
                }
                if (showcaseAdapter == null) {
                    showcaseAdapter = new ShowcaseAdapter(results);
                    showcaseAdapter.showCheckStatus = true;
                    activity_my_lv.setAdapter(showcaseAdapter);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isLoading) {
            return;
        }
        Result result = results.get(position);
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("result", result);
        detailIntent.putExtra("expert_flag", EXPERT_FLAG);
        startActivity(detailIntent);
        finish();
    }
}
