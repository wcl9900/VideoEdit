package com.wcl.videoedit.activity.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wcl.videoedit.EditActivity;
import com.wcl.videoedit.R;
import com.wcl.videoedit.bean.MergeEditEntity;
import com.wcl.videoedit.bean.VideoEditEntity;
import com.wcl.videoedit.constant.Constant;
import com.wcl.videoedit.constant.PathConstant;
import com.wcl.videoedit.utils.DataUtils;
import com.wcl.videoedit.utils.FileUtils;
import com.wcl.videoedit.utils.MediaUtils;
import com.wcl.videoedit.utils.NumberUtils;
import com.wcl.videoedit.utils.ToastUtils;
import com.wcl.videoedit.utils.UriUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 合并视频处理
 * Created by wangchunlong on 2018/7/16.
 */

public class MergeVideoHandle {

    EditActivity activity;

    MergeEditEntity mergeEditEntity;

    List<VideoEditEntity> mergeVideoList = new ArrayList<>();

    @BindView(R.id.recyclerview_merge)
    RecyclerView recyclerViewMerge;

    @BindView(R.id.edt_merge_rotate_degree)
    EditText edtRotateDegree;
    @BindView(R.id.edt_merge_display_out_width)
    EditText edtDisplayOutWidth;
    @BindView(R.id.edt_merge_display_out_height)
    EditText edtDisplayOutHeight;
    @BindView(R.id.btn_merge_display_switch)
    ImageView btnDisplaySwitch;

    @BindView(R.id.tv_merge_music_info)
    TextView tvMergeMusicInfo;

    public MergeVideoHandle(EditActivity activity){
        this.activity = activity;
        ButterKnife.bind(this, this.activity.findViewById(R.id.ll_merge_video));
        init();
    }

    private void init(){
        mergeEditEntity = new MergeEditEntity();
        recyclerViewMerge.addItemDecoration(new MergeItemDecoration(activity));
        recyclerViewMerge.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMerge.setAdapter(adapter);
        adapter.setOnItemClickListener(onItemClickListener);
        helper.attachToRecyclerView(recyclerViewMerge);
    }

    public void addMergeVideo(VideoEditEntity videoEditEntity){
        if(videoEditEntity == null || TextUtils.isEmpty(videoEditEntity.getVideoOutPath())) {
            ToastUtils.show(activity, "未有拼接的视频，请先编辑视频！");
            return;
        }
        if(mergeVideoList.contains(videoEditEntity)){
           ToastUtils.show(activity, "已添加！");
            recyclerViewMerge.getAdapter().notifyDataSetChanged();
            return;
        }
        adapter.addData(videoEditEntity);
        setOutDisplay(videoEditEntity);
    }

