package com.example.shihao.myapplication.utils;

import java.util.Date;

public class TimeUtils {
    public static String formatDuring(long mss) {
        String resultSeconds = "00";
        long minutes = mss / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        if (seconds < 10) {
            resultSeconds = "0" + seconds;
        } else {
            resultSeconds = "" + seconds;
        }
        return minutes + ":" + resultSeconds;
    }

    /**
     * @param begin 时间段的开始
     * @param end   时间段的结束
     * @return 输入的两个Date类型数据之间的时间间格用* days * hours * minutes * seconds的格式展示
     * @author fy.zhang
     */
    public static String formatDuring(Date begin, Date end) {
        return formatDuring(end.getTime() - begin.getTime());
    }
}
