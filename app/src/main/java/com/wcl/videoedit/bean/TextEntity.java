package com.wcl.videoedit.bean;

import VideoHandle.EpText;

/**
 * Created by wangchunlong on 2018/7/23.
 */

public class TextEntity {
    public static final float DEFAULT_SIZE = 40;
    public static final String DEFAULT_COLOR = EpText.Color.White.getColor();

    private String text = "北京暴龙科技有限公司";
    private float size = DEFAULT_SIZE;
    private String color = DEFAULT_COLOR;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
