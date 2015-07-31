package com.ljmob.districtactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
 * Created by london on 15/7/29.
 * 模糊搜索
 */
public class SearchActivity extends AppCompatActivity implements
        LRequestTool.OnResponseListener,
        AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, TextWatcher {
    private static int API_SEARCH = 1;

    Activity selectedActivity;
    ListView activity_search_lv;
    SwipeRefreshLayout swipeRefreshLayout;
    View activity_search_imgBack;
    View activity_search_imgSearch;
    EditText activity_search_etSearch;
    View headView;
    View footView;

    LRequestTool lRequestTool;
    List<Result> results;
    ShowcaseAdapter showcaseAdapter;

    int currentPage;
    boolean isDivDPage;
    boolean isLoading;
    boolean hasMore;
    String keyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        selectedActivity = (Activity) getIntent().getSerializableExtra("activity");
        if (selectedActivity == null) {
            finish();
            return;
        }
        lRequestTool = new LRequestTool(this);
        initView();
    }

    private void initView() {
        activity_search_lv = (ListView) findViewById(R.id.activity_search_lv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        activity_search_imgBack = findViewById(R.id.activity_search_imgBack);
        activity_search_imgSearch = findViewById(R.id.activity_search_imgSearch);
        activity_search_etSearch = (EditText) findViewById(R.id.activity_search_etSearch);

        headView = getLayoutInflater().inflate(R.layout.head_search, activity_search_lv, false);
        footView = getLayoutInflater().inflate(R.layout.foot_more, activity_search_lv, false);

        activity_search_lv.addHeaderView(headView);
        activity_search_lv.addFooterView(footView);
        activity_search_lv.setOnScrollListener(this);
        activity_search_lv.setOnItemClickListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark0);
        swipeRefreshLayout.setOnRefreshListener(this);
        activity_search_imgBack.setOnClickListener(this);
        activity_search_imgSearch.setOnClickListener(this);
        activity_search_etSearch.addTextChangedListener(this);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark0);
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
        if (keyWord == null || keyWord.length() == 0 || keyWord.equals("?")) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            return;
        }
        isLoading = true;
        HashMap<String, Object> params = new DefaultParams();
        params.put("q%5Btitle_cont%5D", keyWord);
        params.put("q%5Bauthor_cont%5D", keyWord);
        params.put("page", page);
        params.put("activity", selectedActivity.id);
        API_SEARCH++;
        lRequestTool.doGet(NetConst.API_SEARCH, params, API_SEARCH);
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
        if (API_SEARCH == response.requestCode) {
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
                ((TextView) footView.findViewById(R.id.foot_more_tv)).setText(R.string.no_more);
            } else {
                hasMore = true;
                ((TextView) footView.findViewById(R.id.foot_more_tv)).setText(R.string.load_more);
            }
            if (showcaseAdapter == null) {
                showcaseAdapter = new ShowcaseAdapter(results);
                activity_search_lv.setAdapter(showcaseAdapter);
            } else {
                showcaseAdapter.setNewData(results);
            }
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isLoading) {
            return;
        }
        Result result = results.get(position - 1);//-1 because of header view
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("result", result);
        startActivity(detailIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_search_imgBack:
                finish();
                break;
            case R.id.activity_search_imgSearch:
                hideKeyBoard();
                break;
        }
    }

    private void hideKeyBoard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(activity_search_etSearch.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        keyWord = s.toString();
        if (keyWord.length() == 0) {
            return;
        }
        refreshData();
    }
}