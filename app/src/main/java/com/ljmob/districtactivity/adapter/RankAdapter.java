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
import com.ljmob.lemoji.LEmoji;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;
import com.londonx.lutil.util.FileUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by london on 15/7/31.
 * 排行榜
 */
public class RankAdapter extends LAdapter {
    ImageLoader imageLoader;

    public RankAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_rank, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        Result result = (Result) lEntities.get(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.itemRankTvRank.setText(position + "");
        holder.itemRankTvVote.setText(result.vote_count + "");
        holder.itemRankTvTitle.setText(result.title + "");
        holder.itemRankTvName.setText(result.author.name + "");
        if (result.items == null || result.items.size() == 0) {//没有附件时
            holder.itemRankLnContent.setVisibility(View.GONE);
            setContentPreview(result, holder);
        } else {
            holder.itemRankLnContent.setVisibility(View.VISIBLE);
            holder.itemRankTvContent.setVisibility(View.GONE);
            int previewCount = 0;
            for (Item item : result.items) {
                if (item.file_url == null) {
                    continue;
                }
                if (FileUtil.getFileType(item.file_url) == FileUtil.FileType.picture) {
                    switch (previewCount) {
                        case 0:
                            imageLoader.displayImage(NetConst.ROOT_URL + item.file_url, holder.itemRankImgPic0);
                            break;
                        case 1:
                            imageLoader.displayImage(NetConst.ROOT_URL + item.file_url, holder.itemRankImgPic1);
                            break;
                        case 2:
                            imageLoader.displayImage(NetConst.ROOT_URL + item.file_url, holder.itemRankImgPic2);
                            break;
                    }
                    previewCount++;
                }

                if (previewCount == 3) {
                    break;
                }
            }
            if (previewCount > 0) {
                holder.itemRankLnContent.setVisibility(View.VISIBLE);
                holder.itemRankImgPic0.setVisibility(View.VISIBLE);
                if (previewCount > 1) {
                    holder.itemRankImgPic1.setVisibility(View.VISIBLE);
                    if (previewCount > 2) {
                        holder.itemRankImgPic2.setVisibility(View.VISIBLE);
                    } else {
                        holder.itemRankImgPic2.setVisibility(View.INVISIBLE);
                    }
                } else {
                    holder.itemRankImgPic1.setVisibility(View.INVISIBLE);
                    holder.itemRankImgPic2.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.itemRankLnContent.setVisibility(View.GONE);
                setContentPreview(result, holder);
            }
        }
        holder.itemRankTvCategory.setText(result.activity);
        holder.itemRankTvInfo.setText(result.author.grade_school + " " + result.author.team_class);
        return convertView;
    }

    private void setContentPreview(Result result, ViewHolder holder) {
        if (result.description.length() == 0) {
            holder.itemRankTvContent.setVisibility(View.GONE);
        } else {
            holder.itemRankTvContent.setVisibility(View.VISIBLE);
            holder.itemRankTvContent.setText(LEmoji.simplify(result.description));
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_rank.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.item_rank_tvRank)
        TextView itemRankTvRank;
        @InjectView(R.id.item_rank_tvVote)
        TextView itemRankTvVote;
        @InjectView(R.id.item_rank_tvTitle)
        TextView itemRankTvTitle;
        @InjectView(R.id.item_rank_tvName)
        TextView itemRankTvName;
        @InjectView(R.id.item_rank_tvContent)
        TextView itemRankTvContent;
        @InjectView(R.id.item_rank_lnContent)
        LinearLayout itemRankLnContent;
        @InjectView(R.id.item_rank_imgPic0)
        ImageView itemRankImgPic0;
        @InjectView(R.id.item_rank_imgPic1)
        ImageView itemRankImgPic1;
        @InjectView(R.id.item_rank_imgPic2)
        ImageView itemRankImgPic2;
        @InjectView(R.id.item_rank_tvCategory)
        TextView itemRankTvCategory;
        @InjectView(R.id.item_rank_tvInfo)
        TextView itemRankTvInfo;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