    private BaseQuickAdapter.OnItemClickListener onItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            setOutDisplay(mergeVideoList.get(position));
        }
    };

    private void setOutDisplay(VideoEditEntity videoEditEntity) {
        edtDisplayOutWidth.setText(videoEditEntity.getWidthOut()+"");
        edtDisplayOutHeight.setText(videoEditEntity.getHeightOut()+"");
    }

    private BaseQuickAdapter adapter = new BaseQuickAdapter<VideoEditEntity, BaseViewHolder>(R.layout.item_rv_merge_video, mergeVideoList) {
        @Override
        protected void convert(final BaseViewHolder helper, VideoEditEntity item) {
            ImageView imageViewThumb = helper.getView(R.id.iv_merge_item_thumb);
            TextView tvInfo = helper.getView(R.id.tv_merge_item_info);
            helper.getView(R.id.iv_merge_video_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.remove(helper.getAdapterPosition());
                }
            });

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(item.getVideoOutPath());
            Bitmap frameAtTime = mmr.getFrameAtTime();
            imageViewThumb.setImageBitmap(frameAtTime);

            String duration = DataUtils.format(item.getClipEnd() - item.getClipEnd());
            tvInfo.setText(duration);
        }
    };

    //为RecycleView绑定触摸事件
    ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            //首先回调的方法 返回int表示是否监听该方向
            int dragFlags = ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;//拖拽
            int swipeFlags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;//侧滑删除
            return makeMovementFlags(dragFlags,swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //滑动事件
            Collections.swap(mergeVideoList,viewHolder.getAdapterPosition(),target.getAdapterPosition());
            adapter.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //侧滑事件
            mergeVideoList.remove(viewHolder.getAdapterPosition());
            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }

        @Override
        public boolean isLongPressDragEnabled() {
            //是否可拖拽
            return true;
        }
    });

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constant.REQUEST_RE_MERGE_AUDIO){
            if(resultCode != Activity.RESULT_OK || data == null) {
                tvMergeMusicInfo.setText("");
                mergeEditEntity.setMergeAudioPath("");
                return;
            }
            Uri uri = data.getData();
            String filePath = UriUtils.getUriRealPath(uri);
            if (!TextUtils.isEmpty(filePath)) {
                tvMergeMusicInfo.setText(FileUtils.getFileName(filePath));
                mergeEditEntity.setMergeAudioPath(filePath);
            }
            else {
                tvMergeMusicInfo.setText("");
                mergeEditEntity.setMergeAudioPath("");
            }
        }
    }

    @OnClick({R.id.button_merge_video_handle, R.id.button_merge_select_music, R.id.button__merge_video_play,R.id.btn_merge_display_switch,
            R.id.button_merge_video_share, R.id.iv_merge_rotate_90_left, R.id.iv_merge_rotate_90_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_merge_video_handle:
                mergeVideo();
                break;
            case R.id.button_merge_select_music:
                selectMusic();
                break;
            case R.id.button__merge_video_play:
                MediaUtils.playVideo(activity, mergeEditEntity.getVideoOutPath());
                break;
            case R.id.button_merge_video_share:
                MediaUtils.share(activity, mergeEditEntity.getVideoOutPath());
                break;
            case R.id.iv_merge_rotate_90_left:
                rotate(-90);
                break;
            case R.id.iv_merge_rotate_90_right:
                rotate(90);
                break;
            case R.id.btn_merge_display_switch:
                outDisplaySwitch();
                break;
        }
    }

    private void selectMusic() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            activity.startActivityForResult(Intent.createChooser(intent, "选择音频"), Constant.REQUEST_RE_MERGE_AUDIO);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
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

    private void mergeVideo() {
        if (mergeVideoList.size() <= 1) {
            ToastUtils.show(activity, String.format("合并的视频数量为 %d，无法合并！", mergeVideoList.size()));
            return;
        }
        List<EpVideo> mergeVideos = new ArrayList<>();
        for (VideoEditEntity videoEditEntity : mergeVideoList) {
            EpVideo epVideo = new EpVideo(videoEditEntity.getVideoOutPath());
            mergeVideos.add(epVideo);
        }
        //输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
        final String outFileTemp = PathConstant.getExVideoDir() + "/" + "ve-merge-" + DataUtils.getCurrentTime() + ".mp4";
        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outFileTemp);
        int outWidth = TextUtils.isEmpty(edtDisplayOutWidth.getText().toString()) ? 1080 : Integer.parseInt(edtDisplayOutWidth.getText().toString());
        int outHeight = TextUtils.isEmpty(edtDisplayOutHeight.getText().toString()) ? 1920 : Integer.parseInt(edtDisplayOutHeight.getText().toString());

        outputOption.setWidth(outWidth);//输出视频宽，默认480
        outputOption.setHeight(outHeight);//输出视频高度,默认360
//        outputOption.frameRate = 30;//输出视频帧率,默认30
//        outputOption.bitRate = 10;//输出视频码率,默认10
        EpEditor.merge(mergeVideos, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                if(TextUtils.isEmpty(mergeEditEntity.getMergeAudioPath())) {
                    ToastUtils.show(activity, "视频合成成功！");
                    mergeEditEntity.setVideoOutPath(outFileTemp);
                    MediaUtils.updateToMedia(activity, outFileTemp);
                }
                else {
                    mergeMusic(outFileTemp, mergeEditEntity.getMergeAudioPath());
                }
            }

            @Override
            public void onFailure() {
                ToastUtils.show(activity, "视频合成失败！");
            }

            @Override
            public void onProgress(final float progress) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progressTemp = NumberUtils.getMiddleValue(0, (int) (NumberUtils.getFloat(progress, 3) * 100), 100);
                        String info = String.format(Locale.getDefault(), "合成进度: %d%%", progressTemp);
                        activity.showProgress(info, progress != 1);
                        Log.i("VideoEdit", "合成视频处理进度：" + progress);
                    }
                });
            }
        });
    }

    private void mergeMusic(final String videoInPath, String audioInPath){
        final String videoOutPath = PathConstant.getExVideoDir() + "/" + "ve-merge-audio-" + DataUtils.getCurrentTime() + ".mp4";
        EpEditor.music(videoInPath, audioInPath, videoOutPath, 1f, 0.7f, new OnEditorListener() {
            @Override
            public void onSuccess() {
                ToastUtils.show(activity, "音视频合成成功！");
                mergeEditEntity.setVideoOutPath(videoOutPath);
                MediaUtils.updateToMedia(activity, mergeEditEntity.getVideoOutPath());
                MediaUtils.updateToMediaDelete(activity, videoInPath);
            }

            @Override
            public void onFailure() {
                ToastUtils.show(activity, "音视频合成失败！");
                mergeEditEntity.setVideoOutPath(null);
            }

            @Override
            public void onProgress(final float v) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progressTemp = NumberUtils.getMiddleValue(0, (int) (NumberUtils.getFloat(v, 3) * 100), 100);
                        String info = String.format(Locale.getDefault(), "音视频合成进度: %d%%", progressTemp);
                        activity.showProgress(info, v != 1);
                        Log.i("VideoEdit", "音视频合成进度：" + v);
                    }
                });
            }
        });
    }
}
