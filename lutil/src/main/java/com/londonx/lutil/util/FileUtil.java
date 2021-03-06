package com.londonx.lutil.util;

import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.londonx.lutil.Lutil;

import java.io.File;
import java.io.IOException;

/**
 * Created by 英伦 on 2015/3/17.
 * FileUtil
 * Update at 2015-08-06 16:52:22 video uri
 */
public class FileUtil {
    private static File cacheFolder = null;

    public static FileType getFileType(String fileUrl) {
        if (fileUrl == null) {
            return FileType.unknown;
        }
        if (fileUrl.endsWith(".mp3") ||
                fileUrl.endsWith(".amr") ||
                fileUrl.endsWith(".wav") ||
                fileUrl.endsWith(".m4a") ||
                fileUrl.endsWith(".flac")) {
            return FileType.music;
        }
        if (fileUrl.endsWith(".mp4") ||
                fileUrl.endsWith(".3gp") ||
                fileUrl.endsWith(".mov")) {
            return FileType.video;
        }
        if (fileUrl.endsWith(".png") || fileUrl.endsWith("jpg") || fileUrl.endsWith(".jpeg") ||
                fileUrl.endsWith(".JPEG") || fileUrl.endsWith(".PENG")|| fileUrl.endsWith(".JPG")) {
            return FileType.picture;
        }
        if (fileUrl.startsWith("http")) {
            return FileType.web;
        }
        return FileType.unknown;
    }

    public static File getFileFromUri(Uri uri) {
        String filePath = "";
        String[] column = {MediaStore.Images.Media.DATA};
        try {
            if (Build.VERSION.SDK_INT >= 19) {
                String wholeID = DocumentsContract.getDocumentId(uri);
                // Split at colon, use second item in the array
                String id = "";
                try {
                    id = wholeID.split(":")[1];
                } catch (IndexOutOfBoundsException e) {
                    return null;
                }
                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = Lutil.context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);
                if (cursor.getCount() == 0) {
                    cursor = Lutil.context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);
                }
                if (cursor.getCount() == 0) {
                    cursor = Lutil.context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);
                }
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            } else {
                CursorLoader cursorLoader = new CursorLoader(
                        Lutil.context,
                        uri, column, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();

                if (cursor != null) {
                    int column_index =
                            cursor.getColumnIndexOrThrow(column[0]);
                    cursor.moveToFirst();
                    filePath = cursor.getString(column_index);
                }
            }
        }catch (Exception e){
            CursorLoader cursorLoader = new CursorLoader(
                    Lutil.context,
                    uri, column, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();

            if (cursor != null) {
                int column_index =
                        cursor.getColumnIndexOrThrow(column[0]);
                cursor.moveToFirst();
                filePath = cursor.getString(column_index);
            }
        }
        return new File(filePath);
    }

    public static File getDownloadFile(String fileApiUrl) throws IOException {
        File file = new File(getCacheFolder(), fileApiUrl.substring(fileApiUrl.lastIndexOf
                ("/")));
        createFile(file);
        return file;
    }

    public static boolean createFile(File file) throws IOException {
        if (!file.exists()) {
            if (file.getParentFile().mkdirs()) {
                return file.createNewFile();
            }
        }
        return false;
    }

    public static File getCacheFolder() throws IOException {
        if (Lutil.context == null) {
            throw new NullPointerException("Lutil.context is null");
        }
        String cachePath;
        if (cacheFolder != null) {
            return cacheFolder;
        } else {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                    !Environment.isExternalStorageRemovable()) {
                File exCache = Lutil.context.getExternalCacheDir();
                if (exCache != null) {
                    cachePath = exCache.getAbsolutePath();
                } else {
                    cachePath = Lutil.context.getCacheDir().getAbsolutePath();
                }
            } else {
                cachePath = Lutil.context.getCacheDir().getAbsolutePath();
            }
            cachePath += File.separator + ".LCache" + File.separator;
        }
        cacheFolder = new File(cachePath);
        return cacheFolder;
    }

    public enum FileType {
        music, video, picture, web, unknown
    }
}
