package com.ljmob.districtactivity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.entity.Item;
import com.ljmob.districtactivity.entity.MessageBox;
import com.ljmob.districtactivity.net.NetConst;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by london on 15/7/22.
 * 我参与的列表
 */
public class JoinedResultAdapter extends LAdapter {
    ImageLoader imageLoader;

    public JoinedResultAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_showcase, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        MessageBox result = (MessageBox) lEntities.get(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.item_showcase_lnBroadcast.setVisibility(View.INVISIBLE);
        holder.item_showcase_tvTitle.setText(result.activity_result.title);
        holder.item_showcase_tvDescription.setText(result.activity_result.description);
        holder.item_showcase_tvUser.setText(result.activity_result.author.name + " "
                + result.activity_result.author.grade_school + " "
                + result.activity_result.author.team_class);
        holder.item_showcase_tvDate.setText(result.activity_result.created_at);
        if (result.message_type == null) {
            holder.item_showcase_tvTag.setVisibility(View.INVISIBLE);
        } else {
            holder.item_showcase_tvTag.setVisibility(View.VISIBLE);
            switch (result.message_type) {
                case "vote":
                    holder.item_showcase_tvTag.setText(R.string.tag_vote);
                    holder.item_showcase_tvTag.setBackgroundResource(R.drawable.shape_circle_check);
                    break;
                case "praise":
                    holder.item_showcase_tvTag.setText(R.string.tag_praise);
                    holder.item_showcase_tvTag.setBackgroundResource(R.drawable.shape_circle_praise);
                    break;
                case "check":
                    holder.item_showcase_tvTag.setText(R.string.tag_check);
                    holder.item_showcase_tvTag.setBackgroundResource(R.drawable.shape_circle_check);
                    break;
                case "comment":
                    holder.item_showcase_tvTag.setText(R.string.tag_reply);
                    holder.item_showcase_tvTag.setBackgroundResource(R.drawable.shape_circle_reply);
                    break;
            }
        }
        if (result.activity_result.items == null) {
            holder.item_showcase_lnPictures.setVisibility(View.GONE);
        } else {
            int previewCount = 0;
            for (Item item : result.activity_result.items) {
                if (item.file_url == null) {
                    continue;
                }
                if (!(item.file_url.endsWith(".jpg") || item.file_url.endsWith(".png"))) {
                    continue;
                }
                switch (previewCount) {
                    case 0:
                        imageLoader.displayImage(NetConst.ROOT_URL + item.file_url, holder.item_showcase_pic0);
                        break;
                    case 1:
                        imageLoader.displayImage(NetConst.ROOT_URL + item.file_url, holder.item_showcase_pic1);
                        break;
                    case 2:
                        imageLoader.displayImage(NetConst.ROOT_URL + item.file_url, holder.item_showcase_pic2);
                        break;
                }
                previewCount++;
                if (previewCount == 3) {
                    break;
                }
            }
            if (previewCount < 3) {
                holder.item_showcase_pic2.setVisibility(View.INVISIBLE);
                if (previewCount < 2) {
                    holder.item_showcase_pic1.setVisibility(View.INVISIBLE);
                    if (previewCount < 1) {
                        holder.item_showcase_lnPictures.setVisibility(View.GONE);
                    }
                }
            }
        }
        return convertView;
    }

    public class ViewHolder {
        //广播
        public final TextView item_showcase_tvPreview;
        public final LinearLayout item_showcase_lnBroadcast;

        //内容
        public final TextView item_showcase_tvTitle;
        public final TextView item_showcase_tvDescription;
        public final ImageView item_showcase_pic0;
        public final ImageView item_showcase_pic1;
        public final ImageView item_showcase_pic2;
        public final LinearLayout item_showcase_lnPictures;
        public final TextView item_showcase_tvUser;
        public final TextView item_showcase_tvDate;
        public final TextView item_showcase_tvTag;
        public final LinearLayout item_showcase_lnTopic;
        public final View root;

        public ViewHolder(View root) {
            item_showcase_tvPreview = (TextView) root.findViewById(R.id.item_showcase_tvPreview);
            item_showcase_lnBroadcast = (LinearLayout) root.findViewById(R.id.item_showcase_lnBroadcast);
            item_showcase_tvTitle = (TextView) root.findViewById(R.id.item_showcase_tvTitle);
            item_showcase_tvDescription = (TextView) root.findViewById(R.id.item_showcase_tvDescription);
            item_showcase_pic0 = (ImageView) root.findViewById(R.id.item_showcase_pic0);
            item_showcase_pic1 = (ImageView) root.findViewById(R.id.item_showcase_pic1);
            item_showcase_pic2 = (ImageView) root.findViewById(R.id.item_showcase_pic2);
            item_showcase_lnPictures = (LinearLayout) root.findViewById(R.id.item_showcase_lnPictures);
            item_showcase_tvUser = (TextView) root.findViewById(R.id.item_showcase_tvUser);
            item_showcase_tvDate = (TextView) root.findViewById(R.id.item_showcase_tvDate);
            item_showcase_tvTag = (TextView) root.findViewById(R.id.item_showcase_tvTag);
            item_showcase_lnTopic = (LinearLayout) root.findViewById(R.id.item_showcase_lnTopic);
            this.root = root;
        }
    }
}
