package com.ljmob.districtactivity.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ljmob.districtactivity.R;
import com.ljmob.districtactivity.util.Recorder;
import com.londonx.lutil.util.LMediaPlayer;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * Created by london on 15/7/10.
 * 多媒体
 */
public class MediaView extends ViewGroup implements View.OnClickListener, Recorder.RecorderListener {
    SurfaceView view_media_surface;
    ImageView view_media_imgPlay;
    SeekBar view_media_sb;
    LinearLayout view_media_linearPlayer;
    View view_media_btnRecord;
    ImageView view_media_imgContent;
    TextView view_media_tvInfo;

    LMediaPlayer lMediaPlayer;
    Recorder recorder;
    String fileUrl;
    ImageLoader imageLoader;
    File file;

    public MediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View rootView = inflate(getContext(), R.layout.view_media, this);
        initView(rootView);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    private void initView(View root) {
        view_media_surface = (SurfaceView) root.findViewById(R.id.view_media_surface);
        view_media_imgPlay = (ImageView) root.findViewById(R.id.view_media_imgPlay);
        view_media_sb = (SeekBar) root.findViewById(R.id.view_media_sb);
        view_media_linearPlayer = (LinearLayout) root.findViewById(R.id.view_media_linearPlayer);
        view_media_btnRecord = root.findViewById(R.id.view_media_btnRecord);
        view_media_imgContent = (ImageView) root.findViewById(R.id.view_media_imgContent);
        view_media_tvInfo = (TextView) findViewById(R.id.view_media_tvInfo);

        view_media_imgPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_media_imgPlay:
                if (lMediaPlayer.mediaPlayer.getVideoHeight() == 0) {
                    lMediaPlayer.playUrl(fileUrl);
                    view_media_imgPlay.setImageResource(R.mipmap.icon_stop);
                    return;
                }
                if (lMediaPlayer.mediaPlayer.isPlaying()) {
                    lMediaPlayer.pause();
                    view_media_imgPlay.setImageResource(R.mipmap.icon_start);
                } else {
                    lMediaPlayer.play();
                    view_media_imgPlay.setImageResource(R.mipmap.icon_stop);
                }
                break;
        }
    }

    private void setStatus(Status status) {
//        hideAllViews();

        switch (status) {
            case nothing:
                break;
            case image:
//                if (imageLoader == null) {
//                    imageLoader = ImageLoader.getInstance();
//                }
//                String uri = fileUrl.startsWith("/") ? "file://" + fileUrl : fileUrl;
//                imageLoader.displayImage(uri, view_media_imgContent);
                view_media_imgContent.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                view_media_imgContent.setVisibility(View.VISIBLE);
                view_media_imgContent.setAdjustViewBounds(true);
                break;
            case audioRecorder:
                recorder = new Recorder(null, this);
                view_media_btnRecord.setVisibility(View.VISIBLE);
                break;
            case audioPlayer:
                lMediaPlayer = new LMediaPlayer(null, view_media_sb);
                view_media_linearPlayer.setVisibility(View.VISIBLE);
                break;
            case videoRecorder:
                recorder = new Recorder(view_media_surface, this);
                view_media_surface.setVisibility(View.VISIBLE);
                view_media_linearPlayer.setVisibility(View.VISIBLE);
                break;
            case videoPlayer:
                lMediaPlayer = new LMediaPlayer(view_media_surface, view_media_sb);
                view_media_surface.setVisibility(View.VISIBLE);
                view_media_btnRecord.setVisibility(View.VISIBLE);
                break;
        }
        view_media_tvInfo.setText(fileUrl);
    }

    private void hideAllViews() {
        view_media_surface.setVisibility(View.GONE);
        view_media_linearPlayer.setVisibility(View.GONE);
        view_media_btnRecord.setVisibility(View.GONE);
        view_media_imgContent.setVisibility(View.GONE);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        setFileUrl(file.getAbsolutePath());
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        String exName = fileUrl.substring(fileUrl.lastIndexOf("."));
        if (exName.equalsIgnoreCase(".amr") || exName.equalsIgnoreCase(".mp3")) {
            setStatus(Status.audioPlayer);
        } else if (exName.equalsIgnoreCase(".3gp") || exName.equalsIgnoreCase(".mp4")) {
            setStatus(Status.videoPlayer);
        } else if (exName.equalsIgnoreCase(".jpg") || exName.equalsIgnoreCase(".png")) {
            setStatus(Status.image);
        }
    }

    @Override
    public void onRecordingComplete(Recorder recorder, File recordFile) {

    }

    private enum Status {
        nothing,
        image,
        audioRecorder,
        audioPlayer,
        videoRecorder,
        videoPlayer,
    }
}
