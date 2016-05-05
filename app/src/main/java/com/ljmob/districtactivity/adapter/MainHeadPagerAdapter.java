package com.ljmob.districtactivity.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.entity.Activity;
import com.ljmob.districtactivity.net.NetConst;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by london on 15/7/21.
 * 活动pager
 */
public class MainHeadPagerAdapter extends PagerAdapter {
    OnActivitySelectListener onActivitySelectListener;
    List<List<Activity>> bundles;
    List<View> pages;
    ImageLoader imageLoader;

    public MainHeadPagerAdapter(List<Activity> activities
            , OnActivitySelectListener onActivitySelectListener) {
        this.onActivitySelectListener = onActivitySelectListener;
        imageLoader = ImageLoader.getInstance();
        int activitiesPerPage = 8;//每页最多8个
        int pageSize = activities.size() / activitiesPerPage +
                ((activities.size() % activitiesPerPage == 0) ? 0 : 1);
        bundles = new ArrayList<>(pageSize);
        for (int i = 0; i < pageSize; i++) {
            List<Activity> activitiesInBundle = new ArrayList<>(activitiesPerPage);
            for (int j = 0; j < activitiesPerPage; j++) {
                try {
                    activitiesInBundle.add(activities.get(i * activitiesPerPage + j));
                } catch (IndexOutOfBoundsException e) {//此页未满
                    break;
                }
            }
            bundles.add(activitiesInBundle);
        }
    }

