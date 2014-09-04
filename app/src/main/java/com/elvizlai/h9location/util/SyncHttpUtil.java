package com.elvizlai.h9location.util;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Elvizlai on 14-8-19.
 */
public class SyncHttpUtil {

    private static SyncHttpClient syncHttpClient = new SyncHttpClient();

    private SyncHttpUtil() {

    }

    static {
        //最多重试3次,每次10秒
        syncHttpClient.setMaxRetriesAndTimeout(3, 10000);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        String URL = "http://h9.huagai.com/h9/JHSoft.WCF/POSTServiceForAndroid.svc/" + relativeUrl;
        LogUtil.d(URL);
        return URL;
    }

    public static void post(String methodName, JSONObject jsonObject, AsyncHttpResponseHandler responseHandler) {

        try {
            StringEntity entity = new StringEntity(jsonObject.toString());
            post(methodName, entity, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //url为方法名称
    public static void post(String methodName, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
        if (syncHttpClient == null)
            syncHttpClient = new SyncHttpClient();

        syncHttpClient.post(ApplictionUtil.getContext(), getAbsoluteUrl(methodName), entity, "application/json", responseHandler);
    }
}
