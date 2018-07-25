package com.wcl.videoedit.utils;

import android.support.annotation.NonNull;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by wangchunlong on 2018/7/16.
 */

public class DataUtils {
    private static Map<String, SimpleDateFormat> dateFormatMap = new HashMap<>();
    /**
     * HH:mm:ss.SSS    12:01:01.001
     * @param time 毫秒
     * @return
     */
    public static String format(long time){
        return format("HH:mm:ss.SSS", time);
    }

    /**
     * 如HH:mm:ss.SSS ，HH-mm-ss.SSS
     * @param pattern 正则表达式
     * @param time 时间  毫秒
     * @return
     */
    public static String format(String pattern, long time){
        SimpleDateFormat simpleDateFormat = getSimpleDateFormat(pattern);
        String format = simpleDateFormat.format(new Date(time));
        return format;
    }

    @NonNull
    private static SimpleDateFormat getSimpleDateFormat(String pattern) {
        SimpleDateFormat simpleDateFormat = dateFormatMap.get(pattern);
        if(simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            dateFormatMap.put(pattern, simpleDateFormat);
        }
        return simpleDateFormat;
    }

    /**
     * 解析默认格式时间数据 HH:mm:ss.SSS
     * @param text
     * @return
     */
    public static long parse(String text){
        Date date = getSimpleDateFormat("HH:mm:ss.SSS").parse(text, new ParsePosition(0));
        return date.getTime();
    }
    /**
     * 解析时间
     * @param pattern 正则表达式
     * @param text 时间文本
     * @return
     */
    public static long parse(String pattern, String text){
        Date date = getSimpleDateFormat(pattern).parse(text, new ParsePosition(0));
        return date.getTime();
    }


    private static SimpleDateFormat simpleDateFormat;

    /**
     * 获取当前的时间的文本格式
     * @return HH-mm-ss  exg:12-01-01
     */
    public static String getCurrentTime(){
        if(simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat("HH-mm-ss", Locale.getDefault());
        }
        String format = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        return format;
    }
}
