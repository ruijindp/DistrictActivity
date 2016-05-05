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
import com.ljmob.districtactivity.adapter.JoinedResultAdapter;
import com.ljmob.districtactivity.entity.MessageBox;
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
 * Created by london on 15/7/22.
 * 我参与的
 */
public class JoinedActivity extends AppCompatActivity implements LRequestTool.OnResponseListener,
        SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener {
    private static final int API_MESSAGE = 1;

    ListView activity_join_lv;
    SwipeRefreshLayout swipeRefreshLayout;
    View foot_more;

    LRequestTool lRequestTool;
    List<MessageBox> messages;
    JoinedResultAdapter messageAdapter;

    int currentPage;
    boolean hasMore;
    boolean isLoading;
    boolean isDivDPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        lRequestTool = new LRequestTool(this);
        initView();
        refreshData();
    }

    private void initView() {
        activity_join_lv = (ListView) findViewById(R.id.activity_join_lv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        foot_more = getLayoutInflater().inflate(R.layout.foot_more, activity_join_lv, false);

        Toolbar toolbar_root = (Toolbar) findViewById(R.id.toolbar_root);
        setSupportActionBar(toolbar_root);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark0);
        activity_join_lv.addFooterView(foot_more);
        activity_join_lv.setOnScrollListener(this);
        activity_join_lv.setOnItemClickListener(this);
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
        params.put("sender", 0);
        params.put("page", page);
        lRequestTool.doGet(NetConst.API_MESSAGE, params, API_MESSAGE);
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
            case API_MESSAGE:
                List<MessageBox> appendResults = gson.fromJson(response.body, new TypeToken<List<MessageBox>>() {
                }.getType());
                if (messages == null) {
                    messages = new ArrayList<>();
                }
                if (currentPage == 1) {
                    messages = appendResults;
                } else {
                    messages.addAll(appendResults);
                }
                if (appendResults == null || appendResults.size() != 15) {
                    hasMore = false;
                    ((TextView) foot_more.findViewById(R.id.foot_more_tv)).setText(R.string.no_more);
                } else {
                    hasMore = true;
                    ((TextView) foot_more.findViewById(R.id.foot_more_tv)).setText(R.string.load_more);
                }
                if (messageAdapter == null) {
                    messageAdapter = new JoinedResultAdapter(messages);
                    activity_join_lv.setAdapter(messageAdapter);
                } else {
                    messageAdapter.setNewData(messages);
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
        MessageBox messageBox = messages.get(position);
        Result result = messageBox.activity_result;
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("result", result);
        startActivity(detailIntent);
    }
}
