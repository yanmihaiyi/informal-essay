package com.example.shihao.myapplication.utils;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class NetWorkSpeedUtils {
    private Context context;
    private Handler mHandler;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    private String result;
    private Timer timer;

    public NetWorkSpeedUtils(Context context, Handler mHandler) {
        this.context = context;
        this.mHandler = mHandler;
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            showNetSpeed();
        }
    };

    public void startShowNetSpeed() {
        lastTotalRxBytes = getTotalRxBytes();
        lastTimeStamp = System.currentTimeMillis();
        timer = new Timer();
        timer.schedule(task, 1000, 2000); // 1s后启动任务，每2s执行一次
    }

    public void stopShowNetSpeed() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    private void showNetSpeed() {
        long nowTotalRxBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        long speed2 = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 % (nowTimeStamp - lastTimeStamp));//毫秒转换
        DecimalFormat df = new DecimalFormat("0.0");

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;

        double resultSpeed = speed + speed2 / (double) 1000;

        if (resultSpeed > 1024 && resultSpeed <= 1024 * 1024) {
            result = df.format(resultSpeed / (double) 1024) + " M/s";
        } else if (resultSpeed > 1024 * 1024) {
            result = df.format(resultSpeed / (double) 1024 * 1024) + " G/s";
        } else {
            result = df.format(resultSpeed) + " K/s";
        }


        Message msg = mHandler.obtainMessage();
        msg.what = 100;
        msg.obj = result;
        mHandler.sendMessage(msg);//更新界面
    }

}
