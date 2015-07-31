package com.ljmob.districtactivity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.entity.Comment;
import com.ljmob.districtactivity.entity.Item;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.lemoji.LEmoji;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;
import com.londonx.lutil.util.FileUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by london on 15/7/28.
 * 评论
 */
public class CommentAdapter extends LAdapter {
    String floor;
    ImageLoader imageLoader;

    public CommentAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (floor == null) {
            floor = parent.getContext().getString(R.string.floor_);
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comment, parent, false);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        Comment comment = (Comment) lEntities.get(position);
        imageLoader.displayImage(NetConst.ROOT_URL + comment.reviewer_avatar,
                viewHolder.item_comment_imgHead);
        viewHolder.item_comment_tvName.setText(comment.reviewer);
        viewHolder.item_comment_tvFloorDate.setText((comment.floor + 1)
                + floor + " " + comment.created_at);
        viewHolder.item_comment_tvComment.setText(LEmoji.translate(comment.content));
        viewHolder.item_comment_lnAttaches.removeAllViews();
        if (comment.comment_item == null || comment.comment_item.size() == 0) {
            viewHolder.item_comment_lnAttaches.setVisibility(View.GONE);
        } else {
            viewHolder.item_comment_lnAttaches.setVisibility(View.VISIBLE);
            for (Item item : comment.comment_item) {
                ImageView imgAttach = (ImageView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_comment_media, parent, false);
                if (FileUtil.getFileType(item.file_url) == FileUtil.FileType.picture) {
                    imageLoader.displayImage(NetConst.ROOT_URL + item.file_url, imgAttach);
                    viewHolder.item_comment_lnAttaches.addView(imgAttach);
                }
            }
        }
        return convertView;
    }

    public class ViewHolder {
        public final CircleImageView item_comment_imgHead;
        public final TextView item_comment_tvName;
        public final TextView item_comment_tvFloorDate;
        public final TextView item_comment_tvComment;
        public final LinearLayout item_comment_lnAttaches;
        public final View root;

        public ViewHolder(View root) {
            item_comment_imgHead = (CircleImageView) root.findViewById(R.id.item_comment_imgHead);
            item_comment_tvName = (TextView) root.findViewById(R.id.item_comment_tvName);
            item_comment_tvFloorDate = (TextView) root.findViewById(R.id.item_comment_tvFloorDate);
            item_comment_tvComment = (TextView) root.findViewById(R.id.item_comment_tvComment);
            item_comment_lnAttaches = (LinearLayout) root.findViewById(R.id.item_comment_lnAttaches);
            this.root = root;
        }
    }
}
