package com.ljmob.districtactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.districtactivity.adapter.ShowcaseAdapter;
import com.ljmob.districtactivity.entity.Activity;
import com.ljmob.districtactivity.entity.Result;
import com.ljmob.districtactivity.entity.School;
import com.ljmob.districtactivity.entity.TeamClass;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.ljmob.districtactivity.util.MyApplication;
import com.ljmob.districtactivity.view.SimpleStringPopup;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by london on 15/7/24.
 * 某一类别列表
 */
public class CategoryActivity extends AppCompatActivity implements
        LRequestTool.OnResponseListener,
        AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener,
        SimpleStringPopup.SimpleStringListener {
    private static final int API_SEARCH_RESULT = 1;
    private static final int API_SCHOOL = 2;
    private static final int API_TEAM_CLASS = 3;
    private static final int MY_TEAM_CLASS_ID = -3;
    public static boolean isResultChanged = false;

    Activity selectedActivity;
    ListView activity_category_lv;
    SwipeRefreshLayout swipeRefreshLayout;
    View headView;
    View footView;
    TextView head_filter_tvLeft;
    FrameLayout head_filter_flLeft;
    TextView head_filter_tvRight;
    FrameLayout head_filter_flRight;

    LRequestTool lRequestTool;
    List<Result> results;
    ShowcaseAdapter showcaseAdapter;
    List<String> filterMethod;
    List<School> schools;
    List<String> schoolNames;
    List<Integer> schoolIds;
    List<TeamClass> teamClasses;
    List<String> teamClassNames;

    SimpleStringPopup popup;

    int currentPage;
    boolean isDivDPage;
    boolean isLoading;
    boolean hasMore;
    int schoolId;
    int classId;
    int dataIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        selectedActivity = (Activity) getIntent().getSerializableExtra("activity");
        if (selectedActivity == null) {
            finish();
            return;
        }
        lRequestTool = new LRequestTool(this);
        initView();
        refreshData();
    }

    private void initView() {
        activity_category_lv = (ListView) findViewById(R.id.activity_category_lv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        headView = getLayoutInflater().inflate(R.layout.head_filter, activity_category_lv, false);
        footView = getLayoutInflater().inflate(R.layout.foot_more, activity_category_lv, false);
        popup = new SimpleStringPopup(this, activity_category_lv);
        filterMethod = new ArrayList<>();
        String[] allMethod = getResources().getStringArray(R.array.filter_method);
        if (MyApplication.currentUser == null) {
            filterMethod.add(allMethod[0]);//未登录只能按学校排
        } else {
            Collections.addAll(filterMethod, allMethod);
        }
        popup.setSimpleStringListener(this);
        initViewInHead();

        activity_category_lv.addHeaderView(headView);
        activity_category_lv.addFooterView(footView);
        activity_category_lv.setOnScrollListener(this);
        activity_category_lv.setOnItemClickListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark0);
        swipeRefreshLayout.setOnRefreshListener(this);

        Toolbar toolbar_root = (Toolbar) findViewById(R.id.toolbar_root);
        setSupportActionBar(toolbar_root);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(selectedActivity.name);
        }
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark0);
    }

    private void initViewInHead() {
        head_filter_tvLeft = (TextView) headView.findViewById(R.id.head_filter_tvLeft);
        head_filter_flLeft = (FrameLayout) headView.findViewById(R.id.head_filter_flLeft);
        head_filter_tvRight = (TextView) headView.findViewById(R.id.head_filter_tvRight);
        head_filter_flRight = (FrameLayout) headView.findViewById(R.id.head_filter_flRight);

        head_filter_flLeft.setOnClickListener(this);
        head_filter_flRight.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isResultChanged) {
            refreshData();
            isResultChanged = false;
        }
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
        loadAllSchools();

        if (MyApplication.currentUser == null) {
            loadAllClassOfSchool(0);
        } else {
            schoolIds = new ArrayList<>();
            for (TeamClass teamClass : MyApplication.currentUser.team_class) {
                if (!schoolIds.contains(teamClass.school.id)) {
                    schoolIds.add(teamClass.school.id);
                }
            }
            for (int mySchool : schoolIds) {
                loadAllClassOfSchool(mySchool);
            }
        }
        loadPage(currentPage);
    }

    private void loadAllClassOfSchool(int mySchoolId) {
        HashMap<String, Object> params = new DefaultParams();
        if (mySchoolId != 0) {
            params.put("school_id", mySchoolId);
        }
        lRequestTool.doGet(NetConst.API_TEAM_CLASS, params, API_TEAM_CLASS);
    }

    private void loadAllSchools() {
        lRequestTool.doGet(NetConst.API_SCHOOL, new DefaultParams(), API_SCHOOL);
    }


    private void loadPage(int page) {
        HashMap<String, Object> params = new DefaultParams();
        boolean isLeveled = false;
        if (schoolId != 0) {
            if (MyApplication.currentUser != null) {
                for (TeamClass teamClass : MyApplication.currentUser.team_class) {
                    if (teamClass.school.id == schoolId) {
                        params.put("level", "school");
                        isLeveled = true;
                        break;
                    }
                }
            }
            if (!isLeveled) {
                params.put("school_id", schoolId);
            }
        } else if (classId != 0) {
            if (classId == MY_TEAM_CLASS_ID) {
                params.put("level", "team_class");
            } else {
                if (MyApplication.currentUser != null) {
                    for (TeamClass teamClass : MyApplication.currentUser.team_class) {
                        if (teamClass.id == classId) {
                            params.put("level", "team_class");
                            break;
                        }
                    }
                }
                params.put("team_class_id", classId);
            }
        }
        params.put("page", page);
        params.put((schoolId == 0 && classId == 0) ? "activity" : "activity_id", selectedActivity.id);
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
            case API_TEAM_CLASS:
                List<TeamClass> appendData = gson.fromJson(response.body, new TypeToken<List<TeamClass>>() {
                }.getType());
                if (dataIndex == 0) {
                    teamClasses = new ArrayList<>(appendData.size());
                    TeamClass defaultClass = new TeamClass();
                    defaultClass.name = getString(R.string.str_default);
                    teamClasses.add(0, defaultClass);
                    teamClasses.addAll(appendData);
                } else {
                    teamClasses.addAll(appendData);
                }
                dataIndex++;
                if (dataIndex == schoolIds.size()) {
                    dataIndex = 0;
                }
                break;
            case API_SCHOOL:
                schools = gson.fromJson(response.body, new TypeToken<List<School>>() {
                }.getType());
                School defaultSchool = new School();
                defaultSchool.district_name = "";
                defaultSchool.name = getString(R.string.str_default);
                schools.add(0, defaultSchool);
                schoolNames = new ArrayList<>(schools.size());
                for (School school : schools) {
                    schoolNames.add(school.district_name + school.name);
                }
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
                    ((TextView) footView.findViewById(R.id.foot_more_tv)).setText(R.string.no_more);
                } else {
                    hasMore = true;
                    ((TextView) footView.findViewById(R.id.foot_more_tv)).setText(R.string.load_more);
                }
                if (showcaseAdapter == null) {
                    showcaseAdapter = new ShowcaseAdapter(results);
                    activity_category_lv.setAdapter(showcaseAdapter);
                    activity_category_lv.setSelection(1);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_search:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                searchIntent.putExtra("activity", selectedActivity);
                startActivity(searchIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (isLoading) {
            return;
        }
        switch (v.getId()) {
            case R.id.head_filter_flLeft:
                popup.showAsDropDown(head_filter_flLeft);
                popup.setStrings(filterMethod);
                break;
            case R.id.head_filter_flRight:
                if (teamClassNames == null) {
                    teamClassNames = new ArrayList<>(teamClasses.size());
                    for (TeamClass teamClass : teamClasses) {
                        teamClassNames.add(teamClass.name);
                    }
                }
                String currentFilterMethod = head_filter_tvLeft.getText().toString();
                if (currentFilterMethod.equals(filterMethod.get(0))) {
                    popup.setStrings(schoolNames);
                } else {
                    popup.setStrings(teamClassNames);
                }
                popup.showAsDropDown(head_filter_flRight);
                break;
        }
    }

    @Override
    public void selectStringAt(SimpleStringPopup popup, int index) {
        schoolId = 0;
        classId = 0;
        switch (popup.lastAnchor.getId()) {
            case R.id.head_filter_flLeft://左边
                head_filter_tvLeft.setText(filterMethod.get(index));
                head_filter_tvRight.setText(R.string.str_default);
                switch (index) {
                    case 0://全部作品
                        head_filter_flRight.setVisibility(View.VISIBLE);
                        break;
                    case 1://本校
                        head_filter_flRight.setVisibility(View.VISIBLE);
                        if (MyApplication.currentUser == null) {
                            ToastUtil.show(R.string.not_login);
                            schoolId = 0;
                        } else {
                            schoolId = MyApplication.currentUser.team_class.get(0).school.id;
                        }
                        break;
                    case 2://本班
                        if (MyApplication.currentUser == null) {
                            ToastUtil.show(R.string.not_login);
                            schoolId = 0;
                        } else {
                            head_filter_flRight.setVisibility(View.INVISIBLE);
                            classId = MY_TEAM_CLASS_ID;//标记为查本班
//                            classId = MyApplication.currentUser.team_class.get(0).id;
                        }
                        break;
                }
                refreshData();
                break;
            case R.id.head_filter_flRight://右边
                if (head_filter_tvLeft.getText().toString().equals(filterMethod.get(0))) {//按学校
                    head_filter_tvRight.setText(schoolNames.get(index));
                    schoolId = schools.get(index).id;
                } else {//按班级
                    if (index == 0) {//默认
                        schoolId = MyApplication.currentUser.team_class.get(0).school.id;
                    } else {
                        head_filter_tvRight.setText(teamClassNames.get(index));
                        classId = teamClasses.get(index).id;
                    }
                }
                refreshData();
                break;
        }
    }
}
