package com.ljmob.districtactivity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ljmob.districtactivity.R;
import com.ljmob.lemoji.entity.Emoji;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;

import java.util.List;

/**
 * Created by london on 15/7/30.
 * 表情列表
 */
public class EmojiAdapter extends LAdapter {
    public EmojiAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_emoji, parent, false);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        Emoji emoji = (Emoji) lEntities.get(position);
        holder.item_emoji_img.setImageBitmap(emoji.picture);
        return convertView;
    }

    public class ViewHolder {
        public final ImageView item_emoji_img;
        public final FrameLayout item_emoji_root;
        public final View root;

        public ViewHolder(View root) {
            item_emoji_img = (ImageView) root.findViewById(R.id.item_emoji_img);
            item_emoji_root = (FrameLayout) root.findViewById(R.id.item_emoji_root);
            this.root = root;
        }
    }
}
