package com.ljmob.districtactivity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.entity.Notice;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by london on 15/8/4.
 * 公告列表
 */
public class NoticeAdapter extends LAdapter {
    public NoticeAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_broadcast_list, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        Notice notice = (Notice) lEntities.get(position);
        holder.itemBroadcastListTvTitle.setText(notice.title);
        holder.itemBroadcastListTvTime.setText(notice.created_at);
        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_broadcast_list.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.item_broadcast_list_tvTitle)
        TextView itemBroadcastListTvTitle;
        @InjectView(R.id.item_broadcast_list_tvTime)
        TextView itemBroadcastListTvTime;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
