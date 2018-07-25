package com.wcl.videoedit.bean;

import android.text.TextUtils;

/**
 * Created by wangchunlong on 2018/7/13.
 */

public class VideoEditEntity implements Cloneable{
    private String videoOrgPath;
    private String videoOutPath;

    private String mergeAudioPath;

    private int widthOrg;
    private int heightOrg;

    private int widthOut;
    private int heightOut;

    //毫秒
    private long duration;

    //毫秒
    private long clipStart;
    //毫秒
    private long clipEnd;

    private int rotation;

    public String getMergeAudioPath() {
        return mergeAudioPath;
    }

    public void setMergeAudioPath(String mergeAudioPath) {
        this.mergeAudioPath = mergeAudioPath;
    }

    public String getVideoOrgPath() {
        return videoOrgPath;
    }

    public void setVideoOrgPath(String videoOrgPath) {
        this.videoOrgPath = videoOrgPath;
    }

    public String getVideoOutPath() {
        return videoOutPath;
    }

    public void setVideoOutPath(String videoOutPath) {
        this.videoOutPath = videoOutPath;
    }

    public int getWidthOrg() {
        return widthOrg;
    }

    public void setWidthOrg(int widthOrg) {
        this.widthOrg = widthOrg;
    }

    public int getHeightOrg() {
        return heightOrg;
    }

    public void setHeightOrg(int heightOrg) {
        this.heightOrg = heightOrg;
    }

    public int getWidthOut() {
        return widthOut;
    }

    public void setWidthOut(int widthOut) {
        this.widthOut = widthOut;
    }

    public int getHeightOut() {
        return heightOut;
    }

    public void setHeightOut(int heightOut) {
        this.heightOut = heightOut;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getClipStart() {
        return clipStart;
    }

    public void setClipStart(long clipStart) {
        this.clipStart = clipStart;
    }

    public long getClipEnd() {
        return clipEnd;
    }

    public void setClipEnd(long clipEnd) {
        this.clipEnd = clipEnd;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public boolean needClip(){
        if(clipStart == 0 && clipEnd == duration) return false;
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        VideoEditEntity o = ((VideoEditEntity)obj);
        return TextUtils.equals(videoOutPath, o.getVideoOutPath());
    }

    @Override
    public VideoEditEntity clone() throws CloneNotSupportedException {
        return (VideoEditEntity) super.clone();
    }
}
