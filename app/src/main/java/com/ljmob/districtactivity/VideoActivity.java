package com.ljmob.districtactivity;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.londonx.lutil.util.LMediaPlayer;
import com.londonx.lutil.util.ToastUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by london on 15/8/21.
 * 视频播放器
 */
public class VideoActivity extends AppCompatActivity implements
        View.OnClickListener {
    @InjectView(R.id.activity_video_surface)
    SurfaceView activityVideoSurface;
    @InjectView(R.id.activity_video_imgPlay)
    ImageView activityVideoImgPlay;
    @InjectView(R.id.activity_video_sb)
    SeekBar activityVideoSb;
    @InjectView(R.id.activity_video_lnPlayer)
    LinearLayout activityVideoLnPlayer;
    @InjectView(R.id.activity_video_pbBuffering)
    ProgressBar activityVideoPbBuffering;

    LMediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String url = getIntent().getStringExtra("url");
        if (url.length() == 0) {
            ToastUtil.show(R.string.toast_err_video);
            finish();
            return;
        }
        setContentView(R.layout.activity_video);
        ButterKnife.inject(this);

        player = new LMediaPlayer(activityVideoSurface, activityVideoSb);
        player.playUrl(url);

        activityVideoImgPlay.setOnClickListener(this);

        new AsyncTask<String, Boolean, Integer>() {

            @Override
            protected Integer doInBackground(String... params) {
                while (!player.mediaPlayer.isPlaying()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                activityVideoImgPlay.setImageResource(R.mipmap.icon_stop);
                activityVideoPbBuffering.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        if (player.mediaPlayer.isPlaying()) {
            player.pause();
            ((ImageView) v).setImageResource(R.mipmap.icon_start);
        } else {
            if (Build.VERSION.SDK_INT >= 16) {
                player.mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            }
            player.play();
            ((ImageView) v).setImageResource(R.mipmap.icon_stop);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
    }
}
