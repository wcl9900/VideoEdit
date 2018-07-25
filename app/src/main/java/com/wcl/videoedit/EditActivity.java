package com.wcl.videoedit;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.wcl.markpanel.EditMarker;
import com.wcl.markpanel.MarkPanelView;
import com.wcl.markpanel.OnMarkerClickListener;
import com.wcl.smartpermission.SmartPermission;
import com.wcl.videoedit.activity.edit.MergeVideoHandle;
import com.wcl.videoedit.bean.TextEntity;
import com.wcl.videoedit.bean.VideoEditEntity;
import com.wcl.videoedit.constant.Constant;
import com.wcl.videoedit.constant.EpMapColor;
import com.wcl.videoedit.constant.PathConstant;
import com.wcl.videoedit.dialog.TextDialog;
import com.wcl.videoedit.utils.DataUtils;
import com.wcl.videoedit.utils.FileUtils;
import com.wcl.videoedit.utils.MediaUtils;
import com.wcl.videoedit.utils.NumberUtils;
import com.wcl.videoedit.utils.ToastUtils;
import com.wcl.videoedit.utils.UriUtils;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import VideoHandle.EpEditor;
import VideoHandle.EpText;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 视频编辑
 */
public class EditActivity extends AppCompatActivity {

    @BindView(R.id.iv_thumb)
    ImageView ivThumb;

    MediaMetadataRetriever mmr;
    VideoEditEntity videoEditEntity;

    MergeVideoHandle mergeVideoHandle;

    private boolean selectVideoType = true;//true:从文件， false:从相册

    @BindView(R.id.edt_rotate_degree)
    EditText edtRotateDegree;
    @BindView(R.id.edt_display_out_width)
    EditText edtDisplayOutWidth;
    @BindView(R.id.edt_display_out_height)
    EditText edtDisplayOutHeight;
    @BindView(R.id.rsb_clip)
    RangeSeekBar rsbClip;

    @BindView(R.id.edt_clip_start)
    EditText edtClipStart;
    @BindView(R.id.edt_clip_end)
    EditText edtClipEnd;

    @BindView(R.id.mark_panel_view)
    MarkPanelView thumbMarkPanelView;

