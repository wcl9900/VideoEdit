package com.wcl.videoedit.constant;

import android.os.Environment;

import java.io.File;

/**
 * Created by wangchunlong on 2018/7/13.
 */

public class PathConstant {
    private static String rootName = "VideoEdit";

    /**
     * 获取外部存储根目录
     * @return
     */
    public static String getExRootDir(){
        String dirs = makeDirs(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + rootName);
        return dirs;
    }

    /**
     * 获取外部存储根目录的子目录
     * @param folder 子目录名称
     * @return
     */
    public static String getExRootDir(String folder){
        String dirs = makeDirs(getExRootDir() + "/" + folder);
        return dirs;
    }

    /**
     * 获取外部视频存储路径
     * @return
     */
    public static String getExVideoDir(){
        return getExRootDir("outVideo");
    }

    private static String makeDirs(String dirs){
        File dir = new File(dirs);
        if(dir.exists()){
            return dirs;
        }
        dir.mkdirs();
        return dirs;
    }
}
