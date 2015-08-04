package com.ljmob.districtactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.districtactivity.entity.Activity;
import com.ljmob.districtactivity.entity.District;
import com.ljmob.districtactivity.entity.FilterCondition;
import com.ljmob.districtactivity.entity.School;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.ljmob.districtactivity.view.SimpleStringPopup;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by london on 15/8/3.
 * 排行榜过滤器
 */
public class FilterActivity extends AppCompatActivity implements
        View.OnClickListener,
        LRequestTool.OnResponseListener, SimpleStringPopup.SimpleStringListener {
    private static final int API_ACTIVITY = 1;
    private static final int API_DISTRICT = 2;
    private static final int API_SCHOOL = 3;

    @InjectView(R.id.toolbar_root)
    Toolbar toolbarRoot;
    @InjectView(R.id.activity_filter_tvCategory)
    TextView activityFilterTvCategory;
    @InjectView(R.id.activity_filter_flCategory)
    FrameLayout activityFilterFlCategory;
    @InjectView(R.id.activity_filter_tvDistrict)
    TextView activityFilterTvDistrict;
    @InjectView(R.id.activity_filter_flDistrict)
    FrameLayout activityFilterFlDistrict;
    @InjectView(R.id.activity_filter_tvSchool)
    TextView activityFilterTvSchool;
    @InjectView(R.id.activity_filter_flSchool)
    FrameLayout activityFilterFlSchool;

    FilterCondition filterCondition;
    LRequestTool lRequestTool;
    Gson gson;

    List<Activity> activities;
    List<District> districts;
    List<School> schools;

    List<String> activityNames;
    List<String> districtNames;
    List<String> schoolNames;

    ProgressDialog progressDialog;
    SimpleStringPopup popup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        filterCondition = new FilterCondition();
        lRequestTool = new LRequestTool(this);
        gson = new Gson();
        progressDialog = new ProgressDialog(this);
        init();
        getAllData();
    }

    private void getAllData() {
        HashMap<String, Object> params = new DefaultParams();
        params.put("activity_type_id", 1);
        lRequestTool.doGet(NetConst.API_ACTIVITY, params, API_ACTIVITY);
        lRequestTool.doGet(NetConst.API_DISTRICT, params, API_DISTRICT);
        lRequestTool.doGet(NetConst.API_SCHOOL, params, API_SCHOOL);
    }

    private void init() {
        ButterKnife.inject(this);
        setSupportActionBar(toolbarRoot);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        popup = new SimpleStringPopup(this, activityFilterFlCategory);

        progressDialog.setMessage(getString(R.string.loading));

        popup.setSimpleStringListener(this);
        activityFilterFlCategory.setOnClickListener(this);
        activityFilterFlDistrict.setOnClickListener(this);
        activityFilterFlSchool.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_filter_flCategory:
                popup.setStrings(activityNames);
                popup.showAsDropDown(v);
                break;
            case R.id.activity_filter_flDistrict:
                popup.setStrings(districtNames);
                popup.showAsDropDown(v);
                break;
            case R.id.activity_filter_flSchool:
                popup.setStrings(schoolNames);
                popup.showAsDropDown(v);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_done) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("filterCondition", filterCondition);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(LResponse response) {
        if (response.responseCode != 200) {
            progressDialog.dismiss();
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        if (!response.body.startsWith("[")) {
            progressDialog.dismiss();
            ToastUtil.serverErr(response.responseCode);
            return;
        }
        switch (response.requestCode) {
            case API_ACTIVITY:
                activities = gson.fromJson(response.body, new TypeToken<List<Activity>>() {
                }.getType());
                activityNames = new ArrayList<>(activities.size());
                for (Activity activity : activities) {
                    activityNames.add(activity.name);
                }
                break;
            case API_DISTRICT:
                districts = gson.fromJson(response.body, new TypeToken<List<District>>() {
                }.getType());
                districtNames = new ArrayList<>(districts.size());
                for (District district : districts) {
                    districtNames.add(district.name);
                }
                break;
            case API_SCHOOL:
                schools = gson.fromJson(response.body, new TypeToken<List<School>>() {
                }.getType());
                schoolNames = new ArrayList<>(schools.size());
                for (School school : schools) {
                    schoolNames.add(school.name);
                }
                break;
        }
        if (activities != null && districts != null && schools != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void selectStringAt(SimpleStringPopup popup, int index) {
        switch (popup.lastAnchor.getId()) {
            case R.id.activity_filter_flCategory:
                filterCondition.activityId = activities.get(index).id;
                activityFilterTvCategory.setText(activities.get(index).name);
                break;
            case R.id.activity_filter_flDistrict:
                filterCondition.districtId = districts.get(index).id;
                filterCondition.schoolId = 0;
                activityFilterTvDistrict.setText(districts.get(index).name);
                activityFilterTvSchool.setText(R.string.invaluable);
                break;
            case R.id.activity_filter_flSchool:
                filterCondition.schoolId = schools.get(index).id;
                filterCondition.districtId = 0;
                activityFilterTvSchool.setText(schools.get(index).name);
                activityFilterTvDistrict.setText(R.string.invaluable);
                break;
        }
    }
}
