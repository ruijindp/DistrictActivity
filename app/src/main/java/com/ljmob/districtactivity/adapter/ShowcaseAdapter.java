package com.ljmob.districtactivity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.entity.Item;
import com.ljmob.districtactivity.entity.Result;
import com.ljmob.districtactivity.net.NetConst;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by london on 15/7/20.
 * 作品展示
 */
public class ShowcaseAdapter extends LAdapter {
    ImageLoader imageLoader;

    public ShowcaseAdapter(List<? extends LEntity> lEntities) {
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
        Result result = (Result) lEntities.get(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.item_showcase_tvTitle.setText(result.title);
        holder.item_showcase_tvDescription.setText(result.description);
        holder.item_showcase_tvUser.setText(result.author.name + " "
                + result.author.grade_school + " "
                + result.author.team_class);
        holder.item_showcase_tvDate.setText(result.created_at);
        if (result.items == null) {
            holder.item_showcase_lnPictures.setVisibility(View.GONE);
        } else {
            int previewCount = 0;
            for (Item item : result.items) {
                if (item.file_url == null) {
                    continue;
                }
                if (item.file_url.endsWith(".jpg") || item.file_url.endsWith(".png")) {
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
                }

                if (previewCount == 3) {
                    break;
                }
            }
            if (previewCount > 0) {
                holder.item_showcase_lnPictures.setVisibility(View.VISIBLE);
                holder.item_showcase_pic0.setVisibility(View.VISIBLE);
                if (previewCount > 1) {
                    holder.item_showcase_pic1.setVisibility(View.VISIBLE);
                    if (previewCount > 2) {
                        holder.item_showcase_pic2.setVisibility(View.VISIBLE);
                    } else {
                        holder.item_showcase_pic2.setVisibility(View.INVISIBLE);
                    }
                } else {
                    holder.item_showcase_pic1.setVisibility(View.INVISIBLE);
                    holder.item_showcase_pic2.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.item_showcase_lnPictures.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    public class ViewHolder {
        //内容
        public final TextView item_showcase_tvTitle;
        public final TextView item_showcase_tvDescription;
        public final ImageView item_showcase_pic0;
        public final ImageView item_showcase_pic1;
        public final ImageView item_showcase_pic2;
        public final LinearLayout item_showcase_lnPictures;
        public final TextView item_showcase_tvUser;
        public final TextView item_showcase_tvDate;
        public final LinearLayout item_showcase_lnTopic;
        public final View root;

        public ViewHolder(View root) {
            item_showcase_tvTitle = (TextView) root.findViewById(R.id.item_showcase_tvTitle);
            item_showcase_tvDescription = (TextView) root.findViewById(R.id.item_showcase_tvDescription);
            item_showcase_pic0 = (ImageView) root.findViewById(R.id.item_showcase_pic0);
            item_showcase_pic1 = (ImageView) root.findViewById(R.id.item_showcase_pic1);
            item_showcase_pic2 = (ImageView) root.findViewById(R.id.item_showcase_pic2);
            item_showcase_lnPictures = (LinearLayout) root.findViewById(R.id.item_showcase_lnPictures);
            item_showcase_tvUser = (TextView) root.findViewById(R.id.item_showcase_tvUser);
            item_showcase_tvDate = (TextView) root.findViewById(R.id.item_showcase_tvDate);
            item_showcase_lnTopic = (LinearLayout) root.findViewById(R.id.item_showcase_lnTopic);
            this.root = root;
        }
    }
}
