package com.londonx.lutil.util;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.londonx.lutil.entity.LResponse;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by london on 15/6/2.
 * LRequestTool
 * Update in 2015-08-04 21:50:33 DELETE not working
 * Update in 2015-08-06 11:17:52 onPostExecute not calling in pre-JELLYBEAN
 * Update in 2015-08-13 12:43:01 twice onResponse called in pre-JELLYBEAN
 */
public class LRequestTool {
    private static HashMap<String, HttpURLConnection> lConnectionPool;
    private OnResponseListener onResponseListener;
    private OnDownloadListener onDownloadListener;

    public LRequestTool(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
        if (lConnectionPool == null) {
            lConnectionPool = new HashMap<>();
        }
    }

    public void doGet(final String strUrl, final HashMap<String, ?> urlParams, final int requestCode) {
        LRequest lRequest = new LRequest(strUrl, LRequest.METHOD_GET, urlParams, requestCode);
        lRequest.execute();
    }

    public void doPost(final String strUrl, final HashMap<String, ?> urlParams, final int requestCode) {
        LRequest lRequest = new LRequest(strUrl, LRequest.METHOD_POST, urlParams, requestCode);
        lRequest.execute();
    }

    public void doPut(final String strUrl, final HashMap<String, ?> urlParams, final int requestCode) {
        LRequest lRequest = new LRequest(strUrl, LRequest.METHOD_PUT, urlParams, requestCode);
        lRequest.execute();
    }

    public void doDelete(final String strUrl, final HashMap<String, ?> urlParams, final int requestCode) {
        LRequest lRequest = new LRequest(strUrl, LRequest.METHOD_DELETE, urlParams, requestCode);
        lRequest.execute();
    }

