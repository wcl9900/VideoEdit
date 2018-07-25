package com.wcl.videoedit.constant;

import android.support.annotation.ColorInt;

/**
 * {@link VideoHandle.EpText.Color}的映射枚举表
 * Created by wangchunlong on 2018/7/24.
 */

public enum EpMapColor {
    Red(0xFFFF0000),
    Blue(0xFF0000FF),
    Yellow(0xFFFFFF00),
    Black(0xFF000000),
    DarkBlue(0xFF00008B),
    Green(0xFF008000),
    SkyBlue(0xFF87CEEB),
    Orange(0xFFFFA500),
    White(0xFFFFFFFF),
    Cyan(0xFF00FFFF);

    int color;
    EpMapColor(@ColorInt int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
