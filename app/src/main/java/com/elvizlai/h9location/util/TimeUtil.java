package com.elvizlai.h9location.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Elvizlai on 14-9-3.
 */
public class TimeUtil {
    private final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static Calendar mCalendar = Calendar.getInstance();


    public static String getFormattedTimeStr() {
        return formatter.format(mCalendar.getTime());
    }
}
