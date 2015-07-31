package com.ljmob.districtactivity.adapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.entity.Item;
import com.ljmob.districtactivity.net.NetConst;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;
import com.londonx.lutil.util.FileUtil;
import com.londonx.lutil.util.LMediaPlayer;
import com.londonx.lutil.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;

/**
 * Created by london on 15/7/23.
 * 作品详情
 */
public class DetailAdapter extends LAdapter {
    ImageLoader imageLoader;
    HashMap<Integer, LMediaPlayer> players;

    public DetailAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
        players = new HashMap<>();
        imageLoader = ImageLoader.getInstance();
        if (lEntities.size() == 0) {
            return;
        }
        lEntities.remove(0);
        this.lEntities = lEntities;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_media2, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        Item item = (Item) lEntities.get(position);
        holder.hideAllViews();
        if (item.file_url == null && item.basic_video_url == null) {
            return convertView;
        }

        if (item.basic_video_url != null) {
            holder.item_media2_lnVideo.setVisibility(View.VISIBLE);
            holder.item_media2_tvVideoUrl.setText(item.basic_video_url);
            holder.item_media2_tvVideoUrl.setOnClickListener(new UrlClickListener(item.basic_video_url));
        } else {
            switch (FileUtil.getFileType(item.file_url)) {
                case music:
                    holder.item_media2_linearPlayer.setVisibility(View.VISIBLE);
                    LMediaPlayer lMediaPlayer = players.get(position);
                    if (lMediaPlayer == null) {
                        lMediaPlayer = new LMediaPlayer(null, holder.item_media2_sb);
                        lMediaPlayer.playUrl(NetConst.ROOT_URL + item.file_url);
                        players.put(position, lMediaPlayer);
                        holder.item_media2_imgPlay.setOnClickListener(new PlayClickListener(lMediaPlayer));
                    }
                    holder.item_media2_imgPlay.setImageResource(lMediaPlayer.mediaPlayer.isPlaying()
                            ? R.mipmap.icon_stop : R.mipmap.icon_start);
                    break;
                case video:
                    break;
                case picture:
                    holder.item_media2_imgContent.setVisibility(View.VISIBLE);
                    holder.item_media2_imgContent.setAdjustViewBounds(true);
                    imageLoader.displayImage(NetConst.ROOT_URL + item.file_url, holder.item_media2_imgContent);
                    break;
                case web:
                    break;
                case unknown:
                    break;
            }
        }
        return convertView;
    }

    public void stopAllMusic() {
        for (int i = 0; i < lEntities.size(); i++) {
            LMediaPlayer player = players.get(i);
            if (player == null) {
                continue;
            }
            player.stop();
        }
    }

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
                v.performClick();
            }
        }
    }

    public class ViewHolder {
        public final ImageView item_media2_imgContent;
        public final ImageView item_media2_imgPlay;
        public final SeekBar item_media2_sb;
        public final LinearLayout item_media2_linearPlayer;
        public final LinearLayout item_media2_lnVideo;
        public final TextView item_media2_tvVideoUrl;

        public final View root;

        public ViewHolder(View root) {
            item_media2_imgContent = (ImageView) root.findViewById(R.id.item_media2_imgContent);
            item_media2_imgPlay = (ImageView) root.findViewById(R.id.item_media2_imgPlay);
            item_media2_sb = (SeekBar) root.findViewById(R.id.item_media2_sb);
            item_media2_linearPlayer = (LinearLayout) root.findViewById(R.id.item_media2_linearPlayer);
            item_media2_lnVideo = (LinearLayout) root.findViewById(R.id.item_media2_lnVideo);
            item_media2_tvVideoUrl = (TextView) root.findViewById(R.id.item_media2_tvVideoUrl);
            this.root = root;
        }

        public void hideAllViews() {
            item_media2_imgContent.setVisibility(View.GONE);
            item_media2_linearPlayer.setVisibility(View.GONE);
            item_media2_lnVideo.setVisibility(View.GONE);
        }
    }

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
}