    @Override
    public int getCount() {
        return bundles.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;//它用于判断你当前要显示的页面
    }

//  getItem(int position)是获取item的位置，
//  instantiateItem(ViewGroup container, final int position)是初始化item用的
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (pages == null) {
            pages = new ArrayList<>(getCount());
        }
        if (pages.size() <= position) {
            View page = LayoutInflater.from(container.getContext()).
                    inflate(R.layout.page_categories, container, false);
            ViewHolder viewHolder = new ViewHolder(page);
            page.setTag(viewHolder);
            pages.add(page);
        }
        View page = pages.get(position);
        List<Activity> bundle = bundles.get(position);
        initPageByBundle(page, bundle);
        container.addView(page);
        return page;
    }

    private void initPageByBundle(View page, List<Activity> bundle) {
        ViewHolder holder = (ViewHolder) page.getTag();
        holder.page_categories_item0.setVisibility(View.INVISIBLE);
        holder.page_categories_item1.setVisibility(View.INVISIBLE);
        holder.page_categories_item2.setVisibility(View.INVISIBLE);
        holder.page_categories_item3.setVisibility(View.INVISIBLE);
        holder.page_categories_item4.setVisibility(View.INVISIBLE);
        holder.page_categories_item5.setVisibility(View.INVISIBLE);
        holder.page_categories_item6.setVisibility(View.INVISIBLE);
        holder.page_categories_item7.setVisibility(View.INVISIBLE);

        View item = null;
        ImageView img = null;
        TextView tv = null;
        for (int i = 0; i < bundle.size(); i++) {
            Activity activity = bundle.get(i);
            switch (i) {
                case 0:
                    item = holder.page_categories_item0;
                    img = holder.page_categories_img0;
                    tv = holder.page_categories_tv0;
                    break;
                case 1:
                    item = holder.page_categories_item1;
                    img = holder.page_categories_img1;
                    tv = holder.page_categories_tv1;
                    break;
                case 2:
                    item = holder.page_categories_item2;
                    img = holder.page_categories_img2;
                    tv = holder.page_categories_tv2;
                    break;
                case 3:
                    item = holder.page_categories_item3;
                    img = holder.page_categories_img3;
                    tv = holder.page_categories_tv3;
                    break;
                case 4:
                    item = holder.page_categories_item4;
                    img = holder.page_categories_img4;
                    tv = holder.page_categories_tv4;
                    break;
                case 5:
                    item = holder.page_categories_item5;
                    img = holder.page_categories_img5;
                    tv = holder.page_categories_tv5;
                    break;
                case 6:
                    item = holder.page_categories_item6;
                    img = holder.page_categories_img6;
                    tv = holder.page_categories_tv6;
                    break;
                case 7:
                    item = holder.page_categories_item7;
                    img = holder.page_categories_img7;
                    tv = holder.page_categories_tv7;
                    break;
            }
            item.setVisibility(View.VISIBLE);
            tv.setText(activity.name);
            item.setOnClickListener(new ActivityClickListener(activity));
            imageLoader.displayImage(NetConst.ROOT_URL + activity.phone_img, img);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View page = pages.get(position);
        if (page != null) {
            container.removeView(page);
        } else {
            container.removeViewAt(0);
        }
    }

    private class ActivityClickListener implements View.OnClickListener {
        Activity activity;

        public ActivityClickListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(View v) {
            onActivitySelectListener.onActivitySelected(MainHeadPagerAdapter.this, activity);
        }
    }

    public class ViewHolder {
        public final CircleImageView page_categories_img0;
        public final TextView page_categories_tv0;
        public final LinearLayout page_categories_item0;
        public final CircleImageView page_categories_img1;
        public final TextView page_categories_tv1;
        public final LinearLayout page_categories_item1;
        public final CircleImageView page_categories_img2;
        public final TextView page_categories_tv2;
        public final LinearLayout page_categories_item2;
        public final CircleImageView page_categories_img3;
        public final TextView page_categories_tv3;
        public final LinearLayout page_categories_item3;
        public final CircleImageView page_categories_img4;
        public final TextView page_categories_tv4;
        public final LinearLayout page_categories_item4;
        public final CircleImageView page_categories_img5;
        public final TextView page_categories_tv5;
        public final LinearLayout page_categories_item5;
        public final CircleImageView page_categories_img6;
        public final TextView page_categories_tv6;
        public final LinearLayout page_categories_item6;
        public final CircleImageView page_categories_img7;
        public final TextView page_categories_tv7;
        public final LinearLayout page_categories_item7;
        public final View root;

        public ViewHolder(View root) {
            page_categories_img0 = (CircleImageView) root.findViewById(R.id.page_categories_img0);
            page_categories_tv0 = (TextView) root.findViewById(R.id.page_categories_tv0);
            page_categories_item0 = (LinearLayout) root.findViewById(R.id.page_categories_item0);
            page_categories_img1 = (CircleImageView) root.findViewById(R.id.page_categories_img1);
            page_categories_tv1 = (TextView) root.findViewById(R.id.page_categories_tv1);
            page_categories_item1 = (LinearLayout) root.findViewById(R.id.page_categories_item1);
            page_categories_img2 = (CircleImageView) root.findViewById(R.id.page_categories_img2);
            page_categories_tv2 = (TextView) root.findViewById(R.id.page_categories_tv2);
            page_categories_item2 = (LinearLayout) root.findViewById(R.id.page_categories_item2);
            page_categories_img3 = (CircleImageView) root.findViewById(R.id.page_categories_img3);
            page_categories_tv3 = (TextView) root.findViewById(R.id.page_categories_tv3);
            page_categories_item3 = (LinearLayout) root.findViewById(R.id.page_categories_item3);
            page_categories_img4 = (CircleImageView) root.findViewById(R.id.page_categories_img4);
            page_categories_tv4 = (TextView) root.findViewById(R.id.page_categories_tv4);
            page_categories_item4 = (LinearLayout) root.findViewById(R.id.page_categories_item4);
            page_categories_img5 = (CircleImageView) root.findViewById(R.id.page_categories_img5);
            page_categories_tv5 = (TextView) root.findViewById(R.id.page_categories_tv5);
            page_categories_item5 = (LinearLayout) root.findViewById(R.id.page_categories_item5);
            page_categories_img6 = (CircleImageView) root.findViewById(R.id.page_categories_img6);
            page_categories_tv6 = (TextView) root.findViewById(R.id.page_categories_tv6);
            page_categories_item6 = (LinearLayout) root.findViewById(R.id.page_categories_item6);
            page_categories_img7 = (CircleImageView) root.findViewById(R.id.page_categories_img7);
            page_categories_tv7 = (TextView) root.findViewById(R.id.page_categories_tv7);
            page_categories_item7 = (LinearLayout) root.findViewById(R.id.page_categories_item7);
            this.root = root;
        }
    }

    public interface OnActivitySelectListener {
        void onActivitySelected(MainHeadPagerAdapter adapter, Activity activity);
    }
}
