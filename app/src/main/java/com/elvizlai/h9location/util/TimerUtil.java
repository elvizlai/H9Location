package com.elvizlai.h9location.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Elvizlai on 14-8-19.
 */
public class TimerUtil {

    private int defaultDelay = 3000;
    private Timer timer = new Timer();
    private onTimerCompleteListener loadListener;

    private TimerUtil() {
        LogUtil.d("创建时间计时器，延时 " + defaultDelay + " 毫秒执行");
        timer.schedule(timerTask, defaultDelay);
    }

    private TimerUtil(int time) {
        LogUtil.d("创建时间计时器，延时 " + time + " 毫秒执行");
        timer.schedule(timerTask, time);
    }

    public static TimerUtil setDefaultDelay() {
        return new TimerUtil();
    }

    public static TimerUtil setTimeDelay(int time) {
        return new TimerUtil(time);
    }

    public void setOnTimerCompleteListerner(onTimerCompleteListener dataComplete) {
        this.loadListener = dataComplete;
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (loadListener != null) {
                LogUtil.d("计时结束，开始执行 timerComplete");

                loadListener.timerComplete();

                timer.cancel();
                timerTask.cancel();
            }
        }
    };

    public interface onTimerCompleteListener {
        public void timerComplete();
    }

}
