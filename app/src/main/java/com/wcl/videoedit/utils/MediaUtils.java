package com.wcl.videoedit.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

/**
 * Created by wangchunlong on 2018/3/21.
 */

public class MediaUtils {
    /**
     * 同步文件至本地媒体资源库
     * @param context
     * @param filePath
     */
    public static void updateToMedia(Context context, String filePath) {
        if (!new File(filePath).exists())
            return;

        Uri localUri = Uri.fromFile(new File(filePath));
        Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                localUri);
        context.sendBroadcast(localIntent);
    }

    public static void updateToMediaDelete(Context context, String filePath){
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();
        String where = MediaStore.Images.Media.DATA + "='" + filePath + "'";
        //删除图片
        mContentResolver.delete(uri, where, null);

        //删除后更新同步相册数据库
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
            MediaScannerConnection.scanFile(context, paths, null, null);
            MediaScannerConnection.scanFile(context, new String[] {filePath},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri)
                        {
                        }
                    });
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    /**
     * 获取视频缩略图
     * @param filePath
     * @return
     */
    public static Bitmap getVideoImage(String filePath){
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }

        if (bitmap == null) return null;

        float scaleWidth = 800;
        // Scale down the bitmap if it's too large.
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int max = Math.max(width, height);
        if (max > scaleWidth) {
            float scale = scaleWidth / max;
            int w = Math.round(scale * width);
            int h = Math.round(scale * height);
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
        }
        return bitmap;
    }

    public static void playVideo(Context context, String videoPath) {
        if (TextUtils.isEmpty(videoPath)) {
            Toast.makeText(context, "播放文件路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(videoPath), "video/mp4");
        context.startActivity(intent);
    }

    public static void share(Context context, String path) {
        if(TextUtils.isEmpty(path)){
            ToastUtils.show(context, "文件路径为空！");
            return;
        }
        if (!new File(path).exists()) {
            Toast.makeText(context, "文件不存在！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        imageIntent.setType("video/mp4");
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        }
        else {
            Uri uri = FileProvider.getUriForFile(context, "com.wcl.videoedit", new File(path));
            imageIntent.putExtra(Intent.EXTRA_STREAM, uri);
        }
        context.startActivity(Intent.createChooser(imageIntent, "分享"));
    }
}