    public void download(final String strUrl, final int requestCode) {
        LRequest lRequest = new LRequest(strUrl, LRequest.METHOD_GET, null, requestCode);
        try {
            File downloadFile = FileUtil.getDownloadFile(strUrl);
            lRequest.setDownloadFile(downloadFile);
            lRequest.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getConnectionBody(HttpURLConnection urlConnection) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    /**
     * parse a HashMap to a key value String
     *
     * @param hashMap normal HashMap
     * @return a key value String (?id=1&name=a)
     */
    public static String hashMapToGetParam(HashMap<String, ?> hashMap) {
        String mapString = "";
        if (hashMap == null || hashMap.isEmpty() || hashMap.size() == 0) {
            return mapString;
        }
        mapString += "?";
        Object[] keys = hashMap.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            if (i != 0) {
                mapString += "&";
            }
            String key = keys[i].toString();
            String value = hashMap.get(key).toString();
            mapString += key + "=" + value;
        }
        return mapString;
    }

    /**
     * parse a HashMap to a {@code MultipartEntity}
     *
     * @param hashMap normal HashMap
     * @return a {@code MultipartEntity}
     */
    public static MultipartEntity hashMapToMultipartEntity(HashMap<String, ?> hashMap) {
        MultipartEntity entity = new MultipartEntity();
        if (hashMap == null || hashMap.isEmpty() || hashMap.size() == 0) {
            return entity;
        }
        Object[] keys = hashMap.keySet().toArray();
        for (Object key1 : keys) {
            String key = key1.toString();
            Object value = hashMap.get(key);
            try {
                if (value instanceof File) {
                    entity.addPart(key, new FileBody((File) value));
                } else {
                    entity.addPart(key, new StringBody(value.toString()));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    private class LRequest extends AsyncTask<String, Integer, LResponse> implements Handler.Callback {
        public static final String METHOD_GET = "GET";
        public static final String METHOD_POST = "POST";
        public static final String METHOD_PUT = "PUT";
        public static final String METHOD_DELETE = "DELETE";

        private static final int WHAT_DOWNLOAD_START = 1;
        private static final int WHAT_DOWNLOADING = 2;
        private static final int WHAT_POST_EXECUTE = 3;

        private String strUrl;
        private String requestMethod;
        private HashMap<String, ?> attr;
        private int requestCode;
        final LResponse lResponse = new LResponse();
        private File downloadFile;
        Handler handler;

        long lastReportTime;

        public LRequest(String strUrl, String requestMethod, HashMap<String, ?> attr, int requestCode) {
            this.strUrl = strUrl;
            this.requestMethod = requestMethod;
            this.attr = attr;
            this.requestCode = requestCode;
            handler = new Handler(this);
        }

        public void setDownloadFile(File downloadFile) {
            this.downloadFile = downloadFile;
        }

        @Override
        protected LResponse doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                if (lConnectionPool.containsKey(strUrl)) {
                    final HttpURLConnection connectionInPool = lConnectionPool.get(strUrl);
                    connectionInPool.disconnect();
                    System.gc();
                }
                URL url = new URL(strUrl +
                        (requestMethod.equals(METHOD_GET) ? hashMapToGetParam(attr) : ""));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(requestMethod);
                lConnectionPool.put(strUrl, urlConnection);

                if (!requestMethod.equals(METHOD_GET)) {
                    MultipartEntity entity = hashMapToMultipartEntity(attr);
                    urlConnection.setRequestProperty("Connection", "Keep-Alive");
                    urlConnection.addRequestProperty("Content-length", entity.getContentLength() + "");
                    urlConnection.addRequestProperty(entity.getContentType().getName(),
                            entity.getContentType().getValue());

                    OutputStream os = urlConnection.getOutputStream();
                    entity.writeTo(os);
                    os.close();
                }

                lResponse.requestCode = requestCode;
                lResponse.responseCode = urlConnection.getResponseCode();
                lResponse.url = url.toString();
                if (downloadFile != null) {
                    lResponse.downloadFile = downloadFile;
                    InputStream is = urlConnection.getInputStream();
                    int totalLen = urlConnection.getContentLength();
                    if (downloadFile.exists() && downloadFile.length() != 0) {
                        is.close();
                        return lResponse;
                    }
                    handler.sendEmptyMessage(WHAT_DOWNLOAD_START);
                    float wroteLen = 0;
                    // 1K的数据缓冲
                    byte[] bs = new byte[1024];
                    // 读取到的数据长度
                    int len;
                    // 输出的文件流
                    FileOutputStream fos = new FileOutputStream(downloadFile);
                    // 开始读取
                    while ((len = is.read(bs)) != -1) {
                        fos.write(bs, 0, len);
                        wroteLen += len;
                        if (System.currentTimeMillis() - lastReportTime > 200) {
                            Message message = handler.obtainMessage(WHAT_DOWNLOADING, wroteLen / totalLen);
                            handler.sendMessage(message);
                            lastReportTime = System.currentTimeMillis();
                        }
                    }
                    // 完毕，关闭所有链接
                    fos.close();
                    is.close();
                } else {
                    lResponse.body = getConnectionBody(urlConnection);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                Message message = handler.obtainMessage();
                message.what = WHAT_POST_EXECUTE;
                message.obj = lResponse;
                handler.sendMessage(message);
                return null;
            }
            return lResponse;
        }

        @Override
        protected void onPostExecute(LResponse response) {
            if (response == null) {
                return;
            }
            if (response.downloadFile == null) {
                onResponseListener.onResponse(lResponse);
            } else {
                onDownloadListener.onDownloaded(lResponse);
            }
            super.onPostExecute(response);
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_DOWNLOAD_START:
                    if (onDownloadListener != null) {
                        onDownloadListener.onStartDownload(lResponse);
                    }
                    break;
                case WHAT_DOWNLOADING:
                    if (onDownloadListener != null) {
                        onDownloadListener.onDownloading((Float) msg.obj);
                    }
                    break;
                case WHAT_POST_EXECUTE:
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                        return false;
                    }
                    onPostExecute((LResponse) msg.obj);
                    break;
            }
            return false;
        }
    }


    public interface OnResponseListener {
        void onResponse(LResponse response);
    }

    public interface OnDownloadListener {
        void onStartDownload(LResponse response);

        void onDownloading(float progress);

        void onDownloaded(LResponse response);
    }
}
