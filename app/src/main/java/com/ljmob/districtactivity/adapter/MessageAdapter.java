package com.ljmob.districtactivity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.entity.MessageBox;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;

import java.util.List;

/**
 * Created by london on 15/7/22.
 * 消息列表
 */
public class MessageAdapter extends LAdapter {
    public MessageAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        MessageBox messageBox = (MessageBox) lEntities.get(position);
        switch (messageBox.message_type) {
            case "vote":
                holder.item_message_tvAction.setText(R.string.to_your);
                holder.item_message_tvActionDesc.setText(R.string.voted);
                holder.item_message_tvActionDesc.setVisibility(View.VISIBLE);
                holder.item_message_tvTag.setText(R.string.tag_vote);
                holder.item_message_tvPreview.setVisibility(View.GONE);
                holder.item_message_tvTag.setBackgroundResource(R.drawable.shape_rect_check);
                break;
            case "praise":
                holder.item_message_tvAction.setText(R.string.to_your);
                holder.item_message_tvActionDesc.setText(R.string.praised);
                holder.item_message_tvActionDesc.setVisibility(View.VISIBLE);
                holder.item_message_tvTag.setText(R.string.tag_praise);
                holder.item_message_tvPreview.setVisibility(View.GONE);
                holder.item_message_tvTag.setBackgroundResource(R.drawable.shape_rect_praise);
                break;
            case "check":
                holder.item_message_tvAction.setText(R.string.checked);
                holder.item_message_tvTag.setText(R.string.tag_check);
                holder.item_message_tvActionDesc.setVisibility(View.GONE);
                holder.item_message_tvPreview.setVisibility(View.GONE);
                holder.item_message_tvTag.setBackgroundResource(R.drawable.shape_rect_check);
                break;
            case "comment":
                holder.item_message_tvAction.setText(R.string.replied);
                holder.item_message_tvTag.setText(R.string.tag_reply);
                holder.item_message_tvActionDesc.setVisibility(View.GONE);
                holder.item_message_tvPreview.setVisibility(View.VISIBLE);
                holder.item_message_tvTag.setBackgroundResource(R.drawable.shape_rect_reply);
                holder.item_message_tvPreview.setText(messageBox.comment_description);
                break;
        }
        holder.item_message_tvUser.setText(messageBox.sender);
        holder.item_message_tvTitle.setText(messageBox.activity_result.title);
        holder.item_message_tvDate.setText(messageBox.created_at);
        return convertView;
    }

    public class ViewHolder {
        public final TextView item_message_tvTag;
        public final TextView item_message_tvUser;
        public final TextView item_message_tvAction;
        public final TextView item_message_tvTitle;
        public final TextView item_message_tvActionDesc;
        public final TextView item_message_tvPreview;
        public final TextView item_message_tvDate;
        public final View root;

        public ViewHolder(View root) {
            item_message_tvTag = (TextView) root.findViewById(R.id.item_message_tvTag);
            item_message_tvUser = (TextView) root.findViewById(R.id.item_message_tvUser);
            item_message_tvAction = (TextView) root.findViewById(R.id.item_message_tvAction);
            item_message_tvTitle = (TextView) root.findViewById(R.id.item_message_tvTitle);
            item_message_tvActionDesc = (TextView) root.findViewById(R.id.item_message_tvActionDesc);
            item_message_tvPreview = (TextView) root.findViewById(R.id.item_message_tvPreview);
            item_message_tvDate = (TextView) root.findViewById(R.id.item_message_tvDate);
            this.root = root;
        }
    }
}
