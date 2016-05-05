package com.ljmob.districtactivity.adapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ljmob.districtactivity.MyUploadActivity;
import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.VideoActivity;
import com.ljmob.districtactivity.entity.FloorItem;
import com.ljmob.districtactivity.net.NetConst;
import com.ljmob.districtactivity.util.DefaultParams;
import com.ljmob.lemoji.LEmoji;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.FileUtil;
import com.londonx.lutil.util.LMediaPlayer;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by london on 15/8/3.
 * 帖子楼层和评论
 */
public class FloorItemAdapter extends LAdapter implements LRequestTool.OnResponseListener {
    private static final int API_PRAISE = 1;

    HashMap<Integer, LMediaPlayer> players;
    HashMap<Integer, PlayClickListener> clickListeners;
    LRequestTool lRequestTool;
    LoginRequestListener loginRequestListener;
    ImageLoader imageLoader;
    String floor;

    TextView tvPraise;
    int praiseCount = 0;//点赞数量
    boolean isPraised;

    public FloorItemAdapter(List<? extends LEntity> lEntities, LoginRequestListener loginRequestListener) {
        super(lEntities);
        this.loginRequestListener = loginRequestListener;
        this.lRequestTool = new LRequestTool(this);
        imageLoader = ImageLoader.getInstance();
        players = new HashMap<>();
        clickListeners = new HashMap<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (floor == null) {
            floor = parent.getContext().getString(R.string.floor_);
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_floor, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        FloorItem floorItem = (FloorItem) lEntities.get(position);
        holder.resetViews();
        switch (floorItem.itemType) {
            case userInfo:
                if (position == 0) {//只有第一行才显示标题
                    holder.itemFloorTvTitle.setVisibility(View.VISIBLE);
                    holder.itemFloorTvTitle.setText(floorItem.title);
                } else {//除了第一行，其他用户前都有分割线
                    holder.itemFloorDiv.setVisibility(View.VISIBLE);
                }
                holder.itemFloorLnUserInfo.setVisibility(View.VISIBLE);
                imageLoader.displayImage(NetConst.ROOT_URL + floorItem.author.avatar, holder.itemFloorImgHead);
                holder.itemFloorTvAuthor.setText(floorItem.author.name);
                holder.itemFloorTvFloorDate.setText(floorItem.floor + floor + " " + floorItem.created_at);
                break;
            case normal:
                if (floorItem.item.content != null && floorItem.item.content.length() != 0) {//文本
                    holder.itemFloorTvTextContent.setVisibility(View.VISIBLE);
                    String one = floorItem.item.content.replace("&nbsp;", " ");
                    String two = one.replace("<div>", "");
                    String three = two.replace("</div>", "");
                    String four = three.replace("<br>", "");
//                    holder.itemFloorTvTextContent.setText(LEmoji.
//                            translate(floorItem.item.content.replace("&nbsp;", " ")));
                    holder.itemFloorTvTextContent.setText(LEmoji.
                            translate(four));
                    break;
                }
                if (floorItem.item.file_url == null && floorItem.item.basic_video_url == null) {//既没有文本也没有文件
                    break;
                }
                if (floorItem.item.basic_video_url != null) {//视频
                    holder.itemFloorLnVideo.setVisibility(View.VISIBLE);
                    holder.itemFloorTvVideoUrl.setText(floorItem.item.basic_video_url);
                    holder.itemFloorTvVideoUrl.setOnClickListener(
                            new UrlClickListener(floorItem.item.basic_video_url));
                    break;
                }
                if (FileUtil.getFileType(floorItem.item.file_url) == FileUtil.FileType.picture) {//图片
                    holder.itemFloorImgContent.setVisibility(View.VISIBLE);
//                  setAdjustViewBounds当你需要在ImageView调整边框时保持可绘制对象的比例时，将该值设为真。
                    holder.itemFloorImgContent.setAdjustViewBounds(true);
                    imageLoader.displayImage(NetConst.ROOT_URL + floorItem.item.file_url, holder.itemFloorImgContent);
                    break;
                }
                if (FileUtil.getFileType(floorItem.item.file_url) == FileUtil.FileType.music) {//音乐
                    holder.itemFloorLinearPlayer.setVisibility(View.VISIBLE);
                    LMediaPlayer lMediaPlayer = players.get(position);
                    if (lMediaPlayer == null) {
                        lMediaPlayer = new LMediaPlayer(null, holder.itemFloorSb);
                        lMediaPlayer.playUrl(NetConst.ROOT_URL + floorItem.item.file_url);
                        players.put(position, lMediaPlayer);
                    }
                    PlayClickListener listener = clickListeners.get(position);
                    if (listener == null) {
                        listener = new PlayClickListener(lMediaPlayer);
                        clickListeners.put(position, listener);
                    }
                    lMediaPlayer.setSeekBar(holder.itemFloorSb);
                    holder.itemFloorImgPlay.setOnClickListener(listener);
                    holder.itemFloorImgPlay.setImageResource(lMediaPlayer.mediaPlayer.isPlaying()
                            ? R.mipmap.icon_stop : R.mipmap.icon_start);
                }
                if (FileUtil.getFileType(floorItem.item.file_url) == FileUtil.FileType.video) {//视频文件
                    holder.itemFloorFlVideo.setVisibility(View.VISIBLE);
                    holder.footDetailTvVideoName.setText(floorItem.item.file_name);
//                    imageLoader.displayImage(NetConst.ROOT_URL + floorItem.item.file_url,
//                            holder.itemFloorImgVideoPreview);
                    holder.itemFloorFlVideo.setOnClickListener(new PreviewClickListener(floorItem.item.file_url));
                }

                break;
            case options:
                holder.footDetailLnPraise.setVisibility(View.VISIBLE);
                holder.footDetailLnPraise.setOnClickListener(
                        new PraiseClickListener(floorItem, holder.footDetailTvPraise));
                if (praiseCount == 0) {
                    praiseCount = floorItem.praiseCount;
                }
                holder.footDetailTvPraise.setText(praiseCount + "");
                break;
        }

        return convertView;
    }

    @Override
    public void onResponse(LResponse response) {
        if (response.responseCode != 200) {
            praiseCount--;
            isPraised = false;
            tvPraise.setText(praiseCount + "");
            if (response.responseCode == 401) {
                loginRequestListener.requestLogin(this);
                return;
            }
            ToastUtil.serverErr(response.responseCode);
        }
        switch (response.requestCode) {
            case API_PRAISE:
                MyUploadActivity.isResultChanged = true;
                ToastUtil.show(R.string.praise_add);
                break;
        }
    }

    public void stopAllMusics() {
        for (int key : players.keySet()) {
            players.get(key).stop();
        }
    }

    //音乐点击事件
    private class PlayClickListener implements View.OnClickListener, MediaPlayer.OnPreparedListener {
        LMediaPlayer lMediaPlayer;
        boolean isPrepared = false;
        boolean playWhenReady = false;
        View v;

        public PlayClickListener(LMediaPlayer lMediaPlayer) {
            this.lMediaPlayer = lMediaPlayer;
            lMediaPlayer.mediaPlayer.setOnPreparedListener(this);
        }

        @Override
        public void onClick(View v) {
            this.v = v;
            if (!isPrepared) {
                playWhenReady = !playWhenReady;
                ToastUtil.show(R.string.buffering);
                return;
            }
            if (lMediaPlayer.mediaPlayer.isPlaying()) {
                lMediaPlayer.pause();
                ((ImageView) v).setImageResource(R.mipmap.icon_start);
            } else {
                lMediaPlayer.play();
                ((ImageView) v).setImageResource(R.mipmap.icon_stop);
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            isPrepared = true;
            if (playWhenReady) {
                //performClickm模拟点击事件
                v.performClick();
            }
        }
    }

    //视频点击事件
    private class PreviewClickListener implements View.OnClickListener {
        String url;

        public PreviewClickListener(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View v) {
            Intent videoIntent = new Intent(v.getContext(), VideoActivity.class);
            videoIntent.putExtra("url", NetConst.ROOT_URL + url);
            v.getContext().startActivity(videoIntent);
        }
    }

    //链接点击事件
    private class UrlClickListener implements View.OnClickListener {
        String url;

        public UrlClickListener(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View v) {
            v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    //点赞点击事件
    private class PraiseClickListener implements View.OnClickListener {
        FloorItem floorItem;

        public PraiseClickListener(FloorItem floorItem, TextView tvPraiseCount) {
            this.floorItem = floorItem;
            isPraised = floorItem.isPraised;
            tvPraise = tvPraiseCount;
        }

        @Override
        public void onClick(View v) {
            if (isPraised) {
                ToastUtil.show(R.string.had_praised);
                return;
            }
            praiseCount++;
            isPraised = true;
            tvPraise.setText(praiseCount + "");
            HashMap<String, Object> praiseParams = new DefaultParams();
            praiseParams.put("activity_result_id", floorItem.resultId);
            lRequestTool.doPost(NetConst.API_PRAISE, praiseParams, API_PRAISE);
        }
    }

    public interface LoginRequestListener {
        void requestLogin(FloorItemAdapter floorItemAdapter);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_floor.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.item_floor_div)
        View itemFloorDiv;
        @InjectView(R.id.item_floor_tvTitle)
        TextView itemFloorTvTitle;
        @InjectView(R.id.item_floor_imgHead)
        CircleImageView itemFloorImgHead;
        @InjectView(R.id.item_floor_tvAuthor)
        TextView itemFloorTvAuthor;
        @InjectView(R.id.item_floor_tvFloorDate)
        TextView itemFloorTvFloorDate;
        @InjectView(R.id.item_floor_lnUserInfo)
        LinearLayout itemFloorLnUserInfo;
        @InjectView(R.id.item_floor_tvTextContent)
        TextView itemFloorTvTextContent;
        @InjectView(R.id.item_floor_imgContent)
        ImageView itemFloorImgContent;
        @InjectView(R.id.item_floor_imgPlay)
        ImageView itemFloorImgPlay;
        @InjectView(R.id.item_floor_sb)
        SeekBar itemFloorSb;
        @InjectView(R.id.item_floor_linearPlayer)
        LinearLayout itemFloorLinearPlayer;
        @InjectView(R.id.item_floor_flVideo)
        FrameLayout itemFloorFlVideo;
        @InjectView(R.id.item_floor_imgVideoPreview)
        ImageView itemFloorImgVideoPreview;
        @InjectView(R.id.item_floor_tvVideoUrl)
        TextView itemFloorTvVideoUrl;
        @InjectView(R.id.item_floor_lnVideo)
        LinearLayout itemFloorLnVideo;
        @InjectView(R.id.foot_detail_tvPraise)
        TextView footDetailTvPraise;
        @InjectView(R.id.foot_detail_lnPraise)
        LinearLayout footDetailLnPraise;
        @InjectView(R.id.item_floor_tvVideoName)
        TextView footDetailTvVideoName;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        public void resetViews() {
            if (itemFloorDiv.getVisibility() != View.GONE) {
                itemFloorDiv.setVisibility(View.GONE);
            }
            if (itemFloorTvTitle.getVisibility() != View.GONE) {
                itemFloorTvTitle.setVisibility(View.GONE);
            }
            if (itemFloorLnUserInfo.getVisibility() != View.GONE) {
                itemFloorLnUserInfo.setVisibility(View.GONE);
            }
            if (itemFloorTvTextContent.getVisibility() != View.GONE) {
                itemFloorTvTextContent.setVisibility(View.GONE);
            }
            if (itemFloorImgContent.getVisibility() != View.GONE) {
                itemFloorImgContent.setVisibility(View.GONE);
            }
            if (itemFloorLinearPlayer.getVisibility() != View.GONE) {
                itemFloorLinearPlayer.setVisibility(View.GONE);
            }
            if (itemFloorLnVideo.getVisibility() != View.GONE) {
                itemFloorLnVideo.setVisibility(View.GONE);
            }
            if (footDetailLnPraise.getVisibility() != View.GONE) {
                footDetailLnPraise.setVisibility(View.GONE);
            }
            if (itemFloorFlVideo.getVisibility() != View.GONE) {
                itemFloorFlVideo.setVisibility(View.GONE);
            }
        }
    }
}
