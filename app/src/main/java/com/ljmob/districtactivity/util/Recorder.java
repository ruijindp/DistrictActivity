package com.ljmob.districtactivity.util;

import android.media.MediaRecorder;
import android.view.SurfaceView;

import com.ljmob.districtactivity.R;
import com.londonx.lutil.util.ToastUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by london on 15/7/10.
 * 录音机
 */
public class Recorder {
    MediaRecorder mRecorder;
    File outputFile;
    RecorderListener recorderListener;
    boolean isPrepared = false;

    public Recorder(SurfaceView surfaceView, RecorderListener recorderListener) {
        this.recorderListener = recorderListener;
        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        if (surfaceView != null) {
            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        }

        // Set output file format
        if (surfaceView != null) {
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        } else {
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        }

        // 这两项需要放在setOutputFormat之后
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        if (surfaceView != null) {
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
            int vw = surfaceView.getContext().getResources().getDimensionPixelSize(R.dimen.video_w);
            int vh = surfaceView.getContext().getResources().getDimensionPixelSize(R.dimen.video_h);
            mRecorder.setVideoSize(vw, vh);
            mRecorder.setVideoFrameRate(20);
        }
        if (surfaceView != null) {
            mRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
        }

        // Set output file path
        outputFile = RecorderFileUtil.getDefaultRecordFile(surfaceView != null);
        mRecorder.setOutputFile(outputFile.getAbsolutePath());
        try {
            mRecorder.prepare();
            isPrepared = true;
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtil.show(R.string.media_err);
        }
    }

    public void startRecording() {
        if (!isPrepared) {
            ToastUtil.show(R.string.media_err);
            stopRecording();
            return;
        }
        mRecorder.start();// Recording is now started
    }

    public void stopRecording() {
        isPrepared = false;
        mRecorder.stop();
        mRecorder.reset();   // You can reuse the object by going back to setAudioSource() step
        if (recorderListener != null) {
            recorderListener.onRecordingComplete(this, outputFile);
        }
    }

    public void setRecorderListener(RecorderListener recorderListener) {
        this.recorderListener = recorderListener;
    }

    public interface RecorderListener {
        void onRecordingComplete(Recorder recorder, File recordFile);
    }
}
