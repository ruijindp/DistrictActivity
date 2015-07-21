package com.ljmob.districtactivity.util;

import com.londonx.lutil.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by london on 15/7/10.
 * 录音录像文件工具
 */
public class RecorderFileUtil {
    public static File getDefaultRecordFile(boolean isVideo) {
        String fileName = "rec_" + System.currentTimeMillis()
                + (isVideo ? ".3gp" : ".amr");
        File file = null;
        try {
            file = new File(FileUtil.getCacheFolder(), fileName);
            FileUtil.createFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
