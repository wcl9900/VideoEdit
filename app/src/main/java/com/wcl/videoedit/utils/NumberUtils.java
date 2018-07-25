package com.wcl.videoedit.utils;

import java.text.NumberFormat;

/**
 * Created by wangchunlong on 2018/7/17.
 */

public class NumberUtils {
    /**
     *
     * @param number
     * @param digits 保留小数点位数
     * @return
     */
    public static float getFloat(float number, int digits){
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(digits);
        float value = 0;
        try {
            value = Float.parseFloat(format.format(number));
            return value;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return number;
        }
    }

    /**
     * 获取中间值
     * @param min 最小值
     * @param value
     * @param max 最大值
     * @return
     */
    public static int getMiddleValue(int min, int value, int max){
        int maxTemp = Math.min(max, value);
        int filter = Math.max(maxTemp, min);
        return filter;
    }
}
