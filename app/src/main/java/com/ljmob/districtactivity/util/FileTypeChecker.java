package com.ljmob.districtactivity.util;

/**
 * Created by london on 15/8/6.
 * 文件可上传检查器
 */
public class FileTypeChecker {

    public static boolean isFileTypeAvailable(String filePath) {
//        mp3 jpg png jpeg mp4 wav
        String[] availableTypes = "mp3 jpg png jpeg mp4 wav".split(" ");
        for (String availableType : availableTypes) {
            if (filePath.endsWith(availableType)) {
                return true;
            }
        }
        return false;
    }
}
