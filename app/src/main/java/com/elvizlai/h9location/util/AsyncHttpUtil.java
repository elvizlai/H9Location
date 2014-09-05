package com.elvizlai.h9location.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by huagai on 14-8-11.
 */
public class AsyncHttpUtil {

    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private AsyncHttpUtil() {
    }

    static {
        //最多重试3次,每次10秒
        asyncHttpClient.setTimeout(10000);
        //asyncHttpClient.addHeader("Content-Type","application/json;charset=UTF-8");

        //asyncHttpClient.setMaxRetriesAndTimeout(3, 10000);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        String URL = "http://h9.huagai.com/h9/JHSoft.WCF/POSTServiceForAndroid.svc/" + relativeUrl;
        LogUtil.d(URL);
        return URL;
    }


    public static void post(String methodName, JSONObject jsonObject, AsyncHttpResponseHandler responseHandler) {

        try {
            StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
            post(methodName, entity, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //url为方法名称
    public static void post(String methodName, StringEntity entity, AsyncHttpResponseHandler responseHandler) {

        if (asyncHttpClient == null)
            asyncHttpClient = new AsyncHttpClient();

        asyncHttpClient.post(ApplictionUtil.getContext(), getAbsoluteUrl(methodName), entity, "application/json;charset=UTF-8", responseHandler);
    }

    public static void post(String methodName, String jsonStr, AsyncHttpResponseHandler responseHandler) {
        try {
            StringEntity entity = new StringEntity(jsonStr, "UTF-8");
            post(methodName, entity, responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


}
