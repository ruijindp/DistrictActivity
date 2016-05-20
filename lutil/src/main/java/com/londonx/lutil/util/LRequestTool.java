package com.londonx.lutil.util;

import com.londonx.lutil.Lutil;
import com.londonx.lutil.entity.LResponse;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * Created by london on 15/6/2.
 * LRequestTool
 * Update in 2015-08-04 21:50:33 DELETE not working
 * Update in 2015-08-06 11:17:52 onPostExecute not calling in pre-JELLYBEAN
 * Update in 2015-08-13 12:43:01 twice onResponse called in pre-JELLYBEAN
 * Update in 2015-08-19 19:58:15 add URLEncoder in GET
 * Update in 2015-08-21 12:40:53 'com.loopj.android:android-async-http:1.4.8' for large file upload
 */
public class LRequestTool {
    private OnResponseListener onResponseListener;
    private OnDownloadListener onDownloadListener;
    private static AsyncHttpClient client;

    public LRequestTool(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
        if (client == null) {
            client = new AsyncHttpClient();
            client.setURLEncodingEnabled(true);
        }
    }

    private RequestParams hashMapToRequestParams(HashMap<String, ?> hashMap) {
        RequestParams params = new RequestParams();
        for (String key : hashMap.keySet()) {
            Object value = hashMap.get(key);
            if (value instanceof File) {
                try {
                    params.put(key, ((File) value));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                params.put(key, value.toString());
            }
        }
        return params;
    }

    public void doGet(final String strUrl, final HashMap<String, ?> urlParams, final int requestCode) {
        client.get(strUrl, hashMapToRequestParams(urlParams), new HttpHandler(requestCode));
    }

    public void doPost(final String strUrl, final HashMap<String, ?> urlParams, final int requestCode) {
        client.post(strUrl, hashMapToRequestParams(urlParams), new HttpHandler(requestCode));
    }

    public void doPut(final String strUrl, final HashMap<String, ?> urlParams, final int requestCode) {
        client.put(strUrl, hashMapToRequestParams(urlParams), new HttpHandler(requestCode));
    }

    public void doDelete(final String strUrl, final HashMap<String, ?> urlParams, final int requestCode) {
        client.delete(strUrl, hashMapToRequestParams(urlParams), new HttpHandler(requestCode));
    }

    public void download(final String strUrl, final int requestCode) {
        final LResponse response = new LResponse();
        response.requestCode = requestCode;
        response.url = strUrl;
        client.get(strUrl, new FileAsyncHttpResponseHandler(Lutil.context) {
            @Override
            public void onStart() {
                super.onStart();
                if (onDownloadListener != null) {
                    onDownloadListener.onStartDownload(response);
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                if (onDownloadListener != null) {
                    onDownloadListener.onDownloading((float) bytesWritten / (float) totalSize);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                response.downloadFile = file;
                response.responseCode = statusCode;
                if (onDownloadListener != null) {
                    onDownloadListener.onDownloaded(response);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                response.downloadFile = file;
                response.responseCode = statusCode;
                if (onDownloadListener != null) {
                    onDownloadListener.onDownloaded(response);
                }
            }
        });
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    public interface OnResponseListener {
        void onResponse(LResponse response);
    }

    public interface OnDownloadListener {
        void onStartDownload(LResponse response);

        void onDownloading(float progress);

        void onDownloaded(LResponse response);
    }

    private final class HttpHandler extends AsyncHttpResponseHandler {
        int requestCode;

        public HttpHandler(int requestCode) {
            this.requestCode = requestCode;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            LResponse lResponse = new LResponse();
            lResponse.requestCode = requestCode;
            lResponse.url = getRequestURI().toString();
            lResponse.responseCode = statusCode;
            lResponse.body = new String(responseBody);
            onResponseListener.onResponse(lResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            LResponse lResponse = new LResponse();
            lResponse.requestCode = requestCode;
            lResponse.url = getRequestURI().toString();
            lResponse.responseCode = statusCode;
            lResponse.body = error.getMessage();
            onResponseListener.onResponse(lResponse);
        }
    }
}