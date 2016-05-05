package com.ljmob.districtactivity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.entity.CheckScores;
import com.ljmob.districtactivity.entity.Item;
import com.ljmob.districtactivity.entity.Result;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.MyApplication;
import com.ljmob.lemoji.LEmoji;
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
    public boolean showCheckStatus = false;

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

        if (showCheckStatus) {
            holder.item_showcase_tvChecked.setVisibility(View.INVISIBLE);
            holder.item_showcase_tvChecking.setVisibility(View.INVISIBLE);
            holder.item_showcase_tvUnChecked.setVisibility(View.INVISIBLE);

            holder.item_showcase_lnExpertScore.setVisibility(View.INVISIBLE);

            for (int i = 0; i < result.check_scores.size(); i++){
                switch (result.check_scores.get(i).score.score) {
                    case 0:
                        if (MyApplication.currentUser.roles.equals("expert")) {
                            holder.item_showcase_lnExpertScore.setVisibility(View.VISIBLE);
                            holder.item_showcase_tvExpert1Score.setText("待审核");
                            holder.item_showcase_tvExpert2Score.setText("待审核");
                            holder.item_showcase_tvExpert1Fen.setVisibility(View.GONE);
                            holder.item_showcase_tvExpert2Fen.setVisibility(View.GONE);
                        }
                        break;
                }
                List<CheckScores> list = result.check_scores;
                holder.item_showcase_tvExpert1.setText(list.get(0).checker);
                holder.item_showcase_tvExpert2.setText(list.get(1).checker);

                if (list.get(0).score.score > 0){
                    holder.item_showcase_tvExpert1Score.setText(list.get(0).score.score+"");
                    holder.item_showcase_tvExpert1Fen.setVisibility(View.VISIBLE);
                } else {
                    holder.item_showcase_tvExpert1Score.setText("待审核");
                    holder.item_showcase_tvExpert1Fen.setVisibility(View.GONE);
                }
                if (list.get(1).score.score > 0){
                    holder.item_showcase_tvExpert2Score.setText(list.get(1).score.score+"");
                    holder.item_showcase_tvExpert2Fen.setVisibility(View.VISIBLE);
                }else {
                    holder.item_showcase_tvExpert2Score.setText("待审核");
                    holder.item_showcase_tvExpert2Fen.setVisibility(View.GONE);
                }
            }



            switch (result.is_check) {
                case "checking":
                    if (MyApplication.currentUser.roles.equals("teacher") || MyApplication.currentUser.roles.equals("grade")
                            || MyApplication.currentUser.roles.equals("general")) {
                        holder.item_showcase_tvChecking.setVisibility(View.VISIBLE);
                    }
                    break;
                case "true":
                    if (MyApplication.currentUser.roles.equals("teacher") || MyApplication.currentUser.roles.equals("grade")
                            || MyApplication.currentUser.roles.equals("general")) {
                        holder.item_showcase_tvChecked.setVisibility(View.VISIBLE);
                    }
                    break;
                case "false":
                    if (MyApplication.currentUser.roles.equals("teacher") || MyApplication.currentUser.roles.equals("grade")
                            || MyApplication.currentUser.roles.equals("general")) {
                        holder.item_showcase_tvUnChecked.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        } else {
            holder.item_showcase_tvChecked.setVisibility(View.INVISIBLE);
            holder.item_showcase_tvChecking.setVisibility(View.INVISIBLE);
            holder.item_showcase_lnExpertScore.setVisibility(View.INVISIBLE);
        }

        if (result.description.length() == 0) {
            holder.item_showcase_tvDescription.setVisibility(View.GONE);
        } else {
            holder.item_showcase_tvDescription.setVisibility(View.VISIBLE);
            String oneFormat = result.description.replace("&nbsp;", " ");
            String twoFormat = oneFormat.replace("&n", "");
            holder.item_showcase_tvDescription.setText(LEmoji.simplify(twoFormat));
        }

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
                if (item.file_url.endsWith(".jpg") || item.file_url.endsWith(".png") || item.file_url.endsWith(".JPG") || item.file_url.endsWith(".PENG")) {
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
        public final View item_showcase_tvChecked;
        public final View item_showcase_tvChecking;
        public final View item_showcase_tvUnChecked;
        public final View root;
        public final LinearLayout item_showcase_lnExpertScore;
        public final TextView item_showcase_tvExpert1Score;
        public final TextView item_showcase_tvExpert2Score;
        public final TextView item_showcase_tvExpert1Fen;
        public final TextView item_showcase_tvExpert2Fen;
        public final TextView item_showcase_tvExpert1;
        public final TextView item_showcase_tvExpert2;

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
            item_showcase_tvChecked = root.findViewById(R.id.item_showcase_tvChecked);
            item_showcase_tvChecking = root.findViewById(R.id.item_showcase_tvChecking);
            item_showcase_tvUnChecked = root.findViewById(R.id.item_showcase_tvUnchecked);
            item_showcase_lnExpertScore = (LinearLayout) root.findViewById(R.id.item_showcase_lnExpertScore);
            item_showcase_tvExpert1Score = (TextView) root.findViewById(R.id.item_showcase_tvExpert1Score);
            item_showcase_tvExpert2Score = (TextView) root.findViewById(R.id.item_showcase_tvExpert2Score);
            item_showcase_tvExpert1Fen = (TextView) root.findViewById(R.id.item_showcase_tvExpert1Fen);
            item_showcase_tvExpert2Fen = (TextView) root.findViewById(R.id.item_showcase_tvExpert2Fen);
            item_showcase_tvExpert1 = (TextView) root.findViewById(R.id.item_showcase_tvExpert1);
            item_showcase_tvExpert2 = (TextView) root.findViewById(R.id.item_showcase_tvExpert2);
            this.root = root;
        }

    }
}