    @BindView(R.id.tv_single_music_info)
    TextView tvMergeMusicInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_edit);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SmartPermission.build(this).addPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).request();

        init();
    }

    private void init() {
        videoEditEntity = new VideoEditEntity();

        onClipRangeChangedListener = new OnClipRangeChangedListener();
        rsbClip.setOnRangeChangedListener(onClipRangeChangedListener);
        rsbClip.setEnabled(false);

        FileUtils.copyFilesFassets(this, "font", PathConstant.getExRootDir("font"));

        mergeVideoHandle =  new MergeVideoHandle(this);

        rsbClip.getLeftSeekBar().setThumbDrawableId(R.drawable.ic_video_cut_left);
        rsbClip.getRightSeekBar().setThumbDrawableId(R.drawable.ic_video_cut_right);
        rsbClip.setValue(rsbClip.getMinProgress(), rsbClip.getMaxProgress());

        thumbMarkPanelView.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public void OnMarkerClick(EditMarker marker) {
                new TextDialog(EditActivity.this, (TextEntity) marker.getData(),
                        (TextView) marker.getMarkerView().findViewById(R.id.edt_add_text)).show();
            }
        });
    }

    private OnClipRangeChangedListener onClipRangeChangedListener;

    private class OnClipRangeChangedListener implements OnRangeChangedListener {
        private float min, max;

        public OnClipRangeChangedListener() {
            reset();
        }

        public void reset() {
            min = rsbClip.getMinProgress();
            max = rsbClip.getMaxProgress();
        }

        @Override
        public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
            this.min = min;
            this.max = max;
        }

        @Override
        public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
        }

        @Override
        public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            Bitmap frameAtTime = mmr.getFrameAtTime((long) ((isLeft ? min : max) * videoEditEntity.getDuration() * 1000));//微秒
            thumbMarkPanelView.setMapImage(frameAtTime);
            if (isLeft) {
                videoEditEntity.setClipStart((long) (min * videoEditEntity.getDuration()));
            } else {
                videoEditEntity.setClipEnd((long) (max * videoEditEntity.getDuration()));
            }

            updateShowClipTime();
        }
    }

    private void updateShowClipTime() {
        String formatStart = DataUtils.format(videoEditEntity.getClipStart());
        String formatEnd = DataUtils.format(videoEditEntity.getClipEnd());
        edtClipStart.setText(formatStart);
        edtClipEnd.setText(formatEnd);
    }

    @OnClick({R.id.button_select_video, R.id.button_select_video_photo,R.id.button_single_select_music, R.id.button_handle,R.id.button_setting,
            R.id.button_single_video_share, R.id.button_play_edit, R.id.button_play_org,R.id.button_add_merge,
            R.id.btn_display_switch, R.id.btn_add_text, R.id.iv_rotate_90_left, R.id.iv_rotate_90_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_select_video:
                selectVideoFromRe();
                break;
            case R.id.button_select_video_photo:
                selectVideoFromPhoto();
                break;
            case R.id.button_single_select_music:
                selectMusic();
                break;
            case R.id.button_handle:
                editVideo();
                break;
            case R.id.button_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.button_add_merge:
                mergeVideoHandle.addMergeVideo(videoEditEntity);
                break;
            case R.id.button_play_org:
                MediaUtils.playVideo(this, videoEditEntity.getVideoOrgPath());
                break;
            case R.id.button_play_edit:
                MediaUtils.playVideo(this, videoEditEntity.getVideoOutPath());
                break;
            case R.id.button_single_video_share:
                MediaUtils.share(this, videoEditEntity.getVideoOutPath());
                break;
            case R.id.btn_display_switch:
                outDisplaySwitch();
                break;
            case R.id.btn_add_text:
                addText();
                break;
            case R.id.iv_rotate_90_left:
                rotate(-90);
                break;
            case R.id.iv_rotate_90_right:
                rotate(90);
                break;
        }
    }

    private void addText() {
        if(TextUtils.isEmpty(videoEditEntity.getVideoOrgPath())){
            ToastUtils.show(this, "请先加载视频，再添加文字！");
            return;
        }
        TextEntity textEntity = new TextEntity();
        View addTextView = LayoutInflater.from(this).inflate(R.layout.layout_edit_text, null);
        EditMarker editMarkerText = new EditMarker(addTextView, 0.5f, 0.5f, -0.0f, -0.5f);
        TextView textView = addTextView.findViewById(R.id.edt_add_text);
        editMarkerText.setTouchView(textView);
        editMarkerText.setData(textEntity);

        textView.setText(textEntity.getText());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textEntity.getSize());
        textView.setTextColor(EpMapColor.valueOf(textEntity.getColor()).getColor());

        thumbMarkPanelView.addMarker(editMarkerText);
    }

    private void rotate(int rotateDegree){
        String rotateStr = edtRotateDegree.getText().toString();
        int rotate = Integer.parseInt(rotateStr) + rotateDegree;
        rotate = Math.max(0, rotate);
        rotate = Math.min(rotate, 270);
        edtRotateDegree.setText(rotate+"");
    }

    private void outDisplaySwitch() {
        String temp = edtDisplayOutWidth.getText().toString();
        edtDisplayOutWidth.setText(edtDisplayOutHeight.getText().toString());
        edtDisplayOutHeight.setText(temp);
    }

    private void selectVideoFromRe() {
        //从文件管理器选择
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择视频"), Constant.REQUEST_PHOTO_VIDEO);
            selectVideoType = true;
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectVideoFromPhoto() {
        //从相册选择
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constant.REQUEST_PHOTO_VIDEO);
        selectVideoType = false;
    }
    private void selectMusic() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择音频"), Constant.REQUEST_RE_SINGLE_AUDIO);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mergeVideoHandle.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_PHOTO_VIDEO) {
            if(resultCode != RESULT_OK || data == null) return;

            String filePath;
            if(!selectVideoType) {
                //处理从相册选择
                Uri uri = data.getData();
                String[] filePathColumn = {MediaStore.Video.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                cursor.close();
            }
            else {
                //处理从文件管理选择
                Uri uri = data.getData();
                filePath = UriUtils.getUriRealPath(uri);
            }

            if (mmr != null) {
                mmr.release();
                mmr = null;
            }
            mmr = new MediaMetadataRetriever();

            try {
                mmr.setDataSource(filePath);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                ToastUtils.show(this, "错误信息："+e.getMessage());
                return;
            }

            rsbClip.setEnabled(true);

            if (videoEditEntity != null) {
                videoEditEntity = null;
            }
            videoEditEntity = new VideoEditEntity();
            videoEditEntity.setVideoOrgPath(filePath);

            tvMergeMusicInfo.setText("");

            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
            String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高

            edtDisplayOutWidth.setText(width);
            edtDisplayOutHeight.setText(height);

            long duration = Integer.parseInt(durationStr);//毫秒
            videoEditEntity.setDuration(duration);
            videoEditEntity.setClipStart(0);
            videoEditEntity.setClipEnd(duration);

            int widthOrg = Integer.parseInt(width);
            int heightOrg = Integer.parseInt(height);

            videoEditEntity.setWidthOrg(widthOrg);
            videoEditEntity.setHeightOrg(heightOrg);

            videoEditEntity.setWidthOut(widthOrg);
            videoEditEntity.setHeightOut(heightOrg);

            thumbMarkPanelView.setMapImage(mmr.getFrameAtTime());

            onClipRangeChangedListener.reset();
            rsbClip.setValue(rsbClip.getMinProgress(), rsbClip.getMaxProgress());

            updateShowClipTime();
        }
        else if(requestCode == Constant.REQUEST_RE_SINGLE_AUDIO){
            if(resultCode != RESULT_OK || data == null) {
                tvMergeMusicInfo.setText("");
                videoEditEntity.setMergeAudioPath("");
                return;
            }
            Uri uri = data.getData();
            String filePath = UriUtils.getUriRealPath(uri);
            if (!TextUtils.isEmpty(filePath)) {
                tvMergeMusicInfo.setText(FileUtils.getFileName(filePath));
                videoEditEntity.setMergeAudioPath(filePath);
            }
            else {
                tvMergeMusicInfo.setText("");
                videoEditEntity.setMergeAudioPath("");
            }
        }
    }

    private void editVideo() {
        if (videoEditEntity.getVideoOrgPath() == null) {
            ToastUtils.show(this, "请选择视频！");
            return;
        }

        try {
            videoEditEntity = videoEditEntity.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        EpVideo epVideo = new EpVideo(videoEditEntity.getVideoOrgPath());
        final String videoOutPath = PathConstant.getExVideoDir() + "/" + "ve-single-" + DataUtils.getCurrentTime() + ".mp4";

        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(videoOutPath);
        if (!TextUtils.isEmpty(edtDisplayOutWidth.getText().toString())) {
            int outWidth = Integer.parseInt(edtDisplayOutWidth.getText().toString());
            videoEditEntity.setWidthOut(outWidth);
            outputOption.setWidth(outWidth);
        }
        if (!TextUtils.isEmpty(edtDisplayOutHeight.getText().toString())) {
            int outHeight = Integer.parseInt(edtDisplayOutHeight.getText().toString());
            videoEditEntity.setHeightOut(outHeight);
            outputOption.setHeight(outHeight);
        }
        if (!TextUtils.isEmpty(edtRotateDegree.getText().toString())) {
            int rotateOut = Integer.parseInt(edtRotateDegree.getText().toString());
            if (rotateOut != 0) {
                epVideo = epVideo.rotation(rotateOut, false);
                videoEditEntity.setRotation(rotateOut);
            }
        }

        if(thumbMarkPanelView.getMarkerList().size() != 0){
            List<EditMarker> textMarkerList = thumbMarkPanelView.getMarkerList();
            for (EditMarker textMarker : textMarkerList){
                TextEntity dataText = (TextEntity) textMarker.getData();
                int x = (int) (videoEditEntity.getWidthOut() * textMarker.getPercentX());
                int y = (int) (videoEditEntity.getHeightOut() * textMarker.getPercentY());
                EpText epText = new EpText(x, y, dataText.getSize(), EpText.Color.valueOf(dataText.getColor()),
                        PathConstant.getExRootDir("font") + "/msyh.ttf", dataText.getText(), null);
                epVideo = epVideo.addText(epText);
            }
        }

        if (videoEditEntity.needClip()) {
            long edtStart = DataUtils.parse(edtClipStart.getText().toString());
            long edtEnd = DataUtils.parse(edtClipEnd.getText().toString());

            float clipStart = edtStart / 1000f;//秒
            float clipDuration = (edtEnd - edtStart) / 1000f;//秒

            epVideo.clip(clipStart, clipDuration);

            videoEditEntity.setClipStart(edtStart);
            videoEditEntity.setClipEnd(edtEnd);
        }

        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                if(TextUtils.isEmpty(videoEditEntity.getMergeAudioPath())) {
                    ToastUtils.show(EditActivity.this, "视频处理成功！");
                    videoEditEntity.setVideoOutPath(videoOutPath);
                    MediaUtils.updateToMedia(EditActivity.this, videoEditEntity.getVideoOutPath());
                }
                else {
                    mergeMusic(videoOutPath, videoEditEntity.getMergeAudioPath());
                }
            }

            @Override
            public void onFailure() {
                ToastUtils.show(EditActivity.this, "视频处理失败！");
                videoEditEntity.setVideoOutPath(null);
            }

            @Override
            public void onProgress(final float v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progressTemp = NumberUtils.getMiddleValue(0, (int) (NumberUtils.getFloat(v, 3) * 100), 100);
                        String info = String.format(Locale.getDefault(), "处理进度: %d%%", progressTemp);
                        showProgress(info, v != 1);
                        Log.i("VideoEdit", "单视频处理进度：" + v);
                    }
                });
            }
        });
    }

    private void mergeMusic(final String videoInPath, String audioInPath){
        final String videoOutPath = PathConstant.getExVideoDir() + "/" + "ve-single-audio-" + DataUtils.getCurrentTime() + ".mp4";
        EpEditor.music(videoInPath, audioInPath, videoOutPath, 1f, 0.7f, new OnEditorListener() {
            @Override
            public void onSuccess() {
                ToastUtils.show(EditActivity.this, "音视频合成成功！");
                videoEditEntity.setVideoOutPath(videoOutPath);
                MediaUtils.updateToMedia(EditActivity.this, videoEditEntity.getVideoOutPath());
                MediaUtils.updateToMediaDelete(EditActivity.this, videoInPath);
            }

            @Override
            public void onFailure() {
                ToastUtils.show(EditActivity.this, "音视频合成失败！");
                videoEditEntity.setVideoOutPath(null);
            }

            @Override
            public void onProgress(final float v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progressTemp = NumberUtils.getMiddleValue(0, (int) (NumberUtils.getFloat(v, 3) * 100), 100);
                        String info = String.format(Locale.getDefault(), "音视频合成进度: %d%%", progressTemp);
                        showProgress(info, v != 1);
                        Log.i("VideoEdit", "音视频合成进度：" + v);
                    }
                });
            }
        });
    }

    AlertDialog progressDialog;
    public void showProgress(String info, boolean show) {
        if (!show) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            return;
        }
        if (progressDialog == null) {
            progressDialog = new AlertDialog.Builder(this)
                    .setTitle("处理中")
                    .setMessage(info).show();
        }
        progressDialog.show();
        progressDialog.setMessage(info);
    }

    private boolean closeEnable = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if (!closeEnable) {
                closeEnable = true;
                ToastUtils.show(this, "再按一次退出程序!");
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        closeEnable = false;
                    }
                }, 2000);
            } else {
                finish();
                System.exit(0);
            }
        }
        return true;
    }
}
