package com.exer.videocollection;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exer.widgets.MyMediaController;
import com.exer.widgets.Tools;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.utils.StringUtils;
import io.vov.vitamio.widget.VideoView;


public class VideoPlayActivity extends Activity  {

    private String path;
    private String title;
    private VideoView mVideoView;
    private LinearLayout mLoadingLayout;
    private ImageView mLoadingImg;
    private ObjectAnimator mOjectAnimator;
    private MyMediaController mediaController;

    /**
     * 当前进度
     */
//    private Long currentPosition = (long) 0;
//    private String mVideoPath = "";
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        path = getIntent().getExtras().getString("VideoUrl");
        title = getIntent().getExtras().getString("VideoTitle");
        Vitamio.isInitialized(getApplicationContext());
        mediaController = new MyMediaController(this);
        setContentView(R.layout.videoplay_layout);
        playfunction();
    }


    void playfunction(){
        mVideoView = findViewById(R.id.video_view);
        if (path.equals("")) {
            // Tell the user to provide a media file URL/path.
            Toast.makeText(VideoPlayActivity.this, "Please edit VideoViewDemo Activity, and set path" + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
        } else {
			/*
			 * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */
            mLoadingLayout= findViewById(R.id.loading_LinearLayout);
            mLoadingImg= findViewById(R.id.loading_image);
            mVideoView.setVideoPath(path);
            mVideoView.setMediaController(mediaController);
            mVideoView.requestFocus();
            setVideoPageSize(2);
            //mVideoView.setBufferSize(1024 * 1024);

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                    mediaController.setFileName(title);
                }
            });
            mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
                    switch (arg1) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            //开始缓存，暂停播放
                            startLoadingAnimator();
                            if (mVideoView.isPlaying()) {
                                stopPlay();
                            }
                            mediaController.mRoot.findViewById(R.id.mediacontroller_play_pause).setEnabled(false);
                            mediaController.mRoot.findViewById(R.id.mediacontroller_previous).setEnabled(false);
                            mediaController.mRoot.findViewById(R.id.mediacontroller_next).setEnabled(false);
                            mediaController.mRoot.findViewById(R.id.mediacontroller_screen_fit).setEnabled(false);
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            //缓存完成，继续播放
                            stopLoadingAnimator();
                            startPlay();
                            mediaController.mRoot.findViewById(R.id.mediacontroller_play_pause).setEnabled(true);
                            break;
                        case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                            //显示 下载速度
                            ((TextView)(mediaController.mRoot.findViewById(R.id.net_work_speed_tv))).setText(arg2+"kB/s");
                            ((TextView)(mediaController.mRoot.findViewById(R.id.currenttime_tv))).setText(Tools.getShortTime());
                            ((TextView)(mediaController.mRoot.findViewById(R.id.download_precent_tv))).setText(Tools.getBatterLevel(VideoPlayActivity.this) + "%");
                            break;
                    }
                    return true;
                }
            });
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                }
            });
        }
    }


    private void startLoadingAnimator() {
        if(mOjectAnimator==null){
            mOjectAnimator = ObjectAnimator.ofFloat(mLoadingImg, "rotation", 0f, 360f);
        }
        mLoadingLayout.setVisibility(View.VISIBLE);

        mOjectAnimator.setDuration(1000);
        mOjectAnimator.setRepeatCount(-1);
        mOjectAnimator.start();
    }

    private void stopLoadingAnimator() {
        mLoadingLayout.setVisibility(View.GONE);
        mOjectAnimator.cancel();
    }

    private void startPlay() {
        mVideoView.start();
    }

    private void stopPlay() {
        mVideoView.pause();
    }

    public void onPause() {
        super.onPause();
//        currentPosition = mVideoView.getCurrentPosition();
        mVideoView.pause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mVideoView!=null){
            mVideoView.stopPlayback();
            mVideoView = null;
        }
    }

    /**
     * 获取视频当前帧
     */
    public Bitmap getCurrentFrame() {
        if(mVideoView!=null){
            MediaPlayer mediaPlayer = mVideoView.getmMediaPlayer();
            return  mediaPlayer.getCurrentFrame();
        }
        return null;
    }
    /**
     * 快退(每次都快进视频总时长的1%)
     */
    public void speedVideo() {
        if(mVideoView!=null){
            long duration = mVideoView.getDuration();
            long currentPosition = mVideoView.getCurrentPosition();
            long goalduration=currentPosition+duration/10;
            if(goalduration>=duration){
                mVideoView.seekTo(duration);
            }else{
                mVideoView.seekTo(goalduration);
            }
            Toast.makeText(this, StringUtils.generateTime(goalduration),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 快退(每次都快退视频总时长的1%)
     */
    public void reverseVideo() {
        if(mVideoView!=null){
            long duration = mVideoView.getDuration();
            long currentPosition = mVideoView.getCurrentPosition();
            long goalduration=currentPosition-duration/10;
            if(goalduration<=0){
                mVideoView.seekTo(0);
            }else{
                mVideoView.seekTo(goalduration);
            }
            Toast.makeText(this, StringUtils.generateTime(goalduration),
                    Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 设置屏幕的显示大小
     */
    public void setVideoPageSize(int currentPageSize) {
        if(mVideoView!=null){
            mVideoView.setVideoLayout(currentPageSize,0);
        }
    }
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (MessageType.values()[(int)(msg.getData().get("MessageType"))]){
                case ExitApp:
                    isExit = false;
                    break;
            }
        }
    };
    private static boolean isExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出播放",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("MessageType",MessageType.ExitApp.ordinal());
            msg.setData(data);
            handler.sendMessageDelayed(msg, 2000);
        } else {
            VideoPlayActivity.this.finish();
        }
    }
}
