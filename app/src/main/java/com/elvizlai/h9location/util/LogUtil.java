package com.elvizlai.h9location.util;

import android.util.Log;

import com.elvizlai.h9location.BuildConfig;


/**
 * Created by Elvizlai on 14-8-18.
 */
public class LogUtil {
    private LogUtil() {
    }

    //debug版本才会输出log
    public static void d(String log) {
        if (BuildConfig.DEBUG)
            Log.d("ElvizLai-d", log);
    }

    public static void e(String log) {
        if (BuildConfig.DEBUG)
            Log.d("ElvizLai-e", log);
    }

    public static void i(String log) {
        if (BuildConfig.DEBUG)
            Log.d("ElvizLai-i", log);
    }


}
